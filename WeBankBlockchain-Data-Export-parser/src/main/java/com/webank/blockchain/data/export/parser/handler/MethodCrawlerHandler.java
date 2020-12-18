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

import com.webank.blockchain.data.export.common.bo.contract.MethodMetaInfo;
import com.webank.blockchain.data.export.common.bo.data.BlockMethodInfo;
import com.webank.blockchain.data.export.common.bo.data.BlockTxDetailInfoBO;
import com.webank.blockchain.data.export.common.bo.data.MethodBO;
import com.webank.blockchain.data.export.common.bo.data.TxRawDataBO;
import com.webank.blockchain.data.export.common.bo.data.TxReceiptRawDataBO;
import com.webank.blockchain.data.export.common.entity.ExportThreadLocal;
import com.webank.blockchain.data.export.common.tools.DateUtils;
import com.webank.blockchain.data.export.common.tools.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.Block;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.TransactionObject;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.TransactionResult;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.Numeric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MethodCrawlerHandler
 *
 * @Description: MethodCrawlerHandler
 * @author maojiayu
 * @data Jul 3, 2019 10:17:15 AM
 *
 */
@Slf4j
public class MethodCrawlerHandler {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static BlockMethodInfo crawl(Block block, Map<String, String> txHashContractAddressMapping) throws IOException {
        BlockMethodInfo blockMethodInfo = new BlockMethodInfo();
        List<BlockTxDetailInfoBO> blockTxDetailInfoList = new ArrayList<>();
        List<TxRawDataBO> txRawDataBOList = new ArrayList<>();
        List<TxReceiptRawDataBO> txReceiptRawDataBOList = new ArrayList<>();
        List<MethodBO> methodInfoList = new ArrayList();
        List<TransactionResult> transactionResults = block.getTransactions();
        Map<String, String> txHashContractNameMapping = new HashMap<>();
        for (TransactionResult result : transactionResults) {
            TransactionObject to = (TransactionObject) result;
            JsonTransactionResponse transaction = to.get();
            Optional<TransactionReceipt> opt =
                    ExportThreadLocal.threadLocal.get().getClient().getTransactionReceipt(transaction.getHash()).getTransactionReceipt();
            if (opt.isPresent()) {
                TransactionReceipt receipt = opt.get();
//                MethodMetaInfo methodMetaInfo = TransactionService.getMethodMetaInfo(transaction, null);
//                if (methodMetaInfo == null) {
//                    continue;
//                }
                // get block tx detail info
                BlockTxDetailInfoBO blockTxDetailInfo =
                        getBlockTxDetailInfo(block, transaction, receipt, null);
                TxRawDataBO txRawDataBO = getTxRawDataBO(block, transaction, receipt);
                TxReceiptRawDataBO txReceiptRawDataBO = getTxReceiptRawDataBO(block,receipt);
                blockTxDetailInfoList.add(blockTxDetailInfo);
                txRawDataBOList.add(txRawDataBO);
                txReceiptRawDataBOList.add(txReceiptRawDataBO);
                txHashContractNameMapping.putIfAbsent(blockTxDetailInfo.getTxHash(),
                        blockTxDetailInfo.getContractName());
            }
        }
        blockMethodInfo.setBlockTxDetailInfoList(blockTxDetailInfoList)
                .setMethodInfoList(methodInfoList)
                .setTxHashContractNameMapping(txHashContractNameMapping)
                .setTxRawDataBOList(txRawDataBOList)
                .setTxReceiptRawDataBOList(txReceiptRawDataBOList);
        return blockMethodInfo;

    }

    public static BlockTxDetailInfoBO getBlockTxDetailInfo(Block block, JsonTransactionResponse transaction,
            TransactionReceipt receipt, MethodMetaInfo methodMetaInfo) {
        BlockTxDetailInfoBO blockTxDetailInfo = new BlockTxDetailInfoBO();
        blockTxDetailInfo.setBlockHash(receipt.getBlockHash()).setBlockHeight(receipt.getBlockNumber())
//                .setContractName(methodMetaInfo.getContractName())
//                .setMethodName(methodMetaInfo.getMethodName())
                .setTxFrom(transaction.getFrom()).setTxTo(transaction.getTo()).setTxHash(receipt.getTransactionHash())
                .setBlockTimeStamp(DateUtils.hexStrToDate(block.getTimestamp()));
        return blockTxDetailInfo;
    }

    public static TxRawDataBO getTxRawDataBO(Block block, JsonTransactionResponse transaction, TransactionReceipt receipt) {
        TxRawDataBO txRawDataBO = new TxRawDataBO();
        txRawDataBO.setBlockHash(receipt.getBlockHash())
                .setBlockHeight(Numeric.decodeQuantity((receipt.getBlockNumber())).longValue())
                .setBlockTimeStamp(DateUtils.hexStrToDate(block.getTimestamp()))
                .setTxHash(receipt.getTransactionHash())
                .setTxIndex(transaction.getTransactionIndex())
                .setFrom(transaction.getFrom())
                .setGas(transaction.getGas())
                .setGasPrice(transaction.getGasPrice())
                .setInput(transaction.getInput())
                .setNonce(transaction.getNonce())
                .setTo(transaction.getTo())
                .setValue(transaction.getValue());
        return txRawDataBO;
    }

    public static TxReceiptRawDataBO getTxReceiptRawDataBO(Block block, TransactionReceipt receipt) {
        TxReceiptRawDataBO txReceiptRawDataBO = new TxReceiptRawDataBO();
        txReceiptRawDataBO.setBlockHash(receipt.getBlockHash())
                .setBlockHeight(Numeric.decodeQuantity((receipt.getBlockNumber())).longValue())
                .setBlockTimeStamp(DateUtils.hexStrToDate(block.getTimestamp()))
                .setTxHash(receipt.getTransactionHash())
                .setContractAddress(receipt.getContractAddress())
                .setFrom(receipt.getFrom())
                .setGasUsed(receipt.getGasUsed())
                .setInput(receipt.getInput())
                .setLogs(receipt.getLogsBloom())
                .setMessage(receipt.getMessage())
                .setOutput(receipt.getOutput())
                .setLogsBloom(JacksonUtils.toJson(receipt.getLogsBloom()))
                .setRoot(receipt.getRoot())
                .setTo(receipt.getTo())
                .setTxIndex(receipt.getTransactionIndex())
                .setTxProof(JacksonUtils.toJson(receipt.getTxProof()))
                .setReceiptProof(JacksonUtils.toJson(receipt.getReceiptProof()));
        return txReceiptRawDataBO;
    }

}
