package cn.bbwres.biscuit.rpc.web;


import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import cn.bbwres.biscuit.rpc.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * rpc 服务端拦截器
 *
 * @author zhanglinfeng
 */
@Slf4j
@RequiredArgsConstructor
public class RpcServerHandlerInterceptorAdapter implements HandlerInterceptor {


    private final ServiceInstance serviceInstance;

    private final RpcProperties rpcProperties;


    /**
     * 拦截器处理
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return true
     * @throws Exception 异常信息
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rpcProperties.getSecurity().isEnable()) {
            return true;
        }
        if (isExclusion(request)) {
            return true;
        }
        String clientTime = request.getHeader(RpcConstants.CLIENT_TIME_HEADER_NAME);
        String authorization = request.getHeader(RpcConstants.AUTHORIZATION_HEADER_NAME);

        String clientPassword = serviceInstance.getMetadata().get(RpcConstants.CLIENT_PASSWORD);
        String clientName = serviceInstance.getServiceId();
        return SecurityUtils.checkDataInfo(clientName, clientPassword, clientTime, authorization);
    }

    /**
     * 判断当前地址是否在白名单中
     *
     * @param request 请求参数
     * @return true
     */
    public boolean isExclusion(HttpServletRequest request) {
        String url = request.getRequestURI();
        String[] whiteListUri = rpcProperties.getSecurity().getWhiteListUri();
        if (Objects.isNull(whiteListUri)) {
            return false;
        }
        for (String uriFilterExclusionValue : whiteListUri) {
            if (url.contains(uriFilterExclusionValue)) {
                log.debug("当前请求url为:[{}],在配置的忽略url列表中,因此直接忽略拦截", url);
                return true;
            }
        }
        return false;
    }
}
