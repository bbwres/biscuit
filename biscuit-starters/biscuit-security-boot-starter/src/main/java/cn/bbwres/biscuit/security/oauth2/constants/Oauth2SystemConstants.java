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

/**
 * Oauth2 相关配置常量
 *
 * @author zhanglinfeng
 */
public interface Oauth2SystemConstants {

    /**
     * 租户id
     */
    String OAUTH2_PARAMETER_NAME_TENANT_ID = "tenant_id";

    /**
     * 自定义的token claims 的前缀
     */
    String CUSTOM_CLAIMS_PREFIX = "custom_";
    /**
     * 中文名称
     */
    String CUSTOM_CLAIMS_PREFIX_ZH_NAME = CUSTOM_CLAIMS_PREFIX + "zh_name";
    /**
     * 用户id
     */
    String CUSTOM_CLAIMS_PREFIX_USER_ID = CUSTOM_CLAIMS_PREFIX + "user_id";
    /**
     * 租户id
     */
    String CUSTOM_CLAIMS_PREFIX_TENANT_ID = CUSTOM_CLAIMS_PREFIX + OAUTH2_PARAMETER_NAME_TENANT_ID;
    /**
     * 角色
     */
    String CUSTOM_CLAIMS_PREFIX_ROLES = CUSTOM_CLAIMS_PREFIX + "roles";
    /**
     * grant_type
     */
    String CUSTOM_CLAIMS_PREFIX_GRANT_TYPE = CUSTOM_CLAIMS_PREFIX + "grant_type";
    /**
     * scop 参数
     */
    String PARAMS_SCOPE = "scope";

    /**
     * 单个用户登录
     */
    String CLIENT_SETTING_SINGLE_USER_LOGIN = "client.single_user_login";


    /**
     * oidc token
     */
    String OAUTH2_OIDC_TOKEN = "oidc_token";


}
