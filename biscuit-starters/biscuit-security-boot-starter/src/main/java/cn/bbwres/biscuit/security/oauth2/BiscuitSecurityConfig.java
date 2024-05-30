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
package cn.bbwres.biscuit.security.oauth2;

import cn.bbwres.biscuit.security.oauth2.event.AuthenticationLoginEventListener;
import cn.bbwres.biscuit.security.oauth2.event.AuthenticationLoginService;
import cn.bbwres.biscuit.security.oauth2.event.DefaultAuthenticationLoginServiceImpl;
import cn.bbwres.biscuit.security.oauth2.granter.EnhancerTokenGranter;
import cn.bbwres.biscuit.security.oauth2.handler.Oauth2ExceptionConvertErrorCode;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.vo.AuthUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.RedisAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权认证配置
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableAuthorizationServer
@EnableConfigurationProperties(BiscuitSecurityProperties.class)
public class BiscuitSecurityConfig {


    /**
     * redis token
     */
    @Configuration
    @ConditionalOnProperty(prefix = "biscuit.security", name = "token-store-type", havingValue = "redis")
    public static class RedisToken {

        /**
         * redis token
         *
         * @param connectionFactory
         * @return
         */
        @Bean
        public TokenStore redisTokenStore(RedisConnectionFactory connectionFactory) {
            return new RedisTokenStore(connectionFactory);
        }


    }

    /**
     * jwt toekn
     */
    @Configuration
    @ConditionalOnProperty(prefix = "biscuit.security", name = "token-store-type", havingValue = "jwt")
    public static class JwtToken {


        /**
         * jwt  token 初始化类
         *
         * @param biscuitSecurityProperties
         * @return
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter(BiscuitSecurityProperties biscuitSecurityProperties) {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setSigningKey(biscuitSecurityProperties.getJwtPrivateKey());
            converter.setVerifierKey(biscuitSecurityProperties.getJwtPublicKey());
            return converter;
        }


        /**
         * jwt token 存储
         *
         * @param jwtAccessTokenConverter
         * @return
         */
        @Bean
        public TokenStore jwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
            return new JwtTokenStore(jwtAccessTokenConverter);
        }

    }


    /**
     * 设置token扩展
     *
     * @return
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            Map<String, Object> additionalInfo = new HashMap<>(8);
            additionalInfo.put("expires_at", accessToken.getExpiration());
            if (authentication.getPrincipal() instanceof AuthUser) {
                additionalInfo.put("zh_name", (((AuthUser) authentication.getPrincipal()).getZhName()));
                additionalInfo.put("user_id", (((AuthUser) authentication.getPrincipal()).getUserId()));
                additionalInfo.put("tenant_id", (((AuthUser) authentication.getPrincipal()).getTenantId()));
            }

            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }

    /**
     * token 增强
     *
     * @param tokenEnhancers
     * @return
     */
    @Bean
    public TokenEnhancerChain tokenEnhancerChain(List<TokenEnhancer> tokenEnhancers) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
        return tokenEnhancerChain;
    }


    /**
     * AuthenticationManager 配置信息
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /**
     * 默认的AuthorizationCodeServices 为redis
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RedisConnectionFactory.class)
    public AuthorizationCodeServices authorizationCodeServices(RedisConnectionFactory connectionFactory) {
        return new RedisAuthorizationCodeServices(connectionFactory);
    }


    /**
     * 创建token服务
     *
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices defaultTokenServices(TokenStore tokenStore,
                                                                 ClientDetailsService clientDetailsService,
                                                                 AuthenticationManager authenticationManager,
                                                                 TokenEnhancerChain tokenEnhancerChain,
                                                                 BiscuitSecurityProperties biscuitSecurityProperties) {

        MyDefaultTokenServices tokenServices = new MyDefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setSupportRefreshToken(biscuitSecurityProperties.getSupportRefreshToken());
        tokenServices.setReuseRefreshToken(biscuitSecurityProperties.getReuseRefreshToken());
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);
        //单终端登陆
        tokenServices.setSingleClientToken(biscuitSecurityProperties.getSingleClientToken());
        tokenServices.setAuthenticationManager(authenticationManager);
        return tokenServices;
    }


    /**
     * 密码管理
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(BiscuitSecurityProperties biscuitSecurityProperties) {
        return new BCryptPasswordEncoder(biscuitSecurityProperties.getPasswordStrength());
    }


    /**
     * 登录事件处理服务
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationLoginService authenticationLoginService() {
        return new DefaultAuthenticationLoginServiceImpl();
    }

    /**
     * 登录事件
     *
     * @param authenticationLoginService
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationLoginEventListener authenticationLoginEventListener(AuthenticationLoginService authenticationLoginService) {
        return new AuthenticationLoginEventListener(authenticationLoginService);
    }


    /**
     * 认证适配器
     *
     * @param tokenStore                tokenStore
     * @param myClientDetailsService    客户端杜甫
     * @param authenticationManager     认证服务
     * @param tokenEnhancerChain        token增强
     * @param biscuitSecurityProperties 配置
     * @param userDetailsService        用户信息
     * @param defaultTokenServices      token服务
     * @param authorizationCodeServices code服务
     * @param enhancerTokenGranters     TokenGranter的扩展
     * @return AuthorizationServerConfigurerAdapter
     */
    @Bean
    public AuthorizationServerConfigurerAdapter authorizationServerConfiguration(TokenStore tokenStore,
                                                                                 ClientDetailsService myClientDetailsService,
                                                                                 AuthenticationManager authenticationManager,
                                                                                 TokenEnhancerChain tokenEnhancerChain,
                                                                                 BiscuitSecurityProperties biscuitSecurityProperties,
                                                                                 UserDetailsService userDetailsService,
                                                                                 AuthorizationServerTokenServices defaultTokenServices,
                                                                                 AuthorizationCodeServices authorizationCodeServices,
                                                                                 List<EnhancerTokenGranter> enhancerTokenGranters) {
        return new AuthorizationServerConfiguration(myClientDetailsService, biscuitSecurityProperties, tokenStore,
                authenticationManager, tokenEnhancerChain, userDetailsService, defaultTokenServices, authorizationCodeServices,
                enhancerTokenGranters);

    }


    /**
     * oauth2 的异常
     *
     * @return Oauth2ExceptionConvertErrorCode
     */
    @Bean("oauth2ExceptionConvertErrorCode")
    public Oauth2ExceptionConvertErrorCode oauth2ExceptionConvertErrorCode() {
        return new Oauth2ExceptionConvertErrorCode();
    }

}

