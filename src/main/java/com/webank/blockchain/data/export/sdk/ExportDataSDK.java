package com.webank.blockchain.data.export.sdk;

import cn.hutool.db.DaoTemplate;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.log.level.Level;
import cn.hutool.setting.dialect.Props;
import com.webank.blockchain.data.export.api.DataExportService;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.db.repository.BlockTaskPoolRepository;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/18
 */
public class ExportDataSDK {

    public static Logger logger = LoggerFactory.getLogger(ExportDataSDK.class);

    public static void main(String[] args) throws ConfigException, SQLException, InterruptedException {
        MysqlDataSource mysqlDataSourc = MysqlDataSource.builder()
                .jdbcUrl("jdbc:mysql://106.12.193.68:3306/bee2")
                .pass("qsdk@2040")
                .user("root")
                .autoCreateTable(false)
                .build();
        ExportDataSource dataSource = ExportDataSource.builder()
                .mysqlDataSource(mysqlDataSourc)
                .build();
        dataSource.setMysqlDataSource(mysqlDataSourc);
        DataExportExecutor exportExecutor = DataExportService.create(dataSource, ChainInfo.builder()
                .nodeStr("106.12.193.68:20200").certPath("config").groupId(1).build(),new ExportConfig());
        DataExportService.start(exportExecutor);
//        Thread.sleep(10000L);
//        DataExportService.stop(exportExecutor);
        System.out.println();
    }

//    public static DataSource createDataSource(String jdbcUrl, String driver, String user, String pass) {
//        final Props config = new Props();
//        DbUtil.setShowSqlGlobal(true,true,true, Level.DEBUG);
//
//        config.put("jdbcUrl", jdbcUrl);
//        if (null != driver) {
//            config.put("driverClassName", driver);
//        }
//        if (null != user) {
//            config.put("username", user);
//        }
//        if (null != pass) {
//            config.put("password", pass);
//        }
//
//        return new HikariDataSource(new HikariConfig(config));
//    }
}
