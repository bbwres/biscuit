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

package cn.bbwres.biscuit.web.file.endpoint;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.web.file.config.FileProperties;
import cn.bbwres.biscuit.web.file.endpoint.vo.DownloadFileInfoParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.service.CustomFileOperation;
import cn.bbwres.biscuit.web.file.service.FileInfoOperation;
import cn.bbwres.biscuit.web.file.utils.ZipByteArrayUtil;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件下载端点
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@Tag(name = "文件下载端点")
@RestController
@RequestMapping("/v1/file/download")
public class FileDownloadEndpoint extends BaseFileEndpoint {


    private final CustomFileOperation customFileOperation;

    private final FileProperties fileProperties;

    private final FileInfoOperation fileInfoOperation;

    /**
     * <p>Constructor for FileDownloadEndpoint.</p>
     *
     * @param customFileOperation a {@link cn.bbwres.biscuit.web.file.service.CustomFileOperation} object
     * @param fileProperties a {@link cn.bbwres.biscuit.web.file.config.FileProperties} object
     * @param fileInfoOperation a {@link cn.bbwres.biscuit.web.file.service.FileInfoOperation} object
     */
    public FileDownloadEndpoint(CustomFileOperation customFileOperation, FileProperties fileProperties, FileInfoOperation fileInfoOperation) {
        this.customFileOperation = customFileOperation;
        this.fileProperties = fileProperties;
        this.fileInfoOperation = fileInfoOperation;
    }


    /**
     * 根据业务id和业务类型获取出文件列表
     *
     * @param downloadFileInfo a {@link cn.bbwres.biscuit.web.file.endpoint.vo.DownloadFileInfoParams} object
     * @return a {@link cn.bbwres.biscuit.dto.Result} object
     */
    @Operation(summary = "根据业务id和业务类型获取出文件列表")
    @PostMapping(value = "/findFileInfoByBusiness")
    public Result<List<FileInfo>> findFileInfoByBusiness(@RequestBody @Validated DownloadFileInfoParams downloadFileInfo) {
        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        if (!fileInfoOperation.checkFilePermission(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId(), requestUser)) {
            log.info("当前获取附件列表据失败！请求参数为:[{}],业务模块不允许获取!", downloadFileInfo);
            return Result.error(GlobalErrorCodeConstants.UNAUTHORIZED);
        }
        //获取文件id
        return Result.success(fileInfoOperation.findByBusinessAndId(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId()));
    }


    /**
     * 下载文件
     *
     * @param downloadFileInfo a {@link cn.bbwres.biscuit.web.file.endpoint.vo.DownloadFileInfoParams} object
     * @param response a {@link jakarta.servlet.http.HttpServletResponse} object
     * @throws java.io.IOException if any.
     */
    @Operation(summary = "下载文件")
    @PostMapping(value = "/downloadFileByFileIds")
    public void downloadFileByFileIds(@RequestBody @Validated DownloadFileInfoParams downloadFileInfo, HttpServletResponse response) throws IOException {
        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        if (!fileInfoOperation.checkFilePermission(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId(), requestUser, downloadFileInfo.getFileId())) {
            log.info("当前附件获取数据失败！请求参数为:[{}],业务模块不允许获取!", downloadFileInfo);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeConstants.UNAUTHORIZED);
            return;
        }

        //获取文件id
        List<FileInfo> fileInfos = fileInfoOperation.findByBusinessAndId(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId(), downloadFileInfo.getFileId());
        if (CollectionUtils.isEmpty(fileInfos)) {
            log.info("当前查询参数:[{}],没有查询到文件数据.", downloadFileInfo);
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, GlobalErrorCodeConstants.BAD_REQUEST);
            return;
        }

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(StringUtils.isBlank(downloadFileInfo.getDownloadContentType()) ? fileProperties.getDefaultDownloadContentType() : downloadFileInfo.getDownloadContentType());
        if (fileInfos.size() > 1) {
            //多文件处理
            List<ZipByteArrayUtil.StreamEntry> streamEntries = new ArrayList<>(16);
            for (FileInfo fileInfo : fileInfos) {
                streamEntries.add(new ZipByteArrayUtil.StreamEntry(customFileOperation.downloadFile(fileInfo), fileInfo.getFileName()));
            }
            String fileName = UUID.randomUUID() + ".zip";
            response.setHeader("Content-Disposition", "attchment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            ZipByteArrayUtil.compressMultipleInputStreamsToZip(streamEntries, response.getOutputStream());
            return;
        }
        //单文件处理下
        FileInfo fileInfo = fileInfos.getFirst();
        try (InputStream inputStream = customFileOperation.downloadFile(fileInfo)) {
            if (fileInfo.getFileSize() != null) {
                response.setHeader("Content-Length", fileInfo.getFileSize() + "");
            }
            response.setHeader("Content-Disposition", "attchment;filename=" + URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8));
            IOUtils.copyLarge(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error("下载文件时出错", e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 下载文件
     *
     * @param downloadFileInfo a {@link cn.bbwres.biscuit.web.file.endpoint.vo.DownloadFileInfoParams} object
     * @param response a {@link jakarta.servlet.http.HttpServletResponse} object
     * @throws java.io.IOException if any.
     */
    @Operation(summary = "下载文件")
    @GetMapping(value = "/downloadFileByFileIds")
    public void downloadFiles(@Validated DownloadFileInfoParams downloadFileInfo, HttpServletResponse response) throws IOException {
        downloadFileByFileIds(downloadFileInfo, response);
    }


}
