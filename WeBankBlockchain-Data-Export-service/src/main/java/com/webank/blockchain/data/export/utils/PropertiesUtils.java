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
        StringBuilder key = new StringBuilder(args[0]);
        for (int i = 1 ; i < args.length; i++) {
            key.append(".").append(args[i]);
        }
        return environment.getProperty(key.toString());
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
            String contractName = attributes[0];
            String[] names = attributes[1].split(",");
            if (!map.containsKey(contractName)) {
                List<String> nameList = Arrays.asList(names);
                map.put(contractName, nameList);
            }else {
                map.get(contractName).addAll(Arrays.asList(names));
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
                List<String> nameList = Arrays.asList(names);
                methodMap.put(methodName, nameList);
            }else {
                methodMap.get(methodName).addAll(Arrays.asList(names));
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


}
