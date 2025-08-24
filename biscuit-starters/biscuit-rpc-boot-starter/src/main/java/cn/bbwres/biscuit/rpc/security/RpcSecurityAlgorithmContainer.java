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

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 算法选择容器
 *
 * @author zhanglinfeng
 */
public class RpcSecurityAlgorithmContainer {

    private final Map<String, RpcSecurityAlgorithmSupport> rpcSecurityAlgorithmSupportMap;


    public RpcSecurityAlgorithmContainer(List<RpcSecurityAlgorithmSupport> rpcSecurityAlgorithmSupports) {
        rpcSecurityAlgorithmSupportMap = new HashMap<>(8);
        if (CollectionUtils.isEmpty(rpcSecurityAlgorithmSupports)) {
            return;
        }
        for (RpcSecurityAlgorithmSupport securityAlgorithmSupport : rpcSecurityAlgorithmSupports) {
            rpcSecurityAlgorithmSupportMap.put(securityAlgorithmSupport.securityAlgorithm(), securityAlgorithmSupport);
        }
    }

    /**
     * 获取安全算法实现
     *
     * @param securityAlgorithm 算法名称
     * @return
     */
    public RpcSecurityAlgorithmSupport getRpcSecurityAlgorithmSupport(String securityAlgorithm, Boolean checkNull) {
        RpcSecurityAlgorithmSupport support = rpcSecurityAlgorithmSupportMap.get(securityAlgorithm);
        if (Objects.isNull(support) && checkNull) {
            throw new SystemRuntimeException("没有找到支持的算法");
        }
        return support;
    }

}
