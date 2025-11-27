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
 * 银行卡号脱敏：保留前6位和后4位，中间用*代替（如622202****0123）
 *
 * @author zhanglinfeng
 */
public class BankCardDesensitization implements DefaultDesensitization {
    /**
     * 脱敏实现
     *
     * @param target 脱敏对象
     * @return 脱敏返回结果
     */
    @Override
    public String desensitize(String target, int prefixLen, int suffixLen) {
        return DesensitizationUtils.desensitizeCustom(target, prefixLen == -1 ? 6 : prefixLen, suffixLen == -1 ? 4 : suffixLen);
    }
}
