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

package cn.bbwres.biscuit.utils.webclient.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * 自定义负载均衡过滤器：仅处理 lb:// 前缀的请求
 *
 * @author zhanglinfeng
 * @since 2025-12-30
 */
@Slf4j
public class LbPrefixOnlyLoadBalancerFilter implements ExchangeFilterFunction {

    private static final String LB_PREFIX = "lb://";

    /**
     * 注入原生负载均衡过滤器
     */
    private final ReactorLoadBalancerExchangeFilterFunction delegate;

    public LbPrefixOnlyLoadBalancerFilter(ReactorLoadBalancerExchangeFilterFunction delegate) {
        this.delegate = delegate;
    }

    /**
     * 处理
     *
     * @param request the current request
     * @param next    the next exchange function in the chain
     * @return Mono<ClientResponse>
     */
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // 1. 获取请求的 URL 字符串
        String requestUrl = request.url().toString();

        // 2. 判断 URL 是否以 lb://
        if (requestUrl.startsWith(LB_PREFIX)) {
            log.debug("requestUrl  startsWith {}, execute ReactorLoadBalancerExchangeFilterFunction", LB_PREFIX);
            return this.delegate.filter(request, next);
        } else {
            log.debug("requestUrl no startsWith {}", LB_PREFIX);
            return next.exchange(request);
        }
    }
}