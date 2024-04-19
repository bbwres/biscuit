package cn.bbwres.biscuit.gateway.authorization;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.constants.GatewayConstant;
import cn.bbwres.biscuit.utils.JsonUtil;
import cn.bbwres.biscuit.dto.Result;
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
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
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
@EnableConfigurationProperties(GatewayProperties.class)
public class ResourceServerConfig {


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
     * @param http a {@link org.springframework.security.config.web.server.ServerHttpSecurity} object
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @param authorizationManager a {@link cn.bbwres.biscuit.gateway.authorization.AuthorizationManager} object
     * @param customServerAccessDeniedHandler a {@link org.springframework.security.web.server.authorization.ServerAccessDeniedHandler} object
     * @param customServerAuthenticationEntryPoint a {@link org.springframework.security.web.server.ServerAuthenticationEntryPoint} object
     * @param jwtReactiveAuthenticationManager a {@link org.springframework.security.authentication.ReactiveAuthenticationManager} object
     * @param reactiveOpaqueTokenIntrospector a {@link org.springframework.beans.factory.ObjectProvider} object
     * @return a {@link org.springframework.security.web.server.SecurityWebFilterChain} object
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            GatewayProperties gatewayProperties,
                                                            AuthorizationManager authorizationManager,
                                                            ServerAccessDeniedHandler customServerAccessDeniedHandler,
                                                            ServerAuthenticationEntryPoint customServerAuthenticationEntryPoint,
                                                            ReactiveAuthenticationManager jwtReactiveAuthenticationManager,
                                                            ObjectProvider<ReactiveOpaqueTokenIntrospector> reactiveOpaqueTokenIntrospector) {
        ServerHttpSecurity.OAuth2ResourceServerSpec serverSpec = http.oauth2ResourceServer()
                .bearerTokenConverter(new ServerBearerTokenAuthenticationConverter());
        if (gatewayProperties.getUseJwtToken()) {
            serverSpec.jwt().authenticationManager(jwtReactiveAuthenticationManager);
        } else {
            ReactiveOpaqueTokenIntrospector introspector = reactiveOpaqueTokenIntrospector.getIfAvailable();
            Assert.notNull(introspector, "不使用jwtToken时，未初始化ReactiveOpaqueTokenIntrospector处理类");
            serverSpec.opaqueToken().introspector(introspector);
        }


        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http.authorizeExchange();
        if (ArrayUtils.isNotEmpty(gatewayProperties.getNoAuthUris())) {
            //无需登录鉴权
            authorizeExchangeSpec.pathMatchers(gatewayProperties.getNoAuthUris()).permitAll();
        }
        if (ArrayUtils.isNotEmpty(gatewayProperties.getLoginAuthUris())) {
            //登录鉴权
            authorizeExchangeSpec.pathMatchers(gatewayProperties.getLoginAuthUris()).authenticated();
        }


        authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll()
                //鉴权管理器配置
                .anyExchange().access(authorizationManager).and().exceptionHandling()
                //处理未授权
                .accessDeniedHandler(customServerAccessDeniedHandler)
                //处理未认证
                .authenticationEntryPoint(customServerAuthenticationEntryPoint).and().csrf().disable().cors().disable();
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
     * @param body
     * @param response
     * @param e
     * @return
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
