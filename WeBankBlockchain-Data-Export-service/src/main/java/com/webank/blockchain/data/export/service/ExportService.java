package com.webank.blockchain.data.export.service;

import com.webank.blockchain.data.export.ExportDataSDK;
import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportDataSource;
import com.webank.blockchain.data.export.common.entity.StashInfo;
import com.webank.blockchain.data.export.config.ServiceConfig;
import com.webank.blockchain.data.export.task.DataExportExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author wesleywang
 * @Description:
 * @date 2021/3/30
 */
@Service
@Slf4j
public class ExportService {

    @Autowired
    private ServiceConfig serviceConfig;

    @PostConstruct
    public void start() {
        ExportDataSource dataSource = ExportDataSource.builder()
                .mysqlDataSources(serviceConfig.getMysqlDataSources())
                .autoCreateTable(serviceConfig.isAutoCreateTable())
                .sharding(serviceConfig.isSharding())
                .shardingNumberPerDatasource(serviceConfig.getShardingNumberPerDatasource())
                .esDataSource(serviceConfig.getEsDataSource())
                .build();
        try {
            if (serviceConfig.getNodeStr() != null) {
                for(String groupId : serviceConfig.getGroupIds()) {
                    ExportConfig config = buildExportConfig();
                    if (serviceConfig.getGroupIds().size() > 1) {
                        config.setTablePrefix("g" + groupId + "_" + config.getTablePrefix());
                    }
                    DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, ChainInfo.builder()
                            .nodeStr(serviceConfig.getNodeStr())
                            .certPath(serviceConfig.getCertPath())
                            .groupId(groupId)
                            .build(), config);
                    ExportDataSDK.start(exportExecutor);
                }
            } else if(serviceConfig.getRpcUrl() != null) {
                for(String groupId : serviceConfig.getGroupIds()) {
                    ExportConfig config = buildExportConfig();
                    if (serviceConfig.getGroupIds().size() > 1) {
                        config.setTablePrefix("g" + groupId + "_" + config.getTablePrefix());
                    }
                    DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, ChainInfo.builder()
                            .rpcUrl(serviceConfig.getRpcUrl())
                            .cryptoTypeConfig(serviceConfig.getCryptoTypeConfig())
                            .groupId(groupId)
                            .build(), config);
                    ExportDataSDK.start(exportExecutor);
                }
            } else if(serviceConfig.getJdbcUrl() != null) {
                ExportConfig config = buildExportConfig();
                DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, StashInfo.builder()
                        .jdbcUrl(serviceConfig.getJdbcUrl())
                        .cryptoTypeConfig(serviceConfig.getCryptoTypeConfig())
                        .pass(serviceConfig.getPassword())
                        .user(serviceConfig.getUser())
                        .build(), config);
                ExportDataSDK.start(exportExecutor);
            }

        } catch (Exception e) {
            log.error("ExportDataSDK.start failed",e);
        }
    }

    private ExportConfig buildExportConfig(){
        ExportConfig config = new ExportConfig();
        config.setStartBlockHeight(serviceConfig.getStartBlockHeight());
        config.setContractInfoList(serviceConfig.getContractInfos());
        config.setTablePostfix(serviceConfig.getTablePostfix());
        config.setTablePrefix(serviceConfig.getTablePrefix());
        config.setCrawlBatchUnit(serviceConfig.getCrawlBatchUnit());
        config.setDataFlowJobCron(serviceConfig.getDataFlowJobCron());
        config.setDataFlowJobItemParameters(serviceConfig.getDataFlowJobItemParameters());
        config.setFrequency(serviceConfig.getFrequency());
        config.setDataFlowJobShardingTotalCount(serviceConfig.getDataFlowJobShardingTotalCount());
        config.setZookeeperServiceLists(serviceConfig.getZookeeperServiceLists());
        config.setStartDate(serviceConfig.getStartDate());
        config.setParamSQLType(serviceConfig.getParamSQLType_SDK());
        config.setNamePostfix(serviceConfig.getNamePostfix());
        config.setNamePrefix(serviceConfig.getNamePrefix());
        config.setPrepareTaskJobCron(serviceConfig.getPrepareTaskJobCron());
        config.setGeneratedOff(serviceConfig.getGeneratedOff_SDK());
        config.setIgnoreParam(serviceConfig.getIgnoreParam_SDK());
        config.setMultiLiving(serviceConfig.isMultiLiving());
        config.setDataTypeBlackList(serviceConfig.getDataTypeBlackList());
        config.setIgnoreBasicDataTableParam(serviceConfig.getIgnoreBasicDataTableParam());
        return config;
    }

}
