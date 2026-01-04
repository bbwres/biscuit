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

package cn.bbwres.biscuit.utils.webclient;

import cn.bbwres.biscuit.utils.webclient.config.WebClientProperties;
import cn.bbwres.biscuit.utils.webclient.filter.LbPrefixOnlyLoadBalancerFilter;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.List;
import java.util.Objects;

@AutoConfiguration
@EnableConfigurationProperties(WebClientProperties.class)
public class WebClientAutoConfiguration {


    /**
     * 底层链接池配置
     *
     * @param webClientProperties 配置信息
     * @return ConnectionProvider
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(ConnectionProvider.class)
    public ConnectionProvider connectionProvider(WebClientProperties webClientProperties) {
        ConnectionProvider.Builder connectionProviderBuilder = ConnectionProvider.builder(this.getClass().getSimpleName());
        if (webClientProperties.getMaxConnections() != null) {
            connectionProviderBuilder.maxConnections(webClientProperties.getMaxConnections());
        }
        if (webClientProperties.getMaxIdleTime() != null) {
            connectionProviderBuilder.maxIdleTime(webClientProperties.getMaxIdleTime());
        }
        if (webClientProperties.getMaxLifeTime() != null) {
            connectionProviderBuilder.maxLifeTime(webClientProperties.getMaxLifeTime());
        }
        if (webClientProperties.getPendingAcquireTimeout() != null) {
            connectionProviderBuilder.pendingAcquireTimeout(webClientProperties.getPendingAcquireTimeout());
        }
        if (webClientProperties.getPendingAcquireMaxCount() != null) {
            connectionProviderBuilder.pendingAcquireMaxCount(webClientProperties.getPendingAcquireMaxCount());
        }
        return connectionProviderBuilder.build();
    }

    /**
     * 底层客户端配置
     *
     * @param webClientProperties 配置信息
     * @return HttpClient
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HttpClient.class)
    public HttpClient httpClient(WebClientProperties webClientProperties,
                                 ConnectionProvider connectionProvider) {
        HttpClient httpClient = HttpClient.create(connectionProvider);
        if (Objects.nonNull(webClientProperties.getWiretap()) && webClientProperties.getWiretap()) {
            httpClient.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
        }
        if (Objects.nonNull(webClientProperties.getConnectTimeoutMillis())) {
            httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeoutMillis());
        }
        if (Objects.nonNull(webClientProperties.getResponseTimeout())) {
            httpClient.responseTimeout(webClientProperties.getResponseTimeout());
        }

        httpClient.doOnConnected(conn -> {
                    if (Objects.nonNull(webClientProperties.getReadTimeoutSeconds())) {
                        conn.addHandlerLast(new ReadTimeoutHandler(webClientProperties.getReadTimeoutSeconds()));
                    }

                    if (Objects.nonNull(webClientProperties.getWriteTimeoutSeconds())) {
                        conn.addHandlerLast(new WriteTimeoutHandler(webClientProperties.getWriteTimeoutSeconds()));
                    }
                }
        );
        return httpClient;
    }


    /**
     * 负载均衡过滤器
     *
     * @param reactorLoadBalancerExchangeFilterFunction 负载均衡过滤器
     * @return LbPrefixOnlyLoadBalancerFilter
     */
    @Bean
    @ConditionalOnClass(ReactorLoadBalancerExchangeFilterFunction.class)
    public LbPrefixOnlyLoadBalancerFilter lbPrefixOnlyLoadBalancerFilter(ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction) {
        return new LbPrefixOnlyLoadBalancerFilter(reactorLoadBalancerExchangeFilterFunction);
    }


    /**
     * 初始化webclient 客户端
     *
     * @param webClientProperties        webClientProperties
     * @param httpClient                 httpclient
     * @param builderObjectProvider      WebClient.Builder
     * @param filterFunctionListProvider filterFunctionListProvider
     * @return WebClient
     */
    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(WebClientProperties webClientProperties, HttpClient httpClient,
                               ObjectProvider<WebClient.Builder> builderObjectProvider,
                               ObjectProvider<List<ExchangeFilterFunction>> filterFunctionListProvider) {

        WebClient.Builder builder = builderObjectProvider.getIfAvailable(WebClient::builder);
        List<ExchangeFilterFunction> functionList = filterFunctionListProvider.getIfAvailable(List::of);
        builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        if (!CollectionUtils.isEmpty(functionList)) {
            builder.filters(exchangeFilterFunctions -> exchangeFilterFunctions.addAll(functionList));
        }
        if (Objects.nonNull(webClientProperties.getMaxInMemorySize())) {
            builder.codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(webClientProperties.getMaxInMemorySize()));
        }

        return builder.build();
    }


}
