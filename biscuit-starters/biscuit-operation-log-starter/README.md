### 操作日志工具包

#### 提供记录操作日志的工具、

## 1.操作日志的注解

OperationLog

| 参数                     | 说明                                 | 默认值                        |
|------------------------|------------------------------------|----------------------------|
| system                 | 当前日志归属的系统                          | 默认取spring.application.name |
| module                 | 当前业务所属的业务模块,必填                     | 无                          |
| business               | 当前业务所属的业务类型,必填                     | 无                          |
| content                | 日志内容，支持el表达式                       | 无                          |
| operation              | 操作类型,必填                            | 无                          |
| businessId             | 业务数据的id,支持el表达式                    | 无                          |
| isAccessRequest        | 是否接入方请求                            | false                      |
| logArgs                | 是否记录方法参数                           | true                       |
| ignoreRequestParamsIdx | 忽略的请求参数序号,默认为不忽略,当logArgs为true时才生效 | -1                         |
| ignoreResponseParams   | 是否忽略响应参数 默认为不忽略                    | false                      |


## 2. 操作日志工具类

```java
import cn.bbwres.biscuit.operation.log.utils.OperationLogUtils;

```
### 主要方法说明
1. cn.bbwres.biscuit.operation.log.utils.OperationLogUtils.setContent 设置日志内容,用于在注解方法内部改变操作日志内容
2. cn.bbwres.biscuit.operation.log.utils.OperationLogUtils.addExt 增加扩展参数，用于在注解方法内部增加扩展参数。

## 3. 操作日志保存
  需要实现接口cn.bbwres.biscuit.operation.log.service.OperationLogSaveService 来完成操作日志的保存