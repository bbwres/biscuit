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
 * @author zhanglinfeng
 */
public interface Oauth2ErrorCodeConstants {

    //############################## 登录相关错误 ##########################################

    /**
     * 配置错误
     */
    ErrorCode SYSTEM_CONFIG_ERROR = new ErrorCode("101001001", "oauth2.system_config_error");
    ErrorCode INVALID_TOKEN = new ErrorCode("101000101", "oauth2.invalid_token");
}
