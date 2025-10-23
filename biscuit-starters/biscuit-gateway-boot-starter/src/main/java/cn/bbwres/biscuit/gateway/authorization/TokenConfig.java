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
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * token配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
public class TokenConfig {



    /**
     * 处理jwt token
     *
     * @param resourceService a {@link cn.bbwres.biscuit.gateway.service.ResourceService} object
     * @return a {@link org.springframework.security.authentication.ReactiveAuthenticationManager} object
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ResourceService resourceService) {
        return new ReactiveAuthenticationManager() {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return Mono.justOrEmpty(authentication)
                        .filter((a) -> a instanceof BearerTokenAuthenticationToken)
                        .cast(BearerTokenAuthenticationToken.class)
                        .map(BearerTokenAuthenticationToken::getToken)
                        .flatMap(this::makeRequest)
                        .flatMap(this::parseToken)
                        .cast(Authentication.class)
                        .onErrorMap(JwtException.class, this::onError);
            }

            /**
             * 创建请求
             * @param token
             * @return
             */
            private Mono<UserBaseInfo<?>> makeRequest(String token) {
                return Mono.justOrEmpty(resourceService.checkToken(token));
            }

            /**
             *
             * @param userBaseInfo
             * @return
             */
            private Mono<MapAuthentication> parseToken(UserBaseInfo<?> userBaseInfo) {
                return Mono.justOrEmpty(new MapAuthentication(userBaseInfo));
            }

            /**
             * 转换失败处理
             * @param ex
             * @return
             */
            private AuthenticationException onError(JwtException ex) {
                if (ex instanceof BadJwtException) {
                    return new InvalidBearerTokenException(ex.getMessage(), ex);
                }
                return new AuthenticationServiceException(ex.getMessage(), ex);
            }
        };
    }

}
