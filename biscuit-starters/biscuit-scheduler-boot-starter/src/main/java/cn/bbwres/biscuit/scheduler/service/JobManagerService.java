/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.scheduler.service;

import cn.bbwres.biscuit.scheduler.dto.TriggerInfo;
import org.quartz.JobDetail;

import java.util.List;

/**
 * job 管理接口
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public interface JobManagerService {


    /**
     * 获取所有任务
     *
     * @return a {@link java.util.List} object
     */
    List<TriggerInfo> queryAllTrigger();

    /**
     * 获取指定任务的明细
     *
     * @param jobName a {@link java.lang.String} object
     * @param jobGroup a {@link java.lang.String} object
     * @return a {@link org.quartz.JobDetail} object
     */
    JobDetail queryJobDetail(String jobName, String jobGroup);

    /**
     * 暂停任务执行
     *
     * @param triggerName a {@link java.lang.String} object
     * @param triggerGroup a {@link java.lang.String} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean pausedTrigger(String triggerName, String triggerGroup);

    /**
     * 批量暂停任务
     *
     * @param triggerKeys a {@link java.util.List} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean pausedTriggers(List<TriggerInfo> triggerKeys);


    /**
     * 恢复任务执行
     *
     * @param triggerName a {@link java.lang.String} object
     * @param triggerGroup a {@link java.lang.String} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean resumeTrigger(String triggerName, String triggerGroup);

    /**
     * 批量恢复任务
     *
     * @param triggerKeys a {@link java.util.List} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean resumeTriggers(List<TriggerInfo> triggerKeys);

    /**
     * 更新定时任务
     *
     * @param triggerName a {@link java.lang.String} object
     * @param triggerGroup a {@link java.lang.String} object
     * @param cron a {@link java.lang.String} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean updateTriggerCron(String triggerName, String triggerGroup,String cron);

    /**
     * 立即执行 一个任务
     *
     * @param jobName a {@link java.lang.String} object
     * @param jobGroup a {@link java.lang.String} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean triggerJob(String jobName, String jobGroup);


    /**
     * 立即执行 一个任务
     *
     * @param triggerName a {@link java.lang.String} object
     * @param triggerGroup a {@link java.lang.String} object
     * @return a {@link java.lang.Boolean} object
     */
    Boolean triggerJobByTrigger(String triggerName, String triggerGroup);
}
