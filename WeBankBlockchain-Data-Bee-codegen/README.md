# WeBASE-Codegen-Monkey

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Gitter](https://badges.gitter.im/WeBASE-Codegen-Monkey/WeBASE-Codegen-Monkey.svg)](https://gitter.im/webase-monkey/community)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bd49c17906cd42f69fb1f6b1fa8c6760)](https://www.codacy.com/manual/dalaocu/WeBASE-Codegen-Monkey?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=WeBankFinTech/WeBASE-Codegen-Monkey&amp;utm_campaign=Badge_Grade)
[![CodeFactor](https://www.codefactor.io/repository/github/webankfintech/webase-codegen-monkey/badge)](https://www.codefactor.io/repository/github/webankfintech/webase-codegen-monkey)
[![codecov](https://codecov.io/gh/WeBankFinTech/WeBASE-Codegen-Monkey/branch/code_refactor_2020.01/graph/badge.svg)](https://codecov.io/gh/WeBankFinTech/WeBASE-Codegen-Monkey)
[![buildStatus](https://travis-ci.org/WeBankFinTech/WeBASE-Codegen-Monkey.svg?branch=master)](https://travis-ci.org/WeBankFinTech/WeBASE-Codegen-Monkey)
[![Documentation Status](https://readthedocs.org/projects/webasedoc/badge/?version=latest)](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Codegen-Monkey/index.html)
[![snyk](https://snyk.io/test/github/WeBankFinTech/WeBASE-Codegen-Monkey/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/WeBankFinTech/WeBASE-Codegen-Monkey?targetFile=build.gradle&tab=issues)
[![Total Lines](https://tokei.rs/b1/github/WeBankFinTech/WeBASE-Codegen-Monkey?category=lines)](https://github.com/WeBankFinTech/WeBASE-Codegen-Monkey)
[![Latest release](https://img.shields.io/github/release/WeBankFinTech/WeBASE-Codegen-Monkey.svg)](https://github.com/WeBankFinTech/WeBASE-Codegen-Monkey/releases/latest)

> 道生一，一生二，二生三，三生万物。
> 万物负阴而抱阳，冲气以为和。
> 人之所恶，唯孤、寡、不谷，而王公以为称。
> 故物或损之而益，或益之而损。
> 人之所教，亦我而教人。
> 强梁者不得其死——吾将以为教父。
> -- 老子

代码自动生成组件：WeBASE-Codegen-Monkey是WeBASE数据导出工具的代码生成组件，可帮助用户自动生成基于[FISCO BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master)的数据导出组件[WeBASE-Collect-Bee](https://github.com/WeBankFinTech/WeBASE-Collect-Bee/tree/master)。

只需要在一个配置文件中进行少量简单的配置，同时按照要求提供相关的智能合约信息；当前版本可支持自动生成[WeBASE-Collect-Bee](https://github.com/WeBankFinTech/WeBASE-Collect-Bee/tree/master)。

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
| FISCO-BCOS | >= 2.0， 1.x版本请参考V0.5版本 dev分支|
| Bash | 需支持Bash（理论上来说支持所有ksh、zsh等其他unix shell，但未测试）|
| Java | >= JDK[1.8] ||
| Git | 下载的安装包使用Git | |
| MySQL | >= mysql-community-server[5.7] | 理论上来说支持主流数据库，但未测试|
| zookeeper | >= zookeeper[3.4] | 只有在进行集群部署的时候需要安装|
| docker    | >= docker[18.0.0] | 只有需要可视化监控页面的时候才需要安装|

## 文档
- [**中文**](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Codegen-Monkey/index.html)
- [**快速安装**](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Codegen-Monkey/install.html#)

## 贡献代码
欢迎参与本项目的社区建设：
- 如项目对您有帮助，欢迎点亮我们的小星星(点击项目左上方Star按钮)。
- 欢迎提交代码(Pull requests)。
- [提问和提交BUG](https://github.com/WeBankFinTech/WeBASE-Codegen-Monkey/issues)。
- 如果发现代码存在安全漏洞，请在[这里](https://security.webank.com)上报。

## 加入我们的社区

FISCO BCOS开源社区是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。

![](https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/images/QR_image.png)

## License
![license](http://img.shields.io/badge/license-Apache%20v2-blue.svg)

开源协议为[Apache License 2.0](http://www.apache.org/licenses/). 详情参考[LICENSE](../LICENSE)。
