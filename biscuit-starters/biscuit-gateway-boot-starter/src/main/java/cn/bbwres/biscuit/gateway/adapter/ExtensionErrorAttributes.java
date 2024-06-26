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

package cn.bbwres.biscuit.gateway.adapter;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.gateway.GatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 异常信息处理
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@RequiredArgsConstructor
public class ExtensionErrorAttributes extends DefaultErrorAttributes {

    private static final String RESULT_CODE = "resultCode";
    private static final String RESULT_MSG = "resultMsg";

    private final GatewayProperties gatewayProperties;

    /**
     * 国际化配置
     */
    private final MessageSourceAccessor messages;


    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        log.warn("当前请求发生异常!{}", error.getMessage());
        String message = error.getMessage();
        String errorCode = getErrorCode(error);

        //异常时清除mdc
        MDC.clear();
        errorAttributes.put(RESULT_CODE, errorCode);
        if (!ObjectUtils.isEmpty(messages)) {
            String language = request.headers().firstHeader("Accept-Language");
            Locale locale = LocaleContextHolder.getLocale();
            if (language != null) {
                locale = Locale.forLanguageTag(language);
            }
            message = messages.getMessage(errorCode, message, locale);
        }
        errorAttributes.put(RESULT_MSG, message);
        return errorAttributes;
    }

    /**
     * 获取错误码
     *
     * @param error
     * @return
     */
    private String getErrorCode(Throwable error) {
        String errorCode = gatewayProperties.getSystemErrCode();

        if (error instanceof SystemRuntimeException) {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) error;
            errorCode = systemRuntimeException.getErrorCode();
        }

        if (error instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) error;
            errorCode = GlobalErrorCodeConstants.GLOBAL_HTTP_CODE_PREFIX.getCode() + responseStatusException.getRawStatusCode();
        }
        return errorCode;
    }

}
