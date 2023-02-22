package com.webank.blockchain.data.export.config;

import cn.hutool.core.collection.CollectionUtil;
import com.webank.blockchain.data.export.common.entity.ContractInfo;
import com.webank.blockchain.data.export.common.entity.ESDataSource;
import com.webank.blockchain.data.export.common.entity.MysqlDataSource;
import com.webank.blockchain.data.export.common.enums.DataType;
import com.webank.blockchain.data.export.common.enums.IgnoreBasicDataParam;
import com.webank.blockchain.data.export.utils.PropertiesUtils;
import com.webank.solc.plugin.compiler.CompileSolToJava;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author wesleywang
 * @Description:
 * @date 2021/3/30
 */
@Configuration
@ConfigurationProperties("system")
@Data
@Slf4j
public class ServiceConfig {

    private String nodeStr;
    private String groupId;
    private String certPath;

    private List<String> groupIds;

    private int cryptoTypeConfig;
    private String rpcUrl;

    private String jdbcUrl;
    private String user;
    private String password;


    private int frequency = 5;

    private String tablePrefix = "";
    private String tablePostfix = "";
    private String namePrefix = "";
    private String namePostfix = "";

    private long startBlockHeight;
    private String startDate;

    @Value("${system.db.autoCreateTable}")
    private boolean autoCreateTable;

    @Value("${system.db.sharding}")
    private boolean sharding;

    private String abiPath;

    private String binaryPath;

    private String solPath;

    @Value("${system.db.shardingNumberPerDatasource}")
    private int shardingNumberPerDatasource;

    /** @Fields crawlBatchUnit : to cut gaint mission to small missions, whose size is this */
    private int crawlBatchUnit = 100;

    private List<MysqlDataSource> mysqlDataSources;

    private List<ContractInfo> contractInfos;

    @Value("${system.es.enabled}")
    private boolean esEnabled;
    @Value("${system.es.clusterName}")
    private String clusterName;
    @Value("${system.es.ip}")
    private String ip;
    @Value("${system.es.port}")
    private int port;

    private boolean multiLiving;

    private String zookeeperServiceLists;
    private String zookeeperNamespace;
    private String prepareTaskJobCron = "0/"+ frequency + " * * * * ?";
    private String dataFlowJobCron = "0/"+ frequency + " * * * * ?";
    private String dataFlowJobItemParameters = "0=A,1=B,2=C,3=D,4=E,5=F,6=G,7=H";
    private int dataFlowJobShardingTotalCount = 8;

    /**
     * ex: Map<contractName, methodName or eventName>
     */
    private Map<String,List<String>> generatedOff_SDK;

    /**
     * ex: Map<contractName, Map<methodName or eventName, List<javaNameParamName>>>
     */
    private Map<String, Map<String,List<String>>> ignoreParam_SDK;

    /**
     * ex: Map<contractName, Map<methodName or eventName, Map<solidityParamName,paramType>>>
     */
    private Map<String, Map<String,Map<String,String>>> paramSQLType_SDK;

    private Map<String,List<String>> ignoreBasicDataTableParam;

    private List<DataType> dataTypeBlackList;

    private ESDataSource esDataSource;

    private boolean grafanaEnable;

    @Autowired
    private PropertiesUtils propertiesUtils;

    @Bean
    public GroupTemplate getGroupTemplateInstance() throws IOException {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("");
        org.beetl.core.Configuration cfg = org.beetl.core.Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        return gt;
    }

    @PostConstruct
    private void init() {
        if(solPath != null) {
            CompileSolToJava compiler = new CompileSolToJava();
            File outputBaseDir = new File("./config/solidity");
            File abiOutputDir = new File(outputBaseDir, "abi");
            File binOutputDir = new File(outputBaseDir, "bin/ecc");
            File smbinOutputDir = new File(outputBaseDir, "bin/sm");
            File solDir = new File(solPath);
            try {
                compiler.compileSolToJava(null,null,
                        solDir, abiOutputDir,binOutputDir,smbinOutputDir,null);
            } catch (Exception e) {
                log.error("CompileSolToJava failed !!! ", e);
            }
            this.abiPath = "./config/solidity/abi";
            if (this.cryptoTypeConfig == 0) {
                this.binaryPath = "./config/solidity/bin/ecc";
            } else {
                this.binaryPath = "./config/solidity/bin/sm";
            }
        }
        mysqlDataSources = obtainMysqlConfigs();
        contractInfos = obtainContractInfos();
        generatedOff_SDK = obtainGeneratedOff();
        ignoreParam_SDK = obtainIgnoreParam();
        paramSQLType_SDK = obtainparamSQLType();
        ignoreBasicDataTableParam = obtainIgnoreBasicDataTableParam();
        dataTypeBlackList = obtainDataTypeBlackList();

        if (esEnabled) {
            esDataSource = new ESDataSource();
            esDataSource.setClusterName(clusterName);
            esDataSource.setIp(ip);
            esDataSource.setPort(port);
            esDataSource.setEnable(true);
        }
        groupIds = new ArrayList<>();
        if (groupId != null && groupId.contains(",")) {
            String[] ids = groupId.split(",");
            for (String id : ids) {
                groupIds.add(id);
            }
        }else {
            if (StringUtils.isNotBlank(groupId)) {
                groupIds.add(groupId);
            }
        }
    }



    public  List<MysqlDataSource> obtainMysqlConfigs() {
        List<MysqlDataSource> dataSources = new ArrayList<>();
        int i = 0;
        while (true) {
            String dbUrl = propertiesUtils.getProperty("system", "db" + i, "dbUrl");
            if (StringUtils.isBlank(dbUrl)) {
                break;
            }
            String user = propertiesUtils.getProperty("system", "db" + i, "user");
            String password = propertiesUtils.getProperty("system", "db" + i, "password");
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

    public  List<ContractInfo> obtainContractInfos() {
        List<ContractInfo> dataSources = new ArrayList<>();
        Map<String,File> abiMap = getFiles(abiPath,".abi");
        Map<String,File> binMap = getFiles(binaryPath,".bin");
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

    public  Map<String, List<String>> obtainGeneratedOff() {
        String generatedOffStr = propertiesUtils.getProperty("system", "generatedOffStr");
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

    public  Map<String, Map<String, List<String>>> obtainIgnoreParam() {
        String ignoreParam = propertiesUtils.getProperty("system", "ignoreParam");
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

    public  Map<String, Map<String, Map<String, String>>> obtainparamSQLType() {
        String paramSQLType = propertiesUtils.getProperty("system", "paramSQLType");
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

    public List<DataType> obtainDataTypeBlackList() {
        String dataTypeBlackList = propertiesUtils.getProperty("system", "dataTypeBlackLists");
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


    public Map<String, List<String>> obtainIgnoreBasicDataTableParam() {
        String ignoreBasicDataTableParams = propertiesUtils.getProperty("system", "ignoreBasicDataTableParams");
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
