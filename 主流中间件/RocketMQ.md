# CentOS7 安装 RocketMQ



## 1.安装RocketMQ

```shell
#1.下载(地址:https://downloads.apache.org/rocketmq/4.8.0/rocketmq-all-4.8.0-bin-release.zip)
#2.解压:
unzip rocketmq-all-4.8.0-bin-release.zip
mv rocketmq-all-4.8.0-bin-release rocketmq-4.8
#3.启动NameServer
nohup sh bin/mqnamesrv &
#4.查看日志
tail -f ~/logs/rocketmqlogs/namesrv.log
#5.启动broker
nohup sh bin/mqbroker -n localhost:9876 &
#6.查看启动日志
tail -f ~/logs/rocketmqlogs/broker.log

```

## 2.问题

RocketMQ默认虚拟机内存较大，启动broker因内存不足失败时，需要修改一下两个配置文件的JVM内存大小

```shell
#编辑runbroker.sh 和runserver.sh修改默认JVM大小
vi runbroker.sh
vi runserver.sh
```

参考设置：

```shell
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
```

重启NameServer:

```shell
sh bin/mqshutdown namesrv
nohup sh bin/mqnamesrv &
nohup sh bin/mqbroker -n localhost:9876 &
```

## 3.测试RocketMQ

### 3.1 发送消息

```shell
# 1.设置环境变量
export NAMESRV_ADDR=localhost:9876
# 2.使用安装包的Demo发送消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
```



### 3.2 接收消息

```shell
# 1.设置环境变量
export NAMESRV_ADDR=localhost:9876
# 2.接收消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
```



### 3.3 关闭RocketMQ

```shell
# 1.关闭NameServer
sh bin/mqshutdown namesrv
# 2.关闭Broker
sh bin/mqshutdown broker
```

## 4 集群搭建

服务器环境

| 序号 | ip            | 角色                    | 架构模式       |
| ---- | ------------- | ----------------------- | -------------- |
| 1    | 192.168.3.128 | nameserver,brokerserver | Master1,slave2 |
| 2    | 192.168.3.130 | nameserver,brokerserver | Master2,slave1 |



### 4.1 Host添加信息

```shell
vim /etc/hosts
# nameserver
192.168.3.128 rocketmq-nameserver1
192.168.3.130 rocketmq-nameserver2
# broker
192.168.3.128 rocketmq-master1
192.168.3.128 rocketmq-salve2
192.168.3.130 rocketmq-master2
192.168.3.130 rocketmq-salve1
```

配置完成后，重启网卡

```shell
systemctl restart network
```

### 4.2 防火墙配置

宿主机需要远程访问虚拟机的rocketmq服务和web服务，需要开放相关的端口号

```shell
# 方式1：直接关闭防火墙
# 关闭防火墙
systemctl stop firewalld.service
# 查看防火墙状态
firewall-cmd --state
# 禁止开机启动
systemctl disable firewalld.service

# 方式2：开放相关端口
# nameserver默认使用端口：9876，master默认使用端口：10911，slave默认使用端口：11011
firewalld-cmd --remove-port=9876/tcp --permanent
firewalld-cmd --remove-port=10911/tcp --permanent
firewalld-cmd --remove-port=11011/tcp --permanent
#重启防火墙
firewall-cmd --reload
```

### 4.3 环境变量配置

```shell
vim /etc/profile
# set rocketmq
ROCKETMQ_HOME=/dsp/soft/rocketmq-4.8.0
PATH=$PATH:$ROCKET_HOME/bin
export ROCKET_HOME PATH

#保存后使配置立即生效
source /etc/profile
```

### 4.4 创建消息存储路径

```shell
mkdir /dsp/soft/rocketmq-4.8.0/store
mkdir /dsp/soft/rocketmq-4.8.0/store/commitlog
mkdir /dsp/soft/rocketmq-4.8.0/store/consumequeue
mkdir /dsp/soft/rocketmq-4.8.0/store/index
```

### 4.5 broker配置文件

#### 1) master1

服务器：192.168.3.128

```shell
vi /dsp/soft/rocketmq-4.8.0/conf/2m-2s-sync/broker-a.properties
# 修改如下配置
# 所属集群名
brokerClusterName=rocket-cluster
# broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-a
# 0表示Master,>0表示slave
brokerId=0
# nameserver地址，分号分割
namesrvAddr=rocketmq-nameserver1:9876;rocketmq-nameserver2:9876
#在发送消息时，自动创建度武器不存在的topic,默认穿件的队列数
defaultTopicQueueNum=4
#是否允许 Broker 自动创建topic,建议线下开启,线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=10911
#删除文件时间点，默认凌晨4点
deleteWhen=04
#文件保留时间，默认48小时
fileReservedTime=48
#commitLog 每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#检测物理文件磁盘空间
diskMaxUseSpaceRatio=88
#存储路径
storePathRootDir=/dsp/soft/rocketmq-4.8.0/store
#commitLog存储路径
storePathCommitLog=/dsp/soft/rocketmq-4.8.0/store/commitlog
#消息队列存储路径
storePathComsumeQueue=/dsp/soft/rocketmq-4.8.0/store/consumequeue
#消息索引存储路径
storePathIndex=/dsp/soft/rocketmq-4.8.0/store/index
#checkpoint 文件存储路径
storeCheckpoint=/dsp/soft/rocketmq-4.8.0/store/checkpoint
#abort 文件存储路径
abortFile=/dsp/soft/rocketmq-4.8.0/store/abort
#限制的消息大小
maxMessageSize=65536
#Broker角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=SYNC_MASTER
#刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH
#发消息线程池数量
sendMessageThreadPoolNums=128
#拉消息线程池数量
pullMessageThreadPoolNums=128
```

#### 2) slave2

服务器：192.168.3.128

```sh
vi /dsp/soft/rocketmq-4.8.0/conf/2m-2s-sync/broker-b-s.properties
# 修改如下配置
# 所属集群名
brokerClusterName=rocket-cluster
# broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-b
# 0表示Master,>0表示slave
brokerId=1
# nameserver地址，分号分割
namesrvAddr=rocketmq-nameserver1:9876;rocketmq-nameserver2:9876
#在发送消息时，自动创建度武器不存在的topic,默认穿件的队列数
defaultTopicQueueNum=4
#是否允许 Broker 自动创建topic,建议线下开启,线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=11011
#删除文件时间点，默认凌晨4点
deleteWhen=04
#文件保留时间，默认48小时
fileReservedTime=48
#commitLog 每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#检测物理文件磁盘空间
diskMaxUseSpaceRatio=88
#存储路径
storePathRootDir=/dsp/soft/rocketmq-4.8.0/store
#commitLog存储路径
storePathCommitLog=/dsp/soft/rocketmq-4.8.0/store/commitlog
#消息队列存储路径
storePathComsumeQueue=/dsp/soft/rocketmq-4.8.0/store/consumequeue
#消息索引存储路径
storePathIndex=/dsp/soft/rocketmq-4.8.0/store/index
#checkpoint 文件存储路径
storeCheckpoint=/dsp/soft/rocketmq-4.8.0/store/checkpoint
#abort 文件存储路径
abortFile=/dsp/soft/rocketmq-4.8.0/store/abort
#限制的消息大小
maxMessageSize=65536
#Broker角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=SLAVE
#刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH
#发消息线程池数量
sendMessageThreadPoolNums=128
#拉消息线程池数量
pullMessageThreadPoolNums=128
```

#### 3) master2

服务器：192.168.3.130

```sh
vi /dsp/soft/rocketmq-4.8.0/conf/2m-2s-sync/broker-b.properties
# 修改如下配置
# 所属集群名
brokerClusterName=rocket-cluster
# broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-b
# 0表示Master,>0表示slave
brokerId=0
# nameserver地址，分号分割
namesrvAddr=rocketmq-nameserver1:9876;rocketmq-nameserver2:9876
#在发送消息时，自动创建度武器不存在的topic,默认穿件的队列数
defaultTopicQueueNum=4
#是否允许 Broker 自动创建topic,建议线下开启,线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=11011
#删除文件时间点，默认凌晨4点
deleteWhen=04
#文件保留时间，默认48小时
fileReservedTime=48
#commitLog 每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#检测物理文件磁盘空间
diskMaxUseSpaceRatio=88
#存储路径
storePathRootDir=/dsp/soft/rocketmq-4.8.0/store
#commitLog存储路径
storePathCommitLog=/dsp/soft/rocketmq-4.8.0/store/commitlog
#消息队列存储路径
storePathComsumeQueue=/dsp/soft/rocketmq-4.8.0/store/consumequeue
#消息索引存储路径
storePathIndex=/dsp/soft/rocketmq-4.8.0/store/index
#checkpoint 文件存储路径
storeCheckpoint=/dsp/soft/rocketmq-4.8.0/store/checkpoint
#abort 文件存储路径
abortFile=/dsp/soft/rocketmq-4.8.0/store/abort
#限制的消息大小
maxMessageSize=65536
#Broker角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=SYNC_MASTER
#刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH
#发消息线程池数量
sendMessageThreadPoolNums=128
#拉消息线程池数量
pullMessageThreadPoolNums=128
```

#### 4) slave1

服务器：192.168.3.130

```sh
vi /dsp/soft/rocketmq-4.8.0/conf/2m-2s-sync/broker-a-s.properties
# 修改如下配置
# 所属集群名
brokerClusterName=rocket-cluster
# broker名字，注意此处不同的配置文件填写的不一样
brokerName=broker-a
# 0表示Master,>0表示slave
brokerId=1
# nameserver地址，分号分割
namesrvAddr=rocketmq-nameserver1:9876;rocketmq-nameserver2:9876
#在发送消息时，自动创建度武器不存在的topic,默认穿件的队列数
defaultTopicQueueNum=4
#是否允许 Broker 自动创建topic,建议线下开启,线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#Broker 对外服务的监听端口
listenPort=10911
#删除文件时间点，默认凌晨4点
deleteWhen=04
#文件保留时间，默认48小时
fileReservedTime=48
#commitLog 每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824
#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
#检测物理文件磁盘空间
diskMaxUseSpaceRatio=88
#存储路径
storePathRootDir=/dsp/soft/rocketmq-4.8.0/store
#commitLog存储路径
storePathCommitLog=/dsp/soft/rocketmq-4.8.0/store/commitlog
#消息队列存储路径
storePathComsumeQueue=/dsp/soft/rocketmq-4.8.0/store/consumequeue
#消息索引存储路径
storePathIndex=/dsp/soft/rocketmq-4.8.0/store/index
#checkpoint 文件存储路径
storeCheckpoint=/dsp/soft/rocketmq-4.8.0/store/checkpoint
#abort 文件存储路径
abortFile=/dsp/soft/rocketmq-4.8.0/store/abort
#限制的消息大小
maxMessageSize=65536
#Broker角色
#- ASYNC_MASTER 异步复制Master
#- SYNC_MASTER 同步双写Master
#- SLAVE
brokerRole=SLAVE
#刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH
#发消息线程池数量
sendMessageThreadPoolNums=128
#拉消息线程池数量
pullMessageThreadPoolNums=128
```

### 4.6 修改启动脚本文件

```sh
vi /dsp/soft/rocketmq-4.8.0/bin/runbroker.sh
#根据内存大小进行适当的JVM参数调整
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m"

vi /dsp/soft/rocketmq-4.8.0/bin/runserver.sh
#根据内存大小进行适当的JVM参数调整
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m -xx:MaxMetaspaceSize=320m"
```

### 4.7 服务启动

#### 1)启动NameServer集群

分别在192.168.3.128和192.168.3.130启动NameServer

```sh
cd /dsp/soft/rocketmq-4.8.0/bin
nohup sh mqnamesrv &
```

#### 2)启动Broker集群

在192.168.3.128上启动master1和slave2

```sh
#master1:
cd /dsp/soft/rocketmq-4.8.0/bin
nohup sh mqbroker -c /dsp/soft/rocketmq-4.8.0/conf/2m-noslave/broker-a.properties &

#slave2:
cd /dsp/soft/rocketmq-4.8.0/bin
nohup sh mqbroker -c /dsp/soft/rocketmq-4.8.0/conf/2m-noslave/broker-b-s.properties &
```

在192.168.3.130上启动master2和slave1

```sh
#master2:
cd /dsp/soft/rocketmq-4.8.0/bin
nohup sh mqbroker -c /dsp/soft/rocketmq-4.8.0/conf/2m-noslave/broker-b.properties &

#slave1:
cd /dsp/soft/rocketmq-4.8.0/bin
nohup sh mqbroker -c /dsp/soft/rocketmq-4.8.0/conf/2m-noslave/broker-a-s.properties &
```

### 4.8 查看进程状态

```sh
#jps命令查看存在一下三个进程
NamesrvStartup
BrokerStartup
BrokerStartup
```

###  4.9 查看日志

```sh
#查看nameserver日志
tail -500f ~/logs/rocketmqlogs/namesrv.log 
#查看broker日志
tail -500f ~/logs/rocketmqlogs/broker.log
```

