/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.security.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全相关配置属性
 *
 * @author zhanglinfeng
 */
@Data
@ConfigurationProperties("biscuit.security")
public class BiscuitSecurityProperties {

    /**
     * token 存储的位置
     */
    private String tokenStoreType = TokenStoreType.redis.name();

    /**
     * token 过期偏移的秒数
     */
    private long tokenExpireOffsetSecond = 3 * 60L;


    /**
     * OAuth2授权同意信息过期时间
     */
    private long authorizationConsentExpireSecond = 10 * 60L;


    /**
     * 当用户连续失败次数达到该值时，触发账户锁定
     */
    private int loginFailureLockThreshold = 5;

    /**
     * 登录账户锁定时间
     */
    private long accountLockExpireSecond = 24 * 60 * 60L;

    /**
     * 是否自动生成jwt密钥
     */
    private Boolean autoGeneratorJwtKey = true;
    /**
     * 自动生成的jwt密钥长度
     */
    private Integer autoGeneratorJwtKeySize = 2048;
    /**
     * jwt 私钥
     */
    private String jwtPrivateKey;

    /**
     * jwt 公钥
     */
    private String jwtPublicKey;

    /**
     * 验证码值的请求参数名称
     */
    private String captchaCodeValueName = "captcha_verification";
    /**
     * 验证码key的请求参数名称
     */
    private String captchaCodeKeyName = "captcha_code_key";

    /**
     * 登录的页面地址
     */
    private String loginUrl = "/login";


}
