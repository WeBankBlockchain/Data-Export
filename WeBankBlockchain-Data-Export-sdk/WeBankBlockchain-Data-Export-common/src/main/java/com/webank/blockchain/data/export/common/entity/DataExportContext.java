package com.webank.blockchain.data.export.common.entity;

import com.webank.blockchain.data.export.common.client.ChainClient;
import com.webank.blockchain.data.export.common.subscribe.TopicRegistry;
import lombok.Data;
import org.elasticsearch.client.transport.TransportClient;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderInterface;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Data
@SuppressWarnings("deprecation")
public class DataExportContext {

    private DataSource dataSource;

    private ExportDataSource exportDataSource;

    private TransportClient esClient;

    private ESDataSource esConfig;

    private ChainInfo chainInfo;

    private StashInfo stashInfo;

    private DataSource stashDataSource;

    private ExportConfig config;

    private ChainClient client;

    private boolean autoCreateTable;

    private TransactionDecoderInterface decoder;

    private Map<String, ContractInfo> contractInfoMap;

    private TopicRegistry topicRegistry;

    public String sqlScript = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n",
            TableSQL.BLOCK_DETAIL_INFO, TableSQL.BLOCK_RAW_DATA, TableSQL.BLOCK_TASK_POOL,
            TableSQL.BLOCK_TX_DETAIL_INFO, TableSQL.DEPLOYED_ACCOUNT_INFO,
            TableSQL.TX_RECEIPT_RAW_DATA, TableSQL.TX_RAW_DATA, TableSQL.CONTRACT_INFO);

}
