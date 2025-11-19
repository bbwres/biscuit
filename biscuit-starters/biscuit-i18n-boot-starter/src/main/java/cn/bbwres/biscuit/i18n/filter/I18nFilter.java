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

package cn.bbwres.biscuit.i18n.filter;

import cn.bbwres.biscuit.constants.SystemAuthConstant;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Locale;

/**
 * i18n过滤器
 *
 * @author zhanglinfeng
 */
public record I18nFilter() implements Filter, Ordered {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String language = httpServletRequest.getHeader(SystemAuthConstant.ACCEPT_LANGUAGE_HEADER);
            if (language != null) {
                LocaleContextHolder.setDefaultLocale(Locale.forLanguageTag(language));
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
}
