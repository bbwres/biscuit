package cn.bbwres.biscuit.rpc.filter;

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR;

/**
 * 网关服务认证过滤器
 *
 * @author zhanglinfeng
 */
@Slf4j
public class GatewayRpcAuthorizationFilter implements GlobalFilter, Ordered {

    private final RpcProperties rpcProperties;

    public GatewayRpcAuthorizationFilter(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }

    public static final int RPC_AUTHORIZATION_FILTER_ORDER = 10151;

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return RPC_AUTHORIZATION_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        SecurityProperties securityProperties = rpcProperties.getSecurity();
        if (Objects.isNull(securityProperties) || !securityProperties.isEnable()) {
            log.debug("当前远程调用安全配置未开启!");
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder builder = request.mutate();
        //获取服务端
        Response<ServiceInstance> response = exchange.getAttribute(GATEWAY_LOADBALANCER_RESPONSE_ATTR);
        if (Objects.isNull(response) || !response.hasServer()) {
            log.debug("当前请求没有获取到可用的服务列表信息!");
            return chain.filter(exchange);
        }
        ServiceInstance serviceInstance = response.getServer();
        String clientPassword = serviceInstance.getMetadata().get(RpcConstants.CLIENT_PASSWORD);
        String clientName = serviceInstance.getServiceId();
        //加密算法的实现
        String currentTimeMillis = System.currentTimeMillis() + "";
        String authorization = SecurityUtils.hashDataInfo(clientName, clientPassword, currentTimeMillis);
        builder.header(RpcConstants.AUTHORIZATION_HEADER_NAME, authorization);
        builder.header(RpcConstants.CLIENT_TIME_HEADER_NAME, currentTimeMillis);
        return chain.filter(exchange.mutate().request(request).build());
    }
}
