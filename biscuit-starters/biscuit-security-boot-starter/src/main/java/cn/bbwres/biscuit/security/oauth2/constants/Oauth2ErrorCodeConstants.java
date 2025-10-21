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

package cn.bbwres.biscuit.security.oauth2.constants;

import cn.bbwres.biscuit.exception.constants.ErrorCode;

/**
 * oauth2 相关错误
 *
 * @author zhanglinfeng
 */
public interface Oauth2ErrorCodeConstants {

    //############################## 登录相关错误 ##########################################

    /**
     * 配置错误
     */
    ErrorCode OAUTH2_SYSTEM_CONFIG_ERROR = new ErrorCode("101001001", "oauth2.system_config_error");
    /**
     * 用户名或密码错误
     */
    ErrorCode OAUTH2_USERNAME_PASSWORD_ERROR = new ErrorCode("101001002", "oauth2.username_password_error");

    /**
     * 用户被锁定
     */
    ErrorCode OAUTH2_USER_LOCKED = new ErrorCode("101001003", "oauth2.user_locked");

    /**
     * 用户被禁用
     */
    ErrorCode OAUTH2_USER_DISABLE = new ErrorCode("101001003", "oauth2.user_disable");

    /**
     * 用户已经过期
     */
    ErrorCode OAUTH2_USER_EXPIRED = new ErrorCode("101001004", "oauth2.user_expired");
    /**
     * 处理失败
     */
    ErrorCode OAUTH2_ERROR = new ErrorCode("101001999", "oauth2.error");
}
