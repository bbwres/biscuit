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

package cn.bbwres.biscuit.operation.log.service.impl;

import cn.bbwres.biscuit.operation.log.annotation.OperationLog;
import cn.bbwres.biscuit.operation.log.entity.OperationLogEntity;
import cn.bbwres.biscuit.operation.log.service.EnhanceOperationLogService;
import cn.bbwres.biscuit.utils.NetworkUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 补充操作日志的web相关参数
 *
 * @author zhanglinfeng
 */
@RequiredArgsConstructor
public class EnhanceOperationLogWebServiceImpl implements EnhanceOperationLogService {

    private static final String[] CLIENT_IP_HTTP_HEADER_NAME = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    /**
     * 用户agent
     */
    private static final String USER_AGENT = "User-Agent";

    protected HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return !(requestAttributes instanceof ServletRequestAttributes) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取ua
     *
     * @param request http
     * @return ua
     */
    protected String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader(USER_AGENT);
        return ua != null ? ua : "";
    }

    /**
     * 获取客户端ip
     *
     * @return ip
     */
    public static String getClientIp(HttpServletRequest request) {
        return getClientIpByHeader(request, CLIENT_IP_HTTP_HEADER_NAME);
    }

    /**
     * 获取客户端ip
     *
     * @param headerNames 请求头参数名称
     * @return ip
     */
    public static String getClientIpByHeader(HttpServletRequest request, String... headerNames) {
        String ip;
        for (String headerName : headerNames) {
            ip = request.getHeader(headerName);
            if (!NetworkUtil.isUnknown(ip)) {
                return NetworkUtil.getMultistageReverseProxyIp(ip);
            }
        }
        ip = request.getRemoteAddr();
        return NetworkUtil.getMultistageReverseProxyIp(ip);
    }


    /**
     * 扩展补充操作日志参数
     *
     * @param loggerMsg  操作日志对象
     * @param operateLog 注解
     * @param joinPoint  切入点
     * @param response   执行响应
     * @param exception  异常信息
     */
    @Override
    public void enhance(OperationLogEntity loggerMsg, OperationLog operateLog, ProceedingJoinPoint joinPoint,
                        Object response, Throwable exception) {
        HttpServletRequest request = getRequest();
        if (Objects.isNull(request)) {
            return;
        }

        loggerMsg.setRequestMethod(request.getMethod());
        loggerMsg.setRequestUrl(request.getRequestURI());
        loggerMsg.setUserIp(getClientIp(request));
        loggerMsg.setUserAgent(getUserAgent(request));

    }
}
