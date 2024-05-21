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


import cn.bbwres.biscuit.gateway.cache.ResourceCacheService;
import cn.bbwres.biscuit.gateway.constants.GatewayConstant;
import cn.bbwres.biscuit.gateway.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 认证配置信息
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@RequiredArgsConstructor
@Slf4j
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    private final ResourceCacheService resourceCacheService;

    private final PathMatcher pathMatcher;

    /**
     * {@inheritDoc}
     *
     * 检查权限
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        //是否是已经认证
        Mono<Authentication> authenticationMono = mono.filter(Authentication::isAuthenticated);
        //获取当前用户的角色和拥有的资源
        Mono<Set<String>> resourceMono = authenticationMono.map(authentication -> {
            Set<String> resources = new HashSet<>();
            //获取只需要登陆就可以拿到的资源
            List<String> noAuthResource = resourceCacheService.getLoginAuthResource();
            if (!CollectionUtils.isEmpty(noAuthResource)) {
                resources.addAll(noAuthResource);
            }
            //设置用户的信息
            authorizationContext.getExchange().getAttributes().put(GatewayConstant.USER_INFO, authentication.getPrincipal());

            //获取用户角色信息
            authentication.getAuthorities().stream()
                    .map(authority -> resourceCacheService.getResourceByRole(authority.getAuthority()))
                    .filter(Objects::nonNull).forEach(resources::addAll);

            return resources;
        });

        return resourceMono.map(resources -> AuthUtils.checkAuth(new ArrayList<>(resources), path, pathMatcher))
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }


}
