package cn.bbwres.biscuit.gateway.adapter;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.exception.AccessRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 异常信息处理
 *
 * @author zhanglinfeng
 */
@Slf4j
@RequiredArgsConstructor
public class ExtensionErrorAttributes extends DefaultErrorAttributes {
    /**
     * 接口请求
     */
    private static final String IS_ACCESS = "IS_ACCESS";


    private final GatewayProperties gatewayProperties;

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        log.warn("当前请求发生异常!", error);
        Boolean isAccess = request.exchange().getAttribute(IS_ACCESS);
        if (isAccess != null && isAccess) {
            if (error instanceof AccessRequestException) {
                //接口请求异常
                errorAttributes.put("status", ((AccessRequestException) error).getStatus().value());
                errorAttributes.put("error", error.getMessage());
            }
            return errorAttributes;
        }
        //异常时清除mdc
        MDC.clear();
        errorAttributes.put("status", 200);
        errorAttributes.put("code", gatewayProperties.getSystemErrCode());
        errorAttributes.put("msg", error.getMessage());
        return errorAttributes;
    }
}
