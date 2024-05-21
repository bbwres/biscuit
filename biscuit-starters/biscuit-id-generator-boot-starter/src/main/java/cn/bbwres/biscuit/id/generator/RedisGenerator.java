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

package cn.bbwres.biscuit.id.generator;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 基于redis的有序的id生成
 *
 * @author zhanglinfeng
 */
@Slf4j
@RequiredArgsConstructor
public class RedisGenerator {

    private static final String REDIS_ID_KEY = "REDIS_ID_KEY:";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 根据前缀获取id
     *
     * @param prefix
     * @return
     */
    public String nextId(String prefix) {
        Long num = redisTemplate.opsForValue().increment(REDIS_ID_KEY + prefix);
        return prefix + num;
    }


    /**
     * @param key          key
     * @param prefix       前缀
     * @param noPrefixSize 不包含前缀的长度
     * @param num          数量
     * @return
     */
    public List<String> nextIds(String key, String prefix, Integer noPrefixSize, Long num) {
        Long maxId = redisTemplate.opsForValue().increment(REDIS_ID_KEY + key, num);
        if (Objects.isNull(maxId)) {
            log.warn("从redis中获取id失败！");
            throw new SystemRuntimeException("id_fail");
        }
        long minId = maxId - num;
        List<String> ids = new ArrayList<>(16);
        for (long i = minId + 1; i <= maxId; i++) {
            String idNum = Long.toString(i);
            int size = noPrefixSize == null ? 10 : noPrefixSize;
            if (idNum.length() < size) {
                idNum = new DecimalFormat("0".repeat(size)).format(num);
            }

            if (!ObjectUtils.isEmpty(prefix)) {
                ids.add(prefix + idNum);
            } else {
                ids.add(idNum);
            }

        }
        return ids;
    }

    /**
     * 获取id
     *
     * @param key          key
     * @param prefix       前缀
     * @param noPrefixSize 不包含前缀的长度
     * @param stemNum      步进
     * @return
     */
    public String nextId(String key, String prefix, Integer noPrefixSize, Long stemNum) {
        Long num = redisTemplate.opsForValue().increment(REDIS_ID_KEY + key, stemNum == null ? 1L : stemNum);
        String strNum = num + "";
        int size = noPrefixSize == null ? 10 : noPrefixSize;
        if (strNum.length() > size) {
            return prefix + num;
        }
        return prefix + new DecimalFormat("0".repeat(size)).format(num);
    }
}
