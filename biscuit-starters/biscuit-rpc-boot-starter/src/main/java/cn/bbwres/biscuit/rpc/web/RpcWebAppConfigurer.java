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

import cn.bbwres.biscuit.rpc.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * rpc webConfigurer 配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class RpcWebAppConfigurer implements WebMvcConfigurer {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher(".");

    private final RpcProperties rpcProperties;

    public RpcWebAppConfigurer(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
    }


    /**
     * rpc web api 配置
     * 给默认的rpc包增加前缀
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
      
        if (StringUtils.isBlank(rpcProperties.getRpcApiPrefix())) {
            log.info("rpcApiPrefix is empty,因此不配置rpc接口前缀");
            return;
        }
        configurer.addPathPrefix(rpcProperties.getRpcApiPrefix(), clazz ->
                (clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class))
                        && ANT_PATH_MATCHER.match(rpcProperties.getRpcApiPackage(), clazz.getPackage().getName()));
    }

}
