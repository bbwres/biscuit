# okhttp

okhttp 的feign配置已经兼容org.springframework.cloud.openfeign.clientconfig.OkHttpFeignConfiguration
因此 配置org.springframework.cloud.openfeign.support.FeignHttpClientProperties即可


````properties
# 启动 okhttp
feign.okhttp.enabled=true
#2s
feign.httpclient.connection-timeout=2000
feign.httpclient.ok-http.read-timeout=30
feign.httpclient.max-connections=200
feign.httpclient.time-to-live=900
````



1. 服务启动时，随机生成密码，放入到注册中心元数据中
2. 客户端调用服务时，从注册中心获取到要调用的服务。
3. 根据调用的服务获取到该服务的随机密码信息
4. 组装签名参数 放入请求头中。
5. mvc接收到请求时，会进行全局拦截，来判断当前签名是否正确
6. 


## 当前等待测试
1. feign 调用 ok
2. gateway 调用 ok
3. RestTemplate调用
4. WebClient 调用


## 当前待实现
1. feign 增加分组功能，参考dubbo 来实现根据传入的分组信息，动态的调用服务。