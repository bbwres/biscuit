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

package cn.bbwres.biscuit.web.file.config;

import lombok.Data;

import java.util.Map;

/**
 * 文件存储配置信息
 *
 * @author zhanglinfeng
 */
@Data
public class FileStorageProperties {


    /**
     * 是否启用
     */
    private Boolean enabled = false;

    /**
     * 存储根目录(桶名称)
     */
    private String storagePath;

    /**
     * 存储访问的ak
     */
    private String ak;

    /**
     * 存储访问的sk
     */
    private String sk;

    /**
     * 存储访问的endPoint
     */
    private String endPoint;

    /**
     * 区域
     */
    private String region;


    /**
     * 自定义扩展配置
     */
    private Map<String, String> customConfig;

}
