# 工程简介

    基于spring cache 的redis 启动包，支持自定义redis value 的序列化。 支持自定义的缓存时间

# 使用方式

1.引入jar包

```
<dependency>
     <groupId>cn.bbwres</groupId>
     <artifactId>biscuit-redis-cache-boot-starter</artifactId>
     <version>${project.version}</version>
 </dependency>
```

2.参数配置

```properties
## 配置 获取缓存时间的分隔符,默认为#
biscuit.redis.delimit-symbol="#"
##是否启用json序列化value
biscuit.redis.enable-json-serializer-value=true
```

3.示例
````java
/**
 * cacheName为demoCache，缓存时间为3600秒
 */
@Cacheable(cacheNames = "demoCache#3600", key = "#id")
````
