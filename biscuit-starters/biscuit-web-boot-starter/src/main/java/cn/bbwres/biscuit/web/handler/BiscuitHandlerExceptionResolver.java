package cn.bbwres.biscuit.web.handler;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

/**
 * web 异常处理类
 *
 * @author zhanglinfeng
 */
public class BiscuitHandlerExceptionResolver extends AbstractHandlerMethodExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(BiscuitHandlerExceptionResolver.class);

    protected MessageSourceAccessor messages;
    private final ObjectMapper objectMapper;

    public BiscuitHandlerExceptionResolver(ObjectMapper objectMapper,
                                           ObjectProvider<MessageSourceAccessor> messagesProvider) {
        this.messages = messagesProvider.getIfAvailable();
        this.objectMapper = objectMapper;
    }

    /**
     * Actually resolve the given exception that got thrown during on handler execution,
     * returning a ModelAndView that represents a specific error page if appropriate.
     * <p>May be overridden in subclasses, in order to apply specific exception checks.
     * Note that this template method will be invoked <i>after</i> checking whether this
     * resolved applies ("mappedHandlers" etc), so an implementation may simply proceed
     * with its actual exception handling.
     *
     * @param request       current HTTP request
     * @param response      current HTTP response
     * @param handlerMethod the executed handler method, or {@code null} if none chosen at the time
     *                      of the exception (for example, if multipart resolution failed)
     * @param ex            the exception that got thrown during handler execution
     * @return a corresponding ModelAndView to forward to, or {@code null} for default processing
     */
    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception ex) {

        LOG.warn("request:[{}],exception:[{}]", request.getRequestURI(), ex.getMessage());
        String message = ex.getMessage();
        String errorCode = GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode();

        if (ex instanceof SystemRuntimeException) {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) ex;
            errorCode = systemRuntimeException.getErrorCode();
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof MissingServletRequestParameterException
                || ex instanceof IllegalArgumentException
                || ex instanceof MethodArgumentTypeMismatchException
                || ex instanceof BindException
                || ex instanceof ValidationException) {
            errorCode = GlobalErrorCodeConstants.BAD_REQUEST.getCode();
            message = GlobalErrorCodeConstants.BAD_REQUEST.getMessage();
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof NoHandlerFoundException) {
            errorCode = GlobalErrorCodeConstants.NOT_FOUND.getCode();
            message = GlobalErrorCodeConstants.NOT_FOUND.getMessage();
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            errorCode = GlobalErrorCodeConstants.METHOD_NOT_ALLOWED.getCode();
            message = GlobalErrorCodeConstants.METHOD_NOT_ALLOWED.getMessage();
            return resultModelAndView(errorCode, message);
        }
        return resultModelAndView(errorCode, message);
    }


    /**
     * 设置返回结果
     *
     * @param errorCode 错误码
     * @param message   错误描述
     * @return 返回结果
     */
    private ModelAndView resultModelAndView(String errorCode, String message) {
        if (!ObjectUtils.isEmpty(messages)) {
            // 国际化处理
            message = messages.getMessage(errorCode, null, message);
        }
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView(objectMapper));
        Result<Void> result = new Result<>(errorCode, message);
        modelAndView.addObject(result);
        return modelAndView;
    }


}
