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

import cn.bbwres.biscuit.security.oauth2.constants.Oauth2SystemConstants;
import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.*;
import jakarta.annotation.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @author zhanglinfeng
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {


    private final RegisteredClientRepository registeredClientRepository;

    private final RedisOperations<Object, Object> redisOperations;

    private final UserDetailsService userDetailsService;

    public RedisOAuth2AuthorizationService(RegisteredClientRepository registeredClientRepository,
                                           RedisOperations<Object, Object> redisOperations, UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(redisOperations,
                "authorizationGrantAuthorizationRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.redisOperations = redisOperations;
    }

    /**
     * Saves the {@link OAuth2Authorization}.
     *
     * @param authorization the {@link OAuth2Authorization}
     */
    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        Instant now = Instant.now();
        long offsetSecond = 50L;
        OAuth2AllTokenKey oauth2AllTokenKey = new OAuth2AllTokenKey();
        oauth2AllTokenKey.setId(authorization.getId());
        OAuth2ClientPrincipalName oauth2ClientPrincipalName = new OAuth2ClientPrincipalName()
                .setRegisteredClientId(authorization.getRegisteredClientId())
                .setPrincipalName(authorization.getPrincipalName());

        checkAndDeleteOtherToken(authorization.getRegisteredClientId(), oauth2ClientPrincipalName.getRedisKey());

        List<BaseOAuth2AuthorizationToken> oauth2AuthorizationTokens = buildTokenInfo(authorization);
        SessionCallback<Void> sessionCallback = new SessionCallback<>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                long maxTimeToLive = -1L;
                Set<String> tokenKeys = new HashSet<>(16);
                for (BaseOAuth2AuthorizationToken oauth2AuthorizationToken : oauth2AuthorizationTokens) {
                    String redisKey = oauth2AuthorizationToken.getRedisKey();
                    tokenKeys.add(redisKey);
                    long timeToLive = oauth2AuthorizationToken.getExpiresAt().getEpochSecond() - now.getEpochSecond() + offsetSecond;
                    if (maxTimeToLive < timeToLive) {
                        maxTimeToLive = timeToLive;
                    }
                    operations.opsForValue().set(redisKey, oauth2AuthorizationToken, timeToLive, TimeUnit.SECONDS);
                }
                String allTokenKey = oauth2AllTokenKey.getRedisKey();
                operations.opsForSet().add(allTokenKey, tokenKeys.toArray());
                operations.expire(allTokenKey, maxTimeToLive, TimeUnit.SECONDS);
                String oauth2ClientPrincipalNameKey = oauth2ClientPrincipalName.getRedisKey();
                operations.opsForSet().add(oauth2ClientPrincipalNameKey, allTokenKey);
                operations.expire(oauth2ClientPrincipalNameKey, maxTimeToLive, TimeUnit.SECONDS);
                return null;
            }
        };

        redisOperations.executePipelined(sessionCallback);


    }


    /**
     * 单个用户在单个客户端只能存在一个有效的token的检查
     *
     * @param registeredClientId                 客户端id
     * @param registeredClientIdPrincipalNameKey 参数key
     */
    private void checkAndDeleteOtherToken(String registeredClientId, String registeredClientIdPrincipalNameKey) {
        RegisteredClient registeredClient = buildRegisteredClient(registeredClientId);
        Boolean singleUserLogin = registeredClient.getClientSettings().getSetting(Oauth2SystemConstants.CLIENT_SETTING_SINGLE_USER_LOGIN);
        if (singleUserLogin != null && singleUserLogin) {
            Set<Object> tokenIds = redisOperations.opsForSet().members(registeredClientIdPrincipalNameKey);
            if (CollectionUtils.isEmpty(tokenIds)) {
                return;
            }
            Set<Object> tokenKeys = new HashSet<>(16);
            for (Object tokenId : tokenIds) {
                tokenKeys.addAll(redisOperations.opsForSet().members(tokenId));
            }


            SessionCallback<Void> sessionCallback = new SessionCallback<>() {
                @org.springframework.lang.Nullable
                @Override
                public Void execute(RedisOperations operations) throws DataAccessException {
                    operations.delete(registeredClientIdPrincipalNameKey);
                    operations.delete(tokenIds);
                    operations.delete(tokenKeys);
                    return null;
                }
            };
            //先删除数据
            redisOperations.executePipelined(sessionCallback);

        }

    }

    /**
     * 设置token相关参数信息
     *
     * @param authorization authorization
     */
    private List<BaseOAuth2AuthorizationToken> buildTokenInfo(OAuth2Authorization authorization) {
        List<BaseOAuth2AuthorizationToken> oauth2AuthorizationTokens = new ArrayList<>(16);
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode != null) {
            OAuth2AuthorizationAuthorizationCode auth2AuthorizationAuthorizationCode = new OAuth2AuthorizationAuthorizationCode();
            buildOauth2AuthorizationToken(authorization, authorizationCode, auth2AuthorizationAuthorizationCode);
            auth2AuthorizationAuthorizationCode.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));
            oauth2AuthorizationTokens.add(auth2AuthorizationAuthorizationCode);
        }
        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        if (userCode != null) {
            OAuth2AuthorizationUserCode oauth2AuthorizationUserCode = new OAuth2AuthorizationUserCode();
            buildOauth2AuthorizationToken(authorization, userCode, oauth2AuthorizationUserCode);
            oauth2AuthorizationTokens.add(oauth2AuthorizationUserCode);
        }

        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if (deviceCode != null) {
            OAuth2AuthorizationDeviceCode oauth2AuthorizationDeviceCode = new OAuth2AuthorizationDeviceCode();
            buildOauth2AuthorizationToken(authorization, deviceCode, oauth2AuthorizationDeviceCode);
            oauth2AuthorizationDeviceCode.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));
            oauth2AuthorizationTokens.add(oauth2AuthorizationDeviceCode);
        }


        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken != null) {
            OAuth2AuthorizationOidcToken oauth2AuthorizationOidcToken = new OAuth2AuthorizationOidcToken();
            buildOauth2AuthorizationToken(authorization, oidcIdToken, oauth2AuthorizationOidcToken);
            oauth2AuthorizationOidcToken.setClaims(oidcIdToken.getClaims());
            oauth2AuthorizationTokens.add(oauth2AuthorizationOidcToken);
        }


        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);

        if (accessToken != null) {
            OAuth2AuthorizationAccessToken oauth2AuthorizationAccessToken = new OAuth2AuthorizationAccessToken();
            buildOauth2AuthorizationToken(authorization, accessToken, oauth2AuthorizationAccessToken);
            oauth2AuthorizationAccessToken.setTokenType(accessToken.getToken().getTokenType().getValue());
            oauth2AuthorizationAccessToken.setScopes(accessToken.getToken().getScopes());
            oauth2AuthorizationTokens.add(oauth2AuthorizationAccessToken);
        }


        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            OAuth2AuthorizationRefreshToken oauth2AuthorizationRefreshToken = new OAuth2AuthorizationRefreshToken();
            buildOauth2AuthorizationToken(authorization, refreshToken, oauth2AuthorizationRefreshToken);
            oauth2AuthorizationTokens.add(oauth2AuthorizationRefreshToken);
        }
        return oauth2AuthorizationTokens;

    }


    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        // this.authorizationGrantAuthorizationRepository.deleteById(authorization.getId());
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
//        return this.authorizationGrantAuthorizationRepository.findById(id)
//                .map(this::toOauth2Authorization)
//                .orElse(null);
        return null;
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
//        OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization = null;
//        if (tokenType == null) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
//                    .findByStateOrAuthorizationCodeTokenValue(token, token);
//            if (authorizationGrantAuthorization == null) {
//                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
//                        .findByAccessTokenValueOrRefreshTokenValue(token, token);
//            }
//            if (authorizationGrantAuthorization == null) {
//                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
//                        .findByIdTokenTokenValue(token);
//            }
//            if (authorizationGrantAuthorization == null) {
//                authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository
//                        .findByStateOrDeviceCodeTokenValueOrUserCodeTokenValue(token, token, token);
//            }
//        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByState(token);
//        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByAuthorizationCodeTokenValue(token);
//        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByAccessTokenValue(token);
//        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByIdTokenTokenValue(token);
//        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByRefreshTokenValue(token);
//        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByUserCodeTokenValue(token);
//        } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
//            authorizationGrantAuthorization = this.authorizationGrantAuthorizationRepository.findByDeviceCodeTokenValue(token);
//        }
//        return authorizationGrantAuthorization != null ? toOauth2Authorization(authorizationGrantAuthorization) : null;
        return null;
    }

    /**
     * 设置数据
     *
     * @param authorizationGrantAuthorization authorizationGrantAuthorization
     * @return OAuth2Authorization
     */
//    private OAuth2Authorization toOauth2Authorization(OAuth2AuthorizationGrantAuthorization authorizationGrantAuthorization) {
//        RegisteredClient registeredClient = buildRegisteredClient(authorizationGrantAuthorization.getRegisteredClientId());
//        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
//        builder.id(authorizationGrantAuthorization.getId())
//                .principalName(authorizationGrantAuthorization.getPrincipalName())
//                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantAuthorization.getAuthorizationGrantType()))
//                .authorizedScopes(authorizationGrantAuthorization.getAuthorizedScopes())
//                .attributes(attributes -> attributes.put(Principal.class.getName(), buildPrincipal(authorizationGrantAuthorization.getPrincipalName())));
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getState())) {
//            builder.attribute(OAuth2ParameterNames.STATE, authorizationGrantAuthorization.getState());
//        }
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getAccessTokenValue())) {
//            builder.token(new OAuth2AccessToken(new OAuth2AccessToken.TokenType(authorizationGrantAuthorization.getAccessTokenTokenType()),
//                            authorizationGrantAuthorization.getAccessTokenValue(), authorizationGrantAuthorization.getAccessTokenIssuedAt(),
//                            authorizationGrantAuthorization.getAccessTokenExpiresAt(), authorizationGrantAuthorization.getAccessTokenScopes()),
//                    metadata -> {
//                        metadata.putAll(JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getAccessTokenMetadata()));
//                        Map<String, Object> claims = (Map<String, Object>) metadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME);
//                        claims.put(OAuth2TokenClaimNames.NBF, authorizationGrantAuthorization.getAccessTokenIssuedAt());
//                    });
//        }
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getRefreshTokenValue())) {
//            builder.token(new OAuth2RefreshToken(authorizationGrantAuthorization.getRefreshTokenValue(), authorizationGrantAuthorization.getRefreshTokenIssuedAt(),
//                            authorizationGrantAuthorization.getRefreshTokenExpiresAt()),
//                    metadata -> metadata.putAll(JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getRefreshTokenMetadata())));
//        }
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getAuthorizationCodeTokenValue())) {
//            builder.token(new OAuth2AuthorizationCode(authorizationGrantAuthorization.getAuthorizationCodeTokenValue(), authorizationGrantAuthorization.getAuthorizationCodeIssuedAt(),
//                            authorizationGrantAuthorization.getAuthorizationCodeExpiresAt()),
//                    metadata -> metadata.putAll(JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getAuthorizationCodeMetadata())));
//        }
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getDeviceCodeTokenValue())) {
//            builder.token(new OAuth2DeviceCode(authorizationGrantAuthorization.getDeviceCodeTokenValue(), authorizationGrantAuthorization.getDeviceCodeIssuedAt(),
//                            authorizationGrantAuthorization.getDeviceCodeExpiresAt()),
//                    metadata -> metadata.putAll(JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getDeviceCodeMetadata())));
//        }
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getUserCodeTokenValue())) {
//            builder.token(new OAuth2UserCode(authorizationGrantAuthorization.getUserCodeTokenValue(), authorizationGrantAuthorization.getUserCodeIssuedAt(),
//                            authorizationGrantAuthorization.getUserCodeExpiresAt()),
//                    metadata -> metadata.putAll(JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getUserCodeMetadata())));
//        }
//
//        if (!ObjectUtils.isEmpty(authorizationGrantAuthorization.getIdTokenTokenValue())) {
//            builder.token(new OidcIdToken(authorizationGrantAuthorization.getIdTokenTokenValue(), authorizationGrantAuthorization.getIdTokenIssuedAt(),
//                            authorizationGrantAuthorization.getIdTokenExpiresAt(), JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getIdTokenClaims())),
//                    metadata -> metadata.putAll(JsonUtil.jsonString2MapObj(authorizationGrantAuthorization.getIdTokenMetadata())));
//        }
//
//        return builder.build();
//    }
//

    /**
     * 配置token参数信息
     *
     * @param authorization
     * @param token
     * @param baseOauth2AuthorizationToken
     * @return
     */
    private void buildOauth2AuthorizationToken(OAuth2Authorization authorization,
                                               OAuth2Authorization.Token<? extends OAuth2Token> token,
                                               BaseOAuth2AuthorizationToken baseOauth2AuthorizationToken) {
        baseOauth2AuthorizationToken.setId(authorization.getId());
        baseOauth2AuthorizationToken.setRegisteredClientId(authorization.getRegisteredClientId());
        baseOauth2AuthorizationToken.setPrincipalName(authorization.getPrincipalName());
        baseOauth2AuthorizationToken.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        baseOauth2AuthorizationToken.setAuthorizedScopes(authorization.getAuthorizedScopes());
        buildTokenValue(token, baseOauth2AuthorizationToken);
    }

    /**
     * 设置token的值
     *
     * @param token
     * @param authorizationToken
     */
    private void buildTokenValue(OAuth2Authorization.Token<? extends OAuth2Token> token,
                                 BaseOAuth2AuthorizationToken authorizationToken) {
        OAuth2Token oauth2Token = token.getToken();
        authorizationToken.setTokenValue(oauth2Token.getTokenValue());
        authorizationToken.setIssuedAt(oauth2Token.getIssuedAt());
        authorizationToken.setExpiresAt(oauth2Token.getExpiresAt());
        authorizationToken.setMetadata(token.getMetadata());
    }


    /**
     * 加载客户端信息
     *
     * @param registeredClientId
     * @return
     */
    private RegisteredClient buildRegisteredClient(String registeredClientId) {
        return this.registeredClientRepository.findById(registeredClientId);
    }

    /**
     * 加载认证的 Principal
     *
     * @param principalName Principal
     * @return Authentication
     */
    private Authentication buildPrincipal(String principalName) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principalName);
        return UsernamePasswordAuthenticationToken.authenticated(userDetails,
                null, userDetails.getAuthorities());
    }
}
