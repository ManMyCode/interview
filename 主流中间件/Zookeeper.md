## Zookeeper集群搭建



### 1、安装JDK

### 2、将zookeeper安装包传到服务器上

将zoo_sample.cfg文件改名为zoo.cfg

### 3、建立集群目录,并进行配置

```sh
mkdir /dsp/soft/zookeeper-cluster/zookeeper-1
mkdir /dsp/soft/zookeeper-cluster/zookeeper-2
mkdir /dsp/soft/zookeeper-cluster/zookeeper-3

# 修改 /dsp/soft/zookeeper-cluster/zookeeper-1/conf/zoo.cfg
clientPort=2181
dataDir=/dsp/soft/zookeeper-cluster/zookeeper-1/data

# 修改 /dsp/soft/zookeeper-cluster/zookeeper-2/conf/zoo.cfg
clientPort=2182
dataDir=/dsp/soft/zookeeper-cluster/zookeeper-2/data

# 修改 /dsp/soft/zookeeper-cluster/zookeeper-3/conf/zoo.cfg
clientPort=2183
dataDir=/dsp/soft/zookeeper-cluster/zookeeper-3/data

```

### 4、配置集群

1、在每个zookeeper的data目录下创建一个myid文件，内容分别为1、2、3,。改文件记录者服务器的ID。

2、在每个zookeeper的zoo.cfg配置客户端访问端口（clientPort）和集群服务器IP列表

```sh
#server.服务器ID=服务器IP地址:服务器间通信端口：服务器之间投票选举端口
server.1=192.168.3.128:2881:3881
server.2=192.168.3.128:2882:3882
server.3=192.168.3.128:2883:3883
```



### 5、启动集群

```sh
#分别启动每个实例
/dsp/soft/zookeeper-cluster/zookeeper-1/bin/zkserver.sh start
/dsp/soft/zookeeper-cluster/zookeeper-2/bin/zkserver.sh start
/dsp/soft/zookeeper-cluster/zookeeper-3/bin/zkserver.sh start
#查看节点是主节点还是从节点
/dsp/soft/zookeeper-cluster/zookeeper-1/bin/zkserver.sh status
```

