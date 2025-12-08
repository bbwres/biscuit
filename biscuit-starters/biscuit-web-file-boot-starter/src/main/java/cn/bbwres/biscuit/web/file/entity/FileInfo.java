package cn.bbwres.biscuit.web.file.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 附件信息
 * </p>
 *
 * @author zlf
 * @since 2022-09-03
 */
@Data
@Accessors(chain = true)
public class FileInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String updater;
    /**
     * 修改人名称
     */
    private String updaterName;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


    /**
     * 文件名称
     */
    private String fileName;


    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 关联的业务id
     */
    private String businessId;

    /**
     * 关联的业务类型
     */
    private String businessType;

    /**
     * 文件路径
     */
    private String filePath;


    /**
     * 文件存储类型
     */
    private String fileStorageType;


    /**
     * 文件hash值
     */
    private String fileHash;



    /**
     * 关联引入的文件id
     */
    private String srcFileId;


    /**
     * 扩展字段1
     */
    private String ext1;

    /**
     * 扩展字段2
     */
    private String ext2;

    /**
     * 扩展字段3
     */
    private String ext3;

    /**
     * 扩展字段4
     */
    private String ext4;

}
