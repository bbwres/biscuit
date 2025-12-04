package cn.bbwres.biscuit.web.file.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 下载文件请求参数
 *
 * @author zlf
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "下载文件请求参数")
public class DownloadFileInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5757877101749319838L;
    /**
     * 关联的业务id
     */
    @Schema(name = "关联的业务id")
    @NotBlank(message = "业务id不能为空")
    private String businessId;
    /**
     * 关联的业务类型
     */
    @Schema(name = "关联的业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;


    /**
     * 文件下载的时候的ContentType，
     * 默认为application/octet-stream
     */
    @Schema(name = "文件下载的时候的ContentType，默认为application/octet-stream")
    private String downloadContentType;

    /**
     * 文件ids
     */
    @Schema(name = "文件ids,为空则下载全部文件")
    private String[] fileId;



}
