package com.webank.blockchain.data.export.tools;

import cn.hutool.setting.dialect.Props;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/17
 */
public class DataSourceUtils {

    public static DataSource createDataSource(String jdbcUrl, String driver, String user, String pass) {
        final Props config = new Props();
        config.put("jdbcUrl", jdbcUrl);
        if (null != driver) {
            config.put("driverClassName", driver);
        }
        if (null != user) {
            config.put("username", user);
        }
        if (null != pass) {
            config.put("password", pass);
        }

        return new HikariDataSource(new HikariConfig(config));
    }

}
