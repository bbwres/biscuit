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

package cn.bbwres.biscuit.service;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户端信息
 *
 * @author zhanglinfeng
 */
@Service("myClientDetailsService")
public class ClientDetailsServiceImpl implements ClientDetailsService {
    /**
     * Load a client by the client id. This method must not return null.
     *
     * @param clientId The client id.
     * @return The client details (never null).
     * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId(clientId);
        baseClientDetails.setClientSecret("$2a$04$gNs2bdaVny5TfM3HjYEraONBbj3wVsSHhd5SkPqMNaLpSTDrWhdBa");
        baseClientDetails.setAccessTokenValiditySeconds(1800);
        baseClientDetails.setRefreshTokenValiditySeconds(18000);
        baseClientDetails.setScope(List.of("all"));
        baseClientDetails.setAuthorizedGrantTypes(List.of("password", "refresh_token","captcha_password"));
        return baseClientDetails;
    }
}
