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

import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * rpc webConfigurer 配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@RequiredArgsConstructor
public class RpcWebAppConfigurer implements WebMvcConfigurer {

    private final RpcServerHandlerInterceptorAdapter rpcServerHandlerInterceptorAdapter;
    private final RpcSecurityProperties rpcSecurityProperties;


    /**
     * {@inheritDoc}
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!rpcSecurityProperties.isEnable()) {
            log.debug("当前未开启rpc接口安全校验");
            return;
        }
        if (ObjectUtils.isEmpty(rpcSecurityProperties.getPathPatterns())) {
            log.debug("当前开启了rpc接口安全校验，但是设置的拦截path为空!");
            return;
        }
        //增加rpc服务端拦截器
        registry.addInterceptor(rpcServerHandlerInterceptorAdapter).addPathPatterns(rpcSecurityProperties.getPathPatterns());
    }
}
