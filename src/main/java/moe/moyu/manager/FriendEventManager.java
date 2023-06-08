package moe.moyu.manager;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import moe.moyu.cache.CurrentUser;
import moe.moyu.cache.PersistCacheHolder;
import moe.moyu.command.*;
import moe.moyu.dao.ImageDao;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FriendEventManager {

    private static final ImageDao IMAGE_DAO = new ImageDao();
    private static final Map<CommandType, Command> commandStrategy = new HashMap<>();
    private static PersistCacheHolder.PersistCache persistCache = null;

    static {
        commandStrategy.put(CommandType.FETCH_ALL, new FetchAll(IMAGE_DAO));
        commandStrategy.put(CommandType.FETCH_BY_KEYWORD_EXACT, new FetchByKeywordExact(IMAGE_DAO));
        commandStrategy.put(CommandType.FETCH_BY_KEYWORD_FUZZY, new FetchByKeywordFuzzy(IMAGE_DAO));
        commandStrategy.put(CommandType.FETCH_CACHE, new FetchCache(IMAGE_DAO));
        commandStrategy.put(CommandType.CLEAR_CACHE, new ClearCache(IMAGE_DAO));
        commandStrategy.put(CommandType.SAVE, new Save(IMAGE_DAO));
        commandStrategy.put(CommandType.DELETE_BY_KEYWORD, new DeleteByKeyword(IMAGE_DAO));
        commandStrategy.put(CommandType.DELETE_BY_INDEX, new DeleteInDuplicate(IMAGE_DAO));
    }

    /**
     * 处理好友消息
     *
     * @param friendMessageEvent 好友消息事件
     */
    public static void process(FriendMessageEvent friendMessageEvent) {
        Friend friend = friendMessageEvent.getFriend();
        MessageChain messageChain = friendMessageEvent.getMessage();
        CurrentUser.set(friend);
        persistCache = PersistCacheHolder.getCache();

        // 处理简单消息
        if (isSimpleMessage(messageChain)) {
            String plainText;
            SingleMessage userMessage = extractSingleMessage(messageChain);
            if ((plainText = extractPlainText(userMessage)) != null) {
                processPlainTextMessage(plainText);
            } else if (userMessage instanceof net.mamoe.mirai.message.data.Image) {
                processImageMessage(userMessage);
            }
        }

        // 处理复杂消息
        else if (isComplexMessage(messageChain)) {
            List<net.mamoe.mirai.message.data.Image> imagesOfMessages = new ArrayList<>();
            String keyword = traverseMessages(imagesOfMessages, messageChain);
            processComplexMessages(imagesOfMessages, keyword);
        }

        PersistCacheHolder.putCache(friend.getId(), persistCache);
        CurrentUser.reset();
    }


    /**
     * 处理复杂消息。
     * 当执行过持久化操作后, 也会撤销预保存指令。
     * <p>case 1: 消息里没有图片, 返回
     * <p>case 2: 消息里存在图片和保存指令。 将这些图片持久化, 包括缓存。 然后撤销预保存指令。
     * <p>case 3: 消息里只有图片, 不存在保存指令, 存在预保存指令。 执行持久化，撤销预保存指令。
     * <p>预保存指令: {@link PersistCacheHolder.PersistCache}不存在图片, 这时输入一条保存指令, 那么下一次发送的图片/批量图片会被持久化。
     *
     * @param imageList 消息里的图片
     * @param keyword   关键词
     */
    private static void processComplexMessages(@Nullable List<net.mamoe.mirai.message.data.Image> imageList, @Nullable String keyword) {
        // case 1: 消息里没有图片, 返回
        if (imageList == null || imageList.size() == 0) return;

        persistCache.addBatch(imageList);

        // case 2: 存在图片和保存指令
        if (keyword != null) {
            persistCache.setCacheKey(keyword);
            persistImages();
        }

        // case 3: messageChain中只有图片。如果有预保存指令, 执行保存
        else if (persistCache.isNextPersist() && persistCache.getNextPersistKey() != null) {
            persistImages();
        }
    }

    /**
     * 持久化到数据库, 撤销预保存命令
     */
    private static void persistImages() {
        Command save = commandStrategy.get(CommandType.SAVE);
        save.execute();
    }

    /**
     * 遍历messageChain, 将图片添加到imagesOfMessages.
     * <p>如果messageChain内有保存指令, 取第一次出现的指令, 返回关键词
     *
     * @param imagesOfMessages 消息中的图片
     * @param messageChain     消息chain
     * @return 关键字
     */
    private static String traverseMessages(List<net.mamoe.mirai.message.data.Image> imagesOfMessages, MessageChain messageChain) {
        String plainText, keyword = null;
        for (SingleMessage singleMessage : messageChain) {
            // 文本指令
            if (singleMessage instanceof PlainText && Command.isCommand(plainText = ((PlainText) singleMessage).getContent())) {
                CommandType commandType = CommandType.type(plainText);
                if (commandType == null) continue;

                // 多条保存指令, 第一条有效
                if (ObjectUtil.equal(commandType, CommandType.SAVE) && keyword == null) {
                    keyword = Command.extractKeyword(plainText, CommandType.SAVE);
                }
            }
            // 图片
            if (singleMessage instanceof net.mamoe.mirai.message.data.Image) {
                imagesOfMessages.add((net.mamoe.mirai.message.data.Image) singleMessage);
            }
        }
        return keyword;
    }

    /**
     * 处理图片消息
     */
    private static void processImageMessage(SingleMessage userMessage) {
        persistCache.add((net.mamoe.mirai.message.data.Image) userMessage);
        if (persistCache.isNextPersist() && persistCache.getNextPersistKey() != null) {
            Command save = commandStrategy.get(CommandType.SAVE);
            save.execute();
        }
    }

    /**
     * 处理纯文本消息
     */
    private static void processPlainTextMessage(String plainText) {
        CommandType type = CommandType.type(plainText);
        if (type == null) return;

        String keyword = Command.extractKeyword(plainText, type);
        Command command = commandStrategy.get(type);

        if (command instanceof Save && persistCache.isEmpty()) {
            // 缓存列表为空, 等待下一次图片消息执行持久化
            persistCache.setNextPersist(true);
            persistCache.setNextPersistKey(keyword);
        } else {
            persistCache.setCacheKey(keyword);
            command.execute();
        }
    }

    /**
     * 提取用户文本消息, 非文本消息返回null
     */
    private static String extractPlainText(SingleMessage singleMessage) {
        String plainText = null;
        if (singleMessage instanceof PlainText) {
            Command.isCommand(plainText = ((PlainText) singleMessage).getContent());
        }
        return plainText;
    }

    /**
     * 提取用户消息
     *
     * @param messageChain 消息
     * @return 用户消息
     */
    private static SingleMessage extractSingleMessage(MessageChain messageChain) {
        if (messageChain == null || messageChain.size() < 2) {
            return null;
        }
        return messageChain.get(1);
    }

    /**
     * <p>简单消息, 例如: 只有文本消息、只有一张图片
     */
    private static boolean isSimpleMessage(MessageChain messageChain) {
        return messageChain.size() < 3;
    }

    /**
     * <p>复杂消息, 例如: 文本消息+图片、多张图片
     */
    private static boolean isComplexMessage(MessageChain messageChain) {
        return messageChain.size() > 2;
    }
}
