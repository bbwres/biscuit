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

package cn.bbwres.biscuit.scheduler.job;

import cn.bbwres.biscuit.scheduler.config.SchedulerConstant;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.MethodInvoker;

/**
 * job
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
public class BeanInvokingJob implements Job {

    @Autowired
    private ApplicationContext applicationContext;

    /** {@inheritDoc} */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String targetBean = null;
        String targetMethod = null;

        try {
            log.debug("start");
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            targetBean = jobDataMap.getString(SchedulerConstant.TARGET_BEAN);
            log.debug("targetBean is " + targetBean);
            if (targetBean == null) {
                throw new JobExecutionException("targetBean cannot be null.", false);
            }

            targetMethod = jobDataMap.getString(SchedulerConstant.TARGET_METHOD);
            log.debug("targetMethod is " + targetMethod);
            if (targetMethod == null) {
                throw new JobExecutionException("targetMethod cannot be null.", false);
            }

            Object bean = applicationContext.getBean(targetBean);
            log.debug("applicationContext resolved bean name/id [{}] to [{}]'", targetBean, bean);
            //设置属性
            try {
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
                MutablePropertyValues pvs = new MutablePropertyValues();
                pvs.addPropertyValues(context.getScheduler().getContext());
                pvs.addPropertyValues(jobDataMap);
                bw.setPropertyValues(pvs, true);
            } catch (SchedulerException ex) {
                throw new JobExecutionException(ex);
            }
            MethodInvoker beanMethod = new MethodInvoker();
            beanMethod.setTargetObject(bean);
            beanMethod.setTargetMethod(targetMethod);
            if(jobDataMap.getBoolean(SchedulerConstant.USE_CONTEXT)){
                beanMethod.setArguments(context);
            }
            beanMethod.prepare();
            beanMethod.invoke();
        } catch (JobExecutionException jobExecutionException) {
            throw jobExecutionException;
        } catch (Exception e) {
            log.error("定时任务[bean=[{}],method=[{}]执行异常：", targetBean, targetMethod, e);
            throw new JobExecutionException(e);
        } finally {
            log.debug("end");
        }

    }
}
