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

package cn.bbwres.biscuit.gateway.service;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.gateway.GatewayProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户资源信息获取
 *
 * @author zhanglinfeng
 */
public class ResourceServiceImpl implements ResourceService {

    private final WebClient webClient;
    private final GatewayProperties gatewayProperties;

    public ResourceServiceImpl(WebClient webClient, GatewayProperties gatewayProperties) {
        this.webClient = webClient;
        this.gatewayProperties = gatewayProperties;
    }

    private final ParameterizedTypeReference<Result<List<String>>> STRING_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    private final ParameterizedTypeReference<Result<UserBaseInfo>> USER_BASE_INFO_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };


    /**
     * 检查并解析token
     *
     * @param token a {@link String} object
     * @return a {@link Map} object
     */
    @Override
    public Mono<UserBaseInfo> checkToken(String token) {
        return webClient.post()
                .uri(gatewayProperties.getAuthorizationServerName() + gatewayProperties.getAuthorizationServerCheckTokenPath())
                .bodyValue(token)
                .retrieve()
                .bodyToMono(USER_BASE_INFO_TYPE_REFERENCE)
                .map(Result::checkAndGetData);
    }

    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return a {@link List} object
     */
    @Override
    public Mono<List<String>> getLoginAuthResource() {
        return webClient.get()
                .uri(gatewayProperties.getAuthorizationServerName() + gatewayProperties.getAuthorizationServerLoginAuthResource())
                .retrieve()
                .bodyToMono(STRING_TYPE_REFERENCE)
                .map(Result::checkAndGetData);
    }

    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleIds 角色id
     * @return a {@link List} object
     */
    @Override
    public Mono<List<String>> getResourceByRole(Set<String> roleIds) {
        return webClient.post()
                .uri(gatewayProperties.getAuthorizationServerName() + gatewayProperties.getAuthorizationServerResourceByRole())
                .bodyValue(roleIds)
                .retrieve()
                .bodyToMono(STRING_TYPE_REFERENCE)
                .map(Result::checkAndGetData);
    }
}
