package com.webank.blockchain.data.export.api;

import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.DataExportContext;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import com.webank.blockchain.data.export.tools.ClientUtil;
import com.webank.blockchain.data.export.tools.DataSourceUtils;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

import javax.sql.DataSource;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
public class DataExportService {


    public static DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        return new DataExportExecutor(buildContext(dataSource,chainInfo,config));
    }

    public static void start(DataExportExecutor exportExecutor){
        exportExecutor.start();
    }

    public static void stop(DataExportExecutor exportExecutor){
        exportExecutor.stop();
    }

    private static DataExportContext buildContext(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        DataExportContext context = new DataExportContext();
        context.setClient(ClientUtil.getClient(chainInfo));
        context.setChainInfo(chainInfo);
        context.setConfig(config);
        context.setDataSource(buildDataSource(dataSource.getMysqlDataSource()));
        context.setEsConfig(dataSource.getEsDataSource());
        context.setAutoCreateTable(dataSource.getMysqlDataSource().isAutoCreateTable());
        return context;
    }

    private static DataSource buildDataSource(MysqlDataSource mysqlDataSource){
        return DataSourceUtils.createDataSource(mysqlDataSource.getJdbcUrl(),
                null,
                mysqlDataSource.getUser(),
                mysqlDataSource.getPass());
    }



}
