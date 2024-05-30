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
package cn.bbwres.biscuit.security.captcha.oauth2;

import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.security.oauth2.granter.EnhancerTokenGranter;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证码
 *
 * @author zhanglinfeng
 */
public class CaptchaTokenGranter implements EnhancerTokenGranter {
    private static final Logger log = LoggerFactory.getLogger(CaptchaTokenGranter.class);

    /**
     * 图形验证码+账号密码验证
     */
    public static final String CODE_GRANT_TYPE = "captcha_password";

    private final CheckCaptchaService checkCaptchaService;

    private final BiscuitSecurityProperties biscuitSecurityProperties;


    public CaptchaTokenGranter(CheckCaptchaService checkCaptchaService, BiscuitSecurityProperties biscuitSecurityProperties) {
        this.checkCaptchaService = checkCaptchaService;
        this.biscuitSecurityProperties = biscuitSecurityProperties;
    }

    /**
     * 装载认证token信息
     *
     * @param client
     * @param tokenRequest
     * @return
     */
    @Override
    public Authentication buildAuthentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String code = parameters.get(biscuitSecurityProperties.getCaptchaCodeValueName());
        String codeKey = parameters.get(biscuitSecurityProperties.getCaptchaCodeKeyName());
        //验证验证码
        if (!checkCaptchaService.check(CODE_GRANT_TYPE, code, codeKey)) {
            log.info("当前验证码校验失败");
            throw new InvalidCaptchaException(GlobalErrorCodeConstants.INVALID_CAPTCHA.getMessage());
        }
        return null;
    }

    /**
     * 可以处理的gentType
     *
     * @return
     */
    @Override
    public String grantType() {
        return CODE_GRANT_TYPE;
    }

    /**
     * 父类的TokenGranter
     * 存在该值时，除了调用buildAuthentication方法，还会调用父类的getOAuth2Authentication方法
     *
     * @return
     */
    @Override
    public Class<?> parentClazz() {
        return ResourceOwnerPasswordTokenGranter.class;
    }
}
