package com.webank.blockchain.data.export.sdk;

import com.webank.blockchain.data.export.api.DataExportService;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/18
 */
public class ExportDataSDK {

    public static DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        return DataExportService.create(dataSource,chainInfo,config);
    }

    public static void start(DataExportExecutor exportExecutor){
        DataExportService.start(exportExecutor);
    }

    public static void stop(DataExportExecutor exportExecutor){
        DataExportService.stop(exportExecutor);
    }


    public static void main(String[] args) throws ConfigException, SQLException, InterruptedException {
        MysqlDataSource mysqlDataSourc = MysqlDataSource.builder()
                .jdbcUrl("jdbc:mysql://106.12.193.68:3306/bee2")
                .pass("qsdk@2040")
                .user("root")
                .build();
        List<MysqlDataSource> mysqlDataSourceList = new ArrayList<>();
        mysqlDataSourceList.add(mysqlDataSourc);
        ExportDataSource dataSource = ExportDataSource.builder()
                .mysqlDataSources(mysqlDataSourceList)
                .build();
        DataExportExecutor exportExecutor = DataExportService.create(dataSource, ChainInfo.builder()
                .nodeStr("106.12.193.68:20200").certPath("config").groupId(1).build(),new ExportConfig());
        start(exportExecutor);
//        Thread.sleep(10000L);
//       stop(exportExecutor);
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
