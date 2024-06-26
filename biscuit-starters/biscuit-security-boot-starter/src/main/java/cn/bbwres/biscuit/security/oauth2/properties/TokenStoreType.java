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

package cn.bbwres.biscuit.security.oauth2.properties;

/**
 * TokenStore的类型
 *
 * @author zhanglinfeng
 */

public enum TokenStoreType {

    /**
     * 内存存储token
     */
    IN_MEMORY,
    /**
     * 数据库存储token信息
     */
    JDBC,
    /**
     * jwk 存储token信息
     */
    JWK,
    /**
     * jwt存储token信息
     */
    JWT,
    /**
     * redis 存储token信息
     */
    REDIS
}
