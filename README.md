
# WeBankBlockchain-Data-Export

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)


WeBankBlockchain-Data-Export 是一个基于[FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)平台的数据导出工具。

**此版本为数据导出SDK版本，无spring等框架依赖，使用更加轻便灵活** 

**此版本只支持**[FISCO BCOS 2.0](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)及以上版本。



## 环境要求

在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件 | 说明 |备注|
| --- | --- | --- |
| FISCO-BCOS | >= 2.0， 1.x版本请参考V0.5版本 dev分支 |
| Java | JDK[1.8] ||
| Git | 下载的安装包使用Git | |
| MySQL | >= mysql-community-server[5.7] | 理论上来说支持主流数据库，但未测试|
| ElasticSearch | >= elasticsearch [7.0] | 只有在需要ES存储时安装 |
| zookeeper | >= zookeeper[3.4] | 只有在进行集群部署的时候需要安装|


## 使用教程

### 1.引入数据导出SDK依赖 

项目打包后，将打包后的SDK-jar包放到项目lib下，建立依赖


### 2.SDK接口介绍

**SDK提供接口如下：**
```
//创建数据导出执行器DataExportExecutor，导出配置采用默认配置
DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo);
DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo, ExportConfig config)
//数据导出启动
start(DataExportExecutor exportExecutor)
//数据导出关闭
stop(DataExportExecutor exportExecutor)
```
**参数ExportDataSource为数据源配置，参数如下：**

| 参数 | 说明 | 类型 | 默认值 |
| --- | --- | --- | ---|
| autoCreateTable | 是否自动建表 | boolean |false |
| sharding | 是否多数据源，采用分库分表 | boolean |false |
| shardingNumberPerDatasource | 单库表分片数，用于路由和建表 | int | 0 |
| mysqlDataSources | mysql数据源配置，支持多数据源 | List<MysqlDataSource> | null |
| esDataSource | es数据源配置 | ESDataSource | null |

**数据源参数支持了mysql和es，包括MysqlDataSource ESDataSource，参数如下：**

**MysqlDataSource**
| 参数 | 说明 | 类型 | 默认值 |
| --- | --- | --- | ---|
| jdbcUrl | jdbc连接配置，格式：jdbc:mysql://[ip]:[port]/[database] | string | null |
| user | 用户名 | string | null |
| pass | 密码 | string | null |

**ESDataSource**
| 参数 | 说明 | 类型 | 默认值 |
| --- | --- | --- | ---|
| enable | es存储开关 | boolean | false |
| clusterName | 集群名称 | string | null |
| ip | IP地址 | string | null |
| port | 端口号 | int | null |


**参数ChainInfo为链参数配置，参数如下：**

| 参数 | 说明 | 类型 | 默认值 |
| --- | --- | --- | ---|
| nodeStr | 节点ip和端口port，格式为：[ip]:[port] | string | null |
| groupId | 分组id | int | null |
| certPath | 链节点连接所需证书路径 | string | null |


**参数ExportConfig为数据导出任务配置，参数如下：**

| 参数 | 说明 | 类型 | 默认值 |
| --- | --- | --- | ---|
| crawlBatchUnit | 链上导出任务每批次数目 | int | 1000 |
| frequency | 任务间隔时间 | int | 5s |
| startBlockHeight | 开始区块高度 | int | 0 |
| startDate | 开始时间 | Date | null |
| dataTypeBlackList | 导出数据表黑名单，默认全部导出，可根据DataType枚举设置 | List<DataType> | null |
| multiLiving | 是否开启多活job | boolean |false |
| zookeeperServiceLists | zk服务节点列表(,分隔),格式：[IP]:[port],[IP]:[port] | string | null |
| zookeeperNamespace | zk命名空间(,分隔) | string | null |
| prepareTaskJobCron | 任务准备job定时配置 | string | "0/"+ frequency + " * * * * ?" |
| dataFlowJobCron | 任务分片执行job定时配置 | string |"0/"+ frequency + " * * * * ?" |
| dataFlowJobItemParameters | 任务分片执行job参数 | string | 如 "0=A,1=B,2=C,3=D,4=E,5=F,6=G,7=H" |
| dataFlowJobShardingTotalCount | 任务分片数目 | int | 8 |

**单库使用方式例子如下（默认导出配置）：**
```
MysqlDataSource mysqlDataSourc = MysqlDataSource.builder()
        .jdbcUrl("jdbc:mysql://[ip]:[port]/[database]")
        .pass("password")
        .user("username")
        .build();
List<MysqlDataSource> mysqlDataSourceList = new ArrayList<>();
mysqlDataSourceList.add(mysqlDataSourc);
ExportDataSource dataSource = ExportDataSource.builder()
        .mysqlDataSources(mysqlDataSourceList)
        .autoCreateTable(true)
        .build();
DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, ChainInfo.builder()
         .nodeStr("[ip]:[port]")
        .certPath("config")
        .groupId(1).build());
ExportDataSDK.start(exportExecutor);
//Thread.sleep(60 *1000L);
//ExportDataSDK.stop(exportExecutor);
```


**分库分表使用方式例子如下（默认导出配置）：**
```
MysqlDataSource mysqlDataSourc = MysqlDataSource.builder()
        .jdbcUrl("jdbc:mysql://[ip]:[port]/[database]")
        .pass("password")
        .user("username")
        .build();
MysqlDataSource mysqlDataSourc1 = MysqlDataSource.builder()
        .jdbcUrl("jdbc:mysql://[ip]:[port]/[database]")
        .pass("password")
        .user("username")
        .build();
List<MysqlDataSource> mysqlDataSourceList = new ArrayList<>();
mysqlDataSourceList.add(mysqlDataSourc);
mysqlDataSourceList.add(mysqlDataSourc1);
ExportDataSource dataSource = ExportDataSource.builder()
        .mysqlDataSources(mysqlDataSourceList)
        .autoCreateTable(true)
        .sharding(true)
        .shardingNumberPerDatasource(2)
        .build();
DataExportExecutor exportExecutor = ExportDataSDK.create(dataSource, ChainInfo.builder()
        .nodeStr("[ip]:[port]")
        .certPath("config")
        .groupId(1).build());
ExportDataSDK.start(exportExecutor);
//Thread.sleep(60 *1000L);
//ExportDataSDK.stop(exportExecutor);
```

**更多使用方式见ExportDataTest.class中测试例子**


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目右上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankBlockchain/WeBankBlockchain-Data-Export/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。


## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
