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
package cn.bbwres.biscuit.security.oauth2.config;

import cn.bbwres.biscuit.i18n.I18nProperties;
import cn.bbwres.biscuit.i18n.support.SystemMessageSource;
import cn.bbwres.biscuit.security.oauth2.event.AuthenticationLoginEventListener;
import cn.bbwres.biscuit.security.oauth2.event.AuthenticationLoginService;
import cn.bbwres.biscuit.security.oauth2.event.DefaultAuthenticationLoginServiceImpl;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 授权认证配置
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(BiscuitSecurityProperties.class)
public class BiscuitSecurityConfig {

    /**
     * 系统默认的messageSource
     *
     * @return
     */
    @Bean("securityMessageBasename")
    public SystemMessageSource systemMessageSource(I18nProperties i18nProperties) {
        return new SystemMessageSource(i18nProperties.getMessageSourceCacheSeconds(),
                i18nProperties.getSecurityMessageBasename());
    }


    /**
     * 登录事件处理服务
     *
     * @return AuthenticationLoginService
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationLoginService authenticationLoginService() {
        return new DefaultAuthenticationLoginServiceImpl();
    }

    /**
     * 登录事件
     *
     * @param authenticationLoginService authenticationLoginService
     * @return AuthenticationLoginEventListener
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationLoginEventListener authenticationLoginEventListener(AuthenticationLoginService authenticationLoginService) {
        return new AuthenticationLoginEventListener(authenticationLoginService);
    }


    /**
     * 密码配置
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

