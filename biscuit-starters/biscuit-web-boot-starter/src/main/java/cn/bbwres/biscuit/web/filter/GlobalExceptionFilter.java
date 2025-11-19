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

package cn.bbwres.biscuit.web.filter;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.utils.JsonUtil;
import cn.bbwres.biscuit.web.handler.ExceptionMessageHandler;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * 全局异常处理
 *
 * @author zhanglinfeng
 */

public record GlobalExceptionFilter(ExceptionMessageHandler exceptionMessageHandler) implements Filter, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionFilter.class);


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            LOG.error("拦截器执行失败:[{}]", e.getMessage());
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            handleException(httpResponse, e);
        }
    }

    /**
     * 输出异常
     *
     * @param response
     * @param e
     */
    private void handleException(HttpServletResponse response, Exception e) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter out = response.getWriter()) {
            Result<?> result = exceptionMessageHandler.exceptionHandler(e);
            out.write(Objects.requireNonNull(JsonUtil.toJson(result)));
            out.flush();
        } catch (IOException ex) {
            LOG.error("异常响应写入失败", ex);
        }
    }

    /**
     * 优先级最高，最先执行
     *
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
