package cn.bbwres.biscuit.gateway.filters.xss;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;


/**
 * 缓存当前请求，解决requestBody只能读取一次
 *
 * @author zhanglinfeng
 */
public class CachedBodyOutputMessage implements ReactiveHttpOutputMessage {

    private final DataBufferFactory bufferFactory;

    private final HttpHeaders httpHeaders;

    private final MultiValueMap<String, String> queryParams;

    private Flux<DataBuffer> body = Flux.error(new IllegalStateException(
            "The body is not set. " + "Did handling complete with success?"));

    public CachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders, MultiValueMap<String, String> queryParams) {
        this.bufferFactory = exchange.getResponse().bufferFactory();
        this.httpHeaders = httpHeaders;
        this.queryParams = queryParams;
    }

    @Override
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    /**
     * Return the request body, or an error stream if the body was never set or when.
     *
     * @return body as {@link Flux}
     */
    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    public MultiValueMap<String, String> getQueryParams() {
        return this.queryParams;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        this.body = Flux.from(body);
        return Mono.empty();
    }

    @Override
    public Mono<Void> writeAndFlushWith(
            Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMap(p -> p));
    }

    @Override
    public Mono<Void> setComplete() {
        return writeWith(Flux.empty());
    }

}