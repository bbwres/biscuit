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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * id 生成配置文件
 * @author zhanglinfeng
 */
@ConfigurationProperties("id.generator")
@Data
public class IdGeneratorProperties {

    /**
     * 机器标识占用的位数
     * 5位 最大32个
     * 默认最大 32台机器
     */
    private  long machineBit = 5;
    /**
     * 数据中心占用的位数
     * 2位 最大3个
     * 默认 3个数据中心
     */
    private  long datacenterBit = 2;

    /**
     * //序列号占用的位数
     * 默认占用14位
     * 每毫秒可以产生 16384 个id
     */
    private  long sequenceBit = 14;

    /**
     * 数据中心的编码
     */
    private long datacenterId = 0;

    /**
     * 机器id编码
     */
    private long machineId = -1L;


}
