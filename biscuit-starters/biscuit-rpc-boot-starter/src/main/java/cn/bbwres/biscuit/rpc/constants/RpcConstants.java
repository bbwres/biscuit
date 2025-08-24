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

package cn.bbwres.biscuit.rpc.constants;

/**
 * rpc 调用的常量
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class RpcConstants {


    /**
     * 服务端支持的安全算法
     */
    public static final String SERVICE_SECURITY_ALGORITHM = "security.algorithm";

    /**
     * 客户端密钥
     */
    public static final String CLIENT_PASSWORD = "client.security.key";

    /**
     * 认证信息传递的请求头名称
     */
    public static final String AUTHORIZATION_HEADER_NAME = "x-client-authorization";

    /**
     * 请求时间戳
     */
    public static final String CLIENT_TIME_HEADER_NAME = "x-client-time";
    /**
     * 客户端系统名称
     */
    public static final String CLIENT_SYSTEM_NAME_HEADER_NAME = "x-client-system-name";


}
