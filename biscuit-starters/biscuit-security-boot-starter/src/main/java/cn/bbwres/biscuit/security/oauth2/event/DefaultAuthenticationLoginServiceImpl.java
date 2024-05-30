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

import cn.bbwres.biscuit.security.oauth2.vo.AuthUser;
import org.springframework.security.core.AuthenticationException;

/**
 * 登录之后的事件服务
 *
 * @author zhanglinfeng
 */
public class DefaultAuthenticationLoginServiceImpl implements AuthenticationLoginService {

    /**
     * 登录成功
     *
     * @param authUser 登录成功的用户
     */
    @Override
    public void loginSuccess(AuthUser authUser) {

    }

    /**
     * 登录失败处理
     *
     * @param username     用户名称
     * @param errorMessage 错误描述
     */
    @Override
    public void loginFail(String username, AuthenticationException errorMessage) {

    }
}
