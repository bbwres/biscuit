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

import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import org.springframework.data.redis.core.RedisOperations;

import java.util.concurrent.TimeUnit;

/**
 * redis 检查与处理用户锁定服务
 *
 * @author zhanglinfeng
 */
public class RedisCheckUserLockService {
    private final RedisOperations<Object, Object> redisOperations;
    private final BiscuitSecurityProperties biscuitSecurityProperties;

    private final static String REDIS_CHECK_USER_LOCK_KEY = "redis_check_user_lock_key:%s:%s";

    public RedisCheckUserLockService(RedisOperations<Object, Object> redisOperations,
                                     BiscuitSecurityProperties biscuitSecurityProperties) {
        this.redisOperations = redisOperations;
        this.biscuitSecurityProperties = biscuitSecurityProperties;
    }


    /**
     * 增加登录失败次数统计
     *
     * @param user
     * @param tenantId
     */
    public void addLoginFailNum(String tenantId, String user) {
        String key = String.format(REDIS_CHECK_USER_LOCK_KEY, tenantId, user);
        redisOperations.opsForValue().increment(key);
        redisOperations.expire(key, biscuitSecurityProperties.getAccountLockExpireSecond(), TimeUnit.SECONDS);
    }

    /**
     * 检查用户是否被锁定
     *
     * @param tenantId
     * @param user
     * @return true-用户已经被锁定，false-用户正常
     */
    public boolean checkLoginFailLock(String tenantId, String user) {
        String key = String.format(REDIS_CHECK_USER_LOCK_KEY, tenantId, user);
        Object result = redisOperations.opsForValue().get(key);
        if (result instanceof Integer num) {
            return num >= biscuitSecurityProperties.getLoginFailureLockThreshold();
        }
        return false;
    }

    /**
     * 删除用户登录失败的锁定
     *
     * @param tenantId
     * @param user
     */
    public void deleteLoginFailLock(String tenantId, String user) {
        String key = String.format(REDIS_CHECK_USER_LOCK_KEY, tenantId, user);
        redisOperations.delete(key);
    }
}
