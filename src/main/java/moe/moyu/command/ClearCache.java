package moe.moyu.command;

import moe.moyu.cache.PersistCache;
import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import net.mamoe.mirai.contact.User;

import java.util.List;

/**
 * 删除持久化缓存
 */
public class ClearCache extends Command {
    public ClearCache(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    CommandType getCommandType() {
        return CommandType.CLEAR_CACHE;
    }

    @Override
    public void execute(User user, List<Image> imageList, String keyword) {
        PersistCache.reset();
    }
}
