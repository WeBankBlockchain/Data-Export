package com.webank.blockchain.data.export.common.entity;

import com.webank.blockchain.data.export.common.entity.ChainInfo;
import com.webank.blockchain.data.export.common.entity.ESDataSource;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import lombok.Data;
import org.elasticsearch.client.transport.TransportClient;
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

    private TransportClient esClient;

    private ESDataSource esConfig;

    private ChainInfo chainInfo;

    private ExportConfig config;

    private Client client;

    private boolean autoCreateTable;


}
