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

package cn.bbwres.biscuit.id;

import cn.bbwres.biscuit.id.generator.RedisGenerator;
import cn.bbwres.biscuit.id.generator.SnowflakeGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * id 生成序列
 *
 * @author zhanglinfeng
 */
@Configuration
@EnableConfigurationProperties(IdGeneratorProperties.class)
public class IdGeneratorAutoConfigure {

    /**
     * 机器序列的key
     */
    private static final String MACHINE_ID_KEY = "MACHINE_ID_KEY";

    /**
     * 初始化id 生成器
     *
     * @param redisTemplate
     * @param idGeneratorProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SnowflakeGenerator snowflakeGenerator(RedisTemplate<String, String> redisTemplate, IdGeneratorProperties idGeneratorProperties) {
        SnowflakeGenerator idGenerator = new SnowflakeGenerator(idGeneratorProperties);
        //最大的机器数
        long maxMachineNum = ~(-1L << idGeneratorProperties.getMachineBit());
        Long machineId = idGeneratorProperties.getMachineId();
        if (machineId == -1) {
            machineId = redisTemplate.opsForValue().increment(MACHINE_ID_KEY);
            if (machineId == null || machineId > maxMachineNum) {
                machineId = 0L;
                redisTemplate.opsForValue().set(MACHINE_ID_KEY, machineId + "");
            }
        }

        return idGenerator.buildMachineId(machineId);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisGenerator redisGenerator(RedisTemplate<String, String> redisTemplate) {
        return new RedisGenerator(redisTemplate);
    }

}
