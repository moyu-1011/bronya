package moe.moyu.command;

import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import net.mamoe.mirai.contact.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 删除: 关键字匹配
 */
public class DeleteByKeyword extends Command{


    public DeleteByKeyword(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    CommandType getCommandType() {
        return CommandType.DELETE_BY_KEYWORD;
    }

    @Override
    public void execute(User user, @Nullable List<Image> imageList, String keyword) {
        imageDao.delete(user.getId(), keyword);
    }
}
