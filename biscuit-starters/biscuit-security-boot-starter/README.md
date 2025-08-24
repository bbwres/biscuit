# oauth2 相关的包

## 使用说明

### 1.引入jar包

```xml

<dependency>
    <groupId>cn.bbwres</groupId>
    <artifactId>biscuit-security-boot-starter</artifactId>
</dependency>
```

### 2. 实现类 org.springframework.security.oauth2.provider.ClientDetailsService

### 3. 实现类 org.springframework.security.core.userdetails.UserDetailsService

### 4. oauth2 接口认证文档

### 登陆接口

----

* 请求路径 /oauth/token
* 请求方式 POST
* 请求头 application/x-www-form-urlencoded

* 请求参数

| 字段名称          | 是否必填 | 说明                               |
|---------------|------|----------------------------------|
| grant_type    | 是    | password-账户密码认证                  
| username      | 否    | 用户名称,password模式必填                |
| password      | 否    | 用户密码,password模式必填                |
| client_id     | 是    | 客户端id </br> 部署服务器之后可以配置在服务器nginx |
| client_secret | 是    | 客户端密钥 </br> 部署服务器之后可以配置在服务器nginx |
| scop          | 是    | 客户端权限 默认为-read                   |
| refresh_token | 否    | 刷新token,refresh_token 模式必填       |


* 响应参数

| 字段名称              | 是否必填 | 说明                       |
|-------------------|------|--------------------------|
| access_token      | 是    | 请求的token ，后续请求需要带上该token |
| token_type        | 是    | token 类型 ，默认为bearer      |
| refresh_token     | 是    | 刷新token                  |
| expires_in        | 是    | access_token 有效期         |
| expires_at        | 是    | access_token 过期时间        |
| zh_name           | 否    | 用户中文名称                   |
| user_id           | 否    | 用户id                     |
| customAuthorities | 是    | 当前用户权限，如果用户有多个角色则会有多条数据  |
| -- authority      | 否    | 当前用户权限                   |
| -- tenantId       | 否    | 租户编码                     |
| -- clientId       | 否    | 客户端id                    |

### 请求流程

----

#### 获取图形验证码

1.请求接口/captcha/create/{code_key} 获取验证码图片
2.其中code_key 为验证码的key，可以随机生成，用户登陆时需要传入该key，对应登陆接口的的code_key参数
3.用户输入账号密码和验证码之后按照自有登陆流程传入参数。


----

#### 自有登陆流程

1. 请求 登陆接口 传入username,password 和其他必填参数
2. 用户密码传输规则为:sha1(用户登录账号+用户密码）
3. 获取返回值
4. 保存 access_token和refresh_token 等信息
5. 后续请求时将返回的token_type+空格+access_token组成认证token 放入请求头Authorization 字段中
6. 客户端在 access_token 即将过期时，需要主动调用 登陆接口(类型为refresh_token) 传入refresh_token 来获取新的access_token
7. 后端返回统一错误码为 A0302 则需要再次登陆

----

#### 统一认证登陆流程 code模式

1. 请求任意接口时，接口返回A0302 并返回loginUrl 字段
2. 前端跳转返回的loginUrl
3. 用户在统一认证登陆
4. 统一认证跳转前端页面，并返回code字段。
5. 前端请求登陆接口并传入code（认证模式为-sso）
6. 获取返回值
7. 保存 access_token和refresh_token 等信息
8. 后续请求时将返回的token_type+空格+access_token组成认证token 放入请求头Authorization 字段中
9. 客户端在 access_token 即将过期时，需要主动调用 登陆接口(类型为refresh_token) 传入refresh_token 来获取新的access_token
