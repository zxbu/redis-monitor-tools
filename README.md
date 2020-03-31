# redis-monitor-tools
redis monitor 分析工具，可以通过采样列出运行时使用频率最高的key和前缀。注意：运行期间会对redis服务端的读写性能会产生影响，因此如在生产环境使用，建议在业务低峰期，充分评估风险。具体可参考：https://redis.io/commands/monitor
## 怎么使用
### 1. 编译
```bash
git clone git@github.com:zxbu/redis-monitor-tools.git
mvn package
```
或者直接下载
```bash
wget https://github.com/zxbu/redis-monitor-tools/releases/download/1.0.0/redis-monitor.jar
```
### 2. 运行
```bash
java -jar redis-monitor.jar --redis.command.servers=127.0.0.1:6378,127.0.0.1:6379  --redis.command.numbers=1000 --redis.command.top=20

redis.command.servers: 可以填多个redis节点地址，用逗号分隔
redis.command.numbers: 最多纪录的monitor数量
redis.command.top: 排行榜Top数量
```
运行结束后，自动输出结果。
```bash
----- Count -----
total : 100
'127.0.0.1:6378' : 58 ( 58.00% )
'127.0.0.1:6379' : 42 ( 42.00% )


----- Top keys -----
'Product:Add' : 32 ( 32.00% )
'Order:Search' : 20 ( 20.00% )
'Foo' : 14 ( 14.00% )
'Product:Delete' : 13 ( 13.00% )


----- Top actions -----
'GET' : 80 ( 88.00% )
'SSCAN' : 1 ( 1.00% )


----- Top prefixes -----
'Product' : 72 ( 72.00% )
'Order' : 1 ( 1.00% )
```

## How to use

### 1. Building
```bash
git clone git@github.com:zxbu/redis-monitor-tools.git
mvn package
```
or download
```bash
wget https://github.com/zxbu/redis-monitor-tools/releases/download/1.0.0/redis-monitor.jar
```
### 2. Run
```bash
java -jar redis-monitor.jar --redis.command.servers=127.0.0.1:6378,127.0.0.1:6379  --redis.command.numbers=1000 --redis.command.top=20

redis.command.servers: host1:port1,host2:port2. the redis server multiple address
redis.command.numbers: the max monitor number was recorder
redis.command.top: the top number
```

auto echo result when end
```bash
----- Count -----
total : 100
'127.0.0.1:6378' : 58 ( 58.00% )
'127.0.0.1:6379' : 42 ( 42.00% )


----- Top keys -----
'Product:Add' : 32 ( 32.00% )
'Order:Search' : 20 ( 20.00% )
'Foo' : 14 ( 14.00% )
'Product:Delete' : 13 ( 13.00% )


----- Top actions -----
'GET' : 80 ( 88.00% )
'SSCAN' : 1 ( 1.00% )


----- Top prefixes -----
'Product' : 72 ( 72.00% )
'Order' : 1 ( 1.00% )
```
