# Docker Command

1. ActiveMq

```
docker run -d --name activemq -p 61616:61616 -p 8161:8161 islandora/activemq:a0bf5a034c2347191a7e38ed9c37c57b3dfc9d25
```

- 61616是 activemq 的容器使用端口
- 8161是 web 页面管理端口

进入activemq

```
docker exec -it activemq /bin/bash
```

搜索文件并找到

```
find / -name 'jetty-realm.properties'
cd /opt/activemq/conf/
```

修改用户名密码

```
vi jetty-realm.properties
```