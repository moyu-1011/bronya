package moe.moyu.manager;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import moe.moyu.cache.PersistCache;
import moe.moyu.command.*;
import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import moe.moyu.util.ObjectConverter;
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
    public static final Map<CommandType, Command> commandStrategy = new HashMap<>();

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

        // 处理简单消息
        if (isSimpleMessage(messageChain)) {
            String plainText;
            SingleMessage userMessage = extractSingleMessage(messageChain);
            if ((plainText = extractPlainText(userMessage)) != null) {
                processPlainTextMessage(friend, plainText);
            } else if (userMessage instanceof net.mamoe.mirai.message.data.Image) {
                processImageMessage(friend, userMessage);
            }
        }

        // 处理复杂消息
        else if (isComplexMessage(messageChain)) {
            List<net.mamoe.mirai.message.data.Image> imagesOfMessages = new ArrayList<>();
            String keyWord = traverseMessages(imagesOfMessages, messageChain);
            processComplexMessages(imagesOfMessages, friend, keyWord);
        }
    }


    /**
     * 处理复杂消息。
     * 当执行过持久化操作后, 也会撤销预保存指令。
     * <p>case 1: 消息里没有图片, 返回
     * <p>case 2: 消息里存在图片和保存指令。 将这些图片持久化, 然后撤销预保存指令。
     * <p>case 3: 消息里只有图片, 不存在保存指令, 存在预保存指令。 执行持久化，撤销预保存指令。
     * <p>case 4: 消息里只有图片, 不存在保存指令, 不存在预保存指令。将图片添加到缓存。
     * <p>预保存指令: {@link PersistCache}不存在图片, 这时输入一条保存指令, 那么下一次发送的图片/批量图片会被持久化。
     *
     * @param imageList 消息里的图片
     * @param friend    好友
     * @param keyword   关键词
     */
    private static void processComplexMessages(@Nullable List<net.mamoe.mirai.message.data.Image> imageList, Friend friend, @Nullable String keyword) {
        // case 1: 消息里没有图片, 返回
        if (imageList == null || imageList.size() == 0) return;

        // case 2: 存在图片和保存指令
        if (keyword != null) {
            List<Image> storageList = ObjectConverter.toImageList(imageList, friend.getId(), keyword);
            persistImagesWithKey(friend, storageList, false);
        }

        // case 3: messageChain中有图片，不存在保存命令。如果有预保存指令, 执行保存
        else if (PersistCache.isNextPersist() && PersistCache.getCacheKey() != null) {
            List<Image> storageList = ObjectConverter.toImageList(imageList, friend.getId(), PersistCache.getCacheKey());
            persistImagesWithKey(friend, storageList, true);
        }

        // case 4: 只有图片
        else if (!PersistCache.isNextPersist()) {
            PersistCache.addBatch(imageList);
        }
    }

    /**
     * 持久化到数据库, 撤销预保存命令
     *
     * @param friend        好友
     * @param imageList     存储对象
     * @param clearCacheImg 是否清空缓存
     */
    private static void persistImagesWithKey(Friend friend, List<Image> imageList, boolean clearCacheImg) {
        Command save = commandStrategy.get(CommandType.SAVE);
        save.execute(friend, imageList, null);
        PersistCache.clearCacheKey();
        PersistCache.setNextPersist(false);
        if (clearCacheImg) PersistCache.clearCacheImage();
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
        String plainText, keyWord = null;
        for (SingleMessage singleMessage : messageChain) {
            // 文本指令
            if (singleMessage instanceof PlainText && Command.isCommand(plainText = ((PlainText) singleMessage).getContent())) {
                CommandType commandType = CommandType.type(plainText);
                if (commandType == null) continue;

                // 多条保存指令, 第一条有效
                if (ObjectUtil.equal(commandType, CommandType.SAVE) && keyWord == null) {
                    keyWord = Command.extractKeyword(plainText, CommandType.SAVE);
                }
            }
            // 图片
            if (singleMessage instanceof net.mamoe.mirai.message.data.Image) {
                imagesOfMessages.add((net.mamoe.mirai.message.data.Image) singleMessage);
            }
        }
        return keyWord;
    }

    /**
     * 处理图片消息
     */
    private static void processImageMessage(Friend friend, SingleMessage userMessage) {
        PersistCache.add((net.mamoe.mirai.message.data.Image) userMessage);
        if (PersistCache.isNextPersist() && PersistCache.getCacheKey() != null) {
            Command save = commandStrategy.get(CommandType.SAVE);
            List<Image> imageList = getCacheEntities(friend.getId(), PersistCache.getCacheKey());
            save.execute(friend, imageList, null);
            PersistCache.reset();
        }
    }

    /**
     * 处理纯文本消息
     */
    private static void processPlainTextMessage(Friend friend, String plainText) {
        CommandType type = CommandType.type(plainText);
        if (type == null) return;

        String keyword = Command.extractKeyword(plainText, type);
        Command command = commandStrategy.get(type);

        if (command instanceof Save) {
            PersistCache.setCacheKey(keyword);
            if (PersistCache.isEmpty()) {
                // 缓存列表为空, 预读取下一张/一批图片执行保存
                PersistCache.setNextPersist(true);
            } else {
                // 缓存列表不空, 保存图片, 清空缓存图片
                List<Image> imageList = getCacheEntities(friend.getId(), keyword);
                command.execute(friend, imageList, null);
                PersistCache.reset();
            }
        } else {
            command.execute(friend, null, keyword);
        }
    }

    /**
     * 获取缓存对象, 并转实体对象
     */
    private static List<Image> getCacheEntities(Long ownerId, String keyword) {
        List<net.mamoe.mirai.message.data.Image> cacheImgList = PersistCache.get();
        return ObjectConverter.toImageList(cacheImgList, ownerId, keyword);
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
