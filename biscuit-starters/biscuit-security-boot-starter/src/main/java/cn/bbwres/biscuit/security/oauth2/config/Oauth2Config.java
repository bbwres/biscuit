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

import cn.bbwres.biscuit.security.oauth2.grant.CustomAuthenticationGrant;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.web.CustomLoginUrlAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * oauth2 相关配置
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
public class Oauth2Config {


    /**
     * AuthorizationServerSettings 配置
     * 配置端点信息
     *
     * @return AuthorizationServerSettings
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * 认证失败登录页面配置
     *
     * @param biscuitSecurityProperties           配置信息
     * @param messageSourceAccessorObjectProvider i18n 信息
     * @return CustomLoginUrlAuthenticationEntryPoint
     */
    @Bean
    public CustomLoginUrlAuthenticationEntryPoint customLoginUrlAuthenticationEntryPoint(BiscuitSecurityProperties biscuitSecurityProperties,
                                                                                         AuthorizationServerSettings authorizationServerSettings,
                                                                                         ObjectProvider<MessageSourceAccessor> messageSourceAccessorObjectProvider) {
        return new CustomLoginUrlAuthenticationEntryPoint(biscuitSecurityProperties.getLoginUrl(), authorizationServerSettings, messageSourceAccessorObjectProvider);
    }


    /**
     * OAuth2TokenGenerator 配置
     *
     * @param jwkSource             jwk
     * @param accessTokenCustomizer accessTokenCustomizer
     * @return OAuth2TokenGenerator
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenGenerator<? extends OAuth2Token> oauth2TokenGenerator(JWKSource<SecurityContext> jwkSource,
                                                                            OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer) {
        OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator = new OAuth2AccessTokenGenerator();
        oAuth2AccessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
        return new DelegatingOAuth2TokenGenerator(
                new JwtGenerator(new NimbusJwtEncoder(jwkSource)),
                oAuth2AccessTokenGenerator,
                new OAuth2RefreshTokenGenerator()
        );
    }


    /**
     * 安全配置
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(-10)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      ObjectProvider<List<CustomAuthenticationGrant>> customAuthenticationGrants,
                                                                      CustomLoginUrlAuthenticationEntryPoint customLoginUrlAuthenticationEntryPoint) throws Exception {

        //初始化oauth2Server的配置
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer.tokenEndpoint(tokenEndpoint -> {
                                            List<CustomAuthenticationGrant> customAuthentications = customAuthenticationGrants.getIfAvailable();
                                            if (!ObjectUtils.isEmpty(customAuthentications)) {
                                                customAuthentications.forEach(custom -> {
                                                    if (!ObjectUtils.isEmpty(custom.getCustomAuthenticationConverter())) {
                                                        tokenEndpoint.accessTokenRequestConverter(custom.getCustomAuthenticationConverter());
                                                    }
                                                    if (!ObjectUtils.isEmpty(custom.getCustomAuthenticationProvider())) {
                                                        tokenEndpoint.authenticationProvider(custom.getCustomAuthenticationProvider());
                                                    }

                                                });
                                            }
                                        }
                                )
                                .oidc(Customizer.withDefaults())
                )
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(customLoginUrlAuthenticationEntryPoint, new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                );

        return http.build();
    }
}
