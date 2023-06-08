package moe.moyu.command;

import moe.moyu.cache.PersistCacheHolder;
import moe.moyu.dao.ImageDao;

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
    public void execute() {
        PersistCacheHolder.PersistCache persistCache = PersistCacheHolder.getCache();
        persistCache.reset();
        PersistCacheHolder.updateCache(persistCache);
    }
}
