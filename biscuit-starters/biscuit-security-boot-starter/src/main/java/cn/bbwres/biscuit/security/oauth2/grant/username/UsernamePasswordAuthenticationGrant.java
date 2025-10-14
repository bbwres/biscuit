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

package cn.bbwres.biscuit.security.oauth2.grant.username;

import cn.bbwres.biscuit.security.oauth2.grant.AbstractCustomAuthenticationGrant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;

/**
 * 账户密码认证支持
 *
 * @author zhanglinfeng
 */
@Slf4j
public class UsernamePasswordAuthenticationGrant extends AbstractCustomAuthenticationGrant {


    /**
     * 初始化账户密码认证支持
     *
     * @param authorizationService
     * @param tokenGenerator
     * @param daoAuthenticationProvider
     * @throws Exception
     */
    public UsernamePasswordAuthenticationGrant(OAuth2AuthorizationService authorizationService, DelegatingOAuth2TokenGenerator tokenGenerator,
                                               DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        super(new UsernamePasswordGrantAuthenticationConverter(), new UsernamePasswordGrantAuthenticationProvider(authorizationService, tokenGenerator,
                daoAuthenticationProvider));
    }


}
