package cn.bbwres.biscuit.utils.okhttp.intercept;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import java.io.IOException;
import java.net.URI;

/**
 *
 * okhttp 负载均衡拦截器
 *
 * @author zlf
 * @version 1.0
 * @since 2025-12-31  15:18
 */
@Slf4j
public class LbPrefixOnlyLoadBalancerIntercept implements OrderInterceptor {

    private static final String LB_PREFIX = "lb://";

    private final LoadBalancerClient loadBalancer;


    public LbPrefixOnlyLoadBalancerIntercept(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    /**
     * 执行拦截处理
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @NotNull
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        // 1. 获取请求的 URL 字符串
        String requestUrl = chain.request().url().toString();

        // 2. 判断 URL 是否以 lb://
        if (requestUrl.startsWith(LB_PREFIX)) {
            log.debug("requestUrl  startsWith {}, execute ReactorLoadBalancerExchangeFilterFunction", LB_PREFIX);

            return lbHandler(chain);
        } else {
            log.debug("requestUrl no startsWith {}", LB_PREFIX);
            return chain.proceed(chain.request());
        }
    }

    /**
     * 负载请求处理
     * @param chain
     * @return
     * @throws IOException
     */
    public Response lbHandler(Interceptor.Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        URI originalUrl = request.url().uri();
        String serviceId = originalUrl.getHost();
        if (serviceId == null) {
            String message = String.format("Request URI does not contain a valid hostname: %s", originalUrl);
            if (log.isWarnEnabled()) {
                log.warn(message);
            }

            return new Response.Builder().code(400).message(message).build();
        }
        ServiceInstance instance = loadBalancer.choose(serviceId);
        URI uri = this.loadBalancer.reconstructURI(instance, originalUrl);
        request = request.newBuilder().url(uri.toString()).build();
        return  chain.proceed(request) ;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
