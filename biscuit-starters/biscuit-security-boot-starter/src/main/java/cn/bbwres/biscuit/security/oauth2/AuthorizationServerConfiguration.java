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

import cn.bbwres.biscuit.security.oauth2.granter.EnhancerTokenGranter;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.properties.TokenStoreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证授权服务
 *
 * @author zhanglinfeng
 */
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationServerConfiguration.class);

    private final ClientDetailsService clientDetailsService;

    private final BiscuitSecurityProperties biscuitSecurityProperties;

    private final TokenStore tokenStore;
    private final AuthenticationManager authenticationManager;
    private final TokenEnhancerChain tokenEnhancerChain;

    private final UserDetailsService userDetailsService;
    private final AuthorizationServerTokenServices defaultTokenServices;

    private final List<EnhancerTokenGranter> enhancerTokenGranters;

    private final AuthorizationCodeServices authorizationCodeServices;


    public AuthorizationServerConfiguration(ClientDetailsService clientDetailsService, BiscuitSecurityProperties biscuitSecurityProperties,
                                            TokenStore tokenStore, AuthenticationManager authenticationManager,
                                            TokenEnhancerChain tokenEnhancerChain,
                                            UserDetailsService userDetailsService,
                                            AuthorizationServerTokenServices defaultTokenServices,
                                            AuthorizationCodeServices authorizationCodeServices,
                                            List<EnhancerTokenGranter> enhancerTokenGranters) {
        this.clientDetailsService = clientDetailsService;
        this.biscuitSecurityProperties = biscuitSecurityProperties;
        this.tokenStore = tokenStore;
        this.authenticationManager = authenticationManager;
        this.tokenEnhancerChain = tokenEnhancerChain;
        this.userDetailsService = userDetailsService;
        this.defaultTokenServices = defaultTokenServices;
        this.authorizationCodeServices = authorizationCodeServices;
        this.enhancerTokenGranters = enhancerTokenGranters;
    }

    /**
     * 配置客户端
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 配置认证端点
     *
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        //设置支持的请求方法
        endpoints.allowedTokenEndpointRequestMethods(biscuitSecurityProperties.getAllowedTokenEndpointRequestMethods());

        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(tokenStore)
                .tokenEnhancer(tokenEnhancerChain)
                .setClientDetailsService(clientDetailsService);

        if (TokenStoreType.REDIS.equals(biscuitSecurityProperties.getTokenStoreType())) {
            //设置token服务
            endpoints.tokenServices(defaultTokenServices);
        }
        endpoints.authorizationCodeServices(authorizationCodeServices);

        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

        endpoints.tokenGranter(new CompositeTokenGranter(getTokenGranters(clientDetailsService, tokenServices, requestFactory, endpoints.getTokenGranter())));
    }


    /**
     * 设置 TokenGranter
     *
     * @param clientDetails
     * @param tokenServices
     * @param requestFactory
     * @return
     */
    public List<TokenGranter> getTokenGranters(ClientDetailsService clientDetails,
                                               AuthorizationServerTokenServices tokenServices,
                                               OAuth2RequestFactory requestFactory, TokenGranter tokenGranter) {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        //增加原始支持的TokenGranter
        tokenGranters.add(tokenGranter);

        if (!CollectionUtils.isEmpty(enhancerTokenGranters)) {
            for (EnhancerTokenGranter enhancerTokenGranter : enhancerTokenGranters) {
                Class<?> parentClazz = enhancerTokenGranter.parentClazz();
                if (ResourceOwnerPasswordTokenGranter.class.equals(parentClazz)) {
                    tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetails, requestFactory, enhancerTokenGranter.grantType()) {
                        @Override
                        protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
                            enhancerTokenGranter.buildAuthentication(client, tokenRequest);
                            return super.getOAuth2Authentication(client, tokenRequest);
                        }
                    });
                }
                tokenGranters.add(new AbstractTokenGranter(tokenServices, clientDetails, requestFactory, enhancerTokenGranter.grantType()) {
                    @Override
                    public OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
                        Authentication authentication = enhancerTokenGranter.buildAuthentication(client, tokenRequest);
                        return new OAuth2Authentication(requestFactory.createOAuth2Request(client, tokenRequest), authentication);
                    }
                });
            }
        }

        return tokenGranters;
    }

    /**
     * 配置认证服务
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess(biscuitSecurityProperties.getTokenKeyAccessSecurity())
                .checkTokenAccess(biscuitSecurityProperties.getCheckTokenAccessSecurity())
                .allowFormAuthenticationForClients();

    }


}
