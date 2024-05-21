# biscuit-mongodb-boot-start

# 简介

## 1. 介绍

biscuit-mongodb-boot-start 是基于 spring-data-mongodb 框架的 mongodb 启动器。实现了mongodb的通用增删改查功能。

## 2. 特性

- 通用增删改查
- 通用分页

## 3. 使用

### 3.1 添加依赖

```xml

<dependency>
    <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-mongodb-boot-start</artifactId>
    <version>${project.version}</version>
</dependency>

```

### 3.2 添加配置

* 配置mongodb链接信息:

```properties
#mongodb
spring.data.mongodb.uri=mongodb://localhost:27017/test
## 是否去掉spring生成的_class字段,默认为false。去掉后，mongodb中存储的文档将不包含_class字段
spring.data.mongodb.remove.class=false
## 自动创建索引---生产可能需要关闭
spring.data.mongodb.auto-index-creation=true
## mongodb dao层 扫描包路径
spring.data.mongodb.repositories.base-packages=xxx.xxx.xx.xx
```

### 3.3 实现类

* dao层需要继承ExtendMongoRepository接口。 dao层代码示例:

```java

/**
 * 继承ExtendMongoRepository 接口，并指定泛型操作的对象和对象主键id的类型
 * @author zhanglinfeng12
 */
public interface BusinessLoggerMongodbDao extends ExtendMongoRepository<BusinessLoggerEntity, String> {


    /**
     * 查询示例
     *  根据业务类型查询日志
     * @param business
     * @return
     */
    List<BusinessLoggerEntity> findByBusiness(String business);

    /**
     * 查询示例
     * 根据系统类型查询
     * @param system
     * @return
     */
    @Query("{'system':?0}")
    List<BusinessLoggerEntity> queryBySystem(String system);
}

```

* service层接口需要继承ExtendMongoService代码示例:

```java

/**
 * @author tangtao27
 * @date 2022/7/2018:27
 */
public interface BusinessLoggerService extends ExtendMongoService<BusinessLoggerEntity, BusinessLogger, String, BusinessLoggerMongodbDao> {



}
```


* service层的实现类需要继承ExtendMongoServiceImpl，并实现方法pageListAddCriteria。代码示例:

```java

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BusinessLoggerServiceImpl extends ExtendMongoServiceImpl<BusinessLoggerEntity, BusinessLogger, String, BusinessLoggerMongodbDao> implements BusinessLoggerService {

    
    /**
     * 实现分页查询参数的组装
     *
     * @param criteria
     * @param queryInfo
     */
    @Override
    protected void pageListAddCriteria(List<CriteriaDefinition> criteria, BusinessLogger queryInfo) {
        if (!Objects.isNull(queryInfo)) {
            if (StringUtil.isNotBlank(queryInfo.getRequestMsg())) {
                criteria.add(Criteria.where("requestMsg").regex(queryInfo.getRequestMsg()));
            }
            if (Objects.nonNull(queryInfo.getAccessRequest())) {
                criteria.add(Criteria.where("accessRequest").is(queryInfo.getAccessRequest()));
            }
            if (StringUtil.isNotBlank(queryInfo.getBusiness())) {
                criteria.add(Criteria.where("business").is(queryInfo.getBusiness()));
            }
            if (StringUtil.isNotBlank(queryInfo.getBusinessId())) {
                criteria.add(Criteria.where("businessId").is(queryInfo.getBusinessId()));
            }
            if (StringUtil.isNotBlank(queryInfo.getOperation())) {
                criteria.add(Criteria.where("operation").is(queryInfo.getOperation()));
            }
            if (StringUtil.isNotBlank(queryInfo.getSystem())) {
                criteria.add(Criteria.where("system").is(queryInfo.getSystem()));
            }
            if (StringUtil.isNotBlank(queryInfo.getContent())) {
                criteria.add(Criteria.where("content").regex(queryInfo.getContent()));
            }
            if (StringUtil.isNotBlank(queryInfo.getLoggerLevel())) {
                criteria.add(Criteria.where("loggerLevel").is(queryInfo.getLoggerLevel()));
            }
            if (StringUtil.isNotBlank(queryInfo.getStartTime()) && StringUtil.isNotBlank(queryInfo.getEndTime())) {
                criteria.add(Criteria.where("createTime").gte(queryInfo.getStartTime()).lte(queryInfo.getEndTime()));
            }
            if (Objects.nonNull(queryInfo.getOperationUser())) {
                criteria.add(Criteria.where("operationUser").regex(queryInfo.getOperationUser().toString()));
            }
            if (StringUtils.hasText(queryInfo.getModule())) {
                criteria.add(Criteria.where("module").regex(queryInfo.getModule()));
            }

        }


    }
    
}

```


