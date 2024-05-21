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


import cn.bbwres.biscuit.id.IdGeneratorProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * id 生成器
 *
 * @author zhanglinfeng
 */
@Slf4j
public class SnowflakeGenerator {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1608168293000L;

    /**
     * 每一部分占用的位数
     */
    /**
     * 序列号占用的位数
     */
    private long sequenceBit = 14;
    /**
     * 机器标识占用的位数
     */
    private long machineBit = 5;
    /**
     * 数据中心占用的位数
     */
    private long datacenterBit = 2;

    /**
     * 当前时间 小于上次时间（润秒）等待时间
     */
    private final long timeOffset = 5;

    /**
     * 最大的序列
     */
    private final long maxSequence = ~(-1L << sequenceBit);

    /**
     * 每一部分向左的位移
     */
    private final long machineLeft = sequenceBit;
    private final long datacenterLeft = sequenceBit + machineBit;
    private final long timestmpLeft = datacenterLeft + datacenterBit;

    /**
     * 数据中心
     */
    private final long datacenterId;
    /**
     * //机器标识
     */
    private long machineId = 0;
    /**
     * //序列号
     */
    private long sequence = 0L;
    /**
     * //上一次时间戳
     */
    private long lastStmp = -1L;

    public SnowflakeGenerator(IdGeneratorProperties properties) {
        this.sequenceBit = properties.getSequenceBit();
        this.machineBit = properties.getMachineBit();
        this.datacenterBit = properties.getDatacenterBit();
        this.datacenterId = properties.getDatacenterId();
        long maxDatacenterNum = ~(-1L << this.datacenterBit);
        if (this.datacenterId > maxDatacenterNum || this.datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
    }

    /**
     * 配置机器id
     *
     * @param machineId
     * @return
     */
    public SnowflakeGenerator buildMachineId(long machineId) {
        this.machineId = machineId;
        long maxMachineNum = ~(-1L << machineBit);
        if (machineId > maxMachineNum || machineId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        return this;
    }


    /**
     * 返回包含前缀的序列
     *
     * @param prefix
     * @return
     */
    public String nextId(String prefix) {
        return prefix + nextId();
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        //闰秒
        if (currStmp < lastStmp) {
            long offset = lastStmp - currStmp;
            if (offset <= timeOffset) {
                try {
                    wait(offset << 1);
                    currStmp = getNewstmp();
                    if (currStmp < lastStmp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & maxSequence;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = ThreadLocalRandom.current().nextLong(1, 6);
        }

        lastStmp = currStmp;
        //时间戳部分｜数据中心部分｜机器标识部分｜序列号部分
        return (currStmp - START_STMP) << timestmpLeft
                | datacenterId << datacenterLeft
                | machineId << machineLeft
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }


}
