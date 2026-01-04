package cn.bbwres.biscuit.utils.okhttp;

import cn.bbwres.biscuit.utils.okhttp.config.OkHttpClientProperties;
import cn.bbwres.biscuit.utils.okhttp.intercept.HttpLogInterceptor;
import cn.bbwres.biscuit.utils.okhttp.intercept.LbPrefixOnlyLoadBalancerIntercept;
import cn.bbwres.biscuit.utils.okhttp.intercept.OrderInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.*;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 * okhttp 基础配置
 *
 * @author zlf
 * @version 1.0
 * @since 2025-12-31  14:55
 */
@AutoConfiguration
@EnableConfigurationProperties(OkHttpClientProperties.class)
public class OkHttpAutoConfiguration {

    /**
     * 负载均衡过滤器
     *
     * @param okHttpClientProperties 配置参数
     * @return HttpLogInterceptor
     */
    @Bean
    @ConditionalOnBooleanProperty(prefix = "biscuit.utils.okhttp", name = "enable-log")
    public HttpLogInterceptor httpLogInterceptor(OkHttpClientProperties okHttpClientProperties) {
        return new HttpLogInterceptor(okHttpClientProperties.getLogTag(), okHttpClientProperties.getEnableLog());
    }


    /**
     * 负载均衡过滤器
     *
     * @param loadBalancer 负载均衡过滤器
     * @return LbPrefixOnlyLoadBalancerFilter
     */
    @Bean
    @ConditionalOnBean(LoadBalancerClient.class)
    public LbPrefixOnlyLoadBalancerIntercept lbPrefixOnlyLoadBalancerIntercept(LoadBalancerClient loadBalancer) {
        return new LbPrefixOnlyLoadBalancerIntercept(loadBalancer);
    }

    /**
     * ok http 客户端
     *
     * @param okHttpClientProperties okhttp 配置信息
     * @param builderProvider        ok http build
     * @param orderInterceptors      拦截器信息信息
     * @return OkHttpClient
     */
    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(OkHttpClientProperties okHttpClientProperties, ObjectProvider<OkHttpClient.Builder> builderProvider,
                                     ObjectProvider<List<OrderInterceptor>> orderInterceptors) {
        //初始化 build
        OkHttpClient.Builder builder = builderProvider.getIfAvailable(OkHttpClient.Builder::new);


        List<OrderInterceptor> interceptors = orderInterceptors.getIfAvailable(List::of);
        if (!CollectionUtils.isEmpty(interceptors)) {
            interceptors.stream().sorted(Comparator.comparingInt(Ordered::getOrder))
                    .forEach(builder::addInterceptor);
        }

        if (Objects.nonNull(okHttpClientProperties.getMaxIdleConnections())) {
            ConnectionPool connectionPool = new ConnectionPool(okHttpClientProperties.getMaxIdleConnections(),
                    okHttpClientProperties.getKeepAliveDuration(), TimeUnit.SECONDS);
            builder.connectionPool(connectionPool);
        }

        if (Objects.nonNull(okHttpClientProperties.getIgnoreHostnameVerifier()) && okHttpClientProperties.getIgnoreHostnameVerifier()) {
            //忽略 hostname 验证
            builder.hostnameVerifier((s, sslSession) -> true);
        }

        if (Objects.nonNull(okHttpClientProperties.getConnectTimeout())) {
            //设置连接超时时间
            builder.connectTimeout(okHttpClientProperties.getConnectTimeout(), TimeUnit.SECONDS);
        }
        if (Objects.nonNull(okHttpClientProperties.getReadTimeoutSeconds())) {
            //设置读超时时间
            builder.readTimeout(okHttpClientProperties.getReadTimeoutSeconds(), TimeUnit.SECONDS);
        }
        if (Objects.nonNull(okHttpClientProperties.getWriteTimeoutSeconds())) {
            //设置写超时时间
            builder.writeTimeout(okHttpClientProperties.getWriteTimeoutSeconds(), TimeUnit.SECONDS);
        }


        return builder.build();
    }


}
