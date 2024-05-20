package cn.bbwres.biscuit.rpc;

import cn.bbwres.biscuit.rpc.filter.GatewayRpcAuthorizationFilter;
import cn.bbwres.biscuit.rpc.filter.RpcLoadBalancerRequestTransformer;
import cn.bbwres.biscuit.rpc.metadata.RegistrationBeanPostProcessor;
import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import cn.bbwres.biscuit.rpc.web.RpcServerHandlerInterceptorAdapter;
import cn.bbwres.biscuit.rpc.web.RpcWebAppConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

/**
 * rpc 的自动配置类
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({RpcProperties.class})
public class RpcAutoConfigure {


    /**
     * 网关配置项
     */
    @ConditionalOnClass({GatewayProperties.class})
    protected static class GatewayAppConfigurer {
        /**
         * 网关路由转发时鉴权过滤器
         *
         * @param rpcProperties rpc 配置参数
         * @return 网关过滤器
         */
        @Bean
        public GatewayRpcAuthorizationFilter gatewayRpcAuthorizationFilter(RpcProperties rpcProperties) {
            return new GatewayRpcAuthorizationFilter(rpcProperties);
        }
    }


    /**
     * web mvc 配置
     */
    @ConditionalOnClass(DelegatingWebMvcConfiguration.class)
    protected static class WebAppConfigurer {

        /**
         * rpcWebAppConfigurer 配置
         *
         * @param rpcServerHandlerInterceptorAdapter 拦截器
         * @return RpcWebAppConfigurer
         */
        @Bean
        public RpcWebAppConfigurer rpcWebAppConfigurer(RpcServerHandlerInterceptorAdapter rpcServerHandlerInterceptorAdapter) {
            return new RpcWebAppConfigurer(rpcServerHandlerInterceptorAdapter);
        }

        /**
         * 初始化web拦截器
         *
         * @param serviceInstance 应用实例
         * @param rpcProperties   rpc参数
         * @return 拦截器
         */
        @Bean
        public RpcServerHandlerInterceptorAdapter rpcServerHandlerInterceptorAdapter(ServiceInstance serviceInstance, RpcProperties rpcProperties) {
            return new RpcServerHandlerInterceptorAdapter(serviceInstance, rpcProperties);
        }
    }


    /**
     * 服务注册之前的处理
     *
     * @return RegistrationBeanPostProcessor
     */
    @Bean
    public RegistrationBeanPostProcessor registrationBeanPostProcessor() {
        return new RegistrationBeanPostProcessor();
    }

    /**
     * RpcLoadBalancerRequestTransformer 负载均衡参数增强
     *
     * @return RpcLoadBalancerRequestTransformer
     */
    @Bean
    @ConditionalOnBean(LoadBalancerClientFactory.class)
    public RpcLoadBalancerRequestTransformer rpcLoadBalancerRequestTransformer() {
        return new RpcLoadBalancerRequestTransformer();
    }

}
