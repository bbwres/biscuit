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

package cn.bbwres.biscuit.web.file;

import cn.bbwres.biscuit.web.file.api.DefaultFileBusinessOperation;
import cn.bbwres.biscuit.web.file.config.FileProperties;
import cn.bbwres.biscuit.web.file.endpoint.FileDownloadEndpoint;
import cn.bbwres.biscuit.web.file.endpoint.FileUploadEndpoint;
import cn.bbwres.biscuit.web.file.service.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * web自动配置
 *
 * @author zhanglinfeng
 */
@AutoConfiguration
@EnableConfigurationProperties(FileProperties.class)
public class FileAutoConfiguration {


    /**
     * 自定义的文件操作类
     *
     * @param fileOperations fileOperations
     * @return CustomFileOperation
     */
    @Bean
    @ConditionalOnMissingBean
    public CustomFileOperation customFileOperation(List<FileOperation> fileOperations) {
        return new CustomFileOperation(fileOperations);
    }


    /**
     * 默认的文件业务操作类
     *
     * @return DefaultFileBusinessOperation
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultFileBusinessOperation defaultFileBusinessOperation(FileInfoOperation fileInfoOperation,
                                                                     CustomFileOperation customFileOperation,
                                                                     FileProperties fileProperties) {
        return new DefaultFileBusinessOperation(fileInfoOperation, customFileOperation, fileProperties);
    }


    /**
     * 文件上传端点
     *
     * @param customFileOperation customFileOperation
     * @param fileProperties      fileProperties
     * @param fileInfoOperation   fileInfoOperation
     * @return FileUploadEndpoint
     */
    @Bean
    @ConditionalOnMissingBean
    public FileUploadEndpoint fileUploadEndpoint(CustomFileOperation customFileOperation,
                                                 FileProperties fileProperties,
                                                 FileInfoOperation fileInfoOperation) {
        return new FileUploadEndpoint(customFileOperation, fileProperties, fileInfoOperation);
    }


    /**
     * 文件下载端点
     *
     * @param customFileOperation customFileOperation
     * @param fileProperties      fileProperties
     * @param fileInfoOperation   fileInfoOperation
     * @return FileDownloadEndpoint
     */
    @Bean
    @ConditionalOnMissingBean
    public FileDownloadEndpoint fileDownloadEndpoint(CustomFileOperation customFileOperation,
                                                     FileProperties fileProperties,
                                                     FileInfoOperation fileInfoOperation) {
        return new FileDownloadEndpoint(customFileOperation, fileProperties, fileInfoOperation);
    }


    /**
     * 本地文件存储操作
     *
     * @param fileProperties fileProperties
     * @return LocalStorageFileOperation
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBooleanProperty(prefix = "file.storage-config", name = LocalStorageFileOperation.STORAGE_TYPE + ".enabled")
    public LocalStorageFileOperation localStorageFileOperation(FileProperties fileProperties) {
        return new LocalStorageFileOperation(fileProperties);
    }


    /**
     * s3文件存储操作
     *
     * @param fileProperties fileProperties
     * @return S3StorageFileOperation
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBooleanProperty(prefix = "file.storage-config", name = S3StorageFileOperation.STORAGE_TYPE + ".enabled")
    public S3StorageFileOperation s3StorageFileOperation(FileProperties fileProperties) {
        return new S3StorageFileOperation(fileProperties);
    }

    

}
