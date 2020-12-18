package com.webank.blockchain.data.export.common.entity;

import lombok.Data;
import org.fisco.bcos.sdk.client.Client;

import javax.sql.DataSource;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Data
public class DataExportContext {

    private DataSource dataSource;

    private ESDataSource esConfig;

    private ChainInfo chainInfo;

    private ExportConfig config;

    private Client client;


}
