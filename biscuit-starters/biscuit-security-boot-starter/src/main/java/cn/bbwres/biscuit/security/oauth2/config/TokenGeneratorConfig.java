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
import cn.bbwres.biscuit.security.oauth2.properties.BiscuitSecurityProperties;
import cn.bbwres.biscuit.security.oauth2.vo.AuthUser;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.OAuth2Token;
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
import java.util.UUID;

/**
 * token 生成相关配置
 * @author zhanglinfeng
 */
@Slf4j
@Configuration
public class TokenGeneratorConfig {


    /**
     * OAuth2TokenGenerator 配置
     *
     * @param tokenGenerators
     * @return
     */
    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> oauth2TokenGenerator(OAuth2TokenGenerator<? extends OAuth2Token>... tokenGenerators) {
        return new DelegatingOAuth2TokenGenerator(tokenGenerators);
    }


    /**
     * accessToken 生成处理
     *
     * @param accessTokenCustomizer accessTokenCustomizer
     * @return OAuth2AccessTokenGenerator
     */
    @Bean
    public OAuth2AccessTokenGenerator oauth2AccessTokenGenerator(OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer) {
        OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator = new OAuth2AccessTokenGenerator();
        oAuth2AccessTokenGenerator.setAccessTokenCustomizer(accessTokenCustomizer);
        return oAuth2AccessTokenGenerator;
    }

    /**
     * RefreshToken  生成处理
     *
     * @return OAuth2RefreshTokenGenerator
     */
    @Bean
    public OAuth2RefreshTokenGenerator oauth2RefreshTokenGenerator() {
        return new OAuth2RefreshTokenGenerator();
    }

    /**
     * JwtGenerator  生成处理
     *
     * @return JwtGenerator
     */
    @Bean
    public JwtGenerator jwtGenerator(JWKSource<SecurityContext> jwkSource) {
        return new JwtGenerator(new NimbusJwtEncoder(jwkSource));
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
                throw new SystemRuntimeException(Oauth2ErrorCodeConstants.SYSTEM_CONFIG_ERROR);
            }
            try (ByteArrayInputStream publicKeyStream = new ByteArrayInputStream(properties.getJwtPublicKey().getBytes(StandardCharsets.UTF_8));
                 ByteArrayInputStream privateKeyStream = new ByteArrayInputStream(properties.getJwtPrivateKey().getBytes(StandardCharsets.UTF_8))) {
                RSAPublicKey publicKey = RsaKeyConverters.x509().convert(publicKeyStream);
                RSAPrivateKey privateKey = RsaKeyConverters.pkcs8().convert(privateKeyStream);
                return new KeyPair(publicKey, privateKey);
            } catch (IOException e) {
                throw new SystemRuntimeException(Oauth2ErrorCodeConstants.SYSTEM_CONFIG_ERROR);
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




    /**
     * 扩展token
     *
     * @return OAuth2TokenCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> accessTokenCustomizer() {
        return context -> {
            // Customize claims
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                // Customize headers/claims for access_token
                OAuth2TokenClaimsSet.Builder claims = context.getClaims();
                if (context.getPrincipal() instanceof AuthUser) {
                    AuthUser user = context.getPrincipal();
                    claims.claim("zh_name", user.getZhName());
                    claims.claim("user_id", user.getUserId());
                    claims.claim("tenant_id", user.getTenantId());
                    claims.claim("scop", context.getAuthorizedScopes());
                }
            }

        };
    }
}
