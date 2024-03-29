package cn.bbwres.biscuit.scheduler.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 调度信息
 *
 * @author zhanglinfeng
 */
@Accessors(chain = true)
@Data
public class TriggerInfo {

    private String name;

    private String group;

    private String jobName;

    private String jobGroup;

    private String description;

    private String nextFireTime;

    private String previousFireTime;

    private String startTime;

    private String endTime;

    private String cronExpression;

    private String status;


}
