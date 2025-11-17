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

package cn.bbwres.biscuit.security.oauth2.service;

import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
import cn.bbwres.biscuit.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 获取用户信息
 * 自定义扩展的用户信息
 *
 * @author zhanglinfeng
 */
@Slf4j
public abstract class AbstractCustomUserDetailsService implements UserDetailsService {

    private final RedisCheckUserLockService redisCheckUserLockService;

    public AbstractCustomUserDetailsService(RedisCheckUserLockService redisCheckUserLockService) {
        this.redisCheckUserLockService = redisCheckUserLockService;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String user = username;
        String tenantId = null;
        String clientId = null;

        if (username.contains(StringUtils.ARRAY_SPLIT)) {
            String[] split = username.split(StringUtils.ARRAY_SPLIT);
            user = new String(Base64.getDecoder().decode(split[0]), StandardCharsets.UTF_8);
            clientId = split[1];
            tenantId = split[2];
        }
        boolean checkLoginFailLock = redisCheckUserLockService.checkLoginFailLock(tenantId, username);
        if (checkLoginFailLock) {
            log.info("当前用户:[{}]因失败次数过多已经被锁定", username);
            throw new LockedException(username);
        }
        return loadUserByUsername(user, tenantId, clientId);
    }

    /**
     * 获取用户信息
     *
     * @param tenantId
     * @param user
     * @param clientId
     * @return
     */
    public abstract UserDetails loadUserByUsername(String user, String tenantId, String clientId);
}
