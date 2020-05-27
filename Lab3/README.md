# Lab 3: A Simple Distributed Key-Value Store

> by M78星云

## 1. 实验概要

利用网络编程和多线程编程知识，实现分布式KV数据库，其中使用**2PC协议**保证数据库一致性。

### 1.1 程序输入输出

```shell
# run cooridinator
java -jar kvstore2pcsystem.jar --config_path ./config/coordinator.conf
# run participants
java -jar kvstore2pcsystem.jar --config_path ./config/participant1.conf
java -jar kvstore2pcsystem.jar --config_path ./config/participant2.conf
```

### 1.2 项目结构

`./KVS`：JAVA源代码

`./kvstore2pcsystem.jar`：可执行jar包

`./lab3_testing.sh`：测试脚本

`./test_out.log`：测试脚本的输出（ALL PASSED）

`./config`：协调者、参与者的配置文件

### 1.3 实验环境

采用的VMvare下的Ubuntu虚拟机，Linux内核版本为4.4.0-21-generic

内存：2GB

CPU型号为Intel(R) Core(TM) i5-7200U CPU @ 2.70GHz，2内核4线程

虚拟机CPU线程数：4

