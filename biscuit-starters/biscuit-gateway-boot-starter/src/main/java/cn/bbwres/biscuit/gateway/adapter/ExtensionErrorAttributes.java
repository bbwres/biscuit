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
