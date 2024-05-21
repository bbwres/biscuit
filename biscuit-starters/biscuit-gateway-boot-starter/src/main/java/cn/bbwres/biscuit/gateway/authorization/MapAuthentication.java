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

package cn.bbwres.biscuit.gateway.authorization;

import cn.bbwres.biscuit.constants.SystemAuthConstant;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * map 转换出来的token信息
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class MapAuthentication implements Authentication {

    private static final long serialVersionUID = -6852083236246571439L;


    /**
     * 角色信息
     */
    private Collection<GrantedAuthority> authorities;


    private Object details;

    private Object principal;
    private String name;

    /**
     * Indicates a successful or unsuccessful authentication.
     */
    private boolean authenticated;

    /**
     * <p>Constructor for MapAuthentication.</p>
     *
     * @param params a {@link java.util.Map} object
     */
    public MapAuthentication(Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            this.authenticated = false;
            return;
        }
        this.name = ObjectUtils.defaultIfNull(params.get(SystemAuthConstant.ZH_NAME), "").toString();
        this.principal = ObjectUtils.defaultIfNull(params.get(SystemAuthConstant.USER_ID), "").toString();
        this.details = params;
        this.authenticated = true;

        authorities = ((List<String>) ObjectUtils.defaultIfNull(params.get(SystemAuthConstant.AUTHORITIES),
                new ArrayList<>(16)))
                .stream()
                .map((Function<String, GrantedAuthority>) SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * Set by an <code>AuthenticationManager</code> to indicate the authorities that the
     * principal has been granted. Note that classes should not rely on this value as
     * being valid unless it has been set by a trusted <code>AuthenticationManager</code>.
     * <p>
     * Implementations should ensure that modifications to the returned collection array
     * do not affect the state of the Authentication object, or use an unmodifiable
     * instance.
     * </p>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * {@inheritDoc}
     *
     * The credentials that prove the principal is correct. This is usually a password,
     * but could be anything relevant to the <code>AuthenticationManager</code>. Callers
     * are expected to populate the credentials.
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Stores additional details about the authentication request. These might be an IP
     * address, certificate serial number etc.
     */
    @Override
    public Object getDetails() {
        return details;
    }

    /**
     * {@inheritDoc}
     *
     * The identity of the principal being authenticated. In the case of an authentication
     * request with username and password, this would be the username. Callers are
     * expected to populate the principal for an authentication request.
     * <p>
     * The AuthenticationManager implementation will often return an
     * Authentication containing richer information as the principal for use by
     * the application. Many of the authentication providers will create a
     * {@code UserDetails} object as the principal.
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }

    /**
     * {@inheritDoc}
     *
     * Used to indicate to {@code AbstractSecurityInterceptor} whether it should present
     * the authentication token to the <code>AuthenticationManager</code>. Typically an
     * <code>AuthenticationManager</code> (or, more often, one of its
     * <code>AuthenticationProvider</code>s) will return an immutable authentication token
     * after successful authentication, in which case that token can safely return
     * <code>true</code> to this method. Returning <code>true</code> will improve
     * performance, as calling the <code>AuthenticationManager</code> for every request
     * will no longer be necessary.
     * <p>
     * For security reasons, implementations of this interface should be very careful
     * about returning <code>true</code> from this method unless they are either
     * immutable, or have some way of ensuring the properties have not been changed since
     * original creation.
     */
    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    /**
     * {@inheritDoc}
     *
     * See {@link #isAuthenticated()} for a full description.
     * <p>
     * Implementations should <b>always</b> allow this method to be called with a
     * <code>false</code> parameter, as this is used by various classes to specify the
     * authentication token should not be trusted. If an implementation wishes to reject
     * an invocation with a <code>true</code> parameter (which would indicate the
     * authentication token is trusted - a potential security risk) the implementation
     * should throw an {@link IllegalArgumentException}.
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the name of this principal.
     */
    @Override
    public String getName() {
        return name;
    }
}
