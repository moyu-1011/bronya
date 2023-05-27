package moe.moyu.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DBConnection {
    private static class ConnectionHolder {
        public static HikariDataSource resource = new HikariDataSource();
        public static final String initSql = "CREATE TABLE if NOT EXISTS `t_image`\n" +
                "(\n" +
                "    `id`          integer PRIMARY KEY AUTOINCREMENT, -- 主键\n" +
                "    `owner_id`    bigint unsigned NOT NULL,         -- 持有者id\n" +
                "    `key_word`    varchar(255) NOT NULL COLLATE NOCASE,            -- 关键词\n" +
                "    `image_id`    varchar(255) NOT NULL,            -- 图片id\n" +
                "    `image_url`   varchar(255) NOT NULL,            -- 图片url\n" +
                "    'is_deleted'  tinyint      NOT NULL DEFAULT 0,            -- 删除标识\n" +
                "    'add_time'    datetime     NOT NULL,            -- 添加日期\n" +
                "    'delete_time' datetime                         -- 删除日期\n" +
                ");";

        static {
            resource.setJdbcUrl("jdbc:sqlite:sqlite/db/bronya.db");
            resource.setUsername("bronya");
            resource.setPassword("bronya");
            resource.setDriverClassName("org.sqlite.JDBC");
            resource.setMaximumPoolSize(10);
            resource.addDataSourceProperty("cachePrepStmts", "true");
            resource.addDataSourceProperty("prepStmtCacheSize", "250");
            resource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            resource.setAutoCommit(true);
            resource.setConnectionInitSql(initSql);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ConnectionHolder.resource.getConnection();
    }

}
