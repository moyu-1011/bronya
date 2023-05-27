package moe.moyu.command;

import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import net.mamoe.mirai.contact.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 持久化
 */
public class Save extends Command {


    public Save(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.SAVE;
    }

    @Override
    public void execute(User user, List<Image> imageList, @Nullable String keyWord) {
        imageDao.persist(user.getId(), imageList);
    }

}
