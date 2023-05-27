package moe.moyu.command;

import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import moe.moyu.util.MessageSender;
import net.mamoe.mirai.contact.User;

import java.util.List;

/**
 *  查询: 关键字匹配
 */
public class FetchByKeywordExact extends Command {

    public FetchByKeywordExact(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.FETCH_BY_KEYWORD_EXACT;
    }

    @Override
    public void execute(User user, List<Image> imageList, String keyWord) {
        List<Image> images = imageDao.fetchObject(user.getId(), keyWord);
        MessageSender.sendForwardMessage(user, images);
    }
}
