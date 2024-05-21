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

package cn.bbwres.biscuit.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import java.util.List;
import java.util.Objects;

/**
 * 认证工具类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class AuthUtils {


    /**
     * 鉴权检查
     *
     * @param noAuthResource a {@link java.util.List} object
     * @param path a {@link java.lang.String} object
     * @param pathMatcher a {@link org.springframework.util.PathMatcher} object
     * @return a boolean
     */
    public static boolean checkAuth(List<String> noAuthResource, String path, PathMatcher pathMatcher) {
        //获取出认证请求地址
        if (!CollectionUtils.isEmpty(noAuthResource)) {
            return noAuthResource.stream()
                    .filter(Objects::nonNull)
                    //根据前缀先过滤一遍
                    .anyMatch(resource -> {
                        boolean match = pathMatcher.match(resource, path);
                        if (match) {
                            log.info("当前请求路径:[{}]与地址:[{}]匹配成功", path, resource);
                        }
                        return match;
                    });
        }
        return false;
    }


}
