```shell
创建并启动
    docker run --name mongodb -p 27017:27017 -d mongo
```

```shell
进入mongo
    docker exec -it containerId mongo
创建用户
    db.createUser({ user:'admin',pwd:'123456',roles:[ { role:'userAdminAnyDatabase', db: 'admin'}]});
认证
    db.auth('admin', '123456')

```

```shell
use databaseName
异常
    Command failed with error 18 (AuthenticationFailed): ‘Authentication failed.
解决
    权限问题，新的数据库需要创建对应的用户
创建对应用户
    db.createUser({user:"xxx",pwd:"123456",roles:[{role:"dbOwner",db:"databaseName"}]})
```