package cn.bbwres.biscuit.gateway.filters;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.filters.xss.XssUriRegexGatewayFilterFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;

/**
 * Filters 配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class FiltersAutoConfigure {


    /**
     * 透传认证信息的过滤器
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @return a {@link cn.bbwres.biscuit.gateway.filters.AuthFilter} object
     */
    @Bean
    public AuthFilter authFilter(GatewayProperties gatewayProperties) {
        return new AuthFilter(gatewayProperties);
    }

    /**
     * 日志过滤器
     *
     * @return a {@link cn.bbwres.biscuit.gateway.filters.LogFilter} object
     */
    @Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }

    /**
     * xss 过滤器
     *
     * @param serverCodecConfigurer a {@link org.springframework.http.codec.ServerCodecConfigurer} object
     * @return a {@link cn.bbwres.biscuit.gateway.filters.xss.XssUriRegexGatewayFilterFactory} object
     */
    @Bean
    @ConditionalOnProperty(prefix = "biscuit.gateway", name = "use-xss-filter", havingValue = "true")
    public XssUriRegexGatewayFilterFactory xssUriRegexGatewayFilterFactory(ServerCodecConfigurer serverCodecConfigurer) {
        return new XssUriRegexGatewayFilterFactory(serverCodecConfigurer);
    }


}
