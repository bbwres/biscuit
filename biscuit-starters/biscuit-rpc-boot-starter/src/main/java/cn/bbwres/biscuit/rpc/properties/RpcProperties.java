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

package cn.bbwres.biscuit.rpc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * rpc 参数配置类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
@ConfigurationProperties("biscuit.rpc")
public class RpcProperties {

    /**
     * 是否透传用户信息
     */
    private boolean transmitUserInfo = true;

    /**
     * 透传的用户请求头名称
     */
    private String userInfoHeaderName = "x-user-info";

    /**
     * rpc相关的api的前缀， 配置之后，rpcApi的接口默认需要增加此前缀才能访问
     */
    private String rpcApiPrefix = "/rpc-api";

    /**
     * rpc相关的api所在包的 Ant 路径规则
     */
    private String rpcApiPackage = "**.module.*.api.**";


}
