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

package cn.bbwres.biscuit.rpc.security;

import cn.bbwres.biscuit.rpc.constants.RpcConstants;
import cn.bbwres.biscuit.rpc.properties.RpcSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * sha1算法支持
 *
 * @author zhanglinfeng
 */
@Slf4j
public class RpcSecurityAlgorithmSha1Support extends AbstractRpcSecurityAlgorithmHashSupport {


    public RpcSecurityAlgorithmSha1Support(RpcSecurityProperties rpcSecurityProperties, Environment environment) {
        super(rpcSecurityProperties, environment);
    }

    /**
     * 增加metadata数据
     *
     * @param metadata
     */
    @Override
    protected void addMetadata(Map<String, String> metadata) {
        metadata.put(RpcConstants.CLIENT_PASSWORD, UUID.randomUUID().toString());
    }

    /**
     * hash 数据生成
     *
     * @param waitSignStr 待hash字符串
     * @return
     */
    @Override
    protected String hash(String waitSignStr) {
        return DigestUtils.sha1Hex(waitSignStr.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 匹配的安全算法
     *
     * @return
     */
    @Override
    public String securityAlgorithm() {
        return "SHA1";
    }
}
