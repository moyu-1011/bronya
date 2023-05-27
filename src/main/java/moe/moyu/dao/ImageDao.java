package moe.moyu.dao;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import moe.moyu.constant.Constant;
import moe.moyu.entity.Image;
import moe.moyu.db.DBUtil;
import moe.moyu.util.ObjectConverter;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ImageDao {


    /**
     * 持久化
     *
     * @param userId 用户id, 也就是企鹅号
     * @param image  存储对象
     */
    public void persist(Long userId, Image image) {
        try {
            image.setOwnerId(userId);
            image.setDeleted(false);
            image.setAddTime(LocalDateTime.now());
            DBUtil.insert(Constant.TABLE_NAME, ObjectConverter.toMap(image));
        } catch (SQLException e) {
            log.error("exception when sql insert, message: {}, sql state: {}", e.getMessage(), e.getSQLState());
            throw new RuntimeException();
        }
    }

    /**
     * 批量持久化
     *
     * @param userId    用户id
     * @param imageList imageList
     */
    public void persist(Long userId, List<Image> imageList) {
        try {
            DBUtil.insertAll(Constant.TABLE_NAME, ObjectConverter.toMapList(imageList, userId));
        } catch (SQLException e) {
            log.error("exception occurred when sql batch insert, message: {}, sql state: {}", e.getMessage(), e.getSQLState());
            throw new RuntimeException();
        }
    }

    /**
     * 逻辑删除
     * @param id 主键
     */
    public void delete(Long id) {
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("id", id);
        whereMap.put("is_deleted", false);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("is_deleted", true);
        valueMap.put("delete_time", LocalDateTime.now());
        try {
            DBUtil.update(Constant.TABLE_NAME, valueMap, whereMap);
        } catch (SQLException e) {
            log.error("exception occurred when sql delete by id, message: {}, sql state: {}", e.getMessage(), e.getSQLState());
            throw new RuntimeException();
        }
    }

    /**
     * 逻辑删除
     * @param userId 用户id
     * @param keyword 关键词
     */
    public void delete(Long userId, String keyword) {
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("key_word", keyword);
        whereMap.put("owner_id", userId);
        whereMap.put("is_deleted", false);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("is_deleted", true);
        valueMap.put("delete_time", LocalDateTime.now());
        try {
            DBUtil.update(Constant.TABLE_NAME, valueMap, whereMap);
        } catch (SQLException e) {
            log.error("exception occurred when sql delete by userId and keyword, message: {}, sql state: {}", e.getMessage(), e.getSQLState());
            throw new RuntimeException();
        }
    }

    public List<Image> fetchAll(Long userId) {
        return fetchObject(userId, null);
    }

    /**
     * 模糊查询
     * @param ownerId 用户id
     * @param keyword 关键词
     */
    public List<Image> fetchObjectFuzzy(Long ownerId, String keyword) {
        List<Image> imageList = null;
        Map<String, Object> equalMap = new HashMap<>();
        equalMap.put("owner_id", ownerId);
        equalMap.put("is_deleted", false);
        Map<String, Object> fuzzyMap = new HashMap<>();
        fuzzyMap.put("key_word", keyword);
        try {
            List<Map<String, Object>> mapList = DBUtil.query(Constant.TABLE_NAME, equalMap, fuzzyMap);
            imageList = ObjectConverter.toEntityList(mapList);
        } catch (SQLException e) {
            log.error("exception occurred when sql fuzzy search, message: {}, sql state: {}", e.getMessage(), e.getSQLState());
            throw new RuntimeException();
        }
        return imageList;
    }


    public List<Image> fetchObject(Long ownerId, String keyWord) {
        List<Image> imageList;
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("is_deleted", false);
        if (ObjectUtil.isNotNull(ownerId)) whereMap.put("owner_id", ownerId);
        if (ObjectUtil.isNotEmpty(keyWord)) whereMap.put("key_word", keyWord);
        try {
            List<Map<String, Object>> mapList = DBUtil.query(Constant.TABLE_NAME, whereMap);
            imageList = ObjectConverter.toEntityList(mapList);
        } catch (SQLException e) {
            log.error("exception occurred when sql search, message: {}, sql state: {}", e.getMessage(), e.getSQLState());
            throw new RuntimeException();
        }
        return imageList;
    }

}
