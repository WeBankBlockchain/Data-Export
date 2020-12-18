package com.webank.blockchain.data.export.common.entity;


import cn.hutool.db.DaoTemplate;
import cn.hutool.db.DbUtil;
import cn.hutool.log.level.Level;

import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/16
 */
public class ExportConstant {

    static {
        DbUtil.setShowSqlGlobal(true,false,true, Level.INFO);
    }

    public static final ThreadLocal<DataExportContext> threadLocal = new ThreadLocal<>();

    public static final ThreadLocal<Map<String, DaoTemplate>> daoThreadLocal = new ThreadLocal<>();

    public static final String BLOCK_TASK_POOL_DAO = "blockTaskPoolDao";

    public static final String BLOCK_DETAIL_DAO = "blockDetailInfoDao";

    public static final String BLOCK_RAW_DAO = "blockRawDataDao";

    public static final String BLOCK_TX_DETAIL_DAO = "blockTxDetailInfoDao";

    public static final String TX_RAW_DAO = "txRawDataDao";

    public static final String TX_RECEIPT_RAW_DAO = "txReceiptRawDataDao";

    public static final String DEPLOYED_ACCOUNT_DAO = "deployedAccountInfoDao";

    public static final String BLOCK_DETAIL_INFO_TABLE = "block_detail_info";

    public static final String BLOCK_TASK_POOL_TABLE = "block_task_pool";

    public static final String BLOCK_RAW_DATA_TABLE = "block_raw_data";

    public static final String BLOCK_TX_DETAIL_INFO_TABLE = "block_tx_detail_info";

    public static final String DEPLOYED_ACCOUNT_INFO_TABLE = "deployed_account_info";

    public static final String TX_RAW_DATA_TABLE = "tx_raw_data";

    public static final String TX_RECEIPT_RAW_DATA_TABLE = "tx_receipt_raw_data";




}
