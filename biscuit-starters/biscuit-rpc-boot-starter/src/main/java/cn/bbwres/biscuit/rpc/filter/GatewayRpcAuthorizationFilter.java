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

package cn.bbwres.biscuit.rpc.filter;

import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import cn.bbwres.biscuit.rpc.properties.SecurityProperties;
import cn.bbwres.biscuit.rpc.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR;

/**
 * 网关服务认证过滤器
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class GatewayRpcAuthorizationFilter implements GlobalFilter, Ordered {

    private final RpcProperties rpcProperties;

    /**
     * <p>Constructor for GatewayRpcAuthorizationFilter.</p>
     *
     * @param rpcProperties a {@link cn.bbwres.biscuit.rpc.properties.RpcProperties} object
     */
    public GatewayRpcAuthorizationFilter(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }

    /** Constant <code>RPC_AUTHORIZATION_FILTER_ORDER=10151</code> */
    public static final int RPC_AUTHORIZATION_FILTER_ORDER = 10151;

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
        return RPC_AUTHORIZATION_FILTER_ORDER;
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        SecurityProperties securityProperties = rpcProperties.getSecurity();
        if (Objects.isNull(securityProperties) || !securityProperties.isEnable()) {
            log.debug("当前远程调用安全配置未开启!");
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        //获取服务端
        Response<ServiceInstance> response = exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR);
        if (Objects.isNull(response) || !response.hasServer()) {
            log.debug("当前请求没有获取到可用的服务列表信息!");
            return chain.filter(exchange);
        }
        ServiceInstance serviceInstance = response.getServer();
        Map<String, List<String>> stringListMap = SecurityUtils.putHeaderAuthorizationInfo(serviceInstance);
        if (!CollectionUtils.isEmpty(stringListMap)) {
            ServerHttpRequest.Builder serverHttpRequestBuilder = request.mutate();
            for (String headerName : stringListMap.keySet()) {
                serverHttpRequestBuilder.header(headerName, stringListMap.get(headerName).toArray(new String[]{}));
            }
            request = serverHttpRequestBuilder.build();
        }
        return chain.filter(exchange.mutate().request(request).build());
    }
}
