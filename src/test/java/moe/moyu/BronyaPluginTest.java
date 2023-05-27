package moe.moyu;

import moe.moyu.constant.Constant;
import moe.moyu.db.DBUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BronyaPluginTest {

    @Test
    public void test_insert() throws Exception {
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("owner_id", "1010101010");
        insertMap.put("key_word", "dingzhen");
        insertMap.put("image_url", "dingzhen-url");
        insertMap.put("image_id", "dingzhen-id");
        insertMap.put("is_deleted", "0");
        insertMap.put("add_time", LocalDateTime.now());
        DBUtil.insert(Constant.TABLE_NAME, insertMap);

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("owner_id", "1010101010");
        queryMap.put("key_word", "dingzhen");
        queryMap.put("is_deleted", "0");
        List<Map<String, Object>> list = DBUtil.query(Constant.TABLE_NAME, queryMap);
        Map<String, Object> resultMap = list.get(0);

        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("dingzhen-id", resultMap.get("image_id"));
        Assertions.assertEquals("dingzhen-url", resultMap.get("image_url"));

        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("owner_id", "1010101010");
        whereMap.put("key_word", "dingzhen");
        whereMap.put("is_deleted", "0");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("is_deleted", "1");
        DBUtil.update(Constant.TABLE_NAME, valueMap, whereMap);
    }

    @Test
    public void test_update() throws Exception {
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("owner_id", "33665544");
        insertMap.put("key_word", "lily");
        insertMap.put("image_url", "lily-url");
        insertMap.put("image_id", "lily-id");
        insertMap.put("is_deleted", "0");
        insertMap.put("add_time", LocalDateTime.now());
        DBUtil.insert(Constant.TABLE_NAME, insertMap);

        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("owner_id", "33665544");
        whereMap.put("key_word", "lily");
        whereMap.put("is_deleted", "0");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("key_word", "lilies");
        valueMap.put("image_id", "lilies-id");
        DBUtil.update(Constant.TABLE_NAME, valueMap, whereMap);

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("owner_id", "33665544");
        queryMap.put("key_word", "lilies");
        queryMap.put("is_deleted", "0");
        List<Map<String, Object>> list = DBUtil.query(Constant.TABLE_NAME, queryMap);
        Map<String, Object> resultMap = list.get(0);

        Assertions.assertEquals("lilies-id", resultMap.get("image_id"));

        Map<String, Object> whereMapOfUpdate = new HashMap<>();
        whereMapOfUpdate.put("owner_id", "33665544");
        whereMapOfUpdate.put("key_word", "lily");
        whereMapOfUpdate.put("is_deleted", "0");
        Map<String, Object> valueMapOfUpdate = new HashMap<>();
        valueMapOfUpdate.put("is_deleted", "1");
        DBUtil.update(Constant.TABLE_NAME, valueMapOfUpdate, whereMapOfUpdate);
    }

    @Test
    public void test_query() throws Exception {
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("owner_id", "6633");
        insertMap.put("key_word", "julian");
        insertMap.put("image_url", "julian-url");
        insertMap.put("image_id", "julian-id");
        insertMap.put("is_deleted", "0");
        insertMap.put("add_time", LocalDateTime.now());
        DBUtil.insert(Constant.TABLE_NAME, insertMap);

        Map<String, Object> map = new HashMap<>();
        map.put("owner_id", "6633");
        map.put("key_word", "julian");
        map.put("is_deleted", "0");
        List<Map<String, Object>> list = DBUtil.query(Constant.TABLE_NAME, map);
        Map<String, Object> resultMap = list.get(0);

        Assertions.assertEquals("julian-url", resultMap.get("image_url"));
        Assertions.assertEquals("julian-id", resultMap.get("image_id"));

        Map<String, Object> whereMapOfUpdate = new HashMap<>();
        whereMapOfUpdate.put("owner_id", "6633");
        whereMapOfUpdate.put("key_word", "julian");
        whereMapOfUpdate.put("is_deleted", "0");
        Map<String, Object> valueMapOfUpdate = new HashMap<>();
        valueMapOfUpdate.put("is_deleted", "1");
        DBUtil.update(Constant.TABLE_NAME, valueMapOfUpdate, whereMapOfUpdate);
    }

    @Test
    void test_fussy_query() throws Exception {
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("owner_id", "2266325");
        insertMap.put("key_word", "ryux");
        insertMap.put("image_url", "ryu-url");
        insertMap.put("image_id", "ryu-id");
        insertMap.put("is_deleted", "0");
        insertMap.put("add_time", LocalDateTime.now());
        DBUtil.insert(Constant.TABLE_NAME, insertMap);

        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("owner_id", "2266325");
        whereMap.put("is_deleted", "0");
        Map<String, Object> likeMap = new HashMap<>();
        likeMap.put("key_word", "ryu");

        List<Map<String, Object>> mapList = DBUtil.query(Constant.TABLE_NAME, whereMap, likeMap);

        Assertions.assertEquals(1, mapList.size());
        Assertions.assertEquals("ryu-id", mapList.get(0).get("image_id"));

        Map<String, Object> whereMapOfUpdate = new HashMap<>();
        whereMapOfUpdate.put("owner_id", "2266325");
        whereMapOfUpdate.put("key_word", "ryux");
        whereMapOfUpdate.put("is_deleted", "0");
        Map<String, Object> valueMapOfUpdate = new HashMap<>();
        valueMapOfUpdate.put("is_deleted", "1");
        DBUtil.update(Constant.TABLE_NAME, valueMapOfUpdate, whereMapOfUpdate);
    }


    @Test
    public void test_delete() throws Exception {
        Map<String, Object> insertMap = new HashMap<>();
        insertMap.put("owner_id", "111000111");
        insertMap.put("key_word", "百合");
        insertMap.put("image_url", "baihe-url");
        insertMap.put("image_id", "baihe-id");
        insertMap.put("is_deleted", "0");
        insertMap.put("add_time", LocalDateTime.now());
        DBUtil.insert(Constant.TABLE_NAME, insertMap);

        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("owner_id", "111000111");
        whereMap.put("key_word", "百合");
        whereMap.put("is_deleted", "0");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("is_deleted", "1");
        DBUtil.update(Constant.TABLE_NAME, valueMap, whereMap);

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("owner_id", "111000111");
        queryMap.put("key_word", "百合");
        List<Map<String, Object>> list = DBUtil.query(Constant.TABLE_NAME, queryMap);
        Map<String, Object> resultMap = list.get(0);

        Assertions.assertEquals(1, resultMap.get("is_deleted"));
    }
}
