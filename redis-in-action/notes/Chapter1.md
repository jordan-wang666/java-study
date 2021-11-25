# Getting to know Redis

> **This chapter covers**
>
>- How Redis is like and unlike other software you’ve used
>- How to use Redis
>- Simple interactions with Redis using example Python code
>- Solving real problems with Redis

## What is Redis?

When I say that Redis is a database, I’m only telling a partial truth. Redis is a very fast non-relational database that
stores a mapping of keys to five different types of values. Redis supports in-memory persistent storage on disk,
replication to scale read performance, and client-side sharding1 to scale write performance. That was a mouthful, but
I’ll break it down by parts.

### Other features 

Redis has two different forms of persistence available for writing in-memory data to disk in a compact format. The first
method is a point-in-time dump either when certain conditions are met (a number of writes in a given period) or when one
of the two dump-to-disk commands is called. The other method uses an append-only file that writes every command that
alters data in Redis to disk as it happens. Depending on how careful you want to be with your data, append-only writing
can be configured to never sync, sync once per second, or sync at the completion of every operation. We’ll discuss these
persistence options in more depth in chapter 4.

Even though Redis is able to perform well, due to its in-memory design there are situations where you may need Redis to
process more read queries than a single Redis server can handle. To support higher rates of read performance (along with
handling failover if the server that Redis is running on crashes), Redis supports master/slave replication where slaves
connect to the master and receive an initial copy of the full database. As writes are performed on the master, they’re
sent to all connected slaves for updating the slave datasets in real time. With continuously updated data on the slaves,
clients can then connect to any slave for reads instead of making requests to the master. We’ll discuss Redis slaves
more thoroughly in chapter 4.

## What Redis data structures look like

|  Structure type   | What it contains  | Structure read/write ability | 
|  ----  | ----  | ---- |
| STRING  | Strings, integers, or floating- point values | Operate on the whole string, parts, increment/ decrement the integers and floats |
| LIST  | Linked list of strings | Push or pop items from both ends, trim based on offsets, read individual or multiple items, find or remove items by value |
| SET  | Unordered collection of unique strings | Add, fetch, or remove individual items, check membership, intersect, union, difference, fetch random items |
| HASH  | Unordered hash table of keys to values | Add, fetch, or remove individual items, fetch the whole hash |
| ZSET (sorted set)  | Ordered mapping of string members to floating-point scores, ordered by score | Add, fetch, or remove individual values, fetch items based on score ranges or member value |

### Strings in Redis

|  Command   | What it does  | 
|  ----  | ----  |
| GET | Fetches the data stored at the given key |
| SET | Sets the value stored at the given key |
| DEL | Deletes the value stored at the given key (works for all types)

```shell
# redis-cli
127.0.0.1:6379> set hello world
OK
127.0.0.1:6379> get hello
"world"
127.0.0.1:6379> del hello 
(integer) 1
127.0.0.1:6379> get hello
(nil)
127.0.0.1:6379> 
```

### Lists in Redis

|  Command   | What it does  | 
|  ----  | ----  |
| LPUSH | Pushes the value onto the left end of the list |
| RPUSH | Pushes the value onto the right end of the list |
| LRANGE | Fetches a range of values from the list |
| LINDEX | Fetches an item at a given position in the list |
| LPOP | Pops the value from the left end of the list and returns it |
| RPOP | Pops the value from the right end of the list and returns it |

```shell
127.0.0.1:6379> del list-key
(integer) 1
127.0.0.1:6379> lrange list-key 0 -1
(empty array)
127.0.0.1:6379> lpush list-key item1
(integer) 1
127.0.0.1:6379> lpush list-key item2
(integer) 2
127.0.0.1:6379> lpush list-key item3
(integer) 3
127.0.0.1:6379> lpush list-key item4
(integer) 4
127.0.0.1:6379> lrange list-key 0 -1
1) "item4"
2) "item3"
3) "item2"
4) "item1"
127.0.0.1:6379> lindex list-key 1
"item3"
127.0.0.1:6379> lindex list-key 0
"item4"
127.0.0.1:6379> lpop list-key
"item4"
127.0.0.1:6379> rpop list-key
"item1"
127.0.0.1:6379> lrange list-key 0 -1
1) "item3"
2) "item2"
```

### Sets in Redis

|  Command   | What it does  | 
|  ----  | ----  |
| SADD | Adds the item to the set |
| SMEMBERS | Returns the entire set of items |
| SISMEMBER | Checks if an item is in the set |
| SREM | Removes the item from the set, if it exists |

```shell
127.0.0.1:6379> sadd set-key item1
(integer) 1
127.0.0.1:6379> sadd set-key item1
(integer) 0
127.0.0.1:6379> sadd set-key item2
(integer) 1
127.0.0.1:6379> sadd set-key item3
(integer) 1
127.0.0.1:6379> sadd set-key item4
(integer) 1
127.0.0.1:6379> smembers set-key
1) "item3"
2) "item1"
3) "item4"
4) "item2"
127.0.0.1:6379> smembers set-key
1) "item3"
2) "item1"
3) "item4"
4) "item2"
127.0.0.1:6379> sismember set-key item5
(integer) 0
127.0.0.1:6379> sismember set-key item4
(integer) 1
127.0.0.1:6379> srem set-key item2
(integer) 1
127.0.0.1:6379> srem set-key item2
(integer) 0
```

### Hashes in Redis

|  Command   | What it does  | 
|  ----  | ----  |
| HSET | Stores the value at the key in the hash |
| HGET | Fetches the value at the given hash key |
| HGETALL | Fetches the entire hash |
| HDEL | Removes a key from the hash, if it exists |

```shell
127.0.0.1:6379> hset hash-key sub-key1 value1
(integer) 1
127.0.0.1:6379> hset hash-key sub-key2 value2
(integer) 1
127.0.0.1:6379> hset hash-key sub-key2 value2
(integer) 0
127.0.0.1:6379> hgetall hash-key
1) "sub-key1"
2) "value1"
3) "sub-key2"
4) "value2"
127.0.0.1:6379> hdel hash-key sub-key2
(integer) 1
127.0.0.1:6379> hdel hash-key sub-key2
(integer) 0
127.0.0.1:6379> hget hash-key sub-key1
"value1"
127.0.0.1:6379> hgetall hash-key
1) "sub-key1"
2) "value1"
```

### Sorted sets in Redis

|  Command   | What it does  | 
|  ----  | ----  |
| ZADD | Adds member with the given score to the ZSET |
| ZRANGE | Fetches the items in the ZSET from their positions in sorted order |
| ZRANGEBYSCORE | Fetches items in the ZSET based on a range of scores |
| ZREM | Removes the item from the ZSET, if it exists |

```shell
127.0.0.1:6379> zadd zset-key 728 member1
(integer) 1
127.0.0.1:6379> zadd zset-key 982 member2
(integer) 1
127.0.0.1:6379> zadd zset-key 982 member2
(integer) 0
127.0.0.1:6379> zrange zset-key 0 -1
1) "member1"
2) "member2"
127.0.0.1:6379> zrange zset-key 0 -1 withscores
1) "member1"
2) "728"
3) "member2"
4) "982"
127.0.0.1:6379> zrangebyscore zset-key 0 800
1) "member1"
127.0.0.1:6379> zrangebyscore zset-key 0 800 withscores
1) "member1"
2) "728"
127.0.0.1:6379> zrem zset-key member1
(integer) 1
127.0.0.1:6379> zrem zset-key member1
(integer) 0
127.0.0.1:6379> zrange zset-key 0 -1 withscores
1) "member2"
2) "982"
```