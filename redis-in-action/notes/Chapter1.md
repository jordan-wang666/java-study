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