# biscuit-gateway-boot-starter

## 介绍

biscuit-gateway-boot-starter 是一个基于 Spring Cloud Gateway 的网关服务，它集成了 全局的错误返回、认证鉴权、等功能

## 快速开始

### 1. 引入依赖

```xml

<dependency>
    <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-gateway-boot-starter</artifactId>
    <version>${biscuit.version}</version>
</dependency>
```

### 2. 实现接口

实现cn.bbwres.biscuit.gateway.service.ResourceService接口，用于获取用户资源列表

```java
public class ResourceServiceImpl implements ResourceService {
    /**
     * 检查并解析token
     *
     * @param token
     * @return
     */
    @Override
    public Map<String, Object> checkToken(String token) {
        //TODO 检查并解析token
        return null;
    }

    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return
     */
    @Override
    public List<String> getLoginAuthResource() {
        return null;
    }

    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleId 角色id
     * @return
     */
    @Override
    public List<String> getResourceByRole(String roleId) {
        return List.of("/userEx");
    }

    /**
     * 获取登陆地址
     *
     * @return
     */
    @Override
    public String getLoginUrl() {
        return ResourceService.super.getLoginUrl();
    }

    /**
     * 获取登陆地址
     *
     * @param state
     * @return
     */
    @Override
    public String getLoginUrlBuildState(String state) {
        //TODO 根据state生成登陆地址

        return ResourceService.super.getLoginUrlBuildState(state);
    }
}

```

### 3. 配置说明

网关的配置参数对应的类为cn.bbwres.biscuit.gateway.GatewayProperties，具体配置如下：

| 参数名                                    | 默认值                     | 说明                                           |
|----------------------------------------|-------------------------|----------------------------------------------|
| biscuit.gateway.noAuthUris             | 无                       | 固定无需鉴权的uri,会在网关启动时加载                         |
| biscuit.gateway.loginAuthUris          | 无                       | 固定登录鉴权的uri ， 会在网关启动时加载                       |
| biscuit.gateway.accessDeniedCode       | 100000403               | 无权访问返回的错误码                                   |
| biscuit.gateway.authFailCode           | 100000401               | 认证失败返回的错误码                                   |
| biscuit.gateway.systemErrCode          | 100000500               | 系统异常返回的错误码                                   |
| biscuit.gateway.userTokenHeader        | X-User-Token            | 认证通过后 后续的用户token 放入请求头的名字                    |
| biscuit.gateway.userInfoHeader         | X-User-Info             | 认证通过后用户信息 放入请求头的名字                           |
| biscuit.gateway.useXssFilter           | false                   | 是否使用xss过滤器                                   |
| biscuit.gateway.cacheResource          | true                    | 是否使用本地缓存用户资源信息                               |
| biscuit.gateway.localCacheResourceTime | 300                     | 本地缓存资源有效期 单位为秒                               |
| biscuit.gateway.session                | 无                       | 是否使用session。 true 使用session， false 不使用。默认不使用 |
| biscuit.gateway.useJwtToken            | true                    | 是否使用jwtToken 默认使用                            |
| biscuit.gateway.loginStateUris         | 无                       | 根据传参的state 跳转到登录的url为Map对象                   |
| biscuit.gateway.disableCsrf            | false                   | 关闭Csrf                                       |
| biscuit.gateway.disableCors            | false                   | 关闭cors                                       |
| biscuit.gateway.dynamicRouteEnabled    | true                    | 是否开启动态路由                                     |
| biscuit.gateway.dynamicRouteGroup      | DEFAULT_GROUP           | 动态路由配置分组                                     |
| biscuit.gateway.dynamicRouteDataId     | appName+'dynamic-route' | 动态路由配置的DataId，默认为当前应用名称+dynamic-route        |

### 4. 动态路由

网关动态路由配置

示例：

```json
[
  {
    "id": "route0",
    "uri": "lb://service-a",
    "predicates": [
      "Path=/service-a/**"
    ],
    "filters": [
      "StripPrefix=1"
    ],
    "order": 0
  }
]

```
