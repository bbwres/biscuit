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

package cn.bbwres.biscuit.web.handler;

import cn.bbwres.biscuit.exception.ExceptionConvertErrorCode;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * web 异常处理类
 *
 * @author zhanglinfeng
 */
public class BiscuitHandlerExceptionResolver extends AbstractHandlerMethodExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(BiscuitHandlerExceptionResolver.class);

    protected MessageSourceAccessor messages;
    private final ObjectMapper objectMapper;

    private final List<ExceptionConvertErrorCode> exceptionConvertErrorCodes;

    public BiscuitHandlerExceptionResolver(ObjectMapper objectMapper, ObjectProvider<MessageSourceAccessor> messagesProvider, List<ExceptionConvertErrorCode> exceptionConvertErrorCodes) {
        this.messages = messagesProvider.getIfAvailable();
        this.objectMapper = objectMapper;
        this.exceptionConvertErrorCodes = exceptionConvertErrorCodes;
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
        String message = null;
        String errorCode = null;

        if (ex instanceof SystemRuntimeException) {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) ex;
            message = ObjectUtils.isEmpty(ex.getMessage()) ? GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getMessage() : ex.getMessage();
            errorCode = systemRuntimeException.getErrorCode();
            return resultModelAndView(errorCode, message);
        }

        for (ExceptionConvertErrorCode exceptionConvertErrorCode : exceptionConvertErrorCodes) {
            errorCode = exceptionConvertErrorCode.exceptionConvertErrorCode(ex);
            if (!ObjectUtils.isEmpty(errorCode)) {
                break;
            }
        }
        for (ExceptionConvertErrorCode exceptionConvertErrorCode : exceptionConvertErrorCodes) {
            message = exceptionConvertErrorCode.exceptionConvertErrorMessage(ex);
            if (!ObjectUtils.isEmpty(message)) {
                break;
            }
        }
        message = ObjectUtils.isEmpty(message) ? GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getMessage() : message;
        errorCode = ObjectUtils.isEmpty(errorCode) ? GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode() : errorCode;
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
            message = messages.getMessage(message, null, message);
        }
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView(objectMapper));
        modelAndView.addObject("resultCode", errorCode);
        modelAndView.addObject("resultMsg", message);
        return modelAndView;
    }


}
