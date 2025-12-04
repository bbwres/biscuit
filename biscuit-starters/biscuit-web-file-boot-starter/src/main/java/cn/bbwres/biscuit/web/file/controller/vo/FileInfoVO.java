package cn.bbwres.biscuit.web.file.controller.vo;

import cn.bbwres.biscuit.web.file.entity.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * <p>
 * 附件信息 传输对象
 * </p>
 *
 * @author zlf
 * @Date 2022-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileInfoVO extends FileInfo {

    @Serial
    private static final long serialVersionUID = -6152163496617974233L;

    /**
     * 文件的下载地址
     */
    @Schema(name = "文件的下载地址,调用获取文件下载地址时才有值")
    private String downloadUrl;


    /**
     * 文件的预览地址
     */
    @Schema(name = "文件的预览地址,调用获取文件下载地址时才有值")
    private String previewUrl;

}
