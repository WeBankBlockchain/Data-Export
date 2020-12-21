
# WeBankBlockchain-Data-Export

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

> 穿花度柳飞如箭，
> 粘絮寻香似落星。
> 小小微躯能负重，
> 器器薄翅会乘风。
> -- 吴承恩


WeBankBlockchain-Data-Export 是一个基于[FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)平台的数据导出工具。

数据导出组件WeBankBlockchain-Data-Export的目的在于降低获取区块链数据的开发门槛，提升研发效率。研发人员几乎不需要编写任何代码，只需要进行简单配置，就可以把数据导出到Mysql数据库。

WeBankBlockchain-Data-Export可以导出区块链上的基础数据，如当前块高、交易总量等。如果正确配置了FISCO-BCOS上运行的所有合约，WeBankBlockchain-Data-Export可以导出区块链上这些合约的业务数据，包括event、构造函数、合约地址、执行函数的信息等。

**此版本只支持**[FISCO BCOS 2.0](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)及以上版本。

**此版本为数据导出SDK版本，无spring等框架依赖，使用更加轻便灵活** 


## 环境要求

在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件 | 说明 |备注|
| --- | --- | --- |
| FISCO-BCOS | >= 2.0， 1.x版本请参考V0.5版本 dev分支 |
| Bash | 需支持Bash（理论上来说支持所有ksh、zsh等其他unix shell，但未测试）|
| Java | JDK[1.8] ||
| Git | 下载的安装包使用Git | |
| MySQL | >= mysql-community-server[5.7] | 理论上来说支持主流数据库，但未测试|

##使用教程

### 1.引入数据导出SDK依赖 

项目打包后，将打包后的SDK-jar包放到项目lib下，建立依赖

### 2.SDK接口介绍

```
//创建数据导出执行器DataExportExecutor
DataExportExecutor create(ExportDataSource dataSource, ChainInfo chainInfo);
//数据导出启动
start(DataExportExecutor exportExecutor)
//数据导出关闭
stop(DataExportExecutor exportExecutor)
```
其中参数ExportDataSource为数据源配置，结构如下：
```
    //是否自动建表
    private boolean autoCreateTable;
    //是否多数据源，采用分库分表
    private boolean sharding;
    //单库表分片数，用于路由和建表
    private int shardingNumberPerDatasource;
    //mysql数据源配置
    private List<MysqlDataSource> mysqlDataSources;
    //es数据源配置
    private ESDataSource esDataSource;

```

其中参数ChainInfo为链参数配置，结构如下：
```
    //节点ip和端口port，格式为：[ip]:[port]
    private String nodeStr;
    //分组id
    private int groupId;
    //链节点连接所需证书路径
    private String certPath;

```

单库使用方式如下：
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
        DataExportExecutor exportExecutor = DataExportService.create(dataSource, ChainInfo.builder()
                .nodeStr("[ip]:[port]")
                .certPath("config")
                .groupId(1).build());
        ExportDataSDK.start(exportExecutor);
        //Thread.sleep(60 *1000L);
        //ExportDataSDK.stop(exportExecutor);
```


分库分表使用方式例子如下：
```

public void shardingTest() throws ConfigException, InterruptedException {
        MysqlDataSource mysqlDataSourc = MysqlDataSource.builder()
                        .jdbcUrl("jdbc:mysql://[ip]:[port]/[database]")
                        .pass("password")
                        .user("username")
                        .build();
        MysqlDataSource mysqlDataSourc1 = MysqlDataSource.builder()
                        .jdbcUrl("jdbc:mysql://[ip]:3306/[database]")
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
        DataExportExecutor exportExecutor = DataExportService.create(dataSource, ChainInfo.builder()
                .nodeStr("[ip]:[port]")
                .certPath("config")
                .groupId(1).build());
        ExportDataSDK.start(exportExecutor);
        //Thread.sleep(60 *1000L);
        //ExportDataSDK.stop(exportExecutor);
    }

```


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目右上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankBlockchain/WeBankBlockchain-Data-Export/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。


## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
