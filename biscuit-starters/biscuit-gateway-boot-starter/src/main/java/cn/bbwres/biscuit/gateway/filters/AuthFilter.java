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

package cn.bbwres.biscuit.gateway.filters;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.constants.GatewayConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 认证服务透传处理
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final GatewayProperties gatewayProperties;

    /**
     * {@inheritDoc}
     *
     * Process the Web request and (optionally) delegate to the next {@code WebFilter}
     * through the given {@link GatewayFilterChain}.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("处理用户认证参数信息");
        exchange = removeHeaders(exchange, gatewayProperties.getUserTokenHeader(), gatewayProperties.getUserInfoHeader());
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder builder = request.mutate();
        Object userToken = exchange.getAttributes().get(GatewayConstant.USER_TOKEN);
        if (Objects.nonNull(userToken)) {
            builder.header(gatewayProperties.getUserTokenHeader(), userToken.toString());
        }
        Object userInfo = exchange.getAttributes().get(GatewayConstant.USER_INFO);
        if (Objects.nonNull(userInfo)) {
            builder.header(gatewayProperties.getUserInfoHeader(), userInfo.toString());
        }
        return chain.filter(exchange.mutate().request(request).build());
    }


    /**
     * 移除请求头的用户
     *
     * @param exchange 请求
     * @return 请求
     */
    private ServerWebExchange removeHeaders(ServerWebExchange exchange, String... headers) {
        if (ArrayUtils.isEmpty(headers)) {
            return exchange;
        }
        List<String> headerList = Arrays.stream(headers)
                .filter(header -> exchange.getRequest().getHeaders().containsKey(header))
                .collect(Collectors.toList());
        // 如果包含，则移除。参考 RemoveRequestHeaderGatewayFilterFactory 实现
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    for (String hh : headerList) {
                        httpHeaders.remove(hh);
                    }

                }).build();
        return exchange.mutate().request(request).build();
    }

    /**
     * {@inheritDoc}
     *
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 9002;
    }
}
