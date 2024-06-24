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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

/**
 * 安全相关配置属性
 *
 * @author zhanglinfeng
 */
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
     * jwt 私钥
     */
    private String jwtPrivateKey;

    /**
     * jwt 公钥
     */
    private String jwtPublicKey;

    /**
     * 是否支持刷新令牌
     */
    private Boolean supportRefreshToken = true;

    /**
     * 重用刷新令牌
     */
    private Boolean reuseRefreshToken = true;

    /**
     * token 支持的请求方法
     */
    private HttpMethod[] allowedTokenEndpointRequestMethods = new HttpMethod[]{HttpMethod.POST};

    /**
     * 获取token的访问权限字符串
     */
    private String tokenKeyAccessSecurity = "permitAll()";

    /**
     * 检查token的访问权限字符串
     */
    private String checkTokenAccessSecurity = "permitAll()";

    /**
     * 验证码值的请求参数名称
     */
    private String captchaCodeValueName = "captcha_verification";
    /**
     * 验证码key的请求参数名称
     */
    private String captchaCodeKeyName = "captcha_code_key";


    public Boolean getSingleClientToken() {
        return singleClientToken;
    }

    public void setSingleClientToken(Boolean singleClientToken) {
        this.singleClientToken = singleClientToken;
    }

    public TokenStoreType getTokenStoreType() {
        return tokenStoreType;
    }

    public void setTokenStoreType(TokenStoreType tokenStoreType) {
        this.tokenStoreType = tokenStoreType;
    }

    public Integer getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(Integer passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    public String getJwtPrivateKey() {
        return jwtPrivateKey;
    }

    public void setJwtPrivateKey(String jwtPrivateKey) {
        this.jwtPrivateKey = jwtPrivateKey;
    }

    public String getJwtPublicKey() {
        return jwtPublicKey;
    }

    public void setJwtPublicKey(String jwtPublicKey) {
        this.jwtPublicKey = jwtPublicKey;
    }

    public Boolean getSupportRefreshToken() {
        return supportRefreshToken;
    }

    public void setSupportRefreshToken(Boolean supportRefreshToken) {
        this.supportRefreshToken = supportRefreshToken;
    }

    public Boolean getReuseRefreshToken() {
        return reuseRefreshToken;
    }

    public void setReuseRefreshToken(Boolean reuseRefreshToken) {
        this.reuseRefreshToken = reuseRefreshToken;
    }

    public HttpMethod[] getAllowedTokenEndpointRequestMethods() {
        return allowedTokenEndpointRequestMethods;
    }

    public void setAllowedTokenEndpointRequestMethods(HttpMethod[] allowedTokenEndpointRequestMethods) {
        this.allowedTokenEndpointRequestMethods = allowedTokenEndpointRequestMethods;
    }

    public String getTokenKeyAccessSecurity() {
        return tokenKeyAccessSecurity;
    }

    public void setTokenKeyAccessSecurity(String tokenKeyAccessSecurity) {
        this.tokenKeyAccessSecurity = tokenKeyAccessSecurity;
    }

    public String getCheckTokenAccessSecurity() {
        return checkTokenAccessSecurity;
    }

    public void setCheckTokenAccessSecurity(String checkTokenAccessSecurity) {
        this.checkTokenAccessSecurity = checkTokenAccessSecurity;
    }

    public String getCaptchaCodeValueName() {
        return captchaCodeValueName;
    }

    public void setCaptchaCodeValueName(String captchaCodeValueName) {
        this.captchaCodeValueName = captchaCodeValueName;
    }

    public String getCaptchaCodeKeyName() {
        return captchaCodeKeyName;
    }

    public void setCaptchaCodeKeyName(String captchaCodeKeyName) {
        this.captchaCodeKeyName = captchaCodeKeyName;
    }

    public TokenStoreType getAuthorizationCodeStoreType() {
        return authorizationCodeStoreType;
    }

    public void setAuthorizationCodeStoreType(TokenStoreType authorizationCodeStoreType) {
        this.authorizationCodeStoreType = authorizationCodeStoreType;
    }
}
