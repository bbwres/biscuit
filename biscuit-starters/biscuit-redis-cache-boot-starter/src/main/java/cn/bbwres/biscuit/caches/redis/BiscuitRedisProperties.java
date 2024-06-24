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

package cn.bbwres.biscuit.caches.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis 配置类
 *
 * @author zhanglinfeng
 */
@ConfigurationProperties(prefix = "biscuit.redis")
public class BiscuitRedisProperties {

    /**
     * 默认获取缓存时间的分隔符
     */
    private String delimitSymbol = "#";

    /**
     * 是否启用json序列化value
     */
    private boolean enableJsonSerializerValue = true;

    public String getDelimitSymbol() {
        return delimitSymbol;
    }

    public void setDelimitSymbol(String delimitSymbol) {
        this.delimitSymbol = delimitSymbol;
    }

    public boolean isEnableJsonSerializerValue() {
        return enableJsonSerializerValue;
    }

    public void setEnableJsonSerializerValue(boolean enableJsonSerializerValue) {
        this.enableJsonSerializerValue = enableJsonSerializerValue;
    }
}
