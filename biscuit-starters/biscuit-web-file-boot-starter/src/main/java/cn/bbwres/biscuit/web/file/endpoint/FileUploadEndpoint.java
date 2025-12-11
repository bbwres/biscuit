package cn.bbwres.biscuit.web.file.endpoint;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidationUtil;
import cn.bbwres.biscuit.web.file.config.FileProperties;
import cn.bbwres.biscuit.web.file.endpoint.vo.UploadFileInfoParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import cn.bbwres.biscuit.web.file.service.CustomFileOperation;
import cn.bbwres.biscuit.web.file.service.FileInfoOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 文件上传端点
 *
 * @author zlf
 * @since 2022-09-03
 * @version $Id: $Id
 */
@Slf4j
@Tag(name = "文件上传端点")
@RestController
@RequestMapping("/v1/file/upload")
public class FileUploadEndpoint {


    private final CustomFileOperation customFileOperation;

    private final FileProperties fileProperties;

    private final FileInfoOperation fileInfoOperation;

    /**
     * <p>Constructor for FileUploadEndpoint.</p>
     *
     * @param customFileOperation a {@link cn.bbwres.biscuit.web.file.service.CustomFileOperation} object
     * @param fileProperties a {@link cn.bbwres.biscuit.web.file.config.FileProperties} object
     * @param fileInfoOperation a {@link cn.bbwres.biscuit.web.file.service.FileInfoOperation} object
     */
    public FileUploadEndpoint(CustomFileOperation customFileOperation, FileProperties fileProperties,
                              FileInfoOperation fileInfoOperation) {
        this.customFileOperation = customFileOperation;
        this.fileProperties = fileProperties;
        this.fileInfoOperation = fileInfoOperation;
    }


    /**
     * 上传单个文件
     * 实现逻辑为：
     * 1.前端先调用文件上传接口 获取上传文件的id
     * 2.把接口范文的文件id传入到业务接口中
     * 3.业务接口调用绑定文件关系接口
     *
     * @param file a {@link org.springframework.web.multipart.MultipartFile} object
     * @param uploadFileInfo a {@link cn.bbwres.biscuit.web.file.endpoint.vo.UploadFileInfoParams} object
     * @return a {@link cn.bbwres.biscuit.dto.Result} object
     */
    @Operation(summary = "上传单个文件")
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadFile(@RequestPart("file") MultipartFile file, @RequestPart(name = "fileInfo", required = false) UploadFileInfoParams uploadFileInfo) {
        if (Objects.isNull(uploadFileInfo)) {
            uploadFileInfo = new UploadFileInfoParams();
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
        TempFileInfo fileInfo = new TempFileInfo()

                .setFileName(uploadFileInfo.getFileName())
                .setFileSize(uploadFileInfo.getFileSize())
                .setFileHash(uploadFileInfo.getFileHash())
                .setFileSuffix(FilenameUtils.getExtension(uploadFileInfo.getFileName()))
                .setFileStorageMenu(StringUtils.isBlank(uploadFileInfo.getFileStorageMenu()) ? fileProperties.getDefaultStorageMenu() : uploadFileInfo.getFileStorageMenu())
                .setFileStorageType(StringUtils.isBlank(uploadFileInfo.getFileStorageType()) ? fileProperties.getDefaultStorageType() : uploadFileInfo.getFileStorageType());

        fileInfo.setFilePath(customFileOperation.uploadFile(file, fileInfo));
        //保存记录数据到临时文件表
        fileInfo = fileInfoOperation.saveTempFileInfo(fileInfo);
        //返回文件的id
        return Result.success(fileInfo.getId());
    }


    /**
     * 文件秒传
     * 1.根据文件的hash检查文件是否存在
     * 2. 根据文件的hash复制文件
     *
     * @param fileHash a {@link java.lang.String} object
     * @return a {@link cn.bbwres.biscuit.dto.Result} object
     */
    @Operation(summary = "根据文件的hash检查文件是否存在-用于文件秒传")
    @GetMapping(value = "/checkFileHashExist")
    public Result<Boolean> checkFileHashExist(@RequestParam("fileHash") String fileHash) {
        FileInfo fileInfo = fileInfoOperation.findByFileHashOne(fileHash);
        if (fileInfo != null) {
            log.info("当前文件的hash:[{}]已经存在在系统中", fileHash);
            return Result.success(true);
        }
        return Result.success(false);
    }

    /**
     * 秒传的文件上传
     *
     * @return Result
     * @param uploadFileInfo a {@link cn.bbwres.biscuit.web.file.endpoint.vo.UploadFileInfoParams} object
     */
    @Operation(summary = "秒传文件上传")
    @PostMapping(value = "/uploadFileBySecondTransfer")
    public Result<String> uploadFileBySecondTransfer(@RequestBody UploadFileInfoParams uploadFileInfo) {
        if (StringUtils.isBlank(uploadFileInfo.getFileHash())) {
            log.info("秒传失败！未传入文件的hash,请求参数为:[{}]", uploadFileInfo);
            return Result.error(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        FileInfo fileInfo = fileInfoOperation.findByFileHashOne(uploadFileInfo.getFileHash());
        if (fileInfo == null) {
            log.info("秒传失败！传入的文件hash找不到文件信息,请求参数为:[{}]", uploadFileInfo);
            return Result.error(GlobalErrorCodeConstants.BAD_REQUEST);
        }
        //设置数据
        TempFileInfo tempFileInfo = new TempFileInfo()
                .setFileName(uploadFileInfo.getFileName())
                .setFileSize(fileInfo.getFileSize())
                .setFileHash(fileInfo.getFileHash())
                .setFileSuffix(fileInfo.getFileSuffix())
                .setFileStorageType(fileInfo.getFileStorageType());

        tempFileInfo.setFilePath(customFileOperation.copyFile(fileInfo));
        //保存记录数据到临时文件表
        tempFileInfo = fileInfoOperation.saveTempFileInfo(tempFileInfo);
        //返回文件的id
        return Result.success(tempFileInfo.getId());

    }


}

