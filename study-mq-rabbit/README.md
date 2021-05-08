# spring-boot-study-rate-limit-redis

> Spring Boot 集成 RabbitMQ，并且演示了基于直接队列模式、分列模式、主题模式、延迟队列的消息发送和接收。

## 使用 Docker 安装 RabbitMQ 镜像集群，达到高可用
> Rabbitmq普通集群模式，是将交换机、绑定、队列的元数据复制到集群里的任何一个节点，但队列内容只存在于特定的节点中，客户端通过连接集群中任意一个节点，即可以生产和消费集群中的任何队列内容（因为每个节点都有集群中所有队列的元数据信息，如果队列内容不在本节点，则本节点会从远程节点获取内容，然后提供给消费者消费）。

> 从该模式不难看出，普通集群可以让不同的繁忙队列从属于不同的节点，这样可以减轻单节点的压力，提升吞吐量，但是普通集群不能保证队列的高可用性，因为一旦队列所在节点宕机直接导致该队列无法使用，只能等待重启，所以要想在队列节点宕机或故障也能正常使用，就要复制队列内容到集群里的每个节点，需要创建镜像队列。
### 环境
- 操作系统：CentOS7
- Docker版本：1.13.1

### 搭建 RabbitMQ 普通集群
> RabbitMQ 镜像集群依赖于普通集群，所以需要先搭建 RabbitMQ 普通集群。
#### 1、Docker 拉取带 management tag 的最新版本,我这里最新版本是3.8.16

``docker pull rabbitmq:management``
#### 2、启动三个同样cookie的rabbitmq实例（同样的cookie才能加入集群）。这里注意使用 `--link` 将容器实例之前网络联系起来

启动可能会出现以下警告：
```shell script
RABBITMQ_ERLANG_COOKIE env variable support is deprecated and will be REMOVED in a future version. Use the $HOME/.erlang.cookie file or the --erlang-cookie switch instead.
```
有些特殊的情况，比如已经运行了一段时间的几个单个物理机，我们在之前没有设置过相同的 Erlang Cookie 值，现在我们要把单个的物理机部署成集群，实现我们需要同步 Erlang 的 Cookie 值。
因为 RabbitMQ 是用 Erlang 实现的，Erlang Cookie 相当于不同节点之间相互通讯的秘钥，Erlang 节点通过交换 Erlang Cookie 获得认证。
要想知道 Erlang Cookie 位置，首先要取得 RabbitMQ 启动日志里面的 home dir 路径，作为根路径。使用：`docker logs 容器名称` 查看，如下：
```shell script
  Starting broker...2021-05-07 07:47:06.715 [info] <0.273.0> 
 node           : rabbit@rabbit1
 home dir       : /var/lib/rabbitmq
 config file(s) : /etc/rabbitmq/rabbitmq.conf
 cookie hash    : l7FRc4s6MFrXQLBiUlLnOA==
 log(s)         : <stdout>
 database dir   : /var/lib/rabbitmq/mnesia/rabbit@rabbit1
```
所以 Erlang Cookie 的全部路径就是“/var/lib/rabbitmq/.erlang.cookie”。把 cookie 加入文件中，需要提前给文件加权限。
```shell script
cd /var/lib/
mkdir rabbitmq
cd rabbitmq
touch .erlang.cookie
chmod 600 .erlang.cookie 
vim .erlang.cookie
```

```shell script
docker run -d --hostname rabbit1 --name rabbit1 -p 15672:15672 -p 5672:5672 -v /var/lib/rabbitmq:/var/lib/rabbitmq -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit2 --name rabbit2 -p 15673:15672 -p 5673:5672 -v /var/lib/rabbitmq:/var/lib/rabbitmq --link rabbit1:rabbit1 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit3 --name rabbit3 -p 15674:15672 -p 5674:5672 -v /var/lib/rabbitmq:/var/lib/rabbitmq --link rabbit1:rabbit1 --link rabbit2:rabbit2 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
```
#### 3、设置第二和第三个实例，使他们加入集群

```shell script
docker exec -it rabbit1 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app
exit
 
docker exec -it rabbit2 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster  rabbit@rabbit1
rabbitmqctl start_app
exit
 
 
docker exec -it rabbit3 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster  rabbit@rabbit1
rabbitmqctl start_app
exit
```

#### 4、安装延迟队列插件
##### 4.1、插件下载地址：https://www.rabbitmq.com/community-plugins.html
```shell script
wget https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.8.0/rabbitmq_delayed_message_exchange-3.8.0.ez
```
#### 4.2、执行以下命令，将插件拷贝到 RabbitMQ 容器内 plugins 目录下
```shell script
docker cp /data/install_package/rabbitmq_delayed_message_exchange-3.8.0.ez rabbit1:/plugins
docker cp /data/install_package/rabbitmq_delayed_message_exchange-3.8.0.ez rabbit2:/plugins
docker cp /data/install_package/rabbitmq_delayed_message_exchange-3.8.0.ez rabbit3:/plugins
```
#### 4.3、依次进入容器，启用插件
```shell script
docker exec -it rabbit1 /bin/bash
rabbitmq-plugins enable rabbitmq_delayed_message_exchange
```

#### 5、进入控制台 web 界面查看 http://ip:15672 , 可以发现普通集群搭建成功（初始账号密码都是 guest）

![image-20210508092741607](https://gitee.com/BigYoungZhao/typora/raw/master/image-20210508092741607.png)

#### 6、搭建 RabbitMQ 镜像集群
> 搭建镜像集群是在 web 控制台完成的，主要操作就是在 Admin 界面添加一个 Policy 。具体参数如下图所示：

![image-20210508094414885](https://gitee.com/BigYoungZhao/typora/raw/master/image-20210508094414885.png)

![image-20210508094544667](https://gitee.com/BigYoungZhao/typora/raw/master/image-20210508094544667.png)

参数含义：
 - pattern: 队列名字的通配符
 - ha-mode：镜像队列提供了三种模式：
   - all：全部的节点队列都做镜像
   - exactly：指定镜像队列的节点最高镜像数量
   - nodes：只为指定具体节点配置镜像队列
 - ha-sync-mode ：节点之前的同步模式。有自动和手动两种，默认是手动，这里设置为自动。
 
###### 设置完成并添加了这个策略后，新建的和已存在的队列默认会支持此策略。
 
 
#### 7、至此，RabbitMQ 镜像集群已搭建成功。 
 
## 参考
1. 集群搭建教程：https://blog.csdn.net/zheyimiao/article/details/108436932
2. RabbitMQ 官网：http://www.rabbitmq.com/