package moe.moyu.cache;

import net.mamoe.mirai.message.data.Image;

import java.util.*;

/**
 * 持久化缓存
 * 发送的图片存到缓存区，在收到保存指令时, 将缓存区持久化
 */
public class PersistCache {

    private static final Deque<Image> cacheImage = new ArrayDeque<>();

    private static final int maxSize = 10;

    private static String cacheKey = null;

    private static boolean nextPersist = false;

    public static List<Image> get() {
        return new ArrayList<>(cacheImage);
    }

    public static void add(Image image) {
        cacheImage.offer(image);
        checkThreshold();
    }

    public static void clearCacheImage() {
        cacheImage.clear();
    }

    public static boolean isEmpty() {
        return cacheImage.isEmpty();
    }

    public static void addBatch(List<Image> imageList) {
        cacheImage.addAll(imageList);
        checkThreshold();
    }

    private static void checkThreshold() {
        while (cacheImage.size() > maxSize) cacheImage.pollFirst();
    }

    public static void setCacheKey(String key) {
        cacheKey = key;
    }

    public static String getCacheKey() {
        return cacheKey;
    }

    public static boolean isNextPersist() {
        return nextPersist;
    }

    public static void setNextPersist(boolean nextPersist) {
        PersistCache.nextPersist = nextPersist;
    }

    public static void clearCacheKey() {
        cacheKey = null;
    }

    public static void reset() {
        clearCacheImage();
        clearCacheKey();
        setNextPersist(false);
    }
}
