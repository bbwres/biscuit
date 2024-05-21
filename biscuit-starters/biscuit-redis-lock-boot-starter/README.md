# 工程简介

    基于redisson的分布式锁实现,扩展了redisson的配置，并增加注解支持

# 使用方式

1.引入jar包

```
<dependency>
     <groupId>com.lightning</groupId>
     <artifactId>boot-redis-lock-start</artifactId>
     <version>${project.version}</version>
 </dependency>
```

2.在接口方法上增加注解cn.bbwres.biscuit.redis.lock.annotations.DistributedLock,并指定加锁的key
例如：

```
    //固定字符串key
    @DistributedLock(key="test",keyUseSpEL=false)
    public String createOrder(){
        //TODO
    }    
    //spel 解析key
    @DistributedLock(key = "'redis_lock_key_'+#roleId")
    public String createOrder(String roleId) {
        //TODO
    }
```

DistributedLock 注解说明:

| 字段         | 说明                                      |
|------------|-----------------------------------------|
| timeout    | 设置锁的超时时间，不设置的话默认为0，则为不限制                |
| timeUnit   | 超时时间的单位，默认为秒                            |
| key        | 加锁的key，默认为spel表达式执行之后的值，也可以为固定字符串       |
| keyUseSpEL | key的解析规则是否使用spel，默认使用。为false时，key为固定字符串 |

默认的情况下，只要方法上面有DistributedLock注解，那么就是对该方法加锁