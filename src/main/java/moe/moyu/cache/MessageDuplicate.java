package moe.moyu.cache;

import lombok.extern.slf4j.Slf4j;
import moe.moyu.entity.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息副本
 * <p> 将bot发送的内容复制创建一份副本, 每次发送都会生成副本并覆盖
 * <p> 副本和bot最近发送的内容相同, 用户根据bot发送的内容, 发送想要删除数据的下标, 于是从副本根据下标取主键, 删除这条数据
 */
@Slf4j
public class MessageDuplicate {
    private static List<Image> duplicate = new ArrayList<>();

    private static int deleteIndex = -1;

    private static int defaultIndex = -1;

    public static void copy(List<Image> imageList) {
        duplicate = new ArrayList<>(imageList);
    }

    public static Long getMessageId() {
        return duplicate.get(deleteIndex).getId();
    }

    public static Long getMessageId(int index) {
        if (validIndex(index) && !duplicate.isEmpty()) {
            return duplicate.get(index).getId();
        }
        return null;
    }

    public static int getDeleteIndex() {
        return deleteIndex;
    }

    /**
     * 设置要删除元素的索引 成功返回true 失败返回false
     * @param deleteIndex 索引,从1开始
     * @return 设置索引是否成功
     */
    public static boolean setDeleteIndex(int deleteIndex) {
        -- deleteIndex;
        if (validIndex(deleteIndex) && !duplicate.isEmpty()) {
            MessageDuplicate.deleteIndex = deleteIndex;
            return true;
        }
        return false;
    }

    public static boolean validIndex(int deleteIndex) {
        return deleteIndex < duplicate.size() && deleteIndex > -1;
    }

    public static void resetIndex() {
        deleteIndex = defaultIndex;
    }
}
