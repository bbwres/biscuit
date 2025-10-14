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

import cn.bbwres.biscuit.security.oauth2.service.redis.pojo.OAuth2UserConsent;
import cn.bbwres.biscuit.security.oauth2.service.redis.repository.OAuth2UserConsentRepository;
import jakarta.annotation.Nullable;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The following listing shows the RedisOAuth2AuthorizationConsentService, which uses an OAuth2UserConsentRepository for persisting an OAuth2UserConsent
 * and maps to and from the OAuth2AuthorizationConsent domain object, using the ModelMapper utility class.
 *
 * @author zhanglinfeng
 */
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final OAuth2UserConsentRepository userConsentRepository;

    public RedisOAuth2AuthorizationConsentService(OAuth2UserConsentRepository userConsentRepository) {
        Assert.notNull(userConsentRepository, "userConsentRepository cannot be null");
        this.userConsentRepository = userConsentRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        OAuth2UserConsent oauth2UserConsent = new OAuth2UserConsent();
        oauth2UserConsent.setId(authorizationConsent.getRegisteredClientId() + authorizationConsent.getPrincipalName());
        oauth2UserConsent.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        oauth2UserConsent.setPrincipalName(authorizationConsent.getPrincipalName());
        oauth2UserConsent.setAuthorities(authorizationConsent.getAuthorities());
        this.userConsentRepository.save(oauth2UserConsent);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        this.userConsentRepository.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    @Nullable
    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        OAuth2UserConsent oauth2UserConsent = this.userConsentRepository
                .findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);
        if (ObjectUtils.isEmpty(oauth2UserConsent)) {
            return null;
        }
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(registeredClientId, principalName);
        builder.authorities(grantedAuthorities -> grantedAuthorities.addAll(oauth2UserConsent.getAuthorities()));
        return builder.build();
    }
}
