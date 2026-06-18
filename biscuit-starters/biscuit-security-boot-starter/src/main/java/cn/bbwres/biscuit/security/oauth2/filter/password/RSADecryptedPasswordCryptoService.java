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
package cn.bbwres.biscuit.security.oauth2.filter.password;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 密码处理
 */
@Slf4j
public class RSADecryptedPasswordCryptoService implements DecryptedPasswordCryptoService {


    private final Map<String, PrivateKey> privateKeyMap = new HashMap<>(16);


    /**
     * 解密密码
     *
     * @param transformation    算法串，格式 算法/模式/填充
     *                          对称：AES/CBC/PKCS5Padding、SM4/ECB/PKCS5Padding
     *                          非对称：RSA/ECB/PKCS1Padding、SM2
     * @param privateKey        密钥
     * @param iv                填充内容
     * @param encryptedPassword 密文
     * @return 解密后的内容
     */
    @Override
    public String decryptedPassword(String transformation, String privateKey, String iv, String encryptedPassword) {
        PrivateKey privateKeyObj = privateKeyMap.get(privateKey);
        if (privateKeyObj == null) {
            privateKeyObj = parsePrivateKey(privateKey);
            privateKeyMap.put(privateKey, privateKeyObj);
        }
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(encryptedPassword);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyObj);
            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("登录密码解密失败: {}", e.getMessage());
            throw new SystemRuntimeException(Oauth2ErrorCodeConstants.OAUTH2_PASSWORD_PAYLOAD_INVALID);
        }
    }

    /**
     * 解析 PKCS#8 Base64 格式的 RSA 私钥
     */
    private static PrivateKey parsePrivateKey(String base64) {
        if (!StringUtils.hasText(base64)) {
            throw new IllegalStateException("biscuit.security.password.private-key 未配置");
        }
        try {
            // 去掉可能的 PEM 头尾和换行
            String cleaned = base64.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] keyBytes = Base64.getDecoder().decode(cleaned);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("解析 RSA 私钥失败", e);
        }
    }
}
