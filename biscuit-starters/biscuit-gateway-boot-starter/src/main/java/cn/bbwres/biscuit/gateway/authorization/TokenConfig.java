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

import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import cn.bbwres.biscuit.gateway.service.ResourceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * token配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
public class TokenConfig {


    @Bean
    @ConditionalOnMissingBean
    public ResourceService resourceService(WebClient webClient, GatewayProperties gatewayProperties) {
        return new ResourceServiceImpl(webClient, gatewayProperties);
    }


    /**
     * webclient
     *
     * @param builder                                   WebClient.Builder
     * @param reactorLoadBalancerExchangeFilterFunction 负载均衡
     * @return
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder,
                               ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction) {

        return builder.filter(reactorLoadBalancerExchangeFilterFunction).build();
    }

    /**
     * 处理jwt token
     *
     * @param resourceService a {@link cn.bbwres.biscuit.gateway.service.ResourceService} object
     * @return a {@link org.springframework.security.authentication.ReactiveAuthenticationManager} object
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ResourceService resourceService) {
        return new ReactiveAuthenticationManager() {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return Mono.justOrEmpty(authentication)
                        .filter((a) -> a instanceof BearerTokenAuthenticationToken)
                        .cast(BearerTokenAuthenticationToken.class)
                        .map(BearerTokenAuthenticationToken::getToken)
                        .flatMap(this::makeRequest)
                        .flatMap(this::parseToken)
                        .cast(Authentication.class)
                        .onErrorMap(Exception.class, this::onError);
            }

            /**
             * 创建请求
             * @param token
             * @return
             */
            private Mono<UserBaseInfo> makeRequest(String token) {
                return resourceService.checkToken(token);
            }

            /**
             *
             * @param userBaseInfo
             * @return
             */
            private Mono<MapAuthentication> parseToken(UserBaseInfo userBaseInfo) {
                return Mono.justOrEmpty(new MapAuthentication(userBaseInfo));
            }

            /**
             * 转换失败处理
             * @param ex
             * @return
             */
            private SystemRuntimeException onError(Exception ex) {
                log.info("当前token请求处理失败!{}", ex.getMessage());
                if (ex instanceof SystemRuntimeException e) {
                    throw e;
                }
                if (ex instanceof BadJwtException) {
                    return new SystemRuntimeException(GlobalErrorCodeConstants.INVALID_TOKEN);
                }
                return new SystemRuntimeException(ex.getMessage());
            }
        };
    }

}
