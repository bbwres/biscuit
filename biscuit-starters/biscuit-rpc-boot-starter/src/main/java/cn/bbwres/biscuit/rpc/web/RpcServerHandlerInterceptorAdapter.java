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
 * @version $Id: $Id
 */
@Slf4j
@RequiredArgsConstructor
public class RpcServerHandlerInterceptorAdapter implements HandlerInterceptor {


    private final ServiceInstance serviceInstance;

    private final RpcProperties rpcProperties;


    /**
     * {@inheritDoc}
     *
     * 拦截器处理
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
