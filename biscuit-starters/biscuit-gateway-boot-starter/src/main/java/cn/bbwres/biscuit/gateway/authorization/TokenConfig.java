package cn.bbwres.biscuit.gateway.authorization;

import cn.bbwres.biscuit.gateway.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * token配置
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
public class TokenConfig {


    /**
     * 处理jwt token
     *
     * @param resourceService
     * @return
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
                        .flatMap((Function<String, Mono<Map<String, Object>>>)
                                token -> Mono.justOrEmpty(resourceService.checkToken(token)))
                        .flatMap(new Function<Map<String, Object>, Mono<AbstractAuthenticationToken>>() {
                            @Override
                            public Mono<AbstractAuthenticationToken> apply(Map<String, Object> stringObjectMap) {
                                return null;
                            }
                        })
                        .cast(Authentication.class)
                        .onErrorMap(JwtException.class, this::onError);
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
     * @return
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
