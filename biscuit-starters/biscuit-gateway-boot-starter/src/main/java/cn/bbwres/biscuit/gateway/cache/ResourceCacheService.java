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

package cn.bbwres.biscuit.gateway.cache;

import cn.bbwres.biscuit.gateway.GatewayProperties;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 获取认证的url资源 的缓存服务
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class ResourceCacheService {

    /**
     * 登陆认证
     */
    public static final String LOGIN_AUTH_RESOURCE = "LOGIN_AUTH_RESOURCE";

    private Cache<String, Mono<List<String>>> resourceNoUserCache;

    private Cache<String, Mono<List<String>>> resourceRoleCache;

    private final GatewayProperties gatewayProperties;

    private final ResourceService resourceService;

    /**
     * <p>Constructor for ResourceCacheService.</p>
     *
     * @param gatewayProperties a {@link cn.bbwres.biscuit.gateway.GatewayProperties} object
     * @param resourceService   a {@link cn.bbwres.biscuit.gateway.service.ResourceService} object
     */
    public ResourceCacheService(GatewayProperties gatewayProperties, ResourceService resourceService) {
        this.gatewayProperties = gatewayProperties;
        this.resourceService = resourceService;
    }

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        if (gatewayProperties.getCacheResource()) {
            resourceNoUserCache = Caffeine.newBuilder()
                    .expireAfterWrite(gatewayProperties.getLocalCacheResourceTime(), TimeUnit.SECONDS)
                    .maximumSize(10)
                    .build();

            resourceRoleCache = Caffeine.newBuilder()
                    .expireAfterWrite(gatewayProperties.getLocalCacheResourceTime(), TimeUnit.SECONDS)
                    .maximumSize(2000)
                    .build();
        }
    }


    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return a {@link java.util.List} object
     */
    public Mono<List<String>> getLoginAuthResource() {
        if (gatewayProperties.getCacheResource()) {
            Mono<List<String>> loginAuthResourceMono = resourceNoUserCache.getIfPresent(LOGIN_AUTH_RESOURCE);
            if (loginAuthResourceMono != null) {
                return loginAuthResourceMono;
            }
            loginAuthResourceMono = resourceService.getLoginAuthResource().cache();
            resourceNoUserCache.put(LOGIN_AUTH_RESOURCE, loginAuthResourceMono);
            return loginAuthResourceMono;
        }
        return resourceService.getLoginAuthResource();
    }


    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleIds 角色id
     * @return a {@link java.util.List} object
     */
    public Mono<List<String>> getResourceByRole(Set<String> roleIds) {

        if (gatewayProperties.getCacheResource()) {
            String jsonKey = String.join("-", roleIds);
            Mono<List<String>> resourceByRoleMono = resourceRoleCache.getIfPresent(jsonKey);
            if (resourceByRoleMono != null) {
                return resourceByRoleMono;
            }
            resourceByRoleMono = resourceService.getResourceByRole(roleIds).cache();
            resourceRoleCache.put(jsonKey, resourceByRoleMono);
            return resourceByRoleMono;
        }
        return resourceService.getResourceByRole(roleIds);
    }


    /**
     * 获取登陆地址
     *
     * @param state a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public Mono<String> getLoginUrlBuildState(String state) {
        return resourceService.getLoginUrlBuildState(state);
    }


}
