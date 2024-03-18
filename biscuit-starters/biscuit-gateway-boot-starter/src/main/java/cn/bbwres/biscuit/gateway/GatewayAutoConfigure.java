package cn.bbwres.biscuit.gateway;

import cn.bbwres.biscuit.gateway.adapter.ErrorWebExceptionHandler;
import cn.bbwres.biscuit.gateway.adapter.ExtensionErrorAttributes;
import cn.bbwres.biscuit.gateway.authorization.AuthorizationManager;
import cn.bbwres.biscuit.gateway.cache.ResourceCacheService;
import cn.bbwres.biscuit.gateway.router.RouterController;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
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
 */
@Slf4j
@AutoConfiguration
@EnableWebFluxSecurity
@EnableConfigurationProperties({GatewayProperties.class})
public class GatewayAutoConfigure {


    /**
     * gateway 统一错误处理
     *
     * @return
     */
    @Bean
    public ExtensionErrorAttributes errorAttributes(GatewayProperties gatewayProperties,
                                                    ObjectProvider<MessageSourceAccessor> messagesProvider) {
        return new ExtensionErrorAttributes(gatewayProperties, messagesProvider.getIfAvailable());
    }

    /**
     * 统一错误处理器
     *
     * @return
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
     * @param resourceCacheService
     * @return
     */
    @Bean
    public AuthorizationManager authorizationManager(ResourceCacheService resourceCacheService,
                                                     PathMatcher pathMatcher) {
        return new AuthorizationManager(resourceCacheService, pathMatcher);
    }

    /**
     * 资源缓存服务
     *
     * @return
     */
    @Bean
    public ResourceCacheService resourceCacheService(GatewayProperties gatewayProperties,
                                                     ResourceService resourceService) {
        return new ResourceCacheService(gatewayProperties, resourceService);
    }


    /**
     * 设置获取token的转换器
     *
     * @return
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
     * @return
     */
    @Bean
    public PathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    /**
     * 空的SessionManager
     *
     * @return
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
     * @param routeLocator
     * @return
     */
    @Bean
    public RouterController routerController(RouteLocator routeLocator) {
        return new RouterController(routeLocator);
    }
}
