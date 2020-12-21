package com.webank.blockchain.data.export.sdk;

import com.webank.blockchain.data.export.api.DataExportService;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/18
 */
public class ExportDataSDK {

    public static DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo) throws ConfigException {
        return DataExportService.create(dataSource,chainInfo, new ExportConfig());
    }

    public static DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config) throws ConfigException {
        return DataExportService.create(dataSource,chainInfo,config);
    }

    public static void start(DataExportExecutor exportExecutor){
        DataExportService.start(exportExecutor);
    }

    public static void stop(DataExportExecutor exportExecutor){
        DataExportService.stop(exportExecutor);
    }

}
