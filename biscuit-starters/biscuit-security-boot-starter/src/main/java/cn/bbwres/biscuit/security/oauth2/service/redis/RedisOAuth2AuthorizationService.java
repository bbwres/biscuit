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
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2AllTokenKey;
import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2AuthorizationTokenKeyInfo;
import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2ClientPrincipalName;
import jakarta.annotation.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
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

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author zhanglinfeng
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {


    private final RegisteredClientRepository registeredClientRepository;

    private final RedisOperations<Object, Object> redisOperations;

    private final UserDetailsService userDetailsService;
    private final BiscuitSecurityProperties biscuitSecurityProperties;

    public RedisOAuth2AuthorizationService(RegisteredClientRepository registeredClientRepository,
                                           RedisOperations<Object, Object> redisOperations, UserDetailsService userDetailsService, BiscuitSecurityProperties biscuitSecurityProperties) {
        this.userDetailsService = userDetailsService;
        this.biscuitSecurityProperties = biscuitSecurityProperties;
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
        long offsetSecond = biscuitSecurityProperties.getTokenExpireOffsetSecond();
        OAuth2AllTokenKey oauth2AllTokenKey = new OAuth2AllTokenKey();
        oauth2AllTokenKey.setId(authorization.getId());
        OAuth2ClientPrincipalName oauth2ClientPrincipalName = new OAuth2ClientPrincipalName()
                .setRegisteredClientId(authorization.getRegisteredClientId())
                .setPrincipalName(authorization.getPrincipalName());

        checkAndDeleteOtherToken(authorization.getRegisteredClientId(), oauth2ClientPrincipalName.getRedisKey());

        List<OAuth2AuthorizationTokenKeyInfo> oauth2AuthorizationTokens = buildTokenInfo(authorization);
        SessionCallback<Void> sessionCallback = new SessionCallback<>() {

            @org.springframework.lang.Nullable
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                long maxTimeToLive = -1L;
                Set<String> tokenKeys = new HashSet<>(16);
                for (OAuth2AuthorizationTokenKeyInfo oauth2AuthorizationToken : oauth2AuthorizationTokens) {
                    String redisKey = oauth2AuthorizationToken.getRedisKey();
                    tokenKeys.add(redisKey);
                    long timeToLive = oauth2AuthorizationToken.getExpiresAt().getEpochSecond() - now.getEpochSecond() + offsetSecond;
                    if (maxTimeToLive < timeToLive) {
                        maxTimeToLive = timeToLive;
                    }
                    operations.opsForValue().set(redisKey, authorization, timeToLive, TimeUnit.SECONDS);
                }
                String allTokenKey = oauth2AllTokenKey.getRedisKey();
                operations.opsForList().rightPushAll(allTokenKey, tokenKeys.toArray());
                operations.expire(allTokenKey, maxTimeToLive, TimeUnit.SECONDS);
                String oauth2ClientPrincipalNameKey = oauth2ClientPrincipalName.getRedisKey();
                operations.opsForList().rightPush(oauth2ClientPrincipalNameKey, allTokenKey);
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
            deleteByRegisteredClientIdPrincipalNameKey(registeredClientIdPrincipalNameKey);
        }

    }

    /**
     * 删除token信息
     *
     * @param registeredClientIdPrincipalNameKey
     */
    private void deleteByRegisteredClientIdPrincipalNameKey(String registeredClientIdPrincipalNameKey) {
        Object tokenId = redisOperations.opsForList().leftPop(registeredClientIdPrincipalNameKey);
        if (Objects.isNull(tokenId)) {
            return;
        }
        deleteTokenByTokenId(tokenId);
        deleteByRegisteredClientIdPrincipalNameKey(registeredClientIdPrincipalNameKey);
    }

    /**
     * 根据tokenId删除token数据
     *
     * @param tokenId
     */
    private void deleteTokenByTokenId(Object tokenId) {
        Set<Object> tokenKeys = new HashSet<>(16);
        tokenKeys.addAll(redisOperations.opsForList().range(tokenId, 0, -1));
        tokenKeys.add(tokenId);
        redisOperations.delete(tokenKeys);
    }


    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        List<String> keys = new ArrayList<>(16);
        String authorizationKeyId = String.format(OAuth2AllTokenKey.KEY_FORMATE, authorization.getId());
        keys.add(authorizationKeyId);

        OAuth2ClientPrincipalName oauth2ClientPrincipalName = new OAuth2ClientPrincipalName()
                .setRegisteredClientId(authorization.getRegisteredClientId())
                .setPrincipalName(authorization.getPrincipalName());
        //获取key
        List<OAuth2AuthorizationTokenKeyInfo> oauth2AuthorizationTokens = buildTokenInfo(authorization);
        for (OAuth2AuthorizationTokenKeyInfo oauth2AuthorizationToken : oauth2AuthorizationTokens) {
            keys.add(oauth2AuthorizationToken.getRedisKey());
        }
        redisOperations.delete(keys);
        redisOperations.opsForList().remove(oauth2ClientPrincipalName, 1, authorizationKeyId);
    }

    /**
     * 根据tokenId查询数据
     *
     * @param id the authorization identifier
     * @return
     */
    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        String authorizationKeyId = String.format(OAuth2AllTokenKey.KEY_FORMATE, id);
        Set<Object> tokenKeys = new HashSet<>(16);
        tokenKeys.addAll(redisOperations.opsForList().range(authorizationKeyId, 0, -1));
        for (Object tokenKey : tokenKeys) {
            Object result = redisOperations.opsForValue().get(tokenKey);
            if (Objects.nonNull(result)) {
                return (OAuth2Authorization) result;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        if (tokenType != null) {
            return findByToken(token, tokenType.getValue());
        }
        List<String> tokenTypes = new ArrayList<>(16);
        tokenTypes.add(OAuth2ParameterNames.CODE);
        tokenTypes.add(OAuth2ParameterNames.ACCESS_TOKEN);
        tokenTypes.add(OAuth2ParameterNames.REFRESH_TOKEN);
        tokenTypes.add(Oauth2SystemConstants.OAUTH2_OIDC_TOKEN);
        tokenTypes.add(OAuth2ParameterNames.USER_CODE);
        tokenTypes.add(OAuth2ParameterNames.DEVICE_CODE);
        for (String type : tokenTypes) {
            OAuth2Authorization authorization = findByToken(token, type);
            if (Objects.nonNull(authorization)) {
                return authorization;
            }
        }
        return null;
    }


    /**
     * 根据token查询信息
     *
     * @param token
     * @param tokenType
     * @return
     */
    private OAuth2Authorization findByToken(String token, String tokenType) {
        String redisKey = String.format(OAuth2AuthorizationTokenKeyInfo.KEY_FORMATE, tokenType, token);
        Object result = redisOperations.opsForValue().get(redisKey);
        return Objects.isNull(result) ? null : (OAuth2Authorization) result;
    }


    /**
     * 设置token的值
     *
     * @param token     token
     * @param tokenType tokenType
     */
    private OAuth2AuthorizationTokenKeyInfo buildTokenValue(OAuth2Authorization.Token<? extends OAuth2Token> token, String tokenType) {
        OAuth2AuthorizationTokenKeyInfo authorizationToken = new OAuth2AuthorizationTokenKeyInfo();
        OAuth2Token oauth2Token = token.getToken();
        authorizationToken.setTokenValue(oauth2Token.getTokenValue());
        authorizationToken.setIssuedAt(oauth2Token.getIssuedAt());
        authorizationToken.setExpiresAt(oauth2Token.getExpiresAt());
        authorizationToken.setTokenType(tokenType);
        return authorizationToken;
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
     * 设置token相关参数信息
     *
     * @param authorization authorization
     */
    private List<OAuth2AuthorizationTokenKeyInfo> buildTokenInfo(OAuth2Authorization authorization) {
        List<OAuth2AuthorizationTokenKeyInfo> oauth2AuthorizationTokens = new ArrayList<>(16);
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode != null) {
            oauth2AuthorizationTokens.add(buildTokenValue(authorizationCode, OAuth2ParameterNames.CODE));
        }
        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        if (userCode != null) {
            oauth2AuthorizationTokens.add(buildTokenValue(userCode, OAuth2ParameterNames.USER_CODE));
        }
        OAuth2Authorization.Token<OAuth2DeviceCode> deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if (deviceCode != null) {
            oauth2AuthorizationTokens.add(buildTokenValue(deviceCode, OAuth2ParameterNames.DEVICE_CODE));
        }
        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken != null) {
            oauth2AuthorizationTokens.add(buildTokenValue(oidcIdToken, Oauth2SystemConstants.OAUTH2_OIDC_TOKEN));
        }
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            oauth2AuthorizationTokens.add(buildTokenValue(accessToken, OAuth2ParameterNames.ACCESS_TOKEN));
        }
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            oauth2AuthorizationTokens.add(buildTokenValue(refreshToken, OAuth2ParameterNames.REFRESH_TOKEN));
        }
        return oauth2AuthorizationTokens;

    }
}
