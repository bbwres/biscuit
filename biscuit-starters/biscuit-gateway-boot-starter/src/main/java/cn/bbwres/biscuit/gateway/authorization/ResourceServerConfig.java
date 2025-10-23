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

package cn.bbwres.biscuit.gateway.authorization;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.cache.ResourceCacheService;
import cn.bbwres.biscuit.gateway.constants.GatewayConstant;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import cn.bbwres.biscuit.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.util.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 资源配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@EnableWebFluxSecurity
@EnableConfigurationProperties(GatewayProperties.class)
public class ResourceServerConfig {


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
     * <p>messageConverters.</p>
     *
     * @param converters a {@link org.springframework.beans.factory.ObjectProvider} object
     * @return a {@link org.springframework.boot.autoconfigure.http.HttpMessageConverters} object
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    /**
     * 安全处理类
     *
     * @param http                                       a {@link org.springframework.security.config.web.server.ServerHttpSecurity} object
     * @param gatewayProperties                          a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @param authorizationManager                       a {@link cn.bbwres.biscuit.gateway.authorization.AuthorizationManager} object
     * @param customServerAccessDeniedHandler            a {@link org.springframework.security.web.server.authorization.ServerAccessDeniedHandler} object
     * @param customServerAuthenticationEntryPoint       a {@link org.springframework.security.web.server.ServerAuthenticationEntryPoint} object
     * @param reactiveAuthenticationManager           a {@link org.springframework.security.authentication.ReactiveAuthenticationManager} object
     * @return a {@link org.springframework.security.web.server.SecurityWebFilterChain} object
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            GatewayProperties gatewayProperties,
                                                            AuthorizationManager authorizationManager,
                                                            ServerAccessDeniedHandler customServerAccessDeniedHandler,
                                                            ServerAuthenticationEntryPoint customServerAuthenticationEntryPoint,
                                                            ObjectProvider<ReactiveAuthenticationManager> reactiveAuthenticationManager) {
        //设置认证信息
        http.oauth2ResourceServer(oauth2ResourceServer -> {
            ReactiveAuthenticationManager authenticationManager = reactiveAuthenticationManager.getIfAvailable();
            Assert.notNull(authenticationManager, "使用jwtToken时，未初始化ReactiveAuthenticationManager处理类");
            oauth2ResourceServer.jwt(jwt -> jwt.authenticationManager(authenticationManager));
        });
        http.authorizeExchange(authorizeExchange -> {
            if (ArrayUtils.isNotEmpty(gatewayProperties.getNoAuthUris())) {
                //无需登录鉴权
                authorizeExchange.pathMatchers(gatewayProperties.getNoAuthUris()).permitAll();
            }
            authorizeExchange.pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .anyExchange().access(authorizationManager);

        });
        //处理未授权
        http.exceptionHandling(exceptions -> exceptions.accessDeniedHandler(customServerAccessDeniedHandler)
                .authenticationEntryPoint(customServerAuthenticationEntryPoint));
        //关闭csrf
        if (gatewayProperties.getDisableCsrf()) {
            http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        }
        //关闭cors
        if (gatewayProperties.getDisableCors()) {
            http.cors(ServerHttpSecurity.CorsSpec::disable);
        }


        return http.build();
    }


    /**
     * 无权访问自定义响应
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @return a {@link org.springframework.security.web.server.authorization.ServerAccessDeniedHandler} object
     */
    @Bean
    public ServerAccessDeniedHandler customServerAccessDeniedHandler(GatewayProperties gatewayProperties) {
        return (serverWebExchange, e) -> {
            Result<Void> result = new Result<>(gatewayProperties.getAccessDeniedCode(), e.getMessage());
            return write(result, serverWebExchange.getResponse(), e);
        };
    }


    /**
     * 无效token/token过期 自定义响应
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @return a {@link org.springframework.security.web.server.ServerAuthenticationEntryPoint} object
     */
    @Bean
    public ServerAuthenticationEntryPoint customServerAuthenticationEntryPoint(GatewayProperties gatewayProperties) {

        return (serverWebExchange, e) -> {
            MultiValueMap<String, String> stateMap = serverWebExchange.getRequest().getQueryParams();
            String state = "";
            if (!CollectionUtils.isEmpty(stateMap)) {
                state = stateMap.getFirst(GatewayConstant.QUERY_PARAMS);
            }
            if (StringUtils.isBlank(state)) {
                state = serverWebExchange.getRequest().getHeaders().getFirst(GatewayConstant.QUERY_PARAMS);
            }
            String url = gatewayProperties.getLoginStateUris().get(state);
            Result<String> result = new Result<>(gatewayProperties.getAuthFailCode(), e.getMessage());
            result.setData(url);
            return write(result, serverWebExchange.getResponse(), e);
        };
    }

    /**
     * 输出错误信息
     *
     * @param body     body
     * @param response response
     * @param e        e
     * @return Mono
     */
    private Mono<Void> write(Result<?> body, ServerHttpResponse response, Exception e) {
        log.info("当前用户访问当前地址失败!错误信息为:[{}]", e.getMessage());
        String resultStr = JsonUtil.toJson(body);
        if (ObjectUtils.isEmpty(resultStr)) {
            resultStr = "error";
        }
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set("Cache-Control", "no-cache");
        DataBuffer buffer = response.bufferFactory().wrap(resultStr.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }


}
