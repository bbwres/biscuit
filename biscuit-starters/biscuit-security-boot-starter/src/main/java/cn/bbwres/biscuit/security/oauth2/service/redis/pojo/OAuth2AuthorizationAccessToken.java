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

package cn.bbwres.biscuit.security.oauth2.service.redis.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Set;

/**
 * accessToken 信息
 *
 * @author zhanglinfeng
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OAuth2AuthorizationAccessToken extends BaseOAuth2AuthorizationToken {
    @Serial
    private static final long serialVersionUID = 914094183203192957L;


    /**
     * accessToken 授权范围
     */
    private Set<String> scopes;

    /**
     * accessToken的TokenType
     */
    private String tokenType;

}
