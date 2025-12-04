package cn.bbwres.biscuit.web.file.controller.vo;

import cn.bbwres.biscuit.validate.ValidateAddGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 上传文件请求参数
 *
 * @author zhanglinfeng12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "上传文件请求参数")
public class UploadFileInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3728931289050312660L;

    /**
     * 文件名称
     */
    @Schema(name = "文件名称")
    @NotBlank(groups = {ValidateAddGroup.class})
    @Size(max = 512)
    private String fileName;
    /**
     * 文件扩展名称
     */
    @Schema(name = "文件扩展名称")
    @NotBlank(groups = {ValidateAddGroup.class})
    @Size(max = 128)
    private String fileSuffix;
    /**
     * 文件大小
     */
    @Schema(name = "文件大小")
    @NotNull(groups = {ValidateAddGroup.class})
    private Long fileSize;
    /**
     * 文件的hash值
     */
    @Schema(name = "文件的hash值")
    @Size(max = 512)
    private String fileHash;


}
