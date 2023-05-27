package moe.moyu.command;

import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import moe.moyu.util.MessageSender;
import net.mamoe.mirai.contact.User;

import java.util.List;

/**
 * 查询所有
 */
public class FetchAll extends Command {

    public FetchAll(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.FETCH_ALL;
    }

    @Override
    public void execute(User user, List<Image> imageList, String keyWord) {
        List<Image> images = imageDao.fetchAll(user.getId());
        MessageSender.sendForwardMessage(user, images);
    }
}
