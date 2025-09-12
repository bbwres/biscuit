###  spring web 包

#### 集成了springdoc-openapi-webmvc-core



### swagger 配置
````yaml
springdoc:
  # 接口文档配置
  api-docs:
    enabled: true
    swagger:
      title: 认证服务
      description: 认证服务
      author: zlf
      version: 1.0
  packages-to-scan:
  ## 扫描包的路径
    - cn.bbwres.biscuit.module.auth.controller
  show-oauth2-endpoints: true
````


### 参数校验



## 防止重复提交

1. 请求的URL
2. 请求方式
3. 请求参数
4. 限制时间 
 

