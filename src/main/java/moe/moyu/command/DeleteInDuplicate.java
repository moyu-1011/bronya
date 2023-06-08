package moe.moyu.command;

import moe.moyu.cache.MessageDuplicate;
import moe.moyu.cache.PersistCacheHolder;
import moe.moyu.dao.ImageDao;

/**
 * 删除: 通过副本索引
 */
public class DeleteInDuplicate extends Command{
    public DeleteInDuplicate(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    CommandType getCommandType() {
        return CommandType.DELETE_BY_INDEX;
    }

    @Override
    public void execute() {
        PersistCacheHolder.PersistCache cache = PersistCacheHolder.getCache();
        int deleteIndex = Integer.parseInt(cache.getCacheKey());
        boolean isLegalCommandIndex = MessageDuplicate.setDeleteIndex(deleteIndex);
        if (isLegalCommandIndex){
            Long messageId = MessageDuplicate.getMessageId();
            imageDao.delete(messageId);
        }
    }
}
