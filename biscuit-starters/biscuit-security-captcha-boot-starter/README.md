## 模块说明

`
验证码校验模块用于登录流程的安全校验，集成该模块后，系统将对用户账号密码登录行为进行次数限制。
当用户账号密码登录失败次数达到指定阈值（默认配置为 2 次）时，后续登录请求需先完成验证码校验，校验通过后方可继续执行账号密码登录流程
`

1. 使用tianai-captcha 来生成图形验证码

```yaml

# 滑块验证码配置， 详细请看 cloud.tianai.captcha.autoconfiguration.ImageCaptchaProperties 类
captcha:
  # 如果项目中使用到了redis，滑块验证码会自动把验证码数据存到redis中， 这里配置redis的key的前缀,默认是captcha:slider
  prefix: captcha
  # 验证码过期时间，默认是2分钟,单位毫秒， 可以根据自身业务进行调整
  expire:
    # 默认缓存时间 2分钟
    default: 10000
    # 针对 点选验证码 过期时间设置为 2分钟， 因为点选验证码验证比较慢，把过期时间调整大一些
    WORD_IMAGE_CLICK: 20000
  # 使用加载系统自带的资源， 默认是 false(这里系统的默认资源包含 滑动验证码模板/旋转验证码模板,如果想使用系统的模板，这里设置为true)
  init-default-resource: true
  # 缓存控制， 默认为false不开启
  local-cache-enabled: false
  # 缓存开启后，验证码会提前缓存一些生成好的验证数据， 默认是20
  local-cache-size: 20
  # 缓存开启后，缓存拉取失败后等待时间 默认是 5秒钟
  local-cache-wait-time: 5000
  # 缓存开启后，缓存检查间隔 默认是2秒钟
  local-cache-period: 2000
  # 配置字体包，供文字点选验证码使用,可以配置多个，不配置使用默认的字体
  font-path:
    - classpath:font/SimHei.ttf
  secondary:
    # 二次验证， 默认false 不开启
    enabled: true
    # 二次验证过期时间， 默认 2分钟
    expire: 120000
    # 二次验证缓存key前缀，默认是 captcha:secondary
    keyPrefix: "captcha:secondary"
# 配置验证码的背景资源
biscuit:
  captcha:
    SLIDER:
      - bgimages/slider
```

2. 扩展的参数配置

| 参数名称                         | 默认值                  | 参数说明                   |  
|------------------------------|----------------------|------------------------|
| captchaCodeValueName         | captcha_verification | 验证码值的请求参数名称            |  
| captchaCodeKeyName           | captcha_code_key     | 验证码key的请求参数名称          |  
| loginFailureCaptchaThreshold | 2                    | 当用户连续失败次数达到该值时，触发验证码校验 |  

3. 验证码请求地址

| 请求路径                           | 请求参数                                                                           | 响应参数 | 备注      |
|--------------------------------|--------------------------------------------------------------------------------|------|---------|
| GET:/captcha/create            | type(可选)，请求验证码的类型：SLIDER-滑块，ROTATE-旋转验证码 ,CONCAT-滑动还原验证码,WORD_IMAGE_CLICK-文字点选 |      | 创建验证码信息 |
| POST:/checkCaptcha/{captchaId} | captchaId(必填)，验证码id,ImageCaptchaTrack-验证码参数                                    |      | 检查验证码   |
