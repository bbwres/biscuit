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
     * 是否只允许单个客户端登录
     * true -是
     * false -否 不限制
     * 默认为false
     */
    private Boolean singleClientToken = false;


    /**
     * token 存储的类型
     * 默认为jwt
     */
    private TokenStoreType tokenStoreType = TokenStoreType.JWT;
    /**
     * 授权码存储的类型
     */
    private TokenStoreType authorizationCodeStoreType = TokenStoreType.REDIS;

    /**
     * the log rounds to use, between 4 and 31
     * 密码强度
     * 最少为4，最大为31. 越大对性能影响越高
     * 默认为10
     */
    private Integer passwordStrength = 10;


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
