# CentOS7 安装 RabbitMQ
## 1.安装依赖
```shell
yum -y install gcc glibc-devel make ncurses-devel openssl-devel xmlto perl wget gtk2-devel binutils-devel xz
```
## 2.安装erlang
```shell
//1.下载(官网:https://www.erlang.org/downloads)
wget http://erlang.org/download/otp_src_22.0.tar.gz
//2.解压
tar -zxvf otp_src_22.0.tar.gz
//3.移动解压目录
mv otp_src_22.0 /usr/local/
//4.切换到解压目录
cd /usr/local/otp_src_22.0/
//5.创建安装目录
mkdir ../erlang
//6.配置安装路径
./configure --prefix=/usr/local/erlang
//7.安装
make install
//8.添加环境变量并刷新
echo 'export PATH=$PATH:/usr/local/erlang/bin' >> /etc/profile
source /etc/profile
//9.验证安装是否成功
erl
//10.退出erlang
halt().
```
## 3.安装RabbitMQ
```shell
//1.下载(地址:https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.7.15)
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.15/rabbitmq-server-generic-unix-3.7.15.tar.xz
//2.第一次解压
/bin/xz -d rabbitmq-server-generic-unix-3.7.15.tar.xz
//3.第二次解压
tar -xvf rabbitmq-server-generic-unix-3.7.15.tar
//4.移动解压目录
mv rabbitmq_server-3.7.15/ /usr/local/
//5.安装目录改名
mv /usr/local/rabbitmq_server-3.7.15  /usr/local/rabbitmq
//6.配置环境变量并刷新
echo 'export PATH=$PATH:/usr/local/rabbitmq/sbin' >> /etc/profile
source /etc/profile
//7.创建配置目录
mkdir /etc/rabbitmq
//8.启动、停止、查状态
rabbitmq-server -detached
rabbitmqctl stop
rabbitmqctl status
//服务启动后开启5672和15672端口）
//9.开启web插件
rabbitmq-plugins enable rabbitmq_management
//访问地址 http://127.0.0.1:15672/ 默认账号密码：guest guest（这个账号只允许本机访问）
//10.用户管理

//10.1 查看所有用户
rabbitmqctl list_users

//10.2 添加一个用户
rabbitmqctl add_user dsp 123456

//10.3 配置权限
rabbitmqctl set_permissions -p "/" dsp ".*" ".*" ".*"

//10.4 查看用户权限
rabbitmqctl list_user_permissions dsp

//10.5 设置tag
rabbitmqctl set_user_tags dsp administrator

//10.6 删除用户（安全起见，删除默认用户）
rabbitmqctl delete_user guest

```