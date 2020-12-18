package com.webank.blockchain.data.export.common.entity;

import lombok.Data;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/17
 */
@Data
public class MysqlDataSource {

    private String jdbcUrl;
    private String driver;
    private String user;
    private String pass;
    private String showSql = "true";
    private String formatSql = "false";
    private String showParams = "true";
    private String sqlLevel = "info";
}
