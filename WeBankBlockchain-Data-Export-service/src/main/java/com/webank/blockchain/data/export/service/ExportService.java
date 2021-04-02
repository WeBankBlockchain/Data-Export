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
        //导出数据源配置
        ExportDataSource dataSource = ExportDataSource.builder()
                //设置mysql源
                .mysqlDataSources(serviceConfig.getMysqlDataSources())
                //自动建表开启
                .autoCreateTable(serviceConfig.isAutoCreateTable())
                .sharding(serviceConfig.isSharding())
                .shardingNumberPerDatasource(serviceConfig.getShardingNumberPerDatasource())
                .esDataSource(serviceConfig.getEsDataSource())
                .build();
        try {
            if (serviceConfig.getNodeStr() != null) {
                for(Integer groupId : serviceConfig.getGroupIds()) {
                    ExportConfig config = buildExportConfig();
                    if (serviceConfig.getGroupIds().size() > 1) {
                        config.setTablePrefix("g" + groupId + "_" + config.getTablePrefix());
                    }
                    DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, ChainInfo.builder()
                            //链节点连接信息
                            .nodeStr(serviceConfig.getNodeStr())
                            //链连接证书位置
                            .certPath(serviceConfig.getCertPath())
                            //群组id
                            .groupId(groupId)
                            .build(), config);
                    //数据导出执行启动
                    ExportDataSDK.start(exportExecutor);
                }
            } else if(serviceConfig.getRpcUrl() != null) {
                for(Integer groupId : serviceConfig.getGroupIds()) {
                    ExportConfig config = buildExportConfig();
                    if (serviceConfig.getGroupIds().size() > 1) {
                        config.setTablePrefix("g" + groupId + "_" + config.getTablePrefix());
                    }
                    DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, ChainInfo.builder()
                            .rpcUrl(serviceConfig.getRpcUrl())
                            .cryptoTypeConfig(serviceConfig.getCryptoTypeConfig())
                            //群组id
                            .groupId(groupId)
                            .build(), config);
                    //数据导出执行启动
                    ExportDataSDK.start(exportExecutor);
                }
            } else if(serviceConfig.getJdbcUrl() != null) {
                ExportConfig config = buildExportConfig();
                DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, StashInfo.builder()
                        .jdbcUrl(serviceConfig.getJdbcUrl())
                        .cryptoTypeConfig(serviceConfig.getCryptoTypeConfig())
                        //群组id
                        .pass(serviceConfig.getPassword())
                        .user(serviceConfig.getUser())
                        .build(), config);
                //数据导出执行启动
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
        config.setParamSQLType(serviceConfig.getParamSQLType());
        config.setNamePostfix(serviceConfig.getNamePostfix());
        config.setNamePrefix(serviceConfig.getNamePrefix());
        config.setPrepareTaskJobCron(serviceConfig.getPrepareTaskJobCron());
        config.setGeneratedOff(serviceConfig.getGeneratedOff());
        config.setIgnoreParam(serviceConfig.getIgnoreParam());
        config.setMultiLiving(serviceConfig.isMultiLiving());
        return config;
    }

}
