package cn.bbwres.biscuit.scheduler.service;

import cn.bbwres.biscuit.scheduler.dto.TriggerInfo;
import org.quartz.JobDetail;

import java.util.List;

/**
 * job 管理接口
 *
 * @author zhanglinfeng
 */
public interface JobManagerService {


    /**
     * 获取所有任务
     *
     * @return
     */
    List<TriggerInfo> queryAllTrigger();

    /**
     * 获取指定任务的明细
     * @param jobName
     * @param jobGroup
     * @return
     */
    JobDetail queryJobDetail(String jobName, String jobGroup);

    /**
     * 暂停任务执行
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    Boolean pausedTrigger(String triggerName, String triggerGroup);

    /**
     * 批量暂停任务
     * @param triggerKeys
     * @return
     */
    Boolean pausedTriggers(List<TriggerInfo> triggerKeys);


    /**
     * 恢复任务执行
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    Boolean resumeTrigger(String triggerName, String triggerGroup);

    /**
     * 批量恢复任务
     * @param triggerKeys
     * @return
     */
    Boolean resumeTriggers(List<TriggerInfo> triggerKeys);

    /**
     * 更新定时任务
     * @param triggerName
     * @param triggerGroup
     * @param cron
     * @return
     */
    Boolean updateTriggerCron(String triggerName, String triggerGroup,String cron);

    /**
     * 立即执行 一个任务
     * @param jobName
     * @param jobGroup
     * @return
     */
    Boolean triggerJob(String jobName, String jobGroup);


    /**
     * 立即执行 一个任务
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    Boolean triggerJobByTrigger(String triggerName, String triggerGroup);
}
