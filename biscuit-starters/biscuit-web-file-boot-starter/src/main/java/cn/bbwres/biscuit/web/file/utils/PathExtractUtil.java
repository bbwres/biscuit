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

package cn.bbwres.biscuit.web.file.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 提取文件路径
 *
 * @author zhanglinfeng
 */
public class PathExtractUtil {

    /**
     * 提取文件路径（不含文件名），跨平台兼容
     *
     * @param fullPath 完整文件路径
     * @return 目录路径（不含文件名），无目录返回 "."
     */
    public static String getParentPath(String fullPath) {
        if (fullPath == null || fullPath.trim().isEmpty()) {
            return ".";
        }
        // 优先使用 NIO Path（Java 7+）
        try {
            Path path = Paths.get(fullPath.trim());
            Path parent = path.getParent();
            return parent != null ? parent.toString() : ".";
        } catch (Exception e) {
            // 降级为 File 方法
            File file = new File(fullPath.trim());
            String parent = file.getParent();
            return parent != null ? parent : ".";
        }
    }


}
