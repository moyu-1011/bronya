package moe.moyu.command;

import moe.moyu.cache.CurrentUser;
import moe.moyu.cache.PersistCacheHolder;
import moe.moyu.dao.ImageDao;

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
    public void execute() {
        PersistCacheHolder.PersistCache cache = PersistCacheHolder.getCache();
        imageDao.delete(CurrentUser.get().getId(), cache.getCacheKey());
    }
}
