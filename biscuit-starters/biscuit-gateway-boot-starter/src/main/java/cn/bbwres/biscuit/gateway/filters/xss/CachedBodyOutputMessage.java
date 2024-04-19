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
 * @version $Id: $Id
 */
public class CachedBodyOutputMessage implements ReactiveHttpOutputMessage {

    private final DataBufferFactory bufferFactory;

    private final HttpHeaders httpHeaders;

    private final MultiValueMap<String, String> queryParams;

    private Flux<DataBuffer> body = Flux.error(new IllegalStateException(
            "The body is not set. " + "Did handling complete with success?"));

    /**
     * <p>Constructor for CachedBodyOutputMessage.</p>
     *
     * @param exchange a {@link org.springframework.web.server.ServerWebExchange} object
     * @param httpHeaders a {@link org.springframework.http.HttpHeaders} object
     * @param queryParams a {@link org.springframework.util.MultiValueMap} object
     */
    public CachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders, MultiValueMap<String, String> queryParams) {
        this.bufferFactory = exchange.getResponse().bufferFactory();
        this.httpHeaders = httpHeaders;
        this.queryParams = queryParams;
    }

    /** {@inheritDoc} */
    @Override
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {

    }

    /** {@inheritDoc} */
    @Override
    public boolean isCommitted() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

    /** {@inheritDoc} */
    @Override
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    /**
     * Return the request body, or an error stream if the body was never set or when.
     *
     * @return body as {@link reactor.core.publisher.Flux}
     */
    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    /**
     * <p>Getter for the field <code>queryParams</code>.</p>
     *
     * @return a {@link org.springframework.util.MultiValueMap} object
     */
    public MultiValueMap<String, String> getQueryParams() {
        return this.queryParams;
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        this.body = Flux.from(body);
        return Mono.empty();
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Void> writeAndFlushWith(
            Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMap(p -> p));
    }

    /** {@inheritDoc} */
    @Override
    public Mono<Void> setComplete() {
        return writeWith(Flux.empty());
    }

}
