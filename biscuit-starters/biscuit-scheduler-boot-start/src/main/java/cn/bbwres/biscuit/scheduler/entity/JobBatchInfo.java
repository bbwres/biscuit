package cn.bbwres.biscuit.scheduler.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 批处理信息
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
public class JobBatchInfo implements Serializable {
    private static final long serialVersionUID = 1133248207627964064L;

    /**
     * id
     */
    private String id;

    /**
     * 批次编码
     */
    private String batchNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 批次名称
     */
    private String batchName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 组名
     */
    private String groupName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
