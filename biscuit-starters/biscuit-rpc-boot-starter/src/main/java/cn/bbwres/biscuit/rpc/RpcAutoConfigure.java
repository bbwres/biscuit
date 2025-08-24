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

package cn.bbwres.biscuit.rpc;

import cn.bbwres.biscuit.rpc.filter.GatewayRpcAuthorizationFilter;
import cn.bbwres.biscuit.rpc.filter.RpcLoadBalancerRequestTransformer;
import cn.bbwres.biscuit.rpc.metadata.RegistrationBeanPostProcessor;
import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import cn.bbwres.biscuit.rpc.security.*;
import cn.bbwres.biscuit.rpc.utils.SecurityUtil;
import cn.bbwres.biscuit.rpc.web.RpcServerHandlerInterceptorAdapter;
import cn.bbwres.biscuit.rpc.web.RpcWebAppConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

import java.util.List;

/**
 * rpc 的自动配置类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({RpcProperties.class, RpcSecurityProperties.class})
public class RpcAutoConfigure {

    /**
     * spring bean 的工具类
     *
     * @return SecurityUtil
     */
    @Bean
    public SecurityUtil securityUtil() {
        return new SecurityUtil();
    }


    /**
     * 安全算法容器
     *
     * @param rpcSecurityAlgorithmSupports 支持的安全算法
     * @return
     */
    @Bean
    public RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer(List<RpcSecurityAlgorithmSupport> rpcSecurityAlgorithmSupports) {
        return new RpcSecurityAlgorithmContainer(rpcSecurityAlgorithmSupports);
    }

    /**
     * md5算法支持
     *
     * @param rpcSecurityProperties
     * @param environment
     * @return
     */
    @Bean("rpcSecurityAlgorithmMd5Support")
    public RpcSecurityAlgorithmMd5Support rpcSecurityAlgorithmMd5Support(RpcSecurityProperties rpcSecurityProperties,
                                                                         Environment environment) {
        return new RpcSecurityAlgorithmMd5Support(rpcSecurityProperties, environment);
    }

    /**
     * sha1算法支持
     *
     * @param rpcSecurityProperties
     * @param environment
     * @return
     */
    @Bean("rpcSecurityAlgorithmSha1Support")
    public RpcSecurityAlgorithmSha1Support rpcSecurityAlgorithmSha1Support(RpcSecurityProperties rpcSecurityProperties,
                                                                           Environment environment) {
        return new RpcSecurityAlgorithmSha1Support(rpcSecurityProperties, environment);
    }

    /**
     * sha256算法支持
     *
     * @param rpcSecurityProperties
     * @param environment
     * @return
     */
    @Bean("rpcSecurityAlgorithmSha256Support")
    public RpcSecurityAlgorithmSha256Support rpcSecurityAlgorithmSha256Support(RpcSecurityProperties rpcSecurityProperties,
                                                                               Environment environment) {
        return new RpcSecurityAlgorithmSha256Support(rpcSecurityProperties, environment);
    }


    /**
     * 网关配置项
     */
    @ConditionalOnClass({GatewayProperties.class})
    protected static class GatewayAppConfigurer {
        /**
         * 网关路由转发时鉴权过滤器
         *
         * @param rpcSecurityAlgorithmContainer rpc 安全容器
         * @return 网关过滤器
         */
        @Bean
        public GatewayRpcAuthorizationFilter gatewayRpcAuthorizationFilter(RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
            return new GatewayRpcAuthorizationFilter(rpcSecurityAlgorithmContainer);
        }
    }

    /**
     * RpcLoadBalancerRequestTransformer 负载均衡参数增强
     *
     * @return RpcLoadBalancerRequestTransformer
     */
    @Bean
    @ConditionalOnBean(LoadBalancerClientFactory.class)
    public RpcLoadBalancerRequestTransformer rpcLoadBalancerRequestTransformer(RpcSecurityProperties rpcSecurityProperties,
                                                                               RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
        return new RpcLoadBalancerRequestTransformer(rpcSecurityProperties, rpcSecurityAlgorithmContainer);
    }


    /**
     * 是否启用安全配置
     */
    @ConditionalOnProperty(name = "biscuit.rpc.security.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(DelegatingWebMvcConfiguration.class)
    public static class SecurityConfigurer {


        /**
         * rpcWebAppConfigurer 配置
         *
         * @param rpcServerHandlerInterceptorAdapter 拦截器
         * @return RpcWebAppConfigurer
         */
        @Bean("rpcWebAppConfigurer")
        public RpcWebAppConfigurer rpcWebAppConfigurer(RpcServerHandlerInterceptorAdapter rpcServerHandlerInterceptorAdapter,
                                                       RpcSecurityProperties rpcSecurityProperties) {
            return new RpcWebAppConfigurer(rpcServerHandlerInterceptorAdapter, rpcSecurityProperties);
        }

        /**
         * 初始化web拦截器
         *
         * @param serviceInstance               应用实例
         * @param rpcSecurityProperties         rpc参数
         * @param rpcSecurityAlgorithmContainer 安全参数
         * @return 拦截器
         */
        @Bean
        public RpcServerHandlerInterceptorAdapter rpcServerHandlerInterceptorAdapter(ServiceInstance serviceInstance,
                                                                                     RpcSecurityProperties rpcSecurityProperties,
                                                                                     RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
            return new RpcServerHandlerInterceptorAdapter(serviceInstance, rpcSecurityProperties, rpcSecurityAlgorithmContainer);

        }


        /**
         * 服务注册之前的处理
         *
         * @return RegistrationBeanPostProcessor
         */
        @Bean
        public RegistrationBeanPostProcessor registrationBeanPostProcessor(RpcSecurityProperties rpcSecurityProperties,
                                                                           RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
            return new RegistrationBeanPostProcessor(rpcSecurityProperties, rpcSecurityAlgorithmContainer);
        }


    }


}
