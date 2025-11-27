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

package cn.bbwres.biscuit.utils.desensitize.impl;

import cn.bbwres.biscuit.utils.desensitize.DefaultDesensitization;
import cn.bbwres.biscuit.utils.desensitize.DesensitizationUtils;

/**
 * 姓名脱敏
 *
 * @author zhanglinfeng
 */
public class NameDesensitization implements DefaultDesensitization {
    /**
     * 脱敏实现
     * 姓名脱敏：
     * - 1字名：不脱敏（如"张"）
     * - 2字名：保留首字（如"张*"）
     * - 3字及以上：保留前2字（如"张三*"、"张三丰*"）
     *
     * @param target 脱敏对象
     * @return 脱敏返回结果
     */
    @Override
    public String desensitize(String target,int prefixLen, int suffixLen) {
        int length = target.length();
        if (length == 1) {
            return target;
        } else if (length == 2) {
            return DesensitizationUtils.desensitizeCustom(target, 1, 0);
        } else {
            return DesensitizationUtils.desensitizeCustom(target, 2, 0);
        }
    }
}
