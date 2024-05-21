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

package cn.bbwres.biscuit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BootstrapProfile 设置
 *
 * @author zhanglinfeng
 */
public class BootstrapProfile {

    private final static Logger log = LoggerFactory.getLogger(BootstrapProfile.class);

    private final static String PROFILE_NAME = "spring.profiles.active";

    /**
     * 设置启动的profile 文件
     */
    public static void setBootstrapProfile() {
        String profile = System.getProperty(PROFILE_NAME);
        log.info("当前启动参数中选择的环境文件为:[{}]", profile == null ? "默认开发" : profile);
        if (profile != null) {
            System.setProperty("spring.cloud.bootstrap.name", "bootstrap-" + profile);
        }
    }
}
