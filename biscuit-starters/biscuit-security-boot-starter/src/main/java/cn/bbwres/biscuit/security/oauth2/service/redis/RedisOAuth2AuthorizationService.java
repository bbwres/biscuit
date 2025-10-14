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

package cn.bbwres.biscuit.security.oauth2.service.redis;

import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2AuthorizationGrantAuthorization;
import cn.bbwres.biscuit.security.oauth2.service.redis.repository.OAuth2AuthorizationGrantAuthorizationRepository;
import jakarta.annotation.Nullable;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Objects;

/**
 * @author zhanglinfeng
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {


    private final RegisteredClientRepository registeredClientRepository;

    private final OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository;

    public RedisOAuth2AuthorizationService(RegisteredClientRepository registeredClientRepository,
                                           OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(authorizationGrantAuthorizationRepository,
                "authorizationGrantAuthorizationRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationGrantAuthorizationRepository = authorizationGrantAuthorizationRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization = new OAuth2AuthorizationGrantAuthorization();
        authorizationGrantAuthorization.setId(authorization.getId());
        authorizationGrantAuthorization.setRegisteredClientId(authorization.getRegisteredClientId());
        authorizationGrantAuthorization.setPrincipalName(authorization.getPrincipalName());
        authorizationGrantAuthorization.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        authorizationGrantAuthorization.setAuthorizedScopes(authorization.getAuthorizedScopes());
        String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(authorizationState)) {
            authorizationGrantAuthorization.setState(authorizationState);
        }

        Instant now = Instant.now();
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (!ObjectUtils.isEmpty(authorizationCode)) {
            OAuth2AuthorizationCode token = authorizationCode.getToken();
            authorizationGrantAuthorization.setAuthorizationCodeTokenValue(token.getTokenValue());
            authorizationGrantAuthorization.setAuthorizationCodeIssuedAt(token.getIssuedAt());
            authorizationGrantAuthorization.setAuthorizationCodeExpiresAt(token.getExpiresAt());
            buildTimeToLive(authorizationGrantAuthorization, now, token.getExpiresAt());
        }

        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        if (!ObjectUtils.isEmpty(userCode)) {
            OAuth2UserCode token = userCode.getToken();
            authorizationGrantAuthorization.setUserCodeTokenValue(token.getTokenValue());
            authorizationGrantAuthorization.setUserCodeIssuedAt(token.getIssuedAt());
            authorizationGrantAuthorization.setUserCodeExpiresAt(token.getExpiresAt());
            buildTimeToLive(authorizationGrantAuthorization, now, token.getExpiresAt());
        }

        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if (!ObjectUtils.isEmpty(deviceCode)) {
            OAuth2DeviceCode token = deviceCode.getToken();
            authorizationGrantAuthorization.setDeviceCodeTokenValue(token.getTokenValue());
            authorizationGrantAuthorization.setDeviceCodeIssuedAt(token.getIssuedAt());
            authorizationGrantAuthorization.setDeviceCodeExpiresAt(token.getExpiresAt());
            buildTimeToLive(authorizationGrantAuthorization, now, token.getExpiresAt());
        }
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (!ObjectUtils.isEmpty(oidcIdToken)) {
            OidcIdToken token = oidcIdToken.getToken();
            authorizationGrantAuthorization.setIdTokenTokenValue(token.getTokenValue());
            authorizationGrantAuthorization.setIdTokenIssuedAt(token.getIssuedAt());
            authorizationGrantAuthorization.setIdTokenExpiresAt(token.getExpiresAt());
            authorizationGrantAuthorization.setIdTokenClaims(token.getClaims());
            buildTimeToLive(authorizationGrantAuthorization, now, token.getExpiresAt());
        }

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (!ObjectUtils.isEmpty(accessToken)) {
            OAuth2AccessToken token = accessToken.getToken();
            authorizationGrantAuthorization.setAccessTokenValue(token.getTokenValue());
            authorizationGrantAuthorization.setAccessTokenScopes(token.getScopes());
            authorizationGrantAuthorization.setAccessTokenIssuedAt(token.getIssuedAt());
            authorizationGrantAuthorization.setAccessTokenExpiresAt(token.getExpiresAt());
            buildTimeToLive(authorizationGrantAuthorization, now, token.getExpiresAt());
        }
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (!ObjectUtils.isEmpty(refreshToken)) {
            OAuth2RefreshToken token = refreshToken.getToken();
            authorizationGrantAuthorization.setRefreshTokenValue(token.getTokenValue());
            authorizationGrantAuthorization.setRefreshTokenIssuedAt(token.getIssuedAt());
            authorizationGrantAuthorization.setRefreshTokenExpiresAt(token.getExpiresAt());
            buildTimeToLive(authorizationGrantAuthorization, now, token.getExpiresAt());
        }
        this.authorizationGrantAuthorizationRepository.save(authorizationGrantAuthorization);
    }

    /**
     * 设置token的过期时间
     *
     * @param authorizationGrantAuthorization authorizationGrantAuthorization
     * @param now                             当前时间
     * @param expiresAt                       expiresAt
     */
    private void buildTimeToLive(OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization, Instant now, Instant expiresAt) {
        if (Objects.isNull(expiresAt)) {
            return;
        }
        long timeToLive = expiresAt.getEpochSecond() - now.getEpochSecond() + 20L;
        if (Objects.isNull(authorizationGrantAuthorization.getTimeToLive())
                || timeToLive > authorizationGrantAuthorization.getTimeToLive()) {
            authorizationGrantAuthorization.setTimeToLive(timeToLive);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        this.authorizationGrantAuthorizationRepository.deleteById(authorization.getId());
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return this.authorizationGrantAuthorizationRepository.findById(id)
                .map(this::toOauth2Authorization)
                .orElse(null);
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization = null;
        if (tokenType == null) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                    .findByStateOrAuthorizationCodeTokenValue(token, token);
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                        .findByAccessTokenValueOrRefreshTokenValue(token, token);
            }
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                        .findByIdTokenTokenValue(token);
            }
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
                        .findByDeviceStateOrDeviceCodeTokenValueOrUserCodeTokenValue(token, token, token);
            }
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByState(token);
            if (authorizationGrantAuthorization == null) {
                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByDeviceState(token);
            }
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByAuthorizationCodeTokenValue(token);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByAccessTokenValue(token);
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByIdTokenTokenValue(token);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByRefreshTokenValue(token);
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByUserCodeTokenValue(token);
        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByDeviceCodeTokenValue(token);
        }
        return authorizationGrantAuthorization != null ? toOauth2Authorization(authorizationGrantAuthorization) : null;
    }

    /**
     * 设置数据
     *
     * @param authorizationGrantAuthorization authorizationGrantAuthorization
     * @return OAuth2Authorization
     */
    private OAuth2Authorization toOauth2Authorization(OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization) {
        RegisteredClient registeredClient = this.registeredClientRepository
                .findById(authorizationGrantAuthorization.getRegisteredClientId());
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        builder.id(authorizationGrantAuthorization.getId())
                .principalName(authorizationGrantAuthorization.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantAuthorization.getAuthorizationGrantType()))
                .attributes(stringObjectMap -> stringObjectMap.putAll(authorizationGrantAuthorization.getAttributes()))
                .authorizedScopes(authorizationGrantAuthorization.getAuthorizedScopes());

        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getState())) {
            builder.attribute(OAuth2ParameterNames.STATE, authorizationGrantAuthorization.getState());
        }
        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getAccessTokenValue())) {
            builder.accessToken(new OAuth2AccessToken(new OAuth2AccessToken.TokenType(authorizationGrantAuthorization.getAccessTokenTokenType()),
                    authorizationGrantAuthorization.getAccessTokenValue(), authorizationGrantAuthorization.getAccessTokenIssuedAt(),
                    authorizationGrantAuthorization.getAccessTokenExpiresAt(), authorizationGrantAuthorization.getAccessTokenScopes()));
        }
        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getRefreshTokenValue())) {
            builder.refreshToken(new OAuth2RefreshToken(authorizationGrantAuthorization.getRefreshTokenValue(), authorizationGrantAuthorization.getRefreshTokenIssuedAt(),
                    authorizationGrantAuthorization.getRefreshTokenExpiresAt()));
        }
        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getAuthorizationCodeTokenValue())) {
            builder.token(new OAuth2AuthorizationCode(authorizationGrantAuthorization.getAuthorizationCodeTokenValue(), authorizationGrantAuthorization.getAuthorizationCodeIssuedAt(),
                    authorizationGrantAuthorization.getAuthorizationCodeExpiresAt()));
        }
        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getDeviceCodeTokenValue())) {
            builder.token(new OAuth2DeviceCode(authorizationGrantAuthorization.getDeviceCodeTokenValue(), authorizationGrantAuthorization.getDeviceCodeIssuedAt(),
                    authorizationGrantAuthorization.getDeviceCodeExpiresAt()));
        }
        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getUserCodeTokenValue())) {
            builder.token(new OAuth2UserCode(authorizationGrantAuthorization.getUserCodeTokenValue(), authorizationGrantAuthorization.getUserCodeIssuedAt(),
                    authorizationGrantAuthorization.getUserCodeExpiresAt()));
        }

        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getIdTokenTokenValue())) {
            builder.token(new OidcIdToken(authorizationGrantAuthorization.getIdTokenTokenValue(), authorizationGrantAuthorization.getIdTokenIssuedAt(),
                    authorizationGrantAuthorization.getIdTokenExpiresAt(), authorizationGrantAuthorization.getIdTokenClaims()));
        }

        return builder.build();
    }

}
