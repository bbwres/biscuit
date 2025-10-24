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

import cn.bbwres.biscuit.security.oauth2.grant.AbstractGrantAuthenticationProvider;
import cn.bbwres.biscuit.utils.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.ObjectUtils;

/**
 * 账号密码登录
 *
 * @author zhanglinfeng
 */
public class UsernamePasswordGrantAuthenticationProvider extends AbstractGrantAuthenticationProvider {

    private final AuthenticationProvider authenticationProvider;


    public UsernamePasswordGrantAuthenticationProvider(OAuth2AuthorizationService authorizationService,
                                                       OAuth2TokenGenerator<?> tokenGenerator,
                                                       AuthenticationProvider authenticationProvider) {
        super(authorizationService, tokenGenerator);
        this.authenticationProvider = authenticationProvider;
    }


    /**
     * 认证处理
     *
     * @param oauth2AuthorizationGrantAuthentication 请求参数
     * @return Authentication 认证数据
     * @throws AuthenticationException
     */
    @Override
    protected Authentication authenticateHandler(OAuth2AuthorizationGrantAuthenticationToken oauth2AuthorizationGrantAuthentication) throws AuthenticationException {
        UsernamePasswordGrantAuthenticationToken customCodeGrantAuthentication = (UsernamePasswordGrantAuthenticationToken) oauth2AuthorizationGrantAuthentication;
        String username = customCodeGrantAuthentication.getUsername();
        if (!ObjectUtils.isEmpty(customCodeGrantAuthentication.getTenantId())) {
            username = String.join(StringUtils.ARRAY_SPLIT, customCodeGrantAuthentication.getTenantId(), username);
        }
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
                customCodeGrantAuthentication.getPassword());
        // Allow subclasses to set the "details" property
        customCodeGrantAuthentication.setPassword(null);
        return authenticationProvider.authenticate(authRequest);
    }


    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
     * indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the presented
     * <code>Authentication</code> object. It simply indicates it can support closer
     * evaluation of it. An <code>AuthenticationProvider</code> can still return
     * <code>null</code> from the {@link #authenticate(Authentication)} method to indicate
     * another <code>AuthenticationProvider</code> should be tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     *
     * @param authentication
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
