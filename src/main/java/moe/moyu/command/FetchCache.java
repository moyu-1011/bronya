package moe.moyu.command;

import moe.moyu.cache.PersistCache;
import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import moe.moyu.util.ObjectConverter;
import moe.moyu.util.MessageSender;
import net.mamoe.mirai.contact.User;

import java.util.List;

/**
 * 查询缓存
 */
public class FetchCache extends Command{

    public FetchCache(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    CommandType getCommandType() {
        return CommandType.FETCH_CACHE;
    }

    @Override
    public void execute(User user, List<Image> storageList, String keyWord) {
        List<net.mamoe.mirai.message.data.Image> imageList = PersistCache.get();
        String cacheKey = PersistCache.getCacheKey();
        List<Image> images = ObjectConverter.toImageList(imageList, user.getId(), cacheKey);
        MessageSender.send(user, images);
    }

}
