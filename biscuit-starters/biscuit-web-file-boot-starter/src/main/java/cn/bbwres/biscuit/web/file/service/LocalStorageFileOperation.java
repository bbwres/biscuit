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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地存储的文件操作实现类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class LocalStorageFileOperation implements FileOperation {

    /** Constant <code>STORAGE_TYPE="local"</code> */
    public static final String STORAGE_TYPE = "local";

    private final FileStorageProperties fileStorageProperties;

    /**
     * <p>Constructor for LocalStorageFileOperation.</p>
     *
     * @param fileProperties a {@link cn.bbwres.biscuit.web.file.config.FileProperties} object
     */
    public LocalStorageFileOperation(FileProperties fileProperties) {
        this.fileStorageProperties = fileProperties.getStorageConfig().get(STORAGE_TYPE);
        if (!StringUtils.isBlank(fileStorageProperties.getStoragePath())) {
            //初始化根目录
            createDir(fileStorageProperties.getStoragePath());
        }
    }

    /**
     * {@inheritDoc}
     *
     * 上传文件
     */
    @Override
    public String uploadFile(MultipartFile file, TempFileInfo fileInfo) {
        if (!StringUtils.isEmpty(fileInfo.getFileStorageMenu())) {
            createDir(fileInfo.getFileStorageMenu());
        }
        // 2. 生成文件存储路径（按日期分目录，避免单目录文件过多）
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativeDir = dateDir + "/";
        String absoluteDir = fileStorageProperties.getStoragePath() + fileInfo.getFileStorageMenu() + relativeDir;
        // 3. 创建日期子目录
        createDir(absoluteDir);
        // 4. 生成唯一文件名（UUID + 原始文件后缀，避免冲突）
        String fileName = UUID.randomUUID() + "." + fileInfo.getFileSuffix();
        String fileFullPath = absoluteDir + fileName;
        // 5. 保存文件到本地
        File targetFile = new File(fileFullPath);
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            log.warn("文件保存在本地失败!文件路径:[{}]", fileFullPath, e);
            throw new SystemRuntimeException("文件保存在存储端失败：" + e.getMessage());
        }
        return fileFullPath;
    }


    /**
     * {@inheritDoc}
     *
     * 根据文件信息获取文件流
     */
    @Override
    public InputStream downloadFile(FileInfo fileInfo) {
        File file = new File(fileInfo.getFilePath());
        if (!file.exists() || !file.isFile()) {
            throw new SystemRuntimeException("获取文件失败！文件不存在：" + fileInfo.getFilePath());
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new SystemRuntimeException("获取文件失败！文件不存在：" + fileInfo.getFilePath());
        }
    }

    /**
     * {@inheritDoc}
     *
     * 复制文件
     */
    @Override
    public String copyFile(FileInfo srcfileInfo) {
        File file = new File(srcfileInfo.getFilePath());
        if (!file.exists() || !file.isFile()) {
            throw new SystemRuntimeException("获取文件失败！文件不存在：" + srcfileInfo.getFilePath());
        }
        String absoluteDir = file.getParent();
        String fileName = UUID.randomUUID() + "." + srcfileInfo.getFileSuffix();
        String fileFullPath = absoluteDir + fileName;
        try {
            FileUtils.copyFile(file, new File(fileFullPath));
        } catch (Exception e) {
            log.warn("文件保存在本地失败!文件路径:[{}]", fileFullPath, e);
            throw new SystemRuntimeException("文件保存在本地失败：" + fileFullPath);
        }
        return fileFullPath;
    }


    /**
     * {@inheritDoc}
     *
     * 根据 fileInfo信息删除文件实体信息
     */
    @Override
    public void deleteFile(FileInfo fileInfo) {
        File file = new File(fileInfo.getFilePath());
        if (!file.exists() || !file.isFile()) {
            return;
        }
        file.delete();
    }

    /**
     * {@inheritDoc}
     *
     * 当前的存储类型
     */
    @Override
    public String storageType() {
        return STORAGE_TYPE;
    }


    /**
     * 创建文件目录
     *
     * @param absoluteDir
     */
    private static void createDir(String absoluteDir) {
        File dir = new File(absoluteDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new SystemRuntimeException("创建本地存储目录失败：" + absoluteDir);
            }
        }
    }
}
