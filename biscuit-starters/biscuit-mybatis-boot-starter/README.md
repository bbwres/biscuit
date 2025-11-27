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

### 多数据源支持
```yaml
spring:
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://xx.xx.xx.xx:3306/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_1:
          url: jdbc:mysql://xx.xx.xx.xx:3307/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_2:
          url: ENC(xxxxx)
          username: ENC(xxxxx)
          password: ENC(xxxxx)
          driver-class-name: com.mysql.jdbc.Driver
```
```java
@Service
@DS("slave")
public class UserServiceImpl implements UserService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  @DS("slave_1")
  public List selectByCondition() {
    return jdbcTemplate.queryForList("select * from user where age >10");
  }
}
```