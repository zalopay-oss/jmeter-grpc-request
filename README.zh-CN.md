# jmeter-grpc-request
<p align="center"><img src="./dist/asset/jmeter-and-grpc.png" width="600px" alt="Apache JMeter and gRPC logo" /></p>

<h4 align="center">这个JMeter采样器允许您向服务器发送一个gRPC请求</h4>
<h4 align="center">它和HTTP请求一样简单</h4>

<p align="center">
	<a target="_blank" href="https://www.javadoc.io/doc/org.apache.jmeter/ApacheJMeter_core">
		<img src="https://www.javadoc.io/badge/org.apache.jmeter/ApacheJMeter_core.svg" alt="Javadocs">
	</a>
	<a target="_blank" href="https://stackoverflow.com/questions/tagged/jmeter">
		<img src="https://img.shields.io/:stack%20overflow-jmeter-brightgreen.svg" alt="Stack Overflow">
	</a>
</p>

[English](./README.md) | 简体中文

## 介绍
他是一个功能强大的JMeter Grpc插件，可用于测试任何gRPC服务器，它不需要生成gRPC类或编译服务的protos二进制文件，只是一个非常简单的输入：

- gRPC服务的主机和端口
- 需要测试的RPC方法
- proto文件路径
- 格式化的JSON请求数据

## 特性
- 支持压测阻塞等调用方式
- 支持在运行时解析proto文件
- 支持TLS连接
- 支持元数据认证(JWT/Token)
- 支持JSON格式的请求数据
- 支持运行在Windows、Mac、Linux中
- 支持自动列出proto文件中的所有完整方法
- 支持根据proto文件自动生成请求Mock
- 支持各种报告生成
- 支持自动化测试

## 如何使用
<p align="center"><img src="./dist/asset/jmeter-grpc-create-testscript.gif" width="820px" alt="jmeter-create-testscript-grpc" /></p>

### 插件安装
你需要将 **jmeter-grpc-request** 插件的 `jar` 包复制到JMeter的 `lib/ext` 目录下面，然后重启你的JMeter工具。

**jmeter-grpc-request** 插件的 `jar` 包，可以从 [Releases Page](https://github.com/zalopay-oss/jmeter-grpc-request/releases) 获得，也可以 在 [JMeter Plugins Manager](https://jmeter-plugins.org/?search=jmeter-grpc-request) 中找到

### 使用 JMeter 发出 gRPC 请求
创建测试脚本：

- 添加线程组：右键单击测试计划 → 添加 → 线程(用户) → 线程组
- 添加GRPC Request：右键单击新建的线程组 → 添加 → 取样器 → GRPC Request
- 填写请求信息：主机、端口、proto文件夹、rpc方法、请求数据
- 保存测试脚本

运行测试：

- 通过JMeter GUI在顶部栏点击启动按钮
- 通过命令行：`bin/jmeter -n -t <test JMX file>.jmx -l <test JMX result>.csv -j <test log file>.log -e -o <Path to output folder>`

### 使用说明
| 序号	| 选项									| 描述																																																						|
|-----	|:-----------------------------------	|:---------------------------------------------------------------------																																						|
| 1		| Server Name or IP						| gRPC服务器地址（域名或IP）																																																|
| 2		| Port Number							| gRPC服务器端口 (80/443)																																																	|
| 3		| SSL/TLS								| 开启SSL/TLS认证（https）																																																			|
| 4     | Disable SSL/TLS Cert Verification     | 禁用SSL/TLS证书校验（自签证书需开启）                                                                                                                                                                                                            |
| 5		| Proto Root Directory					| proto文件的根路径																																																			|
| 6		| Library Directory (Optional)			| proto文件解析需要依赖的额外库的文件夹路径 (googleapis)																																									|
| 7		| Full Method							| 用于请求测试的RPC方法																																																		|
| 8		| Metadata								| Metadata可以用于token身份验证等方式，支持以下两种方式传输（UTF-8）：<br/>1. 使用键值对（Key: Value）：<br/>&nbsp; - key1: value1, key2: value2<br/>2. 使用 JSON String：<br/>&nbsp; - {"key1":"Value1", "key2":"value2"}	|
| 9		| Deadline								| 请求超时时间（单位：毫秒）																																																|
| 10		| Send JSON Format With the Request		| 格式化的JSON请求数据																																																		|

## 运行示例
运行示例说明见 [./dist/example](./dist/example) 目录

## 基准测试
通过基准测试验证，jmeter-grpc-request 插件在对gRPC系统进行负载测试时是稳定的。

了解更多 [Benchmark: jmter-grpc-request](./dist/benchmark)

- CCU: 120 user
- Duration: 30 min

<img src= "./dist/asset/report-120-1800s.jpg" />

## 开发构建
### 构建环境
从源码构建 JMeter GRPC Request 插件，进行开发调试，必须拥有以下环境：

- [Java 8](https://www.oracle.com/downloads/index.html)
- [Apache Maven 3](https://maven.apache.org/)

### 构建命令
```
mvn clean package
```