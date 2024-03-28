# biscuit-common-boot-starter

## 介绍

biscuit-common-boot-starter 是基于SpringBoot的通用开发框架，提供了系统认证常量、基础的实体类、基本的用户信息、基础的枚举类、异常类和json工具类以及通用的返回对象。

## 模块说明

| 模块                             | 说明         |
|:-------------------------------|:-----------|
| SystemAuthConstant             | 系统鉴权认证相关常量 |
| GlobalErrorCodeConstants       | 系统错误码常量    |
| BaseEntity                     | 系统基础的实体类   |
| UserBaseInfo                   | 基本的用户对象    |
| BaseEnum                       | 基础的枚举类     |
| ParamsCheckRuntimeException    | 参数校验失败的异常  |
| SystemBusinessRuntimeException | 系统业务失败的异常  |
| SystemRuntimeException         | 框架基础异常     |
| JsonUtil                       | json工具类    |
| Result                         | 通用的返回对象    |

## 部分说明

* BaseEntity 为基础的实体类，提供了creator、updater、createTime、updateTime、tenantId等字段。
* UserBaseInfo 为基本的用户对象，提供了userId、username、tenantId等字段。
* BaseEnum 为基础的枚举类，提供了getValue、getDisplayName方法，并适配了mybatis的枚举类型转换器，使用getValue的值存入数据库。
* GlobalErrorCodeConstants 定义了系统错误码常量，开发时可以继承该接口，扩展出自有错误码。
* ParamsCheckRuntimeException 为参数校验失败的异常，继承了SystemRuntimeException。
* SystemBusinessRuntimeException 为系统业务失败的异常，继承了SystemRuntimeException。
* JsonUtil 为基于jackson的json工具类，提供了常用的json序列化方法
* Result 提供了Result为通用的返回对象，包含resultCode、resultMsg、data等字段。
