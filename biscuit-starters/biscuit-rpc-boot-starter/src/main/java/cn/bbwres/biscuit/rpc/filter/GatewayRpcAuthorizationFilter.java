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

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmContainer;
import cn.bbwres.biscuit.rpc.security.RpcSecurityAlgorithmSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
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


    private final RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer;

    /**
     * <p>Constructor for GatewayRpcAuthorizationFilter.</p>
     *
     * @param rpcSecurityAlgorithmContainer a {@link RpcSecurityAlgorithmContainer} object
     */
    public GatewayRpcAuthorizationFilter(RpcSecurityAlgorithmContainer rpcSecurityAlgorithmContainer) {
        this.rpcSecurityAlgorithmContainer = rpcSecurityAlgorithmContainer;
    }

    /**
     * Constant <code>RPC_AUTHORIZATION_FILTER_ORDER=10151</code>
     */
    public static final int RPC_AUTHORIZATION_FILTER_ORDER = 10151;

    /**
     * {@inheritDoc}
     * <p>
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return RPC_AUTHORIZATION_FILTER_ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //获取服务端
        Response<ServiceInstance> response = exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR);
        if (Objects.isNull(response) || !response.hasServer()) {
            log.debug("当前请求没有获取到可用的服务列表信息!");
            return chain.filter(exchange);
        }
        ServiceInstance serviceInstance = response.getServer();
        String securityAlgorithm = serviceInstance.getMetadata().get(RpcConstants.SERVICE_SECURITY_ALGORITHM);
        if (ObjectUtils.isEmpty(securityAlgorithm)) {
            log.debug("当前请求的服务端没有设置安全信息!请求服务端:[{}]", serviceInstance.getInstanceId());
            return chain.filter(exchange);
        }
        RpcSecurityAlgorithmSupport rpcSecurityAlgorithmSupport = rpcSecurityAlgorithmContainer.getRpcSecurityAlgorithmSupport(securityAlgorithm, true);
        Map<String, List<String>> stringListMap = rpcSecurityAlgorithmSupport.putHeaderAuthorizationInfo(serviceInstance, request.getURI().getPath());
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
