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

package cn.bbwres.biscuit.security.oauth2.service.redis.repository;

import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2UserConsent;
import org.springframework.data.repository.CrudRepository;

/**
 * The following listing shows the OAuth2UserConsentRepository, which is able to find and delete an OAuth2UserConsent
 * by the registeredClientId and principalName fields that form the composite primary key.
 *
 * @author zhanglinfeng
 */
public interface OAuth2UserConsentRepository extends CrudRepository<OAuth2UserConsent, String> {

    /**
     * 根据客户端id和主体名称查询OAuth2UserConsent
     *
     * @param registeredClientId 客户端id
     * @param principalName      主体名称
     * @return OAuth2UserConsent
     */
    OAuth2UserConsent findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    /**
     * 根据客户端id和主体名称删除OAuth2UserConsent
     *
     * @param registeredClientId 客户端id
     * @param principalName      主体名称
     */
    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

}