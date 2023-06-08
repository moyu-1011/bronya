package moe.moyu.cache;

import net.mamoe.mirai.contact.User;

/**
 * 当前发送命令的用户信息
 */
public class CurrentUser {
    private static User user = null;

    public static User get() {
        return user;
    }

    public static void set(User user) {
        CurrentUser.user = user;
    }

    public static void reset() {
        user = null;
    }
}
