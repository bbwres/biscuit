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

package cn.bbwres.biscuit.nacos;

import cn.bbwres.biscuit.nacos.operation.NacosConfigOperation;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * nacos 自动注入配置
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
public class NacosAutoConfigure {


    /**
     * nacos 配置操作
     *
     * @param nacosConfigService
     * @param environment
     * @return
     */
    @Bean
    public NacosConfigOperation nacosConfigOperation(ConfigService nacosConfigService, Environment environment) {
        return new NacosConfigOperation(nacosConfigService, environment);
    }

}
