# Kafka

### 下载zookeeper并运行

```
docker pull zookeeper
docker run -d --name zookeeper -p 2181:2181 -t zookeeper
```

### 进入zookeeper

```
docker exec -it zookeeper /bin/sh
```

### 下载Kafka并运行

```
docker run  -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=192.168.51.183:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.51.183:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t fogsyio/kafka:2.2.0
```

这里面主要设置了4个参数 KAFKA_BROKER_ID=0 KAFKA_ZOOKEEPER_CONNECT=192.168.51.183:2181 KAFKA_ADVERTISED_LISTENERS=PLAINTEXT:
//192.168.51.183:9092 KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092
中间两个参数的192.168.204.128改为宿主机器的IP地址，如果不这么设置，可能会导致在别的机器上访问不到kafka。

### 测试kafka 进入kafka容器的命令行

```
docker exec -it kafka /bin/bash
```