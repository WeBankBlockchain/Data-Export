package com.webank.blockchain.data.export.common.entity;

import com.webank.blockchain.data.export.common.enums.DataType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Data
public class ExportConfig {

    private int crawlBatchUnit = 1000;
    private long frequency = 5;

    private long startBlockHeight = 0;
    private String startDate;

    private List<DataType> dataTypeBlackList = DataType.getDefault();

    private Map<String, ContractInfo> ContractInfoMap;

    private boolean multiLiving;
    private String zookeeperServiceLists;
    private String zookeeperNamespace;
    private String prepareTaskJobCron = "0/"+ frequency + " * * * * ?";
    private String dataFlowJobCron = "0/"+ frequency + " * * * * ?";
    private String dataFlowJobItemParameters = "0=A,1=B,2=C,3=D,4=E,5=F,6=G,7=H";
    private int dataFlowJobShardingTotalCount = 8;
}
