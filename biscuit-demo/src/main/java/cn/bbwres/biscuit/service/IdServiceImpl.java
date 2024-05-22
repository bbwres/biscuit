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

package cn.bbwres.biscuit.service;

import cn.bbwres.biscuit.id.generator.RedisGenerator;
import cn.bbwres.biscuit.redis.lock.annotations.DistributedLock;
import cn.bbwres.biscuit.redis.lock.annotations.DistributedLockParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 获取id数据
 *
 * @author zhanglinfeng
 */
@Slf4j
@Service
public class IdServiceImpl implements IdService {

    private RedisGenerator redisGenerator;

    @Autowired
    public void setRedisGenerator(RedisGenerator redisGenerator) {
        this.redisGenerator = redisGenerator;
    }

    /**
     * 获取id数据
     *
     * @return 测试加锁获取id
     */
    @Override
    @DistributedLock(key = "#key")
    public String getId( String key) {
        return redisGenerator.nextId(key);
    }

    /**
     * 获取id数据
     *
     * @return 测试加锁获取id
     */
    @Override
    @DistributedLock(key = "#dd",timeout = 0L)
    public String getId2(@DistributedLockParam("dd") String key) {
        log.info("开始执行获取id 操作");
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return redisGenerator.nextId(key);
    }


}
