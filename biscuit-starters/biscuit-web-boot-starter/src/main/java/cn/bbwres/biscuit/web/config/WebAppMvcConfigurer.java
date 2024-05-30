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

package cn.bbwres.biscuit.web.config;

import cn.bbwres.biscuit.web.handler.BiscuitHandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web app 配置
 *
 * @author zhanglinfeng
 */
public class WebAppMvcConfigurer implements WebMvcConfigurer {

    private final BiscuitHandlerExceptionResolver biscuitHandlerExceptionResolver;

    public WebAppMvcConfigurer(BiscuitHandlerExceptionResolver biscuitHandlerExceptionResolver) {
        this.biscuitHandlerExceptionResolver = biscuitHandlerExceptionResolver;
    }

    /**
     * 扩展异常处理类
     *
     * @param resolvers the list of configured resolvers to extend
     */
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, biscuitHandlerExceptionResolver);
    }
}
