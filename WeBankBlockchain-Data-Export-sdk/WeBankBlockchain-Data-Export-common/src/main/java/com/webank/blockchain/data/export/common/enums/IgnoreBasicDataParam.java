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
        BLOCK_RAW_DATA_TABLE("block_raw_data"),
        TX_RAW_DATA_TABLE("tx_raw_data"),
        TX_RECEIPT_RAW_DATA_TABLE("tx_receipt_raw_data");

        private String name;


        IgnoreBasicDataTable(String name) { }
    }

    @Getter
    public enum BlockRawDataParams {
        DB_HASH("db_hash"),
        EXTRA_DATA("extra_data"),
        GAS_LIMIT("gas_limit"),
        GAS_USED("gas_used"),
        LOGS_BLOOM("logs_bloom"),
        PARENT_HASH("parent_hash"),
        RECEIPTS_ROOT("receipts_root"),
        SEALER("sealer"),
        SEALER_LIST("sealer_list"),
        SIGNATURE_LIST("signature_list"),
        STATE_ROOT("state_root"),
        TRANSACTION_LIST("transaction_list"),
        TRANSACTIONS_ROOT("transactions_root");

        private String name;

        BlockRawDataParams(String name) { }
    }

    @Getter
    public enum TxReceiptRawDataParams {
        FROM("from"),
        GAS_USED("gasUsed"),
        LOGS("logs"),
        INPUT("input"),
        MESSAGE("message"),
        OUTPUT("output"),
        LOGS_BLOOM("logsBloom"),
        ROOT("root"),
        TO("to"),
        TX_INDEX("txIndex"),
        TX_PROOF("txProof"),
        RECEIPT_PROOF("receiptProof");

        private String name;

        TxReceiptRawDataParams(String name) { }
    }

    @Getter
    public enum TxRawDataParams {
        FROM("from"),
        GAS("gas"),
        GAS_PRICE("gasPrice"),
        INPUT("input"),
        NONCE("nonce"),
        VALUE("value"),
        TO("to");

        private String name;

        TxRawDataParams(String name) { }
    }

}
