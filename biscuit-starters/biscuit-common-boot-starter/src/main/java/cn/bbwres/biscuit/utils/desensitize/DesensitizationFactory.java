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

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 脱敏器工厂
 *
 * @author zhanglinfeng
 */
public class DesensitizationFactory {

    private DesensitizationFactory() {
    }

    private static final Map<Class<?>, Desensitization<?>> CACHE_MAP = new HashMap<>();


    /**
     * 获取 脱敏器
     *
     * @param clazz
     * @return
     */
    public static Desensitization<?> getDesensitization(Class<?> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("desensitization is interface, what is expected is an implementation class !");
        }
        return CACHE_MAP.computeIfAbsent(clazz, key -> {
            try {
                return (Desensitization<?>) clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new SystemRuntimeException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
            }
        });
    }

}
