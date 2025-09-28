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

package cn.bbwres.guide.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户基本信息表
 *
 * @author zhanglinfeng
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserInfoController {


    /**
     * 获取用户信息s
     *
     * @param id
     * @return
     */
    @GetMapping("/getUserInfo/{id}")
    public ResponseEntity<Map<String, String>> getUserInfo(@PathVariable("id") String id) {
        Map<String, String> resultMap = new HashMap<>(16);
        resultMap.put("id", id);
        resultMap.put("userName", "张三");
        return ResponseEntity.ok(resultMap);

    }
}
