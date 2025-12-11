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
import cn.bbwres.biscuit.web.file.config.FileProperties;
import cn.bbwres.biscuit.web.file.config.FileStorageProperties;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import cn.bbwres.biscuit.web.file.utils.PathExtractUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * s3存储的文件操作实现类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class S3StorageFileOperation implements FileOperation {

    /**
     * Constant <code>STORAGE_TYPE="s3"</code>
     */
    public static final String STORAGE_TYPE = "s3";

    private final FileStorageProperties fileStorageProperties;

    private final S3Client s3Client;

    private final static String FILE_FULL_PATH_FORMATTED_SPLIT = ":";

    /**
     * 是否启用路径样式访问
     */
    private static final String SERVICE_CONFIGURATION_PATH_STYLE_ACCESS_ENABLED = "service.configuration.path.style.access.enabled";

    /**
     * 是否启用分块编码（Chunked Encoding），默认 true，优化大文件上传
     */
    private static final String SERVICE_CONFIGURATION_CHUNKED_ENCODING_ENABLED = "service.configuration.chunked.encoding.enabled";


    /**
     * <p>Constructor for S3StorageFileOperation.</p>
     *
     * @param fileProperties a {@link cn.bbwres.biscuit.web.file.config.FileProperties} object
     */
    public S3StorageFileOperation(FileProperties fileProperties) {
        this.fileStorageProperties = fileProperties.getStorageConfig().get(STORAGE_TYPE);
        //初始化s3client
        // 2. 构建S3客户端
        S3ClientBuilder builder = S3Client.builder();
        //默认的区域
        builder.region(Region.AWS_GLOBAL);
        if (StringUtils.isNotBlank(fileStorageProperties.getRegion())) {
            builder.region(Region.of(fileStorageProperties.getRegion()));
        }
        if (StringUtils.isNotBlank(fileStorageProperties.getEndPoint())) {
            builder.endpointOverride(URI.create(fileStorageProperties.getEndPoint()));
        }
        if (StringUtils.isNotBlank(fileStorageProperties.getAk())) {
            // 配置凭证
            AwsBasicCredentials credentials = AwsBasicCredentials.create(fileStorageProperties.getAk(), fileStorageProperties.getSk());
            builder.credentialsProvider(StaticCredentialsProvider.create(credentials));
        }

        Map<String, String> customConfig = fileStorageProperties.getCustomConfig();
        if (customConfig != null) {
            S3Configuration.Builder configurationBuilder = S3Configuration.builder();
            if (customConfig.containsKey(SERVICE_CONFIGURATION_PATH_STYLE_ACCESS_ENABLED)) {
                configurationBuilder.pathStyleAccessEnabled(Boolean.parseBoolean(customConfig.get(SERVICE_CONFIGURATION_PATH_STYLE_ACCESS_ENABLED)));
            }
            if (customConfig.containsKey(SERVICE_CONFIGURATION_CHUNKED_ENCODING_ENABLED)) {
                configurationBuilder.chunkedEncodingEnabled(Boolean.parseBoolean(customConfig.get(SERVICE_CONFIGURATION_CHUNKED_ENCODING_ENABLED)));
            }
            builder.serviceConfiguration(configurationBuilder.build());
        }
        s3Client = builder.build();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 上传文件
     * 返回的路径为 桶:/xxx/xx.jpg
     */
    @Override
    public String uploadFile(MultipartFile file, TempFileInfo fileInfo) {
        // 生成文件存储路径（按日期分目录，避免单目录文件过多）
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativeDir = dateDir + "/";
        String absoluteDir = (StringUtils.isBlank(fileInfo.getFileStorageMenu()) ? "" : fileInfo.getFileStorageMenu()) + relativeDir;
        // 4. 生成唯一文件名（UUID + 原始文件后缀，避免冲突）
        String fileName = UUID.randomUUID() + "." + fileInfo.getFileSuffix();
        String fileFullPath = absoluteDir + fileName;
        String bucket = fileStorageProperties.getStoragePath();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileFullPath)
                    .build();
            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (Exception e) {
            log.warn("文件保存在存储端失败!文件路径:[{}]", fileFullPath, e);
            throw new SystemRuntimeException("文件保存在存储端失败：" + e.getMessage());
        }
        return bucket + FILE_FULL_PATH_FORMATTED_SPLIT + fileFullPath;
    }


    /**
     * {@inheritDoc}
     * <p>
     * 根据文件信息获取文件流
     */
    @Override
    public InputStream downloadFile(FileInfo fileInfo) {
        BucketAndFileKey bucketAndFileKey = getBucketAndFileKey(fileInfo.getFilePath());
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketAndFileKey.bucket())
                .key(bucketAndFileKey.fileFullPath())
                .build());
    }


    /**
     * {@inheritDoc}
     * <p>
     * 复制文件
     */
    @Override
    public String copyFile(FileInfo srcfileInfo) {
        BucketAndFileKey bucketAndFileKey = getBucketAndFileKey(srcfileInfo.getFilePath());
        String absoluteDir = PathExtractUtil.getParentPath(bucketAndFileKey.fileFullPath()) + "/";
        ;
        String fileName = UUID.randomUUID() + "." + srcfileInfo.getFileSuffix();
        String fileFullPath = absoluteDir + fileName;
        s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(bucketAndFileKey.bucket())
                .sourceKey(bucketAndFileKey.fileFullPath())
                .destinationBucket(bucketAndFileKey.bucket())
                .destinationKey(fileFullPath).build()
        );

        return bucketAndFileKey.bucket() + FILE_FULL_PATH_FORMATTED_SPLIT + fileFullPath;
    }


    /**
     * {@inheritDoc}
     * <p>
     * 根据 fileInfo信息删除文件实体信息
     */
    @Override
    public void deleteFile(FileInfo fileInfo) {
        BucketAndFileKey bucketAndFileKey = getBucketAndFileKey(fileInfo.getFilePath());

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketAndFileKey.bucket())
                .key(bucketAndFileKey.fileFullPath())
                .build());

    }

    /**
     * {@inheritDoc}
     * <p>
     * 当前的存储类型
     */
    @Override
    public String storageType() {
        return STORAGE_TYPE;
    }


    /**
     * 从文件路径中获取桶和文件key
     *
     * @param fileFullPath 文件路径
     * @return
     */
    private BucketAndFileKey getBucketAndFileKey(String fileFullPath) {
        String bucket = fileStorageProperties.getStoragePath();
        if (fileFullPath.contains(FILE_FULL_PATH_FORMATTED_SPLIT)) {
            String[] split = fileFullPath.split(FILE_FULL_PATH_FORMATTED_SPLIT);
            bucket = split[0];
            fileFullPath = split[1];
        }
        return new BucketAndFileKey(fileFullPath, bucket);
    }


    private record BucketAndFileKey(String fileFullPath, String bucket) {
    }

}


