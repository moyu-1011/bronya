package moe.moyu.command;

import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import moe.moyu.util.MessageSender;
import net.mamoe.mirai.contact.User;

import java.util.List;

/**
 * 查询: 关键字模糊匹配
 */
public class FetchByKeywordFuzzy extends Command {

    public FetchByKeywordFuzzy(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.FETCH_BY_KEYWORD_FUZZY;
    }

    @Override
    public void execute(User user, List<Image> imageList, String keyWord) {
        List<Image> images = imageDao.fetchObjectFuzzy(user.getId(), keyWord);
        MessageSender.sendForwardMessage(user, images);
    }
}
