package com.webank.blockchain.data.export.parser.contract;

import com.webank.blockchain.data.export.common.bo.contract.ContractDetail;
import com.webank.blockchain.data.export.common.bo.contract.ContractMapsInfo;
import com.webank.blockchain.data.export.common.bo.contract.MethodMetaInfo;
import com.webank.blockchain.data.export.common.bo.data.ContractInfoBO;
import com.webank.blockchain.data.export.common.constants.ContractConstants;
import com.webank.blockchain.data.export.common.entity.ContractInfo;
import com.webank.blockchain.data.export.common.entity.ExportConstant;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.crypto.hash.Keccak256;
import org.fisco.bcos.sdk.transaction.codec.decode.TransactionDecoderService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/25
 */
@Slf4j
public class ContractParser {

    public static void initContractMaps(){
        ContractMapsInfo contractMapsInfo = new ContractMapsInfo();
        ContractConstants.contractMapsInfo.set(contractMapsInfo);
        Map<String, MethodMetaInfo> methodIdMap = new HashMap<>();
        Map<String, ContractDetail> contractBinaryMap = new HashMap<>();
        contractMapsInfo.setContractBinaryMap(contractBinaryMap);
        contractMapsInfo.setMethodIdMap(methodIdMap);
        for (Map.Entry<String, ContractInfo> entry : ExportConstant.threadLocal.get().getConfig()
                .getContractInfoMap().entrySet()) {
            ContractDetail contractDetail = new ContractDetail();
            ContractInfoBO contractInfoBO = new ContractInfoBO();
            String abi = entry.getValue().getAbi();
            if (abi == null) {
                log.error("abi is null !!! please set it");
            }
            String binary = entry.getValue().getBinary();
            if (binary == null) {
                log.error("binary is null !!! please set it");
            }
            contractInfoBO.setContractABI(abi);
            contractInfoBO.setAbiHash(new Keccak256().hash(abi));
            contractInfoBO.setContractBinary(binary);
            contractInfoBO.setContractName(entry.getKey());
            contractDetail.setContractInfoBO(contractInfoBO);
            contractDetail.setMethodMetaInfos(MethodParser.parseToInfoList(abi,entry.getKey()));
            contractDetail.setEventMetaInfos(EventParser.parseToInfoList(abi,entry.getKey()));
            for (MethodMetaInfo methodMetaInfo : contractDetail.getMethodMetaInfos()) {
                methodIdMap.put(methodMetaInfo.getMethodId(), methodMetaInfo);
                contractBinaryMap.put(binary, contractDetail);
            }
        }
        ExportConstant.threadLocal.get().setDecoder(new TransactionDecoderService(
                ExportConstant.threadLocal.get().getClient().getCryptoSuite()));
    }
}
