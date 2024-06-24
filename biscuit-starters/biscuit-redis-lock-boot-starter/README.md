# 工程简介

    基于redisson的分布式锁实现,扩展了redisson的配置，并增加注解支持

# 使用方式

1.引入jar包

```
<dependency>
     <groupId>cn.bbwres</groupId>
     <artifactId>biscuit-redis-lock-boot-starter</artifactId>
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

## DistributedLock 注解说明:

| 字段         | 说明                                      |
|------------|-----------------------------------------|
| timeout    | 设置锁的超时时间，不设置的话默认为0，则为不限制                |
| timeUnit   | 超时时间的单位，默认为秒                            |
| key        | 加锁的key，默认为spel表达式执行之后的值，也可以为固定字符串       |
| keyUseSpEL | key的解析规则是否使用spel，默认使用。为false时，key为固定字符串 |

默认的情况下，只要方法上面有DistributedLock注解，那么就是对该方法加锁

## jdk 1.8之前 通过反射获取到的方法参数名称为arg0，arg1 之类的 ，jdk1.8之后 在类编译时需要增加-parameters参数，之后才能使得反射可以获取方法的参数名称
### 1. maven 中可以使用以下配置 自动编译的时候加上 -parameters
````xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.3</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
````

### 2. spring boot 下增加spring-boot-maven-plugin时也可以 自动编译的时候加上 -parameters
````xml

<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>repackage</id>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
````
### 3. 为了防止获取不到参数名称 因此增加注解cn.bbwres.biscuit.redis.lock.annotations.DistributedLockParam,方法的参数使用该注解来标明方法的参数名称，该注解标记的名称有最高优先级