```
$ docker network create cassandra-net
$ docker run --name my-cassandra --network cassandra-net \
         -p 9042:9042 -d cassandra:latest
```

```
$ docker run -it --network cassandra-net --rm cassandra cqlsh my-cassandra
cqlsh> create keyspace tacocloud 
WITH replication = {'class': ’SimpleStrategy', 'replication_factor' : 1};
```