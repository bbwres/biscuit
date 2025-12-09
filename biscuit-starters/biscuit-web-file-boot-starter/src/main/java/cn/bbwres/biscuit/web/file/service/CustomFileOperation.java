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

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义的文件操作类
 *
 * @author zhanglinfeng
 */
@Slf4j
public class CustomFileOperation implements FileOperation {

    private static final String CUSTOM = "CUSTOM";

    private final Map<String, FileOperation> fileOperationMap = new HashMap<>(16);

    public CustomFileOperation(List<FileOperation> fileOperations) {
        if (!CollectionUtils.isEmpty(fileOperations)) {
            for (FileOperation fileOperation : fileOperations) {
                if (!CUSTOM.equals(fileOperation.storageType())) {
                    fileOperationMap.put(fileOperation.storageType(), fileOperation);
                }
            }
        }
    }


    /**
     * 上传文件
     *
     * @param file     文件内容
     * @param fileInfo 文件信息
     * @return 文件的唯一路径
     */
    @Override
    public String uploadFile(MultipartFile file, TempFileInfo fileInfo) {
        return getFileOperationByStorageType(fileInfo.getFileStorageType()).uploadFile(file, fileInfo);
    }

    /**
     * 根据文件信息获取文件流
     *
     * @param fileInfo
     * @return
     */
    @Override
    public InputStream downloadFile(FileInfo fileInfo) {
        return getFileOperationByStorageType(fileInfo.getFileStorageType()).downloadFile(fileInfo);
    }

    /**
     * 复制文件
     *
     * @param srcfileInfo 原文件信息
     * @return
     */
    @Override
    public String copyFile(FileInfo srcfileInfo) {
        return getFileOperationByStorageType(srcfileInfo.getFileStorageType()).copyFile(srcfileInfo);
    }

    /**
     * 根据 fileInfo信息删除文件实体信息
     *
     * @param fileInfo
     */
    @Override
    public void deleteFile(FileInfo fileInfo) {
        getFileOperationByStorageType(fileInfo.getFileStorageType()).deleteFile(fileInfo);
    }

    /**
     * 当前的存储类型
     *
     * @return
     */
    @Override
    public String storageType() {
        return "CUSTOM";
    }


    /**
     * 获取文件操作类
     *
     * @param storageType
     * @return
     */
    private FileOperation getFileOperationByStorageType(String storageType) {
        FileOperation fileOperation = fileOperationMap.get(storageType);
        if (fileOperation == null) {
            log.warn("当前没有支持的文件存储类型:[{}]", storageType);
            throw new SystemRuntimeException(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        return fileOperation;
    }
}
