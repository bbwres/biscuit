package cn.bbwres.biscuit.web.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author loukia
 * @since 2022/3/25
 **/
@Component
@Data
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /**
     * 文件存储类型
     */
    private String storageType;

    /**
     * 删除 n天之前没有业务关联的临时文件数据
     */
    private Integer noBusinessDay = 5;


    /**
     * 是否强制前端计算文件hash
     * 计算了文件hash 才可以实现文件秒传等功能
     */
    private boolean mustCalculateFileHash = false;


    /**
     * 默认的下载文件的ContentType
     */
    private String defaultDownloadContentType = "application/octet-stream";

}
