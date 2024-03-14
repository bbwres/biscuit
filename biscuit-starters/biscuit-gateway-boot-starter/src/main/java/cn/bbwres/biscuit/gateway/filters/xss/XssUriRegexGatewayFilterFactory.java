package cn.bbwres.biscuit.gateway.filters.xss;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * xss 过滤器
 *
 * @author zhanglinfeng
 */
@Slf4j
public class XssUriRegexGatewayFilterFactory extends AbstractGatewayFilterFactory<XssUriRegexGatewayFilterFactory.Config> {

    private static final Set<HttpMethod> SUPPORT_METHODS = new HashSet<>(Arrays.asList(HttpMethod.POST, HttpMethod.GET, HttpMethod.PUT, HttpMethod.PATCH));

    /**
     * Regex key.
     */
    public static final String REGEX_KEY = "regex";


    private final ServerCodecConfigurer serverCodecConfigurer;

    public XssUriRegexGatewayFilterFactory(ServerCodecConfigurer serverCodecConfigurer) {
        super(Config.class);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(REGEX_KEY);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            HttpMethod method = serverHttpRequest.getMethod();
            String contentType = serverHttpRequest.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

            // 不支持的方法
            if (!SUPPORT_METHODS.contains(method)) {
                return chain.filter(exchange);
            }
            // 校验uri是否符合
            String regex = config.getRegex();
            String uri = serverHttpRequest.getURI().getPath();
            if (!uri.matches(regex)) {
                return chain.filter(exchange);
            }
            // contentType 为空不处理
            if (StringUtils.isEmpty(contentType)) {
                return chain.filter(exchange);
            }
            String lowerContentType = contentType.toLowerCase();

            // contentType 必须是参数或文本类型
            if (!MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)
                    && !lowerContentType.contains("json") && !lowerContentType.contains("text") && !lowerContentType.contains("xml")) {
                return chain.filter(exchange);
            }
            log.info("当前请求的CONTENT_TYPE为:{}", contentType);
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(serverHttpRequest.getQueryParams().size());
            for (Map.Entry<String, List<String>> entry : serverHttpRequest.getQueryParams().entrySet()) {
                queryParams.addAll(entry.getKey(), entry.getValue().stream().map(XSSUtils::stripXSS).collect(Collectors.toList()));
            }

            // 参考api文档中GatewapFilter中“修改请求消息体拦截器”：ModifyRequestBodyGatewayFilterFactory.java
            ServerRequest serverRequest = ServerRequest.create(exchange, serverCodecConfigurer.getReaders());
            Mono<String> rawBody = serverRequest.bodyToMono(String.class).map(new Function<String, String>() {
                @Override
                public String apply(String s) {
                    log.info("转换之前的报文为:[{}]", s);
                    String msg = XSSUtils.stripXSS(s);
                    log.info("转换之后的报文为:[{}]", msg);
                    return msg;
                }
            });

            BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(rawBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());

            // the new content type will be computed by bodyInserter
            // and then set in the request decorator
            headers.remove(HttpHeaders.CONTENT_LENGTH);

            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers, queryParams);

            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                    .then(Mono.defer(() -> {
                        ServerHttpRequest decorator = decorate(exchange, headers,
                                outputMessage);
                        return chain.filter(exchange.mutate().request(decorator).build());
                    }));
        };
    }


    ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    // httpbin.org
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }

            @Override
            public MultiValueMap<String, String> getQueryParams() {
                return outputMessage.getQueryParams();
            }
        };
    }

    @Data
    public static class Config {
        private String regex;
    }
}