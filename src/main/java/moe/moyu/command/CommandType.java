package moe.moyu.command;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import moe.moyu.constant.Constant;

/**
 * 命令枚举
 */
@Getter
public enum CommandType {

    FETCH_ALL(Constant.SEARCH_ALL_SIGN),
    FETCH_BY_KEYWORD_EXACT(Constant.SEARCH_EXACT_SIGN),
    FETCH_BY_KEYWORD_FUZZY(Constant.SEARCH_FUZZY_SIGN),
    FETCH_CACHE(Constant.FETCH_CACHE_SIGN),
    CLEAR_CACHE(Constant.CLEAR_CACHE_SIGN),
    SAVE(Constant.SAVE_SIGN),
    DELETE_BY_KEYWORD(Constant.DELETE_KEYWORD_SIGN),
    DELETE_BY_INDEX(Constant.DELETE_INDEX_SIGN);
    private final String sign;

    CommandType(String sign) {
        this.sign = sign;
    }

    /**
     * 获取文本的指令类型
     *
     * @param text 文本消息
     * @return 指令类型 {@link CommandType}
     */
    public static CommandType type(String text) {
        String keyword = Command.extractKeyword(text, CommandType.FETCH_ALL);
        boolean isKeyOfSearchAll = ObjectUtil.equal(keyword, Constant.FETCH_ALL_KEY);
        boolean isSearchCache = ObjectUtil.equal(keyword, Constant.FETCH_CACHE);
        boolean isClearCache = ObjectUtil.equal(keyword, Constant.CLEAR_CACHE);
        CommandType commandType = null;

        // 全部查询
        if (text.startsWith(Constant.SEARCH_ALL_SIGN) && isKeyOfSearchAll) commandType = CommandType.FETCH_ALL;
        // 查询缓存
        else if (text.startsWith(Constant.FETCH_CACHE_SIGN) && isSearchCache) commandType = CommandType.FETCH_CACHE;
        // 删除缓存
        else if (text.startsWith(Constant.CLEAR_CACHE_SIGN) && isClearCache) commandType = CommandType.CLEAR_CACHE;
        // 模糊查询
        else if (text.startsWith(Constant.SEARCH_FUZZY_SIGN) && lenGtOne(text)) commandType = CommandType.FETCH_BY_KEYWORD_FUZZY;
        // 匹配查询
        else if (text.startsWith(Constant.SEARCH_EXACT_SIGN) && lenGtOne(text)) commandType = CommandType.FETCH_BY_KEYWORD_EXACT;
        // 保存
        else if (text.startsWith(Constant.SAVE_SIGN) && lenGtOne(text)) commandType = CommandType.SAVE;
        // 关键字删除
        else if (text.startsWith(Constant.DELETE_KEYWORD_SIGN)) commandType = CommandType.DELETE_BY_KEYWORD;
        // 索引删除
        else if (text.startsWith(Constant.DELETE_INDEX_SIGN)) commandType = CommandType.DELETE_BY_INDEX;

        return commandType;
    }

    private static boolean lenGtOne(String str) {
        return str.length() > 1;
    }
}
