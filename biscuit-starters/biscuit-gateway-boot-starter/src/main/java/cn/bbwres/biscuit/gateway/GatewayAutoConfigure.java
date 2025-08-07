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

package cn.bbwres.biscuit.gateway;

import cn.bbwres.biscuit.gateway.adapter.ErrorWebExceptionHandler;
import cn.bbwres.biscuit.gateway.adapter.ExtensionErrorAttributes;
import cn.bbwres.biscuit.gateway.authorization.AuthorizationManager;
import cn.bbwres.biscuit.gateway.cache.ResourceCacheService;
import cn.bbwres.biscuit.gateway.route.DefaultGatewayRoute;
import cn.bbwres.biscuit.gateway.route.GatewayRouteNacosProcessor;
import cn.bbwres.biscuit.gateway.route.RouteController;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import cn.bbwres.biscuit.nacos.operation.NacosConfigOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;

import java.util.List;

/**
 * 网关鉴权 自动注入配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
@EnableWebFluxSecurity
@EnableConfigurationProperties({GatewayProperties.class})
public class GatewayAutoConfigure {


    /**
     * gateway 统一错误处理
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @param messagesProvider  a {@link org.springframework.beans.factory.ObjectProvider} object
     * @return a {@link cn.bbwres.biscuit.gateway.adapter.ExtensionErrorAttributes} object
     */
    @Bean
    public ExtensionErrorAttributes errorAttributes(GatewayProperties gatewayProperties,
                                                    ObjectProvider<MessageSourceAccessor> messagesProvider) {
        return new ExtensionErrorAttributes(gatewayProperties, messagesProvider.getIfAvailable());
    }

    /**
     * 统一错误处理器
     *
     * @param errorAttributes    a {@link org.springframework.boot.web.reactive.error.ErrorAttributes} object
     * @param webProperties      a {@link org.springframework.boot.autoconfigure.web.WebProperties} object
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     * @return a {@link cn.bbwres.biscuit.gateway.adapter.ErrorWebExceptionHandler} object
     */
    @Bean
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
                                                             WebProperties webProperties,
                                                             ApplicationContext applicationContext) {
        ErrorWebExceptionHandler errorWebExceptionHandler = new ErrorWebExceptionHandler(errorAttributes, webProperties.getResources(), applicationContext);
        errorWebExceptionHandler.setMessageWriters(List.of(new EncoderHttpMessageWriter<>(new Jackson2JsonEncoder())));
        return errorWebExceptionHandler;
    }

    /**
     * 认证管理器
     *
     * @param resourceCacheService a {@link cn.bbwres.biscuit.gateway.cache.ResourceCacheService} object
     * @param pathMatcher          a {@link org.springframework.util.PathMatcher} object
     * @return a {@link cn.bbwres.biscuit.gateway.authorization.AuthorizationManager} object
     */
    @Bean
    public AuthorizationManager authorizationManager(ResourceCacheService resourceCacheService,
                                                     PathMatcher pathMatcher) {
        return new AuthorizationManager(resourceCacheService, pathMatcher);
    }

    /**
     * 资源缓存服务
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @param resourceService   a {@link cn.bbwres.biscuit.gateway.service.ResourceService} object
     * @return a {@link cn.bbwres.biscuit.gateway.cache.ResourceCacheService} object
     */
    @Bean
    public ResourceCacheService resourceCacheService(GatewayProperties gatewayProperties,
                                                     ResourceService resourceService) {
        return new ResourceCacheService(gatewayProperties, resourceService);
    }


    /**
     * 设置获取token的转换器
     *
     * @return a {@link org.springframework.security.web.server.authentication.ServerAuthenticationConverter} object
     */
    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        ServerBearerTokenAuthenticationConverter converter = new ServerBearerTokenAuthenticationConverter();
        converter.setAllowUriQueryParameter(true);
        return converter;
    }


    /**
     * 路径匹配器
     *
     * @return a {@link org.springframework.util.PathMatcher} object
     */
    @Bean
    public PathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    /**
     * 空的SessionManager
     *
     * @return a {@link org.springframework.web.server.session.WebSessionManager} object
     */
    @Bean
    @ConditionalOnProperty(prefix = "biscuit.gateway", name = "session", havingValue = "false", matchIfMissing = true)
    public WebSessionManager webSessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionIdResolver(new CookieWebSessionIdResolver() {
            @Override
            public void expireSession(ServerWebExchange exchange) {
                //session 过期不做任何处理
            }
        });

        return defaultWebSessionManager;
    }

    /**
     * 路由控制器
     *
     * @param routeLocator a {@link org.springframework.cloud.gateway.route.RouteLocator} object
     * @return a {@link RouteController} object
     */
    @Bean
    public RouteController routerController(RouteLocator routeLocator) {
        return new RouteController(routeLocator);
    }


    /**
     * 动态路由配置类
     */
    @ConditionalOnProperty(prefix = "biscuit.gateway", name = "dynamic-route-enabled", havingValue = "false", matchIfMissing = true)
    protected static class DynamicRouteConfigurer {
        /**
         * 动态路由
         *
         * @param routeDefinitionLocator
         * @param routeDefinitionWriter
         * @param gatewayProperties
         * @return
         */
        @Bean
        public DefaultGatewayRoute defaultGatewayRoute(RouteDefinitionLocator routeDefinitionLocator, RouteDefinitionWriter routeDefinitionWriter,
                                                       org.springframework.cloud.gateway.config.GatewayProperties gatewayProperties) {
            return new DefaultGatewayRoute(routeDefinitionLocator, routeDefinitionWriter, gatewayProperties);
        }

        /**
         * 动态路由
         *
         * @param nacosConfigOperation
         * @param defaultGatewayRoute
         * @param gatewayProperties
         * @param environment
         * @return
         */
        @Bean
        @ConditionalOnBean(NacosConfigOperation.class)
        public GatewayRouteNacosProcessor gatewayRouteNacosProcessor(NacosConfigOperation nacosConfigOperation,
                                                                     DefaultGatewayRoute defaultGatewayRoute,
                                                                     GatewayProperties gatewayProperties,
                                                                     Environment environment) {
            return new GatewayRouteNacosProcessor(nacosConfigOperation, defaultGatewayRoute, gatewayProperties, environment);
        }

    }


}
