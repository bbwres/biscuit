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

import cn.bbwres.biscuit.security.oauth2.grant.username.UsernamePasswordGrantAuthenticationToken;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
import cn.bbwres.biscuit.security.oauth2.vo.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ObjectUtils;

/**
 * 登录之后的事件服务
 *
 * @author zhanglinfeng
 */
@Slf4j
public class DefaultAuthenticationLoginServiceImpl implements AuthenticationLoginService {

    private final RedisCheckUserLockService redisCheckUserLockService;


    public DefaultAuthenticationLoginServiceImpl(ObjectProvider<RedisCheckUserLockService> redisCheckUserLockServiceObjectProvider) {
        this.redisCheckUserLockService = redisCheckUserLockServiceObjectProvider.getIfAvailable();
    }


    /**
     * 登录成功
     *
     * @param authUser 登录成功的用户
     */
    @Override
    public void loginSuccess(AuthUser authUser) {
        log.info("当前用户:{},登录成功", authUser.getUsername());
    }

    /**
     * 登录失败处理
     *
     * @param user         用户名称
     * @param errorMessage 错误描述
     */
    @Override
    public void loginFail(Authentication user, AuthenticationException errorMessage) {
        if (errorMessage instanceof BadCredentialsException
                && user instanceof UsernamePasswordGrantAuthenticationToken username
                && username.getGrantType().equals(UsernamePasswordGrantAuthenticationToken.PASSWORD)) {
            //账号密码错误
            //更新错误次数并判断状态
            String name = username.getUsername();
            String tenantId = username.getTenantId();
            if (!ObjectUtils.isEmpty(redisCheckUserLockService)) {
                redisCheckUserLockService.addLoginFailNum(tenantId, name);
            }
        }
    }
}
