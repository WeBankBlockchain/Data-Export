package com.webank.blockchain.data.export.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wesleywang
 * @Description:
 * @date 2021/6/23
 */
@AllArgsConstructor
@Getter
public class IgnoreBasicDataParam {

    @Getter
    public enum IgnoreBasicDataTable {
        BLOCK_RAW_DATA_TABLE,
        TX_RAW_DATA_TABLE,
        TX_RECEIPT_RAW_DATA_TABLE;

        IgnoreBasicDataTable() { }
    }

    @Getter
    public enum BlockRawDataParams {
        DB_HASH,
        EXTRA_DATA,
        GAS_LIMIT,
        GAS_USED,
        LOGS_BLOOM,
        PARENT_HASH,
        RECEIPTS_ROOT,
        SEALER,
        SEALER_LIST,
        SIGNATURE_LIST,
        STATE_ROOT,
        TRANSACTION_LIST,
        TRANSACTIONS_ROOT;

        BlockRawDataParams() { }
    }

    @Getter
    public enum TxReceiptRawDataParams {
        FROM,
        GAS_USED,
        LOGS,
        INPUT,
        MESSAGE,
        OUTPUT,
        LOGS_BLOOM,
        ROOT,
        TO,
        TX_INDEX,
        TX_PROOF,
        RECEIPT_PROOF;

        TxReceiptRawDataParams() { }
    }

    @Getter
    public enum TxRawDataParams {
        FROM,
        GAS,
        GAS_PRICE,
        INPUT,
        NONCE,
        VALUE,
        TO;


        TxRawDataParams() { }
    }

}
