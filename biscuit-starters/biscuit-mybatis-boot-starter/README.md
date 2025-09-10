### 基于mybatis-plus的starter类  

#### 实现了以下功能：

* 枚举类的自动转换
* 默认字段的填充
* 自动增加MapperScan注解 多个路径时用逗号隔开, 只扫描有Mapper注解的接口
* 租户插件


### 配置项说明
```yaml
mybatis-plus:
  ## 设置mapper的xml扫描路径
  mapper-locations: classpath*:mapper/*.xml
  mapper:
    ## 设置 dao接口的路径
    base-packages: .xx.dao
  ## 是否启用定制化的处理，默认为true。 开启之后会自动注入cn.bbwres.biscuit.mybatis.handler.BiscuitMybatisEnumTypeHandler 枚举处理类
  enable-customize: true
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
    map-underscore-to-camel-case: true
    cache-enabled: false
    local-cache-scope: statement
```