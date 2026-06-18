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

package cn.bbwres.biscuit.security.oauth2.filter.password;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2ErrorCodeConstants;
import cn.bbwres.biscuit.security.oauth2.filter.BaseSecuriteHttpFilter;
import cn.bbwres.biscuit.security.oauth2.grant.username.UsernamePasswordGrantAuthenticationToken;
import cn.bbwres.biscuit.security.oauth2.properties.PasswordSecurityProperties;
import cn.bbwres.biscuit.utils.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 登录密码 RSA 解密过滤器
 * <p>
 * 前端将 {@code {password, timestamp, nonce}} JSON 用 RSA 公钥（PKCS#1 v1.5 padding）加密后 Base64 编码，
 * 作为 OAuth2 password grant 的 password 字段提交。本过滤器在密码校验前进行解密：
 * <ul>
 *     <li>解密成功且时间戳在配置窗口内 → 用真实密码替换 request 中的 password 字段</li>
 *     <li>解密成功但时间戳过期 → 用 "真实密码 + 分隔符 + 时间戳" 替换，让后续 OAuth2 密码校验自然失败（用户感知为"密码错误"）</li>
 *     <li>解密失败、字段缺失、JSON 不合法 → 直接抛出异常拒绝请求</li>
 * </ul>
 *
 * @author zhanglinfeng
 */
@Slf4j
public class PasswordDecryptFilter extends BaseSecuriteHttpFilter {

    /**
     * 时间戳过期时，与真实密码拼接的分隔符。
     * 拼接后的字符串作为透传 password，自然不会通过 BCrypt 等密码校验。
     */
    private static final String EXPIRED_PASSWORD_SEPARATOR = "::expired::";

    private final AuthorizationServerSettings authorizationServerSettings;
    private final PasswordSecurityProperties properties;
    private final DecryptedPasswordCryptoService decryptedPasswordCryptoService;

    public PasswordDecryptFilter(AuthorizationServerSettings authorizationServerSettings,
                                 PasswordSecurityProperties properties,
                                 DecryptedPasswordCryptoService decryptedPasswordCryptoService) {
        this.authorizationServerSettings = authorizationServerSettings;
        this.properties = properties;
        this.decryptedPasswordCryptoService = decryptedPasswordCryptoService;
    }

    @Override
    public Class<? extends Filter> beforeFilter() {
        return UsernamePasswordAuthenticationFilter.class;
    }

    @Override
    public Class<? extends Filter> afterFilter() {
        return null;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 未启用直接放行
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 不是 token 端点请求直接跳过
        String requestUrl = request.getServletPath();
        if (!requestUrl.equals(authorizationServerSettings.getTokenEndpoint())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 不是密码模式直接跳过
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!UsernamePasswordGrantAuthenticationToken.PASSWORD.getValue().equals(grantType)) {
            filterChain.doFilter(request, response);
            return;
        }

        String encryptedPassword = request.getParameter(OAuth2ParameterNames.PASSWORD);
        if (!StringUtils.hasText(encryptedPassword)) {
            // password 缺失交给后续认证流程报错
            filterChain.doFilter(request, response);
            return;
        }

        // 解密 → 解析 JSON → 时间戳校验 → 替换 password 字段
        String passwordToPass = resolvePasswordToPass(encryptedPassword);
        HttpServletRequest wrapped = new DecryptedPasswordRequestWrapper(request, OAuth2ParameterNames.PASSWORD, passwordToPass);
        filterChain.doFilter(wrapped, response);
    }

    /**
     * 解密 + 校验时间戳，返回需要透传给后续认证流程的密码值
     */
    private String resolvePasswordToPass(String encryptedPassword) {
        PasswordPayloadInfo payload = JsonUtil.toObject(decryptedPasswordCryptoService
                        .decryptedPassword(properties.getTransformation(),
                                properties.getPrivateKey(), properties.getIv(),
                                encryptedPassword),
                PasswordPayloadInfo.class);
        if (payload == null) {
            log.warn("登录密码解密后 JSON 解析失败,解析的内容为空");
            throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_PASSWORD_PAYLOAD_INVALID);
        }

        if (payload.password == null || payload.timestamp == null) {
            log.warn("登录密码 payload 缺少必要字段:{}", payload);
            throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_PASSWORD_PAYLOAD_INVALID);
        }

        String realPassword = payload.password;
        long clientTimestamp;
        try {
            clientTimestamp = Long.parseLong(payload.timestamp);
        } catch (NumberFormatException e) {
            log.warn("登录密码 payload 中 timestamp 不是数字: {}", payload.timestamp);
            throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_PASSWORD_PAYLOAD_INVALID);
        }

        long now = System.currentTimeMillis();
        long diff = Math.abs(now - clientTimestamp);
        long windowMillis = properties.getTimestampWindowSeconds() * 1000L;
        if (diff > windowMillis) {
            // 过期：拼接变形密码，让 BCrypt 等密码校验自然失败，用户看到"密码错误"
            log.info("登录请求时间戳已过期: clientTs={}, serverTs={}, diffMs={}, windowMs={}",
                    clientTimestamp, now, diff, windowMillis);
            return realPassword + EXPIRED_PASSWORD_SEPARATOR + clientTimestamp;
        }

        return realPassword;
    }


    /**
     * 密码载体内容
     *
     * @param password
     * @param timestamp
     */
    public record PasswordPayloadInfo(String password, String timestamp) {

    }

}
