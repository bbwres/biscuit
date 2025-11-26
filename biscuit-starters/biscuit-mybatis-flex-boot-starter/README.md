### 基于mybatis-flex的starter类  

#### 实现了以下功能：

* 枚举类的自动转换
* 默认字段的填充
* 自动增加MapperScan注解 多个路径时用逗号隔开, 只扫描有Mapper注解的接口
* 租户插件


##### !!!!!  mybatis-flex 是基于provider去做了增强的实现处理，因此不会增强@Select和xml的sql

### 配置项说明
```yaml
# mybatis 配置
mybatis-config:
  tenant:
    enabled: true # 启动多租户配置
    ignore-tenant-tables:
      - t_oauth_client_details
  mapper:
    base-packages: cn.bbwres.biscuit.module.basic.dao
  user-info: cn.bbwres.biscuit.web.supplier.UserInfoSupplier

# mybatis-flex 配置
mybatis-flex:
  mapper-locations: classpath*:mapper/*.xml
  configuration:
    #    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
    cache-enabled: false
    local-cache-scope: statement
  global-config:
    print-banner: false
```