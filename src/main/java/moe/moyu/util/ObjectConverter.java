package moe.moyu.util;

import cn.hutool.core.util.ObjectUtil;
import moe.moyu.entity.Image;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectConverter {

    public static Image toImage(net.mamoe.mirai.message.data.Image image, Long ownerId, String keyword) {
        Image storage = new Image();
        storage.setOwnerId(ownerId);
        storage.setKeyword(keyword);
        storage.setImageUrl(net.mamoe.mirai.message.data.Image.queryUrl(image));
        storage.setImageId(image.getImageId());
        return storage;
    }

    public static List<Image> toImageList(List<net.mamoe.mirai.message.data.Image> imageList, Long ownerId, String keyword) {
        List<Image> storageList = new ArrayList<>();
        for (net.mamoe.mirai.message.data.Image image : imageList) {
            Image storage = toImage(image, ownerId, keyword);
            storageList.add(storage);
        }
        return storageList;
    }

    /**
     * entity转map
     */
    public static Map<String, Object> toMap(Image image) {
        Map<String, Object> map = new HashMap<>();
        if (ObjectUtil.isNotNull(image.getId())) map.put("id", image.getId());
        if (ObjectUtil.isNotNull(image.getOwnerId())) map.put("owner_id", image.getOwnerId());
        if (ObjectUtil.isNotNull(image.getKeyword())) map.put("key_word", image.getKeyword());
        if (ObjectUtil.isNotNull(image.getImageId())) map.put("image_id", image.getImageId());
        if (ObjectUtil.isNotNull(image.getImageUrl())) map.put("image_url", image.getImageUrl());
        if (ObjectUtil.isNotNull(image.isDeleted())) map.put("is_deleted", image.isDeleted());
        if (ObjectUtil.isNotNull(image.getAddTime())) map.put("add_time", image.getAddTime());
        return map;
    }

    public static List<Map<String, Object>> toMapList(List<Image> imageList, Long userId) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Image image : imageList) {
            image.setOwnerId(userId);
            image.setDeleted(false);
            image.setAddTime(LocalDateTime.now());
            Map<String, Object> storageMap = toMap(image);
            mapList.add(storageMap);
        }
        return mapList;
    }

    public static Image toEntity(Map<String, Object> map) {
        Image image = new Image();
        if (map.containsKey("id")) image.setId(Long.valueOf(String.valueOf(map.get("id"))));
        if (map.containsKey("owner_id")) image.setOwnerId(toLong(map.get("owner_id")));
        if (map.containsKey("key_word")) image.setKeyword(map.get("key_word").toString());
        if (map.containsKey("image_id")) image.setImageId(map.get("image_id").toString());
        if (map.containsKey("image_url")) image.setImageUrl(map.get("image_url").toString());
        return image;
    }

    public static List<Image> toEntityList(List<Map<String, Object>> mapList) {
        List<Image> imageList = new ArrayList<>();
        for (Map<String, Object> storagemap : mapList) {
            Image image = toEntity(storagemap);
            imageList.add(image);
        }
        return imageList;
    }

    public static Long toLong(Object object) {
        return Long.parseLong(String.valueOf(object));
    }

}
