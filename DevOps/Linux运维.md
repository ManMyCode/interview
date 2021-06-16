# Linux运维

## 1、常用指令

### 1.1、防火墙操作

```shell
#centos7
#查看状态
systemctl status firewalld
#开启防火墙
systemctl start firewalld
#关闭防火墙
systemctl stop firewalld
# 查看所有永久开放的端口（默认为空）
firewall-cmd --list-ports --permanent
# 添加临时开放端口（例如：比如我修改ssh远程连接端口是223，则需要开放这个端口）
firewall-cmd --add-port=223/tcp
# 添加永久开放的端口（例如：223端口）
firewall-cmd --add-port=223/tcp --permanent
# 关闭临时端口
firewall-cmd --remove-port=80/tcp
# 关闭永久端口
firewll-cmd --remove-port=80/tcp --permanent
# 配置结束后需要输入重载命令并重启防火墙以生效配置
firewall-cmd --reload
systemctl restart firewalld
```

### 1.2、文件传输

```shell
#从远程传输文件到本地
scp -P [ssh端口] root@远程ip:/文件路径  本地存放路径
#本地文件传输到远程服务器
scp 本地文件路径 root@远程服务器ip:/远程服务器文件存放路径
```

### 1.3、查找/替换

```shell
#查找文件中的字符串
grep '要查找的字符串' 要查找的文件路径
#查找文件中的字符串(匹配后和它后面的n行)
grep -A100 '要查找的字符串' 要查找的文件路径
#查找文件中的字符串(匹配行和它前面的n行)
grep -B100 '要查找的字符串' 要查找的文件路径
#查找文件中的字符串(匹配行和它前后各n行)
grep -C100 '要查找的字符串' 要查找的文件路径
#替换文件中的字符串
sed -i 's/原字符串/要替换的字符串/g' 目标文件路径
```

### 1.4、rpm安装及卸载

```shell
#rpm包安装
rpm -ivh rpm包路径
#显示安装包信息
rpm -qip rpm包路径
#查看/bin/df 文件所在安装包的信息
rpm -qif /bin/df
# 查看/bin/df 文件所在安装包中的各个文件分别被安装到哪个目录下
rpm -qlf /bin/df
#rpm包卸载
rpm -e 需要卸载的安装包

```



## 2、基础运维

### 2.1、Cpu

主要关注平均负载（Load Average）,CPU使用率，上下文切换次数（Context Switch）

通过top命令可以查看系统平均负载和CPU使用率，如下：

![](.\images\top.png)

平均负载有三个数字：63.66，58.39，57.18，分别表示过去 1 分钟、5 分钟、15 分钟机器的负载。按照经验，若数值小于 0.7*CPU 个数，则系统工作正常；若超过这个值，甚至达到 CPU 核数的四五倍，则系统的负载就明显偏高。

vmstat命令可以查看CPU的上下文切换次数：

安装：`yum install -y sysstat`

![](.\images\vmstat_1.png)

```sh
vmstat 1 10  #1表示每秒采集一次服务器状态，10表示只采集10次。
```

- [ ] | 类别       | 参数说明                                                     |
  | ---------- | ------------------------------------------------------------ |
  | **procs**  | **r:**等待执行的任务数（`` 展示了正在执行和等待cpu资源的任务个数，当这个值超过cpu个数，就会出现cpu瓶颈 ``）<br />**b:**等待IO的进程数<br /> |
  | **memory** | **swpd:**正在使用的虚拟内存大小，单位为k<br />**free:**空闲内存大小<br />**buff:**已用的buff大小，对块设备的读写进行缓冲<br />**cache:**已用的cache大小，文件系统的cache<br />**inact:**非活跃内存大小（即被标明可回收的内存，区别于free和active，当使用-a选项时显示）<br />**active:**活跃的内存大小（当使用-a选项时显示）<br /> |
  | **swap**   | **si:**每秒从交换区写入内存的大小（单位：kb/s）<br />**so**:每秒从内存写到交换区的大小<br /> |
  | **io**     | **bi**:每秒读取的块数（读磁盘）块设备每秒接收的块数量，单位是block，这里的块设备是指系统上所有的磁盘和其他块设备，现在的Linux版本块的大小为1024bytes<br />**bo**:每秒写入的块数（写磁盘）块设备每秒发送的块数量，单位是block<br /> |
  | **system** | **in**:每秒中断数，包括时钟中断（``值越大，会看到由内核消耗的cpu时间sy会越多``）<br />**cs**:每秒上下文切换数（``秒上下文切换次数，例如我们调用系统函数，就要进行上下文切换，线程的切换，也要进程上下文切换，这个值要越小越好，太大了，要考虑调低线程或者进程的数目``）<br /> |
  | **cpu**    | **us**:用户进程执行消耗cpu时间(user time)（us的值比较高时，说明用户进程消耗的cpu时间多，``但是如果长期超过50%的使用，那么我们就该考虑优化程序算法或其他措施了``）<br />**sy**:系统进程消耗cpu时间(system time)（sys的值过高时，说明系统内核消耗的cpu资源多，这个不是良性的表现，我们应该检查原因。``这里us + sy的参考值为80%，如果us+sy 大于 80%说明可能存在CPU不足``）<br />**id**:空闲时间(包括IO等待时间)（一般来说 us+sy+id=100）<br />wa:等待IO时间（wa过高时，说明io等待比较严重，这可能是由于磁盘大量随机访问造成的，也有可能是磁盘的带宽出现瓶颈。）<br /> |



### 2.2、Memory

可以使用 free –m 命令查看内存的使用情况。

通过 top 命令可以查看进程使用的虚拟内存 VIRT 和物理内存 RES，根据公式 VIRT = SWAP + RES 可以推算出具体应用使用的交换分区（Swap）情况，使用交换分区过大会影响 Java 应用性能，可以将 swappiness 值调到尽可能小。

### 2.3、 I/O

I/O 包括磁盘 I/O 和网络 I/O，一般情况下磁盘更容易出现 I/O 瓶颈。通过 iostat 可以查看磁盘的读写情况，通过 CPU 的 I/O wait 可以看出磁盘 I/O 是否正常。

如果磁盘 I/O 一直处于很高的状态，说明磁盘太慢或故障，成为了性能瓶颈，需要进行应用优化或者磁盘更换。

除了常用的 top、 ps、vmstat、iostat 等命令，还有其他 Linux 工具可以诊断系统问题，如 mpstat、tcpdump、netstat、pidstat、sar 等。

### 2.4、java应用诊断



#### 2.4.1、jstack

jstack 命令通常配合 top 使用，通过 top -H -p pid 定位 Java 进程和线程，再利用 jstack -l pid 导出线程栈。由于线程栈是瞬态的，因此需要多次 dump，一般 3 次 dump，一般每次隔 5s 就行。将 top 定位的 Java 线程 pid 转成 16 进制，得到 Java 线程栈中的 nid，可以找到对应的问题线程栈。

#### 2.4.2、jstat

jstat 命令可打印 GC 详细信息，Young GC 和 Full GC 次数，堆信息等。其命令格式为jstat –gcxxx -t pid

![image-20210615222003904](.\images\jstat.png)

```
S0C：年轻代中第一个survivor（幸存区）的容量 (字节)         
S1C：年轻代中第二个survivor（幸存区）的容量 (字节)         
S0U：年轻代中第一个survivor（幸存区）目前已使用空间 (字节)         
 
S1U：年轻代中第二个survivor（幸存区）目前已使用空间 (字节)         
EC：年轻代中Eden（伊甸园）的容量 (字节)         
EU：年轻代中Eden（伊甸园）目前已使用空间 (字节)         
OC：Old代的容量 (字节)         
OU：Old代目前已使用空间 (字节)         
PC：Perm(持久代)的容量 (字节)         
PU：Perm(持久代)目前已使用空间 (字节)         
YGC：从应用程序启动到采样时年轻代中gc次数         
YGCT：从应用程序启动到采样时年轻代中gc所用时间(s)         
FGC：从应用程序启动到采样时old代(全gc)gc次数         
FGCT：从应用程序启动到采样时old代(全gc)gc所用时间(s)         
GCT：从应用程序启动到采样时gc用的总时间(s)         
NGCMN：年轻代(young)中初始化(最小)的大小 (字节)         
NGCMX：年轻代(young)的最大容量 (字节)         
NGC：年轻代(young)中当前的容量 (字节)         
OGCMN：old代中初始化(最小)的大小 (字节)         
OGCMX：old代的最大容量 (字节)         
OGC：old代当前新生成的容量 (字节)         
PGCMN：perm代中初始化(最小)的大小 (字节)         
PGCMX：perm代的最大容量 (字节)           
PGC：perm代当前新生成的容量 (字节)         
S0：年轻代中第一个survivor（幸存区）已使用的占当前容量百分比         
S1：年轻代中第二个survivor（幸存区）已使用的占当前容量百分比         
E：年轻代中Eden（伊甸园）已使用的占当前容量百分比         
O：old代已使用的占当前容量百分比         
P：perm代已使用的占当前容量百分比         
S0CMX：年轻代中第一个survivor（幸存区）的最大容量 (字节)         
S1CMX ：年轻代中第二个survivor（幸存区）的最大容量 (字节)         
ECMX：年轻代中Eden（伊甸园）的最大容量 (字节)         
DSS：当前需要survivor（幸存区）的容量 (字节)（Eden区已满）         
TT： 持有次数限制         
MTT ： 最大持有次数限制
```



#### 2.4.3、jmap

jmap 打印 Java 进程堆信息 jmap –heap pid。通过 jmap -dump:live,format=b,file=./dump20210318.dat [pid] 可 dump 堆到文件，然后通过其它工具进一步分析其堆使用情况

jdk自带工具查看堆对象情况：
jmap -histo pid | sort -n -r -k 2 | head -10
