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
package com.webank.blockchain.data.export.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.webank.blockchain.data.export.common.entity.ContractInfo;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.common.enums.DataType;
import com.webank.blockchain.data.export.common.enums.IgnoreBasicDataParam;
import com.webank.blockchain.data.export.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PropertiesUtils
 *
 * @author maojiayu
 * @Description: PropertiesUtils
 * @data Dec 28, 2018 4:10:49 PM
 */
@Slf4j
@Component
public class PropertiesUtils {

    @Autowired
    private Environment environment;

    @Autowired
    private ServiceConfig config;


    /**
     * return the first mapping result of args.
     *
     * @return property value
     */
    public  String getProperty(String... args) {
        final String delimiter = ".";
        final String key = String.join(delimiter, args);
        return environment.getProperty(key);
    }


    public  List<MysqlDataSource> getMysqlConfigs() {
        List<MysqlDataSource> dataSources = new ArrayList<>();
        int i = 0;
        while (true) {
            String dbUrl = getProperty("system", "db" + i, "dbUrl");
            if (StringUtils.isBlank(dbUrl)) {
                break;
            }
            String user = getProperty("system", "db" + i, "user");
            String password = getProperty("system", "db" + i, "password");
            MysqlDataSource mysqlDataSource = MysqlDataSource.builder()
                    .jdbcUrl(dbUrl)
                    .user(user)
                    .pass(password)
                    .build();
            dataSources.add(mysqlDataSource);
            i++;
        }
        return dataSources;
    }

    public  List<ContractInfo> getContractInfos() {
        List<ContractInfo> dataSources = new ArrayList<>();
        Map<String,File> abiMap = getFiles(config.getAbiPath(),".abi");
        Map<String,File> binMap = getFiles(config.getBinaryPath(),".bin");
        if (CollectionUtil.isEmpty(abiMap) || CollectionUtil.isEmpty(binMap)){
            return dataSources;
        }
        for(Map.Entry<String,File> entry : abiMap.entrySet()) {
            if (!binMap.containsKey(entry.getKey())){
                continue;
            }
            StringBuilder abi = new StringBuilder();
            try {
                List<String> abis = Files.readAllLines(Paths.get(entry.getValue().toURI()), StandardCharsets.UTF_8);
                for (String str : abis){
                    abi.append(str);
                }
            } catch (IOException e) {
                log.error("abi read failed ", e);
            }
            StringBuilder bin = new StringBuilder();
            try {
                List<String> bins = Files.readAllLines(Paths.get(binMap.get(entry.getKey()).toURI()), StandardCharsets.UTF_8);
                for (String str : bins){
                    bin.append(str);
                }
            } catch (IOException e) {
                log.error("abi read failed ", e);
            }
            ContractInfo contractInfo = new ContractInfo()
                    .setBinary(bin.toString())
                    .setAbi(abi.toString())
                    .setContractName(entry.getKey());
            dataSources.add(contractInfo);
        }
        return dataSources;
    }


    public static Map<String,File> getFiles(String path, String fileType) {
        Map<String,File> abiMap = new HashMap<>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null){
                return abiMap;
            }
            for (File value : files) {
                if (!value.isDirectory()) {
                    if (value.getName().endsWith(fileType)) {
                        abiMap.put(value.getName().substring(0, value.getName().length() - 4), value);
                    }
                }
            }
        }
        return abiMap;
    }

    public  Map<String, List<String>> getGeneratedOff() {
        String generatedOffStr = getProperty("system", "generatedOffStr");
        if (generatedOffStr == null) {
            return null;
        }
        Map<String, List<String>> map = new HashMap<>();
        String[] strings = generatedOffStr.split("\\|");
        for (String str : strings) {
            String[] attributes = str.split("\\.");
            if (attributes.length != 2){
                log.error("Incorrect system.generatedOffStr size ! Wrong field : " + str);
                System.exit(1);
            }
            String contractName = attributes[0];
            String[] names = attributes[1].split(",");
            if (!map.containsKey(contractName)) {
                List<String> nameList = new ArrayList<>(Arrays.asList(names));
                map.put(contractName, nameList);
            }else {
                for (String name : names) {
                    map.get(contractName).add(name);
                }
            }
        }
        return map;
    }

    public  Map<String, Map<String, List<String>>> getIgnoreParam() {
        String ignoreParam = getProperty("system", "ignoreParam");
        if (ignoreParam == null) {
            return null;
        }
        Map<String, Map<String, List<String>>> map = new HashMap<>();
        String[] strings = ignoreParam.split("\\|");
        for (String str : strings) {
            String[] attributes = str.split("\\.");
            if (attributes.length != 3){
                log.error("Incorrect system.ignoreParam size ! Wrong field : " + str);
                System.exit(1);
            }
            String contractName = attributes[0];
            String methodName = attributes[1];
            String[] names = attributes[2].split(",");
            Map<String, List<String>> methodMap;
            if (!map.containsKey(contractName)) {
                methodMap = new HashMap<>();
                map.put(contractName, methodMap);
            }
            methodMap = map.get(contractName);
            if (!methodMap.containsKey(methodName)) {
                List<String> nameList = new ArrayList<>(Arrays.asList(names));
                methodMap.put(methodName, nameList);
            }else {
                for (String name : names) {
                    methodMap.get(methodName).add(name);
                }
            }

        }
        return map;
    }

    public  Map<String, Map<String, Map<String, String>>> getparamSQLType() {
        String paramSQLType = getProperty("system", "paramSQLType");
        if (paramSQLType == null) {
            return null;
        }
        Map<String, Map<String, Map<String, String>>> map = new HashMap<>();
        String[] strings = paramSQLType.split("\\|");
        for (String str : strings) {
            String[] attributes = str.split("\\.");
            if (attributes.length != 4){
                log.error("Incorrect system.paramSQLType size ! Wrong field : " + str);
                System.exit(1);
            }
            String contractName = attributes[0];
            String methodName = attributes[1];
            String solidityParamName = attributes[2];
            String paramType = attributes[3];
            Map<String, Map<String, String>> methodMap;
            if (!map.containsKey(contractName)) {
                methodMap = new HashMap<>();
                map.put(contractName, methodMap);
            }
            methodMap = map.get(contractName);
            Map<String, String> paramMap;
            if (!methodMap.containsKey(methodName)) {
                paramMap = new HashMap<>();
                methodMap.put(methodName, paramMap);
                paramMap.put(solidityParamName, paramType);
            }
            map.get(contractName).get(methodName).put(solidityParamName, paramType);
        }
        return map;
    }

    public List<DataType> getDataTypeBlackList() {
        String dataTypeBlackList = getProperty("system", "dataTypeBlackLists");
        if (dataTypeBlackList == null) {
            return null;
        }
        String[] strings = dataTypeBlackList.split(",");
        List<DataType> dataTypes = new ArrayList<>();
        for (String dataType : strings) {
            DataType type = DataType.getDataType(dataType);
            if (!type.equals(DataType.NULL)){
                dataTypes.add(type);
            }
        }
        return dataTypes;
    }


    public Map<String, List<String>> getIgnoreBasicDataTableParam() {
        String ignoreBasicDataTableParams = getProperty("system", "ignoreBasicDataTableParams");
        Map<String, List<String>> map = new HashMap<>();
        if (ignoreBasicDataTableParams == null) {
            return map;
        }
        String[] strings = ignoreBasicDataTableParams.split("\\|");
        for (String str : strings) {
            String[] attributes = str.split("\\.");
            if (attributes.length != 2){
                log.error("Incorrect system.ignoreBasicDataTableParams size ! Wrong field : " + str);
                System.exit(1);
            }
            String tableName = attributes[0];
            String[] paramStrs = attributes[1].split(",");
            List<String> params = getParams(tableName,paramStrs);
            if (tableName.equals("block_raw_data")){
                map.put(IgnoreBasicDataParam.IgnoreBasicDataTable.BLOCK_RAW_DATA_TABLE.name(),params);
            }
            if (tableName.equals("tx_raw_data")){
                map.put(IgnoreBasicDataParam.IgnoreBasicDataTable.TX_RAW_DATA_TABLE.name(),params);
            }
            if (tableName.equals("tx_receipt_raw_data")){
                map.put(IgnoreBasicDataParam.IgnoreBasicDataTable.TX_RECEIPT_RAW_DATA_TABLE.name(),params);
            }
        }
        return map;
    }


    private List<String> getParams(String tableName, String[] paramStrs) {
        List<String> params = new ArrayList<>();
        if (tableName.equals("block_raw_data")) {
            for (String param : paramStrs) {
                if (param.equals("db_hash")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.DB_HASH.name());
                }
                if (param.equals("extra_data")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.EXTRA_DATA.name());
                }
                if (param.equals("gas_limit")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.GAS_LIMIT.name());
                }
                if (param.equals("gas_used")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.GAS_USED.name());
                }
                if (param.equals("logs_bloom")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.LOGS_BLOOM.name());
                }
                if (param.equals("parent_hash")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.PARENT_HASH.name());
                }
                if (param.equals("receipts_root")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.RECEIPTS_ROOT.name());
                }
                if (param.equals("sealer")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.SEALER.name());
                }
                if (param.equals("sealer_list")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.SEALER_LIST.name());
                }
                if (param.equals("signature_list")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.SIGNATURE_LIST.name());
                }
                if (param.equals("state_root")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.STATE_ROOT.name());
                }
                if (param.equals("transaction_list")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.TRANSACTION_LIST.name());
                }
                if (param.equals("transactions_root")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.TRANSACTIONS_ROOT.name());
                }
                if (param.equals("sealer")) {
                    params.add(IgnoreBasicDataParam.BlockRawDataParams.SEALER.name());
                }
            }
        }

        if (tableName.equals("tx_raw_data")) {
            for (String param : paramStrs) {
                if (param.equals("from")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.FROM.name());
                }
                if (param.equals("gas")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.GAS.name());
                }
                if (param.equals("gas_price")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.GAS_PRICE.name());
                }
                if (param.equals("input")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.INPUT.name());
                }
                if (param.equals("nonce")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.NONCE.name());
                }
                if (param.equals("to")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.TO.name());
                }
                if (param.equals("value")) {
                    params.add(IgnoreBasicDataParam.TxRawDataParams.VALUE.name());
                }
            }
        }

        if (tableName.equals("tx_receipt_raw_data")) {
            for (String param : paramStrs) {
                if (param.equals("gas_used")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.GAS_USED.name());
                }
                if (param.equals("input")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.INPUT.name());
                }
                if (param.equals("logs")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.LOGS.name());
                }
                if (param.equals("logs_bloom")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.LOGS_BLOOM.name());
                }
                if (param.equals("message")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.MESSAGE.name());
                }
                if (param.equals("output")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.OUTPUT.name());
                }
                if (param.equals("receipt_proof")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.RECEIPT_PROOF.name());
                }
                if (param.equals("root")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.ROOT.name());
                }
                if (param.equals("tx_proof")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.TX_PROOF.name());
                }
                if (param.equals("from")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.FROM.name());
                }
                if (param.equals("to")) {
                    params.add(IgnoreBasicDataParam.TxReceiptRawDataParams.TO.name());
                }
            }
        }
        return params;
    }

}
