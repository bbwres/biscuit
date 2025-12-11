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

package cn.bbwres.biscuit.web.file.service;

import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件操作服务接口
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public interface FileOperation {

    /**
     * 上传文件
     *
     * @param file     文件内容
     * @param fileInfo 文件信息
     * @return 文件的唯一路径
     */
    String uploadFile(MultipartFile file, TempFileInfo fileInfo);


    /**
     * 根据文件信息获取文件流
     *
     * @param fileInfo a {@link cn.bbwres.biscuit.web.file.entity.FileInfo} object
     * @return a {@link java.io.InputStream} object
     */
    InputStream downloadFile(FileInfo fileInfo);


    /**
     * 复制文件
     *
     * @param srcfileInfo 原文件信息
     * @return a {@link java.lang.String} object
     */
    String copyFile(FileInfo srcfileInfo);

    /**
     * 根据 fileInfo信息删除文件实体信息
     *
     * @param fileInfo a {@link cn.bbwres.biscuit.web.file.entity.FileInfo} object
     */
    void deleteFile(FileInfo fileInfo);


    /**
     * 当前的存储类型
     *
     * @return a {@link java.lang.String} object
     */
    String storageType();
}
