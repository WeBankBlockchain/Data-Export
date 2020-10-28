
# WeBASE-Collect-Bee

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/WeBASE-Collect-Bee/WeBASE-Collect-Bee.svg)](https://gitter.im/webase-bee/community)
[![Documentation Status](https://readthedocs.org/projects/webasedoc/badge/?version=latest)](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Codegen-Monkey/index.html)
[![snyk](https://snyk.io/test/github/WeBankFinTech/WeBASE-Collect-Bee/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/WeBankFinTech/WeBASE-Collect-Bee?targetFile=build.gradle&tab=issues)
[![Total Lines](https://tokei.rs/b1/github/WeBankFinTech/WeBASE-Collect-Bee?category=lines)](https://github.com/WeBankFinTech/WeBASE-Collect-Bee)
[![Latest release](https://img.shields.io/github/release/WeBankFinTech/WeBASE-Collect-Bee.svg)](https://github.com/WeBankFinTech/WeBASE-Collect-Bee/releases/latest)



> 穿花度柳飞如箭，
> 粘絮寻香似落星。
> 小小微躯能负重，
> 器器薄翅会乘风。
> -- 吴承恩


WeBASE-Collect-Bee 是一个基于[FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)平台的数据导出工具。

数据导出组件WeBASE-Collect-Bee的目的在于降低获取区块链数据的开发门槛，提升研发效率。研发人员几乎不需要编写任何代码，只需要进行简单配置，就可以把数据导出到Mysql数据库。

WeBASE-Collect-Bee可以导出区块链上的基础数据，如当前块高、交易总量等。如果正确配置了FISCO-BCOS上运行的所有合约，WeBASE-Collect-Bee可以导出区块链上这些合约的业务数据，包括event、构造函数、合约地址、执行函数的信息等。

你可以通过[WeBASE-Codegen-Monkey](https://github.com/WeBankFinTech/WeBASE-Codegen-Monkey/tree/master)来自动生成本工程，只需要在一个配置文件中进行少量简单的配置，同时按照要求提供相关的智能合约信息；我们推荐这种方式。

**此版本只支持**[FISCO BCOS 2.0](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)。


## 关键特性

- 自动生成数据导出组件

- 支持自定义导出数据内容

- 内置Restful API，提供常用的查询功能

- 支持多数据源，支持读写分离和分库分表

- 支持多活部署，多节点自动导出

- 支持区块重置导出

- 支持可视化的监控页面

- 提供可视化的互动API控制台

## 环境要求

在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件 | 说明 |备注|
| --- | --- | --- |
| FISCO-BCOS | >= 2.0， 1.x版本请参考V0.5版本 dev分支 |
| Bash | 需支持Bash（理论上来说支持所有ksh、zsh等其他unix shell，但未测试）|
| Java | >= JDK[1.8] ||
| Git | 下载的安装包使用Git | |
| MySQL | >= mysql-community-server[5.7] | 理论上来说支持主流数据库，但未测试|
| zookeeper | >= zookeeper[3.4] | 只有在进行集群部署的时候需要安装|
| docker    | >= docker[18.0.0] | 只有需要可视化监控页面的时候才需要安装|

## 文档
- [**中文**](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Collect-Bee/index.html)
- [**快速安装**](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Collect-Bee/install.html)


## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目左上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankFinTech/WeBASE-Collect-Bee/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。

## 加入我们的社区

FISCO BCOS开源社区是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。


![](https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/images/QR_image.png)


## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
