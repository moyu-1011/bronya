package moe.moyu.cache;

import net.mamoe.mirai.message.data.Image;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存用户持久化缓存的容器
 */
public class PersistCacheHolder {
    private static final Map<Long, PersistCache> cacheHolder = new ConcurrentHashMap<>();

    public static PersistCache getCache() {
        Long currentUserId = CurrentUser.get().getId();
        return getCache(currentUserId);
    }

    /**
     * 获取用户的持久化缓存
     * <p> 不存在时返回一个新创建的缓存
     *
     * @param userId 用户id
     * @return 用户的持久化缓存
     */
    public static PersistCache getCache(Long userId) {
        return cacheHolder.getOrDefault(userId, new PersistCache());
    }

    public static void updateCache(PersistCache cache) {
        Long currentUserId = CurrentUser.get().getId();
        cacheHolder.put(currentUserId, cache);
    }

    public static void putCache(Long userId, PersistCache cache) {
        cacheHolder.put(userId, cache);
    }


    /**
     * 持久化缓存
     * 发送的图片存到缓存区，在收到保存指令时, 将缓存区持久化
     */
    public static class PersistCache {

        private final Deque<Image> imageCache = new ArrayDeque<>();

        private final int maxSize = 10;

        /**
         * 缓存关键词, 在当前request中就会被消费
         */
        private String cacheKey = null;

        /**
         * 是否持久化下一次图片消息
         */
        private boolean nextPersist = false;

        /**
         * imageCache为空时, 输入持久化命令, 由于无法执行这次持久化操作,
         * <p>就会把关键词缓存, 等待下一次图片消息到来时, 消费掉该关键字, 执行持久化。
         * <p>这个关键字不会在当前request被消费
         */
        private String nextPersistKey = null;

        public List<Image> get() {
            return new ArrayList<>(imageCache);
        }

        public void add(Image image) {
            imageCache.offer(image);
            checkThreshold();
        }

        public void clearCacheImage() {
            imageCache.clear();
        }

        public boolean isEmpty() {
            return imageCache.isEmpty();
        }

        public void addBatch(List<Image> imageList) {
            if (imageList != null && imageList.size() > 0) {
                imageCache.addAll(imageList);
                checkThreshold();
            }
        }

        private void checkThreshold() {
            while (imageCache.size() > maxSize) imageCache.pollFirst();
        }

        public void setCacheKey(String key) {
            cacheKey = key;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public boolean isNextPersist() {
            return nextPersist;
        }

        public void setNextPersist(boolean nextPersist) {
            this.nextPersist = nextPersist;
        }

        public void setNextPersistKey(String nextPersistKey) {
            this.nextPersistKey = nextPersistKey;
        }

        public String getNextPersistKey() {
            return this.nextPersistKey;
        }

        public void clearNextPersistKey() {
            this.nextPersistKey = null;
        }

        public void clearCacheKey() {
            cacheKey = null;
        }

        public void reset() {
            clearCacheImage();
            clearCacheKey();
            setNextPersist(false);
        }

        public void resetNextPersist() {
            clearCacheImage();
            clearNextPersistKey();
        }

    }

}