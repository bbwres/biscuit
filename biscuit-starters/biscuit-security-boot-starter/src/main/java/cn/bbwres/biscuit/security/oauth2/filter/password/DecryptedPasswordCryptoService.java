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

/**
 * 密码解密算法套件
 *
 * @author zhanglinfeng
 */
public interface DecryptedPasswordCryptoService {

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
    String decryptedPassword(String transformation, String privateKey, String iv, String encryptedPassword);
}
