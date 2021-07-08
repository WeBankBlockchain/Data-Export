/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.export.parser.handler;

import com.webank.blockchain.data.export.common.bo.data.BlockDetailInfoBO;
import com.webank.blockchain.data.export.common.bo.data.BlockDetailInfoBO.Status;
import com.webank.blockchain.data.export.common.bo.data.BlockRawDataBO;
import com.webank.blockchain.data.export.common.entity.ExportConfig;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import com.webank.blockchain.data.export.common.enums.IgnoreBasicDataParam;
import com.webank.blockchain.data.export.common.tools.DateUtils;
import com.webank.blockchain.data.export.common.tools.JacksonUtils;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;

import java.util.List;
import java.util.Map;

/**
 * BlockCrawlerHandler is responsible for crawling block info.
 *
 * @Description: BlockCrawlerHandler
 * @author graysonzhang
 * @author maojiayu
 * @data 2018-12-20 14:32:58
 *
 */
public class BlockCrawlerHandler {

    /**
     * Get block detail info form block object, and insert into db. Block detail info contains block height,
     * transaction's count in current block, block hash and block timestamp.
     * 
     * @param block
     * @return boolean
     */
    public static BlockDetailInfoBO handleBlockDetail(Block block) {
        BlockDetailInfoBO blockDetailInfo = new BlockDetailInfoBO();
        blockDetailInfo.setBlockHeight(block.getNumber().longValue());
        blockDetailInfo.setTxCount(block.getTransactions().size());
        blockDetailInfo.setBlockHash(block.getHash());
        blockDetailInfo.setBlockTimeStamp(DateUtils.hexStrToDate(block.getTimestamp()));
        blockDetailInfo.setStatus((short) Status.COMPLETED.ordinal());
        return blockDetailInfo;
    }

    public static BlockRawDataBO handleBlockRawData(Block block) {
        ExportConfig config = ExportConstant.getCurrentContext().getConfig();
        Map<String, List<String>> ignoreBasicDataTableParam = config.getIgnoreBasicDataTableParam();
        BlockRawDataBO blockRawDataBO = new BlockRawDataBO();
        blockRawDataBO.setBlockHeight(block.getNumber().longValue());
        blockRawDataBO.setBlockHash(block.getHash());
        blockRawDataBO.setBlockTimeStamp(DateUtils.hexStrToDate(block.getTimestamp()));
        if (!ignoreBasicDataTableParam.containsKey(IgnoreBasicDataParam.IgnoreBasicDataTable.BLOCK_RAW_DATA_TABLE.name())) {
            blockRawDataBO.setDbHash(block.getDbHash());
            blockRawDataBO.setExtraData(JacksonUtils.toJson(block.getExtraData()));
            blockRawDataBO.setGasLimit(block.getGasLimit());
            blockRawDataBO.setGasUsed(block.getGasUsed());
            blockRawDataBO.setLogsBloom(block.getLogsBloom());
            blockRawDataBO.setParentHash(block.getParentHash());
            blockRawDataBO.setReceiptsRoot(block.getReceiptsRoot());
            blockRawDataBO.setSealer(block.getSealer());
            blockRawDataBO.setSealerList(JacksonUtils.toJson(block.getSealerList()));
            blockRawDataBO.setSignatureList(JacksonUtils.toJson(block.getSignatureList()));
            blockRawDataBO.setStateRoot(block.getStateRoot());
            blockRawDataBO.setTransactionsRoot(block.getTransactionsRoot());
            blockRawDataBO.setTransactionList(JacksonUtils.toJson(block.getTransactions()));
        }else {
            List<String> params = ignoreBasicDataTableParam.get(IgnoreBasicDataParam.IgnoreBasicDataTable.BLOCK_RAW_DATA_TABLE.name());
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.DB_HASH.name())) {
                blockRawDataBO.setDbHash(block.getDbHash());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.EXTRA_DATA.name())) {
                blockRawDataBO.setExtraData(JacksonUtils.toJson(block.getExtraData()));
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.GAS_LIMIT.name())) {
                blockRawDataBO.setGasLimit(block.getGasLimit());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.GAS_USED.name())) {
                blockRawDataBO.setGasUsed(block.getGasUsed());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.LOGS_BLOOM.name())) {
                blockRawDataBO.setLogsBloom(block.getLogsBloom());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.PARENT_HASH.name())) {
                blockRawDataBO.setParentHash(block.getParentHash());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.RECEIPTS_ROOT.name())) {
                blockRawDataBO.setReceiptsRoot(block.getReceiptsRoot());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.SEALER.name())) {
                blockRawDataBO.setSealer(block.getSealer());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.SEALER_LIST.name())) {
                blockRawDataBO.setSealerList(JacksonUtils.toJson(block.getSealerList()));
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.SEALER_LIST.name())) {
                blockRawDataBO.setSignatureList(JacksonUtils.toJson(block.getSignatureList()));
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.STATE_ROOT.name())) {
                blockRawDataBO.setStateRoot(block.getStateRoot());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.TRANSACTIONS_ROOT.name())) {
                blockRawDataBO.setTransactionsRoot(block.getTransactionsRoot());
            }
            if (!params.contains(IgnoreBasicDataParam.BlockRawDataParams.TRANSACTION_LIST.name())) {
                blockRawDataBO.setTransactionList(JacksonUtils.toJson(block.getTransactions()));
            }
        }
        return blockRawDataBO;
    }
}
