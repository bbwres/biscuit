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

package cn.bbwres.biscuit.security.captcha.filter;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.security.captcha.CaptchaProperties;
import cn.bbwres.biscuit.security.captcha.config.CheckCaptchaService;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2ErrorCodeConstants;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2SystemConstants;
import cn.bbwres.biscuit.security.oauth2.filter.BaseSecuriteHttpFilter;
import cn.bbwres.biscuit.security.oauth2.grant.username.UsernamePasswordGrantAuthenticationToken;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * 验证码验证过滤器
 *
 * @author zhanglinfeng
 */
@Slf4j
public class CaptchaCodeFilter extends BaseSecuriteHttpFilter {

    private final AuthorizationServerSettings authorizationServerSettings;
    private final CheckCaptchaService checkCaptchaService;

    private final CaptchaProperties captchaProperties;
    private final RedisCheckUserLockService redisCheckUserLockService;

    public CaptchaCodeFilter(AuthorizationServerSettings authorizationServerSettings,
                             CheckCaptchaService checkCaptchaService, CaptchaProperties captchaProperties,
                             RedisCheckUserLockService redisCheckUserLockService) {
        this.authorizationServerSettings = authorizationServerSettings;
        this.checkCaptchaService = checkCaptchaService;
        this.captchaProperties = captchaProperties;
        this.redisCheckUserLockService = redisCheckUserLockService;
    }

    /**
     * 在返回的过滤器之前
     *
     * @return class
     */
    @Override
    public Class<? extends Filter> beforeFilter() {
        return UsernamePasswordAuthenticationFilter.class;
    }

    /**
     * 在返回的过滤器之后
     *
     * @return class
     */
    @Override
    public Class<? extends Filter> afterFilter() {
        return null;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = request.getServletPath();

        // 不是登录URL 请求直接跳过
        if (!requestUrl.equals(authorizationServerSettings.getTokenEndpoint())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 不是密码模式时，直接跳过
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!UsernamePasswordGrantAuthenticationToken.PASSWORD.getValue().equals(grantType)) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = request.getParameter(OAuth2ParameterNames.USERNAME);
        String tenantId = request.getParameter(Oauth2SystemConstants.OAUTH2_PARAMETER_NAME_TENANT_ID);
        if (!redisCheckUserLockService.checkLoginFailNum(tenantId, username, captchaProperties.getLoginFailureCaptchaThreshold())) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("当前登录用户:[{}],所属租户:[{}]因失败次数超过:[{}]次数，开始校验验证码!", username, tenantId, captchaProperties.getLoginFailureCaptchaThreshold());
        String code = request.getParameter(captchaProperties.getCaptchaCodeValueName());
        String codeKey = request.getParameter(captchaProperties.getCaptchaCodeKeyName());
        if (checkCaptchaService.check(grantType, code, codeKey)) {
            filterChain.doFilter(request, response);
        }
        throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_INVALID_CAPTCHA);
    }
}
