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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * Determines if access is granted for a specific authentication and object.
     *
     * @param mono                 the Authentication to check
     * @param authorizationContext the object to check
     * @return an decision or empty Mono if no decision could be made.
     * @deprecated please use {@link #authorize(Mono, Object)} instead
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        //是否是已经认证
        //获取当前用户的角色和拥有的资源
        Mono<Set<String>> resourceMono = mono.filter(Authentication::isAuthenticated)
                .flatMap(authentication -> {
                    //获取只需要登陆就可以拿到的资源
                    Mono<List<String>> loginAuthResource = resourceCacheService.getLoginAuthResource();
                    //获取用户角色信息
                    Set<String> roles = authentication.getAuthorities().stream()
                            .map((Function<GrantedAuthority, String>) GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    if (authentication instanceof MapAuthentication mapAuthentication) {
                        mapAuthentication.setAuthorities(null);
                    }
                    //设置用户的信息
                    authorizationContext.getExchange().getAttributes().put(GatewayConstant.USER_INFO, authentication);

                    return loginAuthResource.switchIfEmpty(Mono.just(new ArrayList<>(0)))
                            .zipWith(resourceCacheService.getResourceByRole(roles)
                                    .switchIfEmpty(Mono.just(new ArrayList<>(0))))
                            .map(objects -> {
                                Set<String> resources = new HashSet<>();
                                List<String> t1 = objects.getT1();
                                if (!CollectionUtils.isEmpty(t1)) {
                                    resources.addAll(t1);
                                }
                                List<String> t2 = objects.getT2();
                                if (!CollectionUtils.isEmpty(t2)) {
                                    resources.addAll(t2);
                                }
                                return resources;
                            });
                });

        return resourceMono.map(resources -> AuthUtils.checkAuth(resources, path, pathMatcher))
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }


}
