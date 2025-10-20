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
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * clientId 与token的关系
 *
 * @author zhanglinfeng
 */
@Data
@Accessors(chain = true)
public class OAuth2ClientPrincipalName implements Serializable {
    public static final String KEY_FORMATE = "oauth2_authorization_user:%s:%s";

    @Serial
    private static final long serialVersionUID = 2354732181431339641L;

    /**
     * 客户端id
     */
    private String registeredClientId;

    /**
     * 主体名称
     */
    private String principalName;


    /**
     * 获取redis的key
     *
     * @return key
     */
    public String getRedisKey() {
        return String.format(KEY_FORMATE, registeredClientId, principalName);
    }


}
