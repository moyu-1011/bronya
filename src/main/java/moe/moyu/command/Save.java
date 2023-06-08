package moe.moyu.command;

import moe.moyu.cache.CurrentUser;
import moe.moyu.cache.PersistCacheHolder;
import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import moe.moyu.util.ObjectConverter;
import net.mamoe.mirai.contact.User;

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
    public void execute() {
        User user = CurrentUser.get();
        PersistCacheHolder.PersistCache persistCache = PersistCacheHolder.getCache();
        String keyword = persistCache.isNextPersist() ? persistCache.getNextPersistKey() : persistCache.getCacheKey();

        List<Image> imageList = ObjectConverter.toImageList(persistCache.get(), user.getId(), keyword);
        imageDao.persist(user.getId(), imageList);

        if (persistCache.isNextPersist()) {
            persistCache.resetNextPersist();
        } else {
            persistCache.reset();
        }

        PersistCacheHolder.putCache(user.getId(), persistCache);
    }

}
