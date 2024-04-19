package cn.bbwres.biscuit.gateway.authorization;

import cn.bbwres.biscuit.gateway.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

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
    public ReactiveAuthenticationManager jwtReactiveAuthenticationManager(ResourceService resourceService) {
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
            private Mono<Map<String, Object>> makeRequest(String token) {
                return Mono.justOrEmpty(resourceService.checkToken(token));
            }

            /**
             *
             * @param claims
             * @return
             */
            private Mono<MapAuthentication> parseToken(Map<String, Object> claims) {
                return Mono.justOrEmpty(new MapAuthentication(claims));
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

    /**
     * 处理不透明token
     *
     * @param resourceService a {@link cn.bbwres.biscuit.gateway.service.ResourceService} object
     * @return a {@link org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector} object
     */
    @Bean
    public ReactiveOpaqueTokenIntrospector reactiveOpaqueTokenIntrospector(ResourceService resourceService) {
        return new ReactiveOpaqueTokenIntrospector() {
            @Override
            public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
                return Mono.just(token)
                        //请求
                        .flatMap(this::makeRequest)
                        //转换
                        .map(this::convertClaimsSet)
                        .onErrorMap((e) -> !(e instanceof OAuth2IntrospectionException),
                                ex -> new OAuth2IntrospectionException(ex.getMessage(), ex));
            }

            /**
             * 创建认证对象
             * @param claims
             * @return
             */
            private OAuth2AuthenticatedPrincipal convertClaimsSet(Map<String, Object> claims) {
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.AUD, (k, v) -> {
                    if (v instanceof String) {
                        return Collections.singletonList(v);
                    }
                    return v;
                });
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.CLIENT_ID, (k, v) -> v.toString());
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.EXP,
                        (k, v) -> Instant.ofEpochSecond(((Number) v).longValue()));
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.IAT,
                        (k, v) -> Instant.ofEpochSecond(((Number) v).longValue()));
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.ISS, (k, v) -> v.toString());
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.NBF,
                        (k, v) -> Instant.ofEpochSecond(((Number) v).longValue()));
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                claims.computeIfPresent(OAuth2TokenIntrospectionClaimNames.SCOPE, (k, v) -> {
                    if (v instanceof String) {
                        Collection<String> scopes = Arrays.asList(((String) v).split(" "));
                        for (String scope : scopes) {
                            authorities.add(new SimpleGrantedAuthority(scope));
                        }
                        return scopes;
                    }
                    return v;
                });

                return new OAuth2IntrospectionAuthenticatedPrincipal(claims, authorities);
            }

            /**
             * 创建请求
             * @param token
             * @return
             */
            private Mono<Map<String, Object>> makeRequest(String token) {
                return Mono.justOrEmpty(resourceService.checkToken(token));
            }

        };
    }
}
