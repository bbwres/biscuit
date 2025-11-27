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

package cn.bbwres.biscuit.utils.desensitize;


import org.apache.commons.lang3.StringUtils;

/**
 * 通用脱敏方法
 *
 * @author zhanglinfeng
 */
public class DesensitizationUtils {

    /**
     * 自定义脱敏规则：保留前缀和后缀，中间用*填充
     *
     * @param value     原始字符串
     * @param prefixLen 保留前缀长度
     * @param suffixLen 保留后缀长度
     * @return 脱敏后字符串
     */
    public static String desensitizeCustom(String value, int prefixLen, int suffixLen) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        int length = value.length();
        if (prefixLen + suffixLen >= length) {
            // 前缀+后缀长度超过总长度，返回原字符串（避免无意义脱敏）
            return value;
        }
        String prefix = value.substring(0, prefixLen);
        String suffix = value.substring(length - suffixLen);
        String mask = StringUtils.repeat("*", length - prefixLen - suffixLen);
        return prefix + mask + suffix;
    }

    /**
     * 根据正则表达式返回
     * value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
     *
     * @param value
     * @param regex
     * @param replacement
     * @return
     */
    private static String desensitizeRegex(String value, String regex, String replacement) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return value.replaceAll(regex, replacement);
    }

}
