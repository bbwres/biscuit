实现基于雪花算法的id生成器
----
依赖的外部组件
----

* redis

----
依赖的jar包
-----

* spring-data-redis

-----------
使用方式
---------

1. 引入jar包

```
<dependency>
   <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-id-generator-boot-start</artifactId>
     <version>${project.version}</version>
 </dependency>

```

2.配置redis

3.使用SnowflakeGenerator 来生成id

```
Long id  = snowflakeGenerator.nextId();
//生成带有前缀的id
String id  = snowflakeGenerator.nextId("前缀");
```

4.配置说明

```
1.应用启动时，系统会自动根据累加存入redis中的机器号来避免机器号的重复
2.可以通过配置id.gen.machineId 来设置机器的编码和id.gen.datacenterId数据中心的编码

```

# 实现基于redis的自增id

