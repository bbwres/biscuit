package cn.bbwres.biscuit.gateway.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 全局异常处理
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@Order(-2)
public class ErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public ErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                    WebProperties.Resources resources, ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
    }

    /**
     * {@inheritDoc}
     *
     * Create a {@link RouterFunction} that can route and handle errors as JSON responses
     * or HTML views.
     * <p>
     * If the returned {@link RouterFunction} doesn't route to a {@code HandlerFunction},
     * the original exception is propagated in the pipeline and can be processed by other
     * {@link WebExceptionHandler}s.
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 异常处理
     *
     * @param request
     * @return
     */
    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {

        final Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));

        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
