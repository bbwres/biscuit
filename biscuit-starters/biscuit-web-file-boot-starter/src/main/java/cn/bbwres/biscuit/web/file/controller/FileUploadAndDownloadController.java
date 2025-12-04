package cn.bbwres.biscuit.web.file.controller;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.exception.constants.ErrorCode;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.utils.JsonUtil;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidationUtil;
import cn.bbwres.biscuit.web.file.config.FileProperties;
import cn.bbwres.biscuit.web.file.controller.vo.DownloadFileInfoVO;
import cn.bbwres.biscuit.web.file.controller.vo.FileInfoVO;
import cn.bbwres.biscuit.web.file.controller.vo.UploadFileInfoVO;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import cn.bbwres.biscuit.web.file.service.FileOperation;
import cn.bbwres.biscuit.web.file.service.FileUploadAndDownloadService;
import cn.bbwres.biscuit.web.file.utils.ZipByteArrayUtil;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 文件上传与下载
 *
 * @author zlf
 * @since 2022-09-03
 */
@Slf4j
@Tag(name = "附件信息")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/v1/fileUploadAndDownload")
public class FileUploadAndDownloadController {


    private final FileOperation fileOperation;

    private final FileProperties fileProperties;

    private final FileUploadAndDownloadService fileUploadAndDownloadService;


    /**
     * 上传单个文件
     * 实现逻辑为：
     * 1.前端先调用文件上传接口 获取上传文件的id
     * 2.把接口范文的文件id传入到业务接口中
     * 3.业务接口调用绑定文件关系接口
     *
     * @param file
     * @return
     */
    @Operation(summary = "上传单个文件")
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadFile(@RequestPart("file") MultipartFile file, @RequestPart(name = "fileInfo", required = false) UploadFileInfoVO uploadFileInfo) {
        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        if (Objects.isNull(uploadFileInfo)) {
            uploadFileInfo = new UploadFileInfoVO();
            uploadFileInfo.setFileName(file.getOriginalFilename());
            uploadFileInfo.setFileSize(file.getSize());
        } else {
            ValidationUtil.doValidate(uploadFileInfo, ValidateAddGroup.class);
        }
        if (fileProperties.isMustCalculateFileHash() && StringUtils.isBlank(uploadFileInfo.getFileHash())) {
            log.warn("当前已配置必须开启文件hash的参数，但是上传文件时未设置文件hash值");
            return Result.error(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        //设置数据
        TempFileInfo fileInfo = new TempFileInfo().setFileName(uploadFileInfo.getFileName()).setFileSize(uploadFileInfo.getFileSize()).setFileHash(uploadFileInfo.getFileHash()).setFileSuffix(FilenameUtils.getExtension(uploadFileInfo.getFileName())).setCreateTime(LocalDateTime.now()).setFileStorageType(fileProperties.getStorageType());
        if (Objects.nonNull(requestUser)) {
            fileInfo.setCreator(requestUser.getUserId()).setCreatorName(requestUser.getZhName()).setTenantId(requestUser.getTenantId());
        }
        fileInfo.setFilePath(fileOperation.uploadFile(file));

        //保存记录数据到临时文件表
        fileInfo = fileUploadAndDownloadService.saveTempFileInfo(fileInfo);
        //返回文件的id
        return Result.success(fileInfo.getId());
    }

    /**
     * 根据业务id和业务类型获取出文件列表
     *
     * @param downloadFileInfo
     * @return
     */
    @Operation(summary = "根据业务id和业务类型获取出文件列表")
    @PostMapping(value = "/findFileInfoByBusiness")
    public Result<List<FileInfoVO>> findFileInfoByBusiness(@RequestBody @Validated DownloadFileInfoVO downloadFileInfo) {
        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        if (!fileUploadAndDownloadService.checkFilePermission(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId(), requestUser)) {
            log.info("当前获取附件列表据失败！请求参数为:[{}],业务模块不允许获取!", downloadFileInfo);
            return Result.error(GlobalErrorCodeConstants.UNAUTHORIZED);
        }
        //获取文件id
        return Result.success(fileUploadAndDownloadService.findByBusiness(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId()));
    }


    /**
     * 下载文件
     *
     * @param downloadFileInfo
     * @return
     */
    @Operation(summary = "下载文件")
    @PostMapping(value = "/downloadFileByFileIds")
    public void downloadFileByFileIds(@RequestBody @Validated DownloadFileInfoVO downloadFileInfo, HttpServletResponse response) throws IOException {
        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        if (!fileUploadAndDownloadService.checkFilePermission(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId(), requestUser, downloadFileInfo.getFileId())) {
            log.info("当前附件获取数据失败！请求参数为:[{}],业务模块不允许获取!", downloadFileInfo);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, GlobalErrorCodeConstants.UNAUTHORIZED);
            return;
        }

        //获取文件id
        List<FileInfo> fileInfos = fileUploadAndDownloadService.findByBusinessAndId(downloadFileInfo.getBusinessType(), downloadFileInfo.getBusinessId(), downloadFileInfo.getFileId());
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
                streamEntries.add(new ZipByteArrayUtil.StreamEntry(fileOperation.downloadFile(fileInfo), fileInfo.getFileName()));
            }
            String fileName = UUID.randomUUID() + ".zip";
            response.setHeader("Content-Disposition", "attchment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            ZipByteArrayUtil.compressMultipleInputStreamsToZip(streamEntries, response.getOutputStream());
            return;
        }
        //单文件处理下
        FileInfo fileInfo = fileInfos.getFirst();
        try (InputStream inputStream = fileOperation.downloadFile(fileInfo)) {
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
     * @param
     * @return
     */
    @Operation(summary = "下载文件")
    @GetMapping(value = "/downloadFile")
    public void downloadFiles(@Validated DownloadFileInfoVO downloadFileInfo, HttpServletResponse response) throws IOException {
        downloadFileByFileIds(downloadFileInfo, response);
    }


    /**
     * 文件秒传
     * 1.根据文件的hash检查文件是否存在
     * 2. 根据文件的hash复制文件
     */
    @Operation(summary = "根据文件的hash检查文件是否存在-用于文件秒传")
    @GetMapping(value = "/checkFileHashExist")
    public Result<Boolean> checkFileHashExist(@RequestParam("fileHash") String fileHash) {
     //   fileUploadAndDownloadService.findByFileHash(fileHash);
        return Result.success(true);
    }


    /**
     * 处理
     *
     * @param response
     * @param httpStatus
     * @param errorCode
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorCode errorCode) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        try {
            String respBody = JsonUtil.toJson(Result.error(errorCode));
            response.getWriter().write(respBody == null ? "" : respBody);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("输出错误信息时发生异常", e);
        }

    }


}

