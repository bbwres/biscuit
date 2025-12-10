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

package cn.bbwres.biscuit.web.file.api;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessRefOldParams;
import cn.bbwres.biscuit.web.file.config.FileProperties;
import cn.bbwres.biscuit.web.file.endpoint.vo.FileInfoParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import cn.bbwres.biscuit.web.file.service.CustomFileOperation;
import cn.bbwres.biscuit.web.file.service.FileInfoOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的文件业务信息操作实现类
 *
 * @author zhanglinfeng
 */
@Slf4j
public class DefaultFileBusinessOperation implements FileBusinessOperation {

    private final FileInfoOperation fileInfoOperation;
    private final CustomFileOperation customFileOperation;
    private final FileProperties fileProperties;

    public DefaultFileBusinessOperation(FileInfoOperation fileInfoOperation, CustomFileOperation customFileOperation,
                                        FileProperties fileProperties) {
        this.fileInfoOperation = fileInfoOperation;
        this.customFileOperation = customFileOperation;
        this.fileProperties = fileProperties;
    }

    /**
     * 1.文件与业务id绑定
     *
     * @param fileBindBusinessExpandParams 文件业务绑定参数
     */
    @Override
    public void fileBindBusiness(FileBindBusinessExpandParams fileBindBusinessExpandParams) {
        if (CollectionUtils.isEmpty(fileBindBusinessExpandParams.getFileIds())) {
            log.warn("文件绑定业务失败！请求参数为:[{}]传入的文件id为空!", fileBindBusinessExpandParams);
            throw new SystemRuntimeException(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        if (fileBindBusinessExpandParams.getDeleteHistory() != null && fileBindBusinessExpandParams.getDeleteHistory()) {
            List<FileInfoParams> list = fileInfoOperation.findByBusiness(fileBindBusinessExpandParams.getBusinessType(), fileBindBusinessExpandParams.getBusinessId());
            if (!CollectionUtils.isEmpty(list)) {
                try {
                    list.stream()
                            //过滤出不在要绑定的文件id列表中的文件。以最新绑定数据为准
                            .filter(fileInfoParams -> !fileBindBusinessExpandParams.getFileIds().contains(fileInfoParams.getId()))
                            .filter(fileInfoParams -> StringUtils.isNotBlank(fileInfoParams.getFilePath()))
                            .forEach(customFileOperation::deleteFile);
                } catch (Exception e) {
                    log.warn("删除文件中台文件异常！", e);
                }
            }
        }
        //获取出业务绑定的临时文件表
        List<TempFileInfo> tempFileInfos = fileInfoOperation.findTempFileInfoByIds(fileBindBusinessExpandParams.getFileIds());
        if (CollectionUtils.isEmpty(tempFileInfos)) {
            log.warn("临时表中不存在文件信息！{}", fileBindBusinessExpandParams.getFileIds());
            throw new SystemRuntimeException(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        List<FileInfo> fileInfos = tempFileInfos.stream().map(tempFileInfo -> {
            FileInfo fileInfo = new FileInfo()
                    .setId(tempFileInfo.getId())
                    .setFileName(tempFileInfo.getFileName())
                    .setFileSuffix(tempFileInfo.getFileSuffix())
                    .setFileSize(tempFileInfo.getFileSize())
                    .setFilePath(tempFileInfo.getFilePath())
                    .setFileStorageType(tempFileInfo.getFileStorageType())
                    .setFileHash(tempFileInfo.getFileHash());

            fileInfo.setBusinessId(fileBindBusinessExpandParams.getBusinessId())
                    .setBusinessType(fileBindBusinessExpandParams.getBusinessType());
            return fileInfo;
        }).collect(Collectors.toList());
        //更新业务数据
        fileInfoOperation.fileBindBusiness(fileBindBusinessExpandParams, fileInfos);
    }

    /**
     * 2. 文件绑定的业务变更（用于将原文件绑定到新业务上）
     *
     * @param fileBindBusinessRefOldParams 文件业务绑定参数
     */
    @Override
    public void fileBindBusinessChange(FileBindBusinessRefOldParams fileBindBusinessRefOldParams) {
        //查询原业务绑定的文件
        List<FileInfo> fileInfos = queryAndFilter(fileBindBusinessRefOldParams.getOldBusinessType(), fileBindBusinessRefOldParams.getOldBusinessId(), fileBindBusinessRefOldParams.getFileIds());
        if (CollectionUtils.isEmpty(fileInfos)) {
            log.info("没有匹配的文件要处理。请求参数为:[{}]", fileBindBusinessRefOldParams);
            return;
        }

        for (FileInfo fileInfo : fileInfos) {
            fileInfo.setBusinessType(fileBindBusinessRefOldParams.getBusinessType())
                    .setBusinessId(fileBindBusinessRefOldParams.getBusinessId());
        }

        fileInfoOperation.updateFileInfo(fileInfos);
    }

    /**
     * 3. 文件绑定复制（用于将原业务关联的附件 复制到新的业务上）
     *
     * @param fileBindBusinessRefOldParams 文件业务绑定参数
     */
    @Override
    public void fileBindBusinessCopy(FileBindBusinessRefOldParams fileBindBusinessRefOldParams) {
        List<FileInfo> fileInfos = queryAndFilter(fileBindBusinessRefOldParams.getOldBusinessType(), fileBindBusinessRefOldParams.getOldBusinessId(), fileBindBusinessRefOldParams.getFileIds());
        if (CollectionUtils.isEmpty(fileInfos)) {
            log.info("没有匹配的文件可以copy。请求参数为:[{}]", fileBindBusinessRefOldParams);
            return;
        }
        for (FileInfo fileInfo : fileInfos) {
            fileInfo.setBusinessType(fileBindBusinessRefOldParams.getBusinessType())
                    .setBusinessId(fileBindBusinessRefOldParams.getBusinessId())
                    .setId(null);
        }
        fileInfoOperation.saveFileInfo(fileInfos);
    }


    /**
     * 4. 根据业务id删除附件（支持删除单个或者多个）
     *
     * @param fileBindBusinessParams 文件业务绑定参数
     */
    @Override
    public void deleteFileByBusinessInfo(FileBindBusinessParams fileBindBusinessParams) {
        if (StringUtils.isBlank(fileBindBusinessParams.getBusinessId()) || StringUtils.isBlank(fileBindBusinessParams.getBusinessType())) {
            log.warn("文件删除时请求参数异常！请求参数为:[{}]传入的文件类型或者业务id为空!", fileBindBusinessParams);
            throw new SystemRuntimeException(GlobalErrorCodeConstants.BAD_REQUEST);
        }

        List<FileInfo> fileInfos = queryAndFilter(fileBindBusinessParams.getBusinessType(), fileBindBusinessParams.getBusinessId(), fileBindBusinessParams.getFileIds());
        if (CollectionUtils.isEmpty(fileInfos)) {
            log.info("没有匹配的文件可以删除。请求参数为:[{}]", fileBindBusinessParams);
            return;
        }
        fileInfoOperation.deleteByFileIds(fileInfos.stream().map(FileInfo::getId).toList());
        //删除文件
        fileInfos.stream().filter(fileInfoParams -> StringUtils.isNotBlank(fileInfoParams.getFilePath()))
                .forEach(customFileOperation::deleteFile);

    }

    /**
     * 5. 删除过期没有关联的临时文件
     */
    @Override
    public void deleteTempFile() {
        LocalDateTime gtData = LocalDateTime.now().minusDays(fileProperties.getNoBusinessDay());
        List<TempFileInfo> noBusinessList = fileInfoOperation.findByNoBusiness(gtData);
        if (!CollectionUtils.isEmpty(noBusinessList)) {
            fileInfoOperation.deleteTempFileInfo(noBusinessList);
            try {
                noBusinessList.stream()
                        .filter(tempfileInfo -> StringUtils.isNotBlank(tempfileInfo.getFilePath()))
                        .forEach(tempfileInfo -> customFileOperation.deleteFile(new FileInfo()
                                .setFilePath(tempfileInfo.getFilePath())
                                .setFileHash(tempfileInfo.getFileHash())
                                .setFileStorageType(tempfileInfo.getFileStorageType())));
            } catch (Exception e) {
                log.warn("删除文件异常！", e);
            }
        }
    }

    /**
     * 6. 获取文件信息
     *
     * @param fileBindBusinessParams 业务信息
     * @return 文件列表
     */
    @Override
    public List<FileInfo> findFileInfoByBusiness(FileBindBusinessParams fileBindBusinessParams) {
        if (CollectionUtils.isEmpty(fileBindBusinessParams.getFileIds())) {
            return fileInfoOperation.findByBusinessAndId(fileBindBusinessParams.getBusinessType(), fileBindBusinessParams.getBusinessId());
        }
        return fileInfoOperation.findByBusinessAndId(fileBindBusinessParams.getBusinessType(), fileBindBusinessParams.getBusinessId(),
                fileBindBusinessParams.getFileIds().toArray(new String[0]));
    }

    /**
     * 获取文件流
     *
     * @param businessType
     * @param businessId
     * @param fileId
     * @return
     */
    @Override
    public InputStream getFileInputStream(String businessType, String businessId, String fileId) {
        List<FileInfo> fileInfos = fileInfoOperation.findByBusinessAndId(businessType, businessId, fileId);
        if (CollectionUtils.isEmpty(fileInfos)) {
            log.warn("根据原始业务类型:[{}]原始业务id:[{}],文件ID:[{}]查询不出文件信息!", businessType, businessId, fileId);
            throw new SystemRuntimeException(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        FileInfo fileInfo = fileInfos.getFirst();
        return customFileOperation.downloadFile(fileInfo);
    }


    /**
     * 查询并过滤旧数据
     *
     * @param businessType 业务类型
     * @param businessId   业务id
     * @param fileIds      文件id
     * @return
     */
    private List<FileInfo> queryAndFilter(String businessType, String businessId, List<String> fileIds) {
        //查询原业务绑定的文件
        List<FileInfo> fileInfos = fileInfoOperation.findByBusinessAndId(businessType, businessId);
        if (CollectionUtils.isEmpty(fileInfos)) {
            log.warn("根据原始业务类型:[{}]原始业务id:[{}]查询不出文件信息!", businessType, businessId);
            throw new SystemRuntimeException(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        if (!CollectionUtils.isEmpty(fileIds)) {
            fileInfos = fileInfos.stream()
                    .filter(fileInfo -> fileIds.contains(fileInfo.getId()))
                    .toList();
        }
        return fileInfos;
    }


}
