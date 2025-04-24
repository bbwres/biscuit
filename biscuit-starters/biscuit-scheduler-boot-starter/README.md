# 工程简介
定时任务调度服务，基于quartz实现，目前暂时是通过数据库表锁来解决并发问题。

# 使用说明
 
1. 配置说明：
  ``````
# 配置job存储类型，默认为jdbc
spring.quartz.jdbc.jobStoreType=jdbc  
# 配置数据库表前缀
spring.quartz.jdbc.table-prefix=T_QUARTZ_
  
  ``````
2. 定义一个执行定时任务的类和任务执行的方法，在类上打上注解
```
// spring bean 注解
@Service 
//job 定义
@JobDefinition(jobName = "test",group = "test",targetMethod = "execute", cron = "0/3 * * * * ? *", description = "测试任务")
   
```


# 待实现内容

1. 上一次运行时间，下一次运行时间，运行结果
2. 多节点运行时，时间不同步时，不同节点会重复执行。
3. JobDefinition注解可以在方法上
4. 任务批处理