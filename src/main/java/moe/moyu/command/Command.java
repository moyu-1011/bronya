package moe.moyu.command;

import moe.moyu.constant.Constant;
import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import net.mamoe.mirai.contact.User;

import java.util.List;

/**
 * 抽象命令类
 * 具体命令继承此类
 */
public abstract class Command {

    protected final ImageDao imageDao;

    protected Command(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    abstract CommandType getCommandType();

    abstract public void execute(User user, List<Image> imageList, String keyWord);

    /**
     * 文本消息是不是指令
     *
     * @param text 文本消息
     * @return 是不是指令
     */
    public static boolean isCommand(String text) {
        return existCommandSign(text) && text.length() > 1;
    }

    /**
     * 文本消息是否包含指令符号
     *
     * @param text 文本消息
     * @return 是否包含指令符号
     */
    private static boolean existCommandSign(String text) {
        text = text.toLowerCase();
        return text.startsWith(Constant.SAVE_SIGN.toLowerCase())
                || text.startsWith(Constant.SEARCH_ALL_SIGN.toLowerCase())
                || text.startsWith(Constant.SEARCH_FUZZY_SIGN.toLowerCase())
                || text.startsWith(Constant.SEARCH_EXACT_SIGN.toLowerCase());
    }

    /**
     * 从指令中提取关键字。
     * <p> 未检查command入参
     *
     * @param command 文本指令
     * @param type    指令类型
     * @return 关键字
     */
    public static String extractKeyWord(String command, CommandType type) {
        return command.substring(type.getSign().length());
    }
}
