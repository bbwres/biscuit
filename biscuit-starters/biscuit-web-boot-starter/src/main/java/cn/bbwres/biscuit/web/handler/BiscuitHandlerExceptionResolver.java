package cn.bbwres.biscuit.web.handler;

import cn.bbwres.biscuit.exception.ParamsCheckRuntimeException;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.vo.Result;
import cn.bbwres.biscuit.web.i18n.BiscuitWebMessageSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * web 异常处理类
 *
 * @author zhanglinfeng
 */
public class BiscuitHandlerExceptionResolver extends AbstractHandlerMethodExceptionResolver {

    protected MessageSourceAccessor messages = BiscuitWebMessageSource.getAccessor();
    private final ObjectMapper objectMapper;

    public BiscuitHandlerExceptionResolver(ObjectMapper objectMapper) {
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

        String message = ex.getMessage();
        String errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value() + "";

        if (ex instanceof SystemRuntimeException) {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) ex;
            errorCode = systemRuntimeException.getErrorCode();
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof MissingServletRequestParameterException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof IllegalArgumentException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof BindException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof ConstraintViolationException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof ValidationException) {
            errorCode = ParamsCheckRuntimeException.DEFAULT_ERROR_CODE;
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof NoHandlerFoundException) {
            errorCode = HttpStatus.NOT_FOUND.value() + "";
            return resultModelAndView(errorCode, message);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            errorCode = HttpStatus.METHOD_NOT_ALLOWED.value() + "";
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
        // 国际化处理
        message = messages.getMessage(errorCode, null, message);
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView(objectMapper));
        Result<Void> result = new Result<>(errorCode, message);
        modelAndView.addObject(result);
        return modelAndView;
    }


}
