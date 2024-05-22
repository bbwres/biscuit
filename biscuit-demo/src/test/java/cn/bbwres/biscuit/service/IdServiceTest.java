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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
class IdServiceTest {

    @Autowired
    private RedisGenerator redisGenerator;

    @Autowired
    private IdService idService;


    @Test
    public void testIds() {
        List<String> strings = redisGenerator.nextIds("test-18-", null, 18, 10000L);
        System.out.println(strings);
    }

    @Test
    public void testGetId() {
        String key = "test_app_id";
        String id = idService.getId(key);
        System.out.println(id);

    }

    @Test
    public void testGetId2() {
        String key = "test_app_id1";
        List<CompletableFuture> futures = new ArrayList<>(16);
        futures.add(CompletableFuture.runAsync(() -> {
            String id = idService.getId2(key);
            System.out.println(id);
        }));


        futures.add(CompletableFuture.runAsync(() -> {
            String id = idService.getId2(key);
            System.out.println(id);
        }));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
    }

}