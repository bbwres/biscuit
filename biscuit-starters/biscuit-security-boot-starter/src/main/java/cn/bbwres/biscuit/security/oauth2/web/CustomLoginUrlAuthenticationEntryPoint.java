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

package cn.bbwres.biscuit.security.oauth2.web;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2ErrorCodeConstants;
import cn.bbwres.biscuit.utils.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

/**
 * 自定义的CustomLoginUrlAuthenticationEntryPoint
 * 针对与非code认证的请求，返回json数据
 *
 * @author zhanglinfeng
 */
@Slf4j
public class CustomLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {


    private final AuthorizationServerSettings authorizationServerSettings;

    private final MessageSourceAccessor messages;

    /**
     * @param loginFormUrl URL where the login page can be found. Should either be
     *                     relative to the web-app context path (include a leading {@code /}) or an absolute
     *                     URL.
     */
    public CustomLoginUrlAuthenticationEntryPoint(String loginFormUrl,
                                                  AuthorizationServerSettings authorizationServerSettings,
                                                  ObjectProvider<MessageSourceAccessor> messageSourceAccessorObjectProvider) {
        super(loginFormUrl);
        this.authorizationServerSettings = authorizationServerSettings;
        this.messages = messageSourceAccessorObjectProvider.getIfAvailable();
    }

    /**
     * Performs the redirect (or forward) to the login form URL.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (!request.getRequestURI().equals(authorizationServerSettings.getAuthorizationEndpoint())) {
            Result<Void> error = oauth2AuthenticationException(authException);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JsonUtil.toJson(error));
            return;
        }
        super.commence(request, response, authException);
    }

    /**
     * 登录异常转换为oauth2异常
     *
     * @param authenticationException 身份验证异常
     * @return {@link OAuth2AuthenticationException}
     */
    private Result<Void> oauth2AuthenticationException(AuthenticationException authenticationException) {

        if (authenticationException instanceof BadCredentialsException ||
                authenticationException instanceof UsernameNotFoundException) {

            return new Result<>(messages, Oauth2ErrorCodeConstants.OAUTH2_USERNAME_PASSWORD_ERROR);
        }
        if (authenticationException instanceof LockedException) {
            return new Result<>(messages, Oauth2ErrorCodeConstants.OAUTH2_USER_LOCKED);
        }
        if (authenticationException instanceof DisabledException) {
            return new Result<>(messages, Oauth2ErrorCodeConstants.OAUTH2_USER_DISABLE);
        }
        if (authenticationException instanceof AccountExpiredException
                || authenticationException instanceof CredentialsExpiredException) {
            return new Result<>(messages, Oauth2ErrorCodeConstants.OAUTH2_USER_EXPIRED);
        }
        Throwable cause = authenticationException.getCause();
        if(cause instanceof SystemRuntimeException systemRuntimeException){
            return new Result<>(systemRuntimeException.getErrorCode(),systemRuntimeException.getMessage());
        }

        return new Result<>(messages, Oauth2ErrorCodeConstants.OAUTH2_ERROR);
    }

}
