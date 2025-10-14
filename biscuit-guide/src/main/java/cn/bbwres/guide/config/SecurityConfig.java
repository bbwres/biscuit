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

package cn.bbwres.guide.config;

import cn.bbwres.biscuit.security.oauth2.web.JsonAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.io.IOException;
import java.time.Duration;

/**
 * 安全配置
 *
 * @author zhanglinfeng
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> {
                            authorize.anyRequest().authenticated();
                            //authorize.anyRequest().permitAll();
                        }
                )
                .csrf(AbstractHttpConfigurer::disable)
//                .addFilterBefore(new Filter() {
//                    @Override
//                    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//                        System.out.println("密码校验之前的检查");
//                        chain.doFilter(request, response);
//                    }
//                }, UsernamePasswordAuthenticationFilter.class)

                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                //.formLogin(form -> form.loginPage("/user/login"))
                .formLogin(Customizer.withDefaults())
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedHandler(new AccessDeniedHandler() {
                            @Override
                            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
                                response.setStatus(401);
                                response.getWriter().write("{\"code\":\"11111\"}");
                            }
                        })
                        .defaultAuthenticationEntryPointFor(
                                new JsonAuthenticationEntryPoint(),
                                new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
                        )
                )
                .oauth2ResourceServer((oauth2ResourceServer) -> {
                            oauth2ResourceServer.jwt((jwt) ->
                                    jwt.decoder(jwtDecoder));
//                            oauth2ResourceServer.opaqueToken(opaqueToken -> {
//                                opaqueToken.introspector(new OpaqueTokenIntrospector() {
//                                    @Override
//                                    public OAuth2AuthenticatedPrincipal introspect(String token) {
//                                        return null;
//                                    }
//                                });
//                            });
                        }
                )
        ;

        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("zlf")
                .roles("USER_1")
               // .passwordEncoder(aa->"{MD5}45fda22435f89f22f2ce6756a3cf32c4")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId("admin")
                .clientId("admin")
                .clientSecret("{noop}zlf")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                //设置公开的、无需客户端认证的客户端
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .tokenSettings(TokenSettings.builder()
                        //设置不透明token
                       // .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        //刷新token只能使用一次
                        .reuseRefreshTokens(false)
                        // .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        .accessTokenTimeToLive(Duration.ofSeconds(1800))
                        .refreshTokenTimeToLive(Duration.ofSeconds(3600)).build())
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

}
