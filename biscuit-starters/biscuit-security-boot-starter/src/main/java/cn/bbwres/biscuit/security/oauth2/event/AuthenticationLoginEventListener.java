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
package cn.bbwres.biscuit.security.oauth2.event;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;

/**
 * 增加登录日志
 *
 * @author zhanglinfeng12
 */
public class AuthenticationLoginEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationLoginEventListener.class);

    private final AuthenticationLoginService authenticationLoginService;

    public AuthenticationLoginEventListener(AuthenticationLoginService authenticationLoginService) {
        this.authenticationLoginService = authenticationLoginService;
    }

    /**
     * 登陆鉴权成功事件处理
     *
     * @param event
     */
    @EventListener
    public void successEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof OAuth2AccessTokenAuthenticationToken auth2AccessTokenAuthenticationToken
                && auth2AccessTokenAuthenticationToken.isAuthenticated()) {
            String userName = auth2AccessTokenAuthenticationToken.getName();
            log.info("当前用户:{} 登录成功的！", userName);
            authenticationLoginService.loginSuccess(auth2AccessTokenAuthenticationToken);
        }
    }

    /**
     * 登陆鉴权错误事件处理
     *
     * @param event
     */
    @EventListener
    public void failureBadCredentialsEvent(AbstractAuthenticationFailureEvent event) {
        if (event instanceof AuthenticationFailureProviderNotFoundEvent) {
            return;
        }
        if (event.getAuthentication().getDetails() != null) {
            Authentication username = event.getException().getAuthenticationRequest();
            AuthenticationException errorMessage = event.getException();
            log.info("当前用户:{} 登录失败！失败原因:{}", username, errorMessage.getMessage());
            authenticationLoginService.loginFail(username, errorMessage);
        }
    }

}
