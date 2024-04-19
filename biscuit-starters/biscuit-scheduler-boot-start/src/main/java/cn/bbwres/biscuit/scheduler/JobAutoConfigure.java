package cn.bbwres.biscuit.scheduler;

import cn.bbwres.biscuit.scheduler.annotation.JobDefinition;
import cn.bbwres.biscuit.scheduler.job.StatefulBeanInvokingJob;
import cn.bbwres.biscuit.scheduler.service.JobManagerService;
import cn.bbwres.biscuit.scheduler.service.JobManagerServiceImpl;
import cn.bbwres.biscuit.scheduler.config.QuartzJdbcProperties;
import cn.bbwres.biscuit.scheduler.config.SchedulerConstant;
import cn.bbwres.biscuit.scheduler.job.BeanInvokingJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * job配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(QuartzJdbcProperties.class)
public class JobAutoConfigure {


    /**
     * <p>quartzProperties.</p>
     *
     * @param quartzJdbcProperties a {@link cn.bbwres.biscuit.scheduler.config.QuartzJdbcProperties} object
     * @return a {@link org.springframework.boot.autoconfigure.quartz.QuartzProperties} object
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "spring.quartz.jdbc", name = "jobStoreType", havingValue = "JDBC")
    public QuartzProperties quartzProperties(QuartzJdbcProperties quartzJdbcProperties) {
        //基础参数
        QuartzProperties quartzProperties = new QuartzProperties();
        quartzProperties.setJobStoreType(quartzJdbcProperties.getJobStoreType());
        quartzProperties.setAutoStartup(quartzJdbcProperties.getAutoStartup());
        quartzProperties.setSchedulerName(quartzJdbcProperties.getSchedulerName());
        quartzProperties.setStartupDelay(quartzJdbcProperties.getStartupDelay());
        quartzProperties.setOverwriteExistingJobs(quartzJdbcProperties.getOverwriteExistingJobs());
        quartzProperties.setWaitForJobsToCompleteOnShutdown(quartzJdbcProperties.getWaitForJobsToCompleteOnShutdown());
        //map 参数
        Map<String, String> properties = quartzProperties.getProperties();
        properties.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME,quartzJdbcProperties.getInstanceName());
        properties.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_ID,quartzJdbcProperties.getInstanceId());
        properties.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS,quartzJdbcProperties.getJobStoreClass());
        if(!StringUtils.isEmpty(quartzJdbcProperties.getDataSource())){
            properties.put("org.quartz.jobStore.dataSource",quartzJdbcProperties.getDataSource());
        }
        properties.put("org.quartz.jobStore.driverDelegateClass",quartzJdbcProperties.getDriverDelegateClass());
        properties.put("org.quartz.jobStore.tablePrefix",quartzJdbcProperties.getTablePrefix());
        properties.put("org.quartz.jobStore.isClustered",quartzJdbcProperties.getIsClustered());
        properties.put("org.quartz.jobStore.clusterCheckinInterval",quartzJdbcProperties.getClusterCheckinInterval());
        properties.put("org.quartz.threadPool.threadCount",quartzJdbcProperties.getThreadCount());
        properties.put("org.quartz.threadPool.threadPriority",quartzJdbcProperties.getThreadPriority());
        properties.put("org.quartz.threadPool.class",quartzJdbcProperties.getThreadPoolClass());
        properties.put("org.quartz.jobStore.acquireTriggersWithinLock",quartzJdbcProperties.getAcquireTriggersWithinLock());
        return quartzProperties;
    }


    /**
     * 初始 quartzScheduler
     *
     * @param properties a {@link org.springframework.boot.autoconfigure.quartz.QuartzProperties} object
     * @param customizers a {@link org.springframework.beans.factory.ObjectProvider} object
     * @param calendars a {@link java.util.Map} object
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     * @param dataSource a DataSource object
     * @return a {@link org.springframework.scheduling.quartz.SchedulerFactoryBean} object
     */
    @Bean
    public SchedulerFactoryBean quartzScheduler(QuartzProperties properties,
                                                ObjectProvider<SchedulerFactoryBeanCustomizer> customizers,
                                                Map<String, Calendar> calendars,
                                                ApplicationContext applicationContext,
                                                DataSource dataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        if (properties.getSchedulerName() != null) {
            schedulerFactoryBean.setSchedulerName(properties.getSchedulerName());
        }
        if(!properties.getProperties().containsKey("org.quartz.jobStore.dataSource")){
            schedulerFactoryBean.setDataSource(dataSource);
        }
        schedulerFactoryBean.setAutoStartup(properties.isAutoStartup());
        schedulerFactoryBean.setStartupDelay((int) properties.getStartupDelay().getSeconds());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setOverwriteExistingJobs(properties.isOverwriteExistingJobs());
        if (!properties.getProperties().isEmpty()) {
            schedulerFactoryBean.setQuartzProperties(asProperties(properties.getProperties()));
        }
        setJobInfo(schedulerFactoryBean, applicationContext);
        schedulerFactoryBean.setCalendars(calendars);
        customizers.orderedStream().forEach((customizer) -> customizer.customize(schedulerFactoryBean));
        return schedulerFactoryBean;
    }

    /**
     * 初始化bean 管理器
     *
     * @param scheduler a {@link org.quartz.Scheduler} object
     * @return a {@link cn.bbwres.biscuit.scheduler.service.JobManagerService} object
     */
    @Bean
    public JobManagerService jobManagerService(Scheduler scheduler) {
        return new JobManagerServiceImpl(scheduler);
    }

    /**
     * 设置job
     *
     * @param schedulerFactoryBean
     * @param applicationContext
     */
    private void setJobInfo(SchedulerFactoryBean schedulerFactoryBean, ApplicationContext applicationContext) {
        //获取到所有JobDefinition的bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(JobDefinition.class);
        if (CollectionUtils.isEmpty(beans)) {
            log.info("当前无定时任务需要初始化");
            return;
        }
        List<JobDetail> jobDetails = new ArrayList<>();
        List<Trigger> triggers = new ArrayList<>();
        for (String beanName : beans.keySet()) {
            Object bean = beans.get(beanName);
            JobDefinition jobDefinition = bean.getClass().getAnnotation(JobDefinition.class);
            if (jobDefinition == null) {
                continue;
            }
            JobDetail jobDetail = buildJobDetail(jobDefinition, beanName);
            Trigger trigger = buildTrigger(jobDefinition, jobDetail);

            jobDetails.add(jobDetail);
            triggers.add(trigger);
        }

        schedulerFactoryBean.setJobDetails(jobDetails.toArray(new JobDetail[]{}));
        schedulerFactoryBean.setTriggers(triggers.toArray(new Trigger[]{}));
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }


    /**
     * 创建任务
     *
     * @param jobDefinition
     * @param beanName
     * @return
     */
    private JobDetail buildJobDetail(JobDefinition jobDefinition, String beanName) {
        JobDataMap jobDataMap = new JobDataMap();
        if (jobDefinition.arguments().length > 0) {
            for (cn.bbwres.biscuit.scheduler.annotation.JobDataMap argument : jobDefinition.arguments()) {
                jobDataMap.put(argument.key(), argument.value());
            }
        }
        jobDataMap.put(SchedulerConstant.TARGET_BEAN, beanName);
        jobDataMap.put(SchedulerConstant.TARGET_METHOD, jobDefinition.targetMethod());
        jobDataMap.put(SchedulerConstant.USE_CONTEXT, jobDefinition.useContext());
        return JobBuilder.newJob(jobDefinition.concurrent() ? BeanInvokingJob.class : StatefulBeanInvokingJob.class)
                .withIdentity(jobDefinition.jobName(), jobDefinition.group())
                .storeDurably()
                .setJobData(jobDataMap)
                .withDescription(jobDefinition.description())
                .build();


    }

    /**
     * 创建  Trigger
     *
     * @param jobDefinition
     * @param jobDetail
     * @return
     */
    private Trigger buildTrigger(JobDefinition jobDefinition, JobDetail jobDetail) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobDefinition.cron())
                .withMisfireHandlingInstructionDoNothing();
        // 创建任务触发器
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withDescription(jobDetail.getDescription())
                .withIdentity(jobDefinition.jobName() + "Trigger", jobDefinition.group())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
