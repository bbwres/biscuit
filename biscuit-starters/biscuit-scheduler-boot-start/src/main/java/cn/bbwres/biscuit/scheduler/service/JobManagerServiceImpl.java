package cn.bbwres.biscuit.scheduler.service;

import cn.bbwres.biscuit.scheduler.dto.TriggerInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务管理接口
 *
 * @author zhanglinfeng
 */
@Slf4j
@RequiredArgsConstructor
public class JobManagerServiceImpl implements JobManagerService {

    private final Scheduler scheduler;


    /**
     * 获取所有任务
     *
     * @return
     */
    @Override
    public List<TriggerInfo> queryAllTrigger() {
        try {
            List<String> groups = scheduler.getTriggerGroupNames();
            Set<TriggerKey> triggerKeys = new HashSet<>(10);
            for (String group : groups) {
                triggerKeys.addAll(scheduler.getTriggerKeys(GroupMatcher.groupEquals(group)));
            }

            return triggerKeys.parallelStream().map(new Function<TriggerKey, TriggerInfo>() {
                @SneakyThrows
                @Override
                public TriggerInfo apply(TriggerKey triggerKey) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                    return new TriggerInfo().setName(cronTrigger.getKey().getName())
                            .setDescription(cronTrigger.getDescription())
                            .setGroup(cronTrigger.getKey().getGroup())
                            .setJobName(cronTrigger.getJobKey().getName())
                            .setJobGroup(cronTrigger.getJobKey().getGroup())
                            .setStatus(scheduler.getTriggerState(triggerKey).name())
                            .setCronExpression(cronTrigger.getCronExpression())
                            .setStartTime(cronTrigger.getStartTime() != null ? sdf.format(cronTrigger.getStartTime()) : null)
                            .setEndTime(cronTrigger.getEndTime() != null ? sdf.format(cronTrigger.getEndTime()) : null)
                            .setPreviousFireTime(cronTrigger.getPreviousFireTime() != null ? sdf.format(cronTrigger.getPreviousFireTime()) : null)
                            .setNextFireTime(cronTrigger.getNextFireTime() != null ? sdf.format(cronTrigger.getNextFireTime()) : null);
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.info("查询任务失败!", e);
        }
        return null;
    }

    /**
     * 获取指定任务的明细
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    @Override
    public JobDetail queryJobDetail(String jobName, String jobGroup) {
        try {
            return scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroup));
        } catch (Exception e) {
            log.info("查询任务失败!", e);
        }
        return null;
    }

    /**
     * 暂停任务执行
     *
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    @Override
    public Boolean pausedTrigger(String triggerName, String triggerGroup) {
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
            return true;
        } catch (Exception e) {
            log.info("查询任务失败!", e);
        }
        return false;
    }

    /**
     * 批量暂停任务
     *
     * @param triggerKeys
     * @return
     */
    @Override
    public Boolean pausedTriggers(List<TriggerInfo> triggerKeys) {
        try {
            for (TriggerInfo triggerKey : triggerKeys) {
                scheduler.pauseTrigger(TriggerKey.triggerKey(triggerKey.getName(), triggerKey.getGroup()));
            }
            return true;
        } catch (Exception e) {
            log.info("查询任务失败!", e);
        }
        return false;
    }

    /**
     * 恢复任务执行
     *
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    @Override
    public Boolean resumeTrigger(String triggerName, String triggerGroup) {
        try {
            scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
            return true;
        } catch (Exception e) {
            log.info("查询任务失败!", e);
        }
        return false;
    }

    /**
     * 批量恢复任务
     *
     * @param triggerKeys
     * @return
     */
    @Override
    public Boolean resumeTriggers(List<TriggerInfo> triggerKeys) {
        try {
            for (TriggerInfo triggerKey : triggerKeys) {
                scheduler.resumeTrigger(TriggerKey.triggerKey(triggerKey.getName(), triggerKey.getGroup()));
            }
            return true;
        } catch (Exception e) {
            log.info("查询任务失败!", e);
        }
        return false;
    }

    /**
     * 更新定时任务
     *
     * @param triggerName
     * @param triggerGroup
     * @param cron
     * @return
     */
    @Override
    public Boolean updateTriggerCron(String triggerName, String triggerGroup, String cron) {
        try {
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            //错过任务后不执行已错过的任务，只执行当前的
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
            scheduler.rescheduleJob(cronTrigger.getKey(), cronTrigger.getTriggerBuilder()
                    .withSchedule(scheduleBuilder)
                    .build());
            return true;
        } catch (Exception e) {
            log.info("更新任务失败!", e);
        }
        return false;
    }

    /**
     * 立即执行
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    @Override
    public Boolean triggerJob(String jobName, String jobGroup) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup));
            return true;
        } catch (Exception e) {
            log.info("立即执行任务失败!", e);
        }
        return false;
    }

    /**
     * 立即执行 一个任务
     *
     * @param triggerName
     * @param triggerGroup
     * @return
     */
    @Override
    public Boolean triggerJobByTrigger(String triggerName, String triggerGroup) {
        try {
            JobKey jobKey = scheduler.getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup)).getJobKey();
            return triggerJob(jobKey.getName(), jobKey.getGroup());
        } catch (Exception e) {
            log.info("立即执行任务失败!", e);
        }
        return false;
    }
}
