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

package cn.bbwres.biscuit.web.file.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件hash计算
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class LargeFileHashUtil {
    /**
     * 缓冲区大小（推荐8KB~64KB，平衡性能与内存）
     * // 32KB
     */
    private static final int BUFFER_SIZE = 32 * 1024;

    /**
     * 预定义十六进制字符，避免重复计算
     */
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();


    /**
     * 计算文件哈希
     *
     * @param file      目标文件
     * @param algorithm 哈希算法（MD5/SHA-256/SHA-512）
     * @return 十六进制哈希字符串
     * @throws java.security.NoSuchAlgorithmException if any.
     * @throws java.io.IOException if any.
     */
    public static String calculateFileHash(File file, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            // 分块读取文件并更新摘要
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        // 生成最终哈希值并转为十六进制
        return bytesToHex(digest.digest());
    }

    /**
     * 字节数组转十六进制字符串（优化性能）
     *
     * @param hash
     * @return
     */
    private static String bytesToHex(byte[] hash) {
        char[] hexChars = new char[hash.length * 2];
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & 0xFF;
            hexChars[i * 2] = HEX_DIGITS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_DIGITS[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 简化方法：计算SHA-256（推荐用于大文件）
     *
     * @param file a {@link java.io.File} object
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @return a {@link java.lang.String} object
     */
    public static String calculateSHA256(File file) throws NoSuchAlgorithmException, IOException {
        return calculateFileHash(file, "SHA-256");
    }

    /**
     * 简化方法：计算MD5（仅用于非加密场景）
     *
     * @param file a {@link java.io.File} object
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @return a {@link java.lang.String} object
     */
    public static String calculateMD5(File file) throws NoSuchAlgorithmException, IOException {
        return calculateFileHash(file, "MD5");
    }
}
