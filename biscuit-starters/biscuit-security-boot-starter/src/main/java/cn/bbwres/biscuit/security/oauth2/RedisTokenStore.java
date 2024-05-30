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
package cn.bbwres.biscuit.security.oauth2;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 实现了刷新token与用户关联的功能
 *
 * @author zhanglinfeng
 */
public class RedisTokenStore extends org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore {
    public RedisTokenStore(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
        this.connectionFactory = connectionFactory;
        if (springDataRedis_2_0) {
            this.loadRedisConnectionMethods_2_0_a();
        }
    }


    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    /**
     * 用户与刷新token关联关系
     */
    private static final String UNAME_TO_REFRESH = "uname_to_refresh:";

    private static final boolean springDataRedis_2_0 = ClassUtils.isPresent(
            "org.springframework.data.redis.connection.RedisStandaloneConfiguration",
            org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore.class.getClassLoader());

    private final RedisConnectionFactory connectionFactory;
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    private String prefix = "";

    private Method redisConnectionSet_2_0;


    @Override
    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private void loadRedisConnectionMethods_2_0_a() {
        this.redisConnectionSet_2_0 = ReflectionUtils.findMethod(
                RedisConnection.class, "set", byte[].class, byte[].class);
    }

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }


    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    /**
     * 存储刷新token
     *
     * @param refreshToken
     * @param authentication
     */
    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());
        byte[] approvalKey = serializeKey(UNAME_TO_REFRESH + getApprovalKey(authentication));
        byte[] serializedRefreshToken = serialize(refreshToken);
        try (RedisConnection conn = getConnection()) {
            conn.openPipeline();
            if (springDataRedis_2_0) {
                try {
                    this.redisConnectionSet_2_0.invoke(conn, refreshKey, serializedRefreshToken);
                    this.redisConnectionSet_2_0.invoke(conn, refreshAuthKey, serialize(authentication));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                conn.set(refreshKey, serializedRefreshToken);
                conn.set(refreshAuthKey, serialize(authentication));
            }
            if (!authentication.isClientOnly()) {
                conn.sAdd(approvalKey, serializedRefreshToken);
            }
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
                Date expiration = expiringRefreshToken.getExpiration();
                if (expiration != null) {
                    long seconds = (expiration.getTime() - System.currentTimeMillis()) / 1000L;
                    conn.expire(refreshKey, seconds);
                    conn.expire(refreshAuthKey, seconds);
                    conn.expire(approvalKey, seconds);
                }
            }
            conn.closePipeline();
        }
    }


    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }


    /**
     * 删除刷新token
     *
     * @param refreshToken
     */
    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }


    /**
     * 删除刷新token
     * 删除时，会删除用户与refreshToken的关联关系
     *
     * @param tokenValue
     */
    @Override
    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = serializeKey(REFRESH + tokenValue);
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
        byte[] refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);

        try (RedisConnection conn = getConnection()) {
            conn.openPipeline();
            conn.get(refreshKey);
            conn.get(refreshAuthKey);
            conn.del(refreshKey);
            conn.del(refreshAuthKey);
            conn.del(refresh2AccessKey);
            List<Object> results = conn.closePipeline();
            byte[] refresh = (byte[]) results.get(0);
            byte[] auth = (byte[]) results.get(1);
            OAuth2Authentication authentication = deserializeAuthentication(auth);
            if (authentication != null) {
                byte[] unameKey = serializeKey(UNAME_TO_REFRESH + getApprovalKey(authentication));
                conn.sRem(unameKey, refresh);
            }
        }
    }

    /**
     * 根据client+username 获取刷新token
     *
     * @param clientId
     * @param userName
     * @return
     */
    public Collection<OAuth2RefreshToken> findRefreshTokensByClientIdAndUserName(String clientId, String userName) {
        byte[] approvalKey = serializeKey(UNAME_TO_REFRESH + getApprovalKey(clientId, userName));
        List<byte[]> byteList;
        try (RedisConnection conn = getConnection()) {
            byteList = getByteLists(approvalKey, conn);
        }
        if (byteList.size() == 0) {
            return Collections.emptySet();
        }
        List<OAuth2RefreshToken> refreshTokens = new ArrayList<>(byteList.size());
        for (byte[] bytes : byteList) {
            OAuth2RefreshToken refreshToken = deserializeRefreshToken(bytes);
            refreshTokens.add(refreshToken);
        }
        return Collections.unmodifiableCollection(refreshTokens);
    }

    private List<byte[]> getByteLists(byte[] approvalKey, RedisConnection conn) {
        List<byte[]> byteList;
        Long size = conn.sCard(approvalKey);
        assert size != null;
        byteList = new ArrayList<>(size.intValue());
        Cursor<byte[]> cursor = conn.sScan(approvalKey, ScanOptions.NONE);
        while (cursor.hasNext()) {
            byteList.add(cursor.next());
        }
        return byteList;
    }

}
