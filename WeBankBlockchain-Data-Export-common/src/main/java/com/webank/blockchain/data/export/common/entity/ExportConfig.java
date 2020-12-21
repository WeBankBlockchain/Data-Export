package com.webank.blockchain.data.export.common.entity;

import lombok.Data;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
@Data
public class ExportConfig {

    private int crawlBatchUnit = 1000;
    private long frequency = 5;
    private boolean multiLiving;

    private long startBlockHeight = 0;
    private String startDate;

    //    private String zookeeperServiceLists;
//    private String zookeeperNamespace;
//    private String elasticJobName;
//    private String elasticJobcron;
//    private int elasticJobshardingTotalCount;
}
