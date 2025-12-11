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
 * @version $Id: $Id
 */
@Slf4j
public class CustomFileOperation implements FileOperation {

    private static final String CUSTOM = "CUSTOM";

    private final Map<String, FileOperation> fileOperationMap = new HashMap<>(16);

    /**
     * <p>Constructor for CustomFileOperation.</p>
     *
     * @param fileOperations a {@link java.util.List} object
     */
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
     * {@inheritDoc}
     *
     * 上传文件
     */
    @Override
    public String uploadFile(MultipartFile file, TempFileInfo fileInfo) {
        return getFileOperationByStorageType(fileInfo.getFileStorageType()).uploadFile(file, fileInfo);
    }

    /**
     * {@inheritDoc}
     *
     * 根据文件信息获取文件流
     */
    @Override
    public InputStream downloadFile(FileInfo fileInfo) {
        return getFileOperationByStorageType(fileInfo.getFileStorageType()).downloadFile(fileInfo);
    }

    /**
     * {@inheritDoc}
     *
     * 复制文件
     */
    @Override
    public String copyFile(FileInfo srcfileInfo) {
        return getFileOperationByStorageType(srcfileInfo.getFileStorageType()).copyFile(srcfileInfo);
    }

    /**
     * {@inheritDoc}
     *
     * 根据 fileInfo信息删除文件实体信息
     */
    @Override
    public void deleteFile(FileInfo fileInfo) {
        getFileOperationByStorageType(fileInfo.getFileStorageType()).deleteFile(fileInfo);
    }

    /**
     * {@inheritDoc}
     *
     * 当前的存储类型
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
