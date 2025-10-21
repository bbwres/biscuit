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

package cn.bbwres.biscuit.security.oauth2.config;


import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2ErrorCodeConstants;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2SystemConstants;
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.vo.AuthUser;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

/**
 * token 生成相关配置
 *
 * @author zhanglinfeng
 */
@Slf4j
@AutoConfiguration
public class TokenGeneratorConfig {


    /**
     * OAuth2TokenGenerator 配置
     *
     * @return DelegatingOAuth2TokenGenerator
     */
    @Bean
    public DelegatingOAuth2TokenGenerator oauth2TokenGenerator(ObjectProvider<OAuth2TokenCustomizer<OAuth2TokenClaimsContext>> accessTokenCustomizer,
                                                               ObjectProvider<OAuth2TokenCustomizer<JwtEncodingContext>> jwtCustomizer,
                                                               JWKSource<SecurityContext> jwkSource) {
        OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2TokenCustomizer<OAuth2TokenClaimsContext> oauth2TokenClaimsContextCustomizer = accessTokenCustomizer.getIfAvailable();
        if (!ObjectUtils.isEmpty(oauth2TokenClaimsContextCustomizer)) {
            oAuth2AccessTokenGenerator.setAccessTokenCustomizer(oauth2TokenClaimsContextCustomizer);
        }
        JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
        OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextCustomizer = jwtCustomizer.getIfAvailable();
        if (!ObjectUtils.isEmpty(jwtEncodingContextCustomizer)) {
            jwtGenerator.setJwtCustomizer(jwtEncodingContextCustomizer);
        }
        return new DelegatingOAuth2TokenGenerator(oAuth2AccessTokenGenerator,
                new OAuth2RefreshTokenGenerator(),
                jwtGenerator);
    }


    /**
     * 扩展token
     *
     * @return OAuth2TokenCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                OAuth2TokenClaimsSet.Builder claims = context.getClaims();
                claims.claims(claimsMap -> claimsMap.putAll(buildClaimsMap(context)));
            }
        };
    }

    /**
     * 扩展token
     *
     * @return OAuth2TokenCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContext() {
        return context -> {
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                JwtClaimsSet.Builder claims = context.getClaims();
                claims.claims(claimsMap -> claimsMap.putAll(buildClaimsMap(context)));
            }
        };
    }

    /**
     * 设置ClaimsMap信息
     *
     * @param context
     * @return
     */
    private Map<String, Object> buildClaimsMap(OAuth2TokenContext context) {
        Map<String, Object> claims = new HashMap<>(16);
        Authentication principal = context.getPrincipal();
        if (principal.getPrincipal() instanceof AuthUser user) {
            claims.put(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX + "zh_name", user.getZhName());
            claims.put(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX + "user_id", user.getUserId());
            claims.put(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX + "tenant_id", user.getTenantId());
        }
        Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                .stream()
                .map(c -> c.replaceFirst("^ROLE_", ""))
                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));

        claims.put(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX + "roles", roles);
        claims.put(Oauth2SystemConstants.CUSTOM_CLAIMS_PREFIX + "grant_type", context.getAuthorizationGrantType().getValue());
        return claims;
    }

    /**
     * JwtDecoder 解码配置
     *
     * @param jwkSource
     * @return
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource(BiscuitSecurityProperties properties) {
        KeyPair keyPair = generateRsaKey(properties);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();


        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }


    /**
     * 初始化生成rsa密钥信息
     *
     * @param properties
     * @return
     */
    private KeyPair generateRsaKey(BiscuitSecurityProperties properties) {

        if (!properties.getAutoGeneratorJwtKey()) {
            log.info("使用配置的jwt密钥信息");
            if (ObjectUtils.isEmpty(properties.getJwtPrivateKey()) || ObjectUtils.isEmpty(properties.getJwtPublicKey())) {
                log.warn("当前使用配置的jwt密钥时处理失败！未设置私钥或者公钥");
                throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_SYSTEM_CONFIG_ERROR);
            }
            try (ByteArrayInputStream publicKeyStream = new ByteArrayInputStream(properties.getJwtPublicKey().getBytes(StandardCharsets.UTF_8));
                 ByteArrayInputStream privateKeyStream = new ByteArrayInputStream(properties.getJwtPrivateKey().getBytes(StandardCharsets.UTF_8))) {
                RSAPublicKey publicKey = RsaKeyConverters.x509().convert(publicKeyStream);
                RSAPrivateKey privateKey = RsaKeyConverters.pkcs8().convert(privateKeyStream);
                return new KeyPair(publicKey, privateKey);
            } catch (IOException e) {
                throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_SYSTEM_CONFIG_ERROR);
            }
        }
        KeyPair keyPair;
        try {
            log.info("使用自动生成的jwt密钥信息");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(properties.getAutoGeneratorJwtKeySize());
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


}
