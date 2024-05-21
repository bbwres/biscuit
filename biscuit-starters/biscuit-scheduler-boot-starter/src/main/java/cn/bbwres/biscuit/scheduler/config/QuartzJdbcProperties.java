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

package cn.bbwres.biscuit.scheduler.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * QuartzJdbc 配置
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
@ConfigurationProperties("spring.quartz.jdbc")
public class QuartzJdbcProperties {

    /**
     * jdbc 配置
     */
    private JobStoreType jobStoreType = JobStoreType.JDBC;

    /**
     * 数据源
     */
    private String dataSource;

    /**
     * name
     */
    private String schedulerName = "scheduler";

    /**
     * 是否覆盖已有 Job 的配置
     */
    private Boolean overwriteExistingJobs = true;

    /**
     * Quartz 是否自动启动
     */
    private Boolean autoStartup = true;
    /**
     * 延迟 N 秒启动
     */
    private Duration startupDelay = Duration.ofSeconds(3L);

    /**
     * 应用关闭时，是否等待定时任务执行完成。默认为 false ，建议设置为 true
     */
    private Boolean waitForJobsToCompleteOnShutdown = false;

    /**
     * 实例名称
     */
    private String instanceName = "scheduler";

    /**
     * 实例id 需要设置为自动
     */
    private String instanceId = "AUTO";

    /**
     * jobStoreClass
     */
    private String jobStoreClass = "org.quartz.impl.jdbcjobstore.JobStoreTX";

    /**
     * driverDelegateClass
     */
    private String driverDelegateClass = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";

    /**
     * 表前缀
     */
    private String tablePrefix = "t_quart_";

    /**
     * 是否集群
     */
    private String isClustered = "true";

    /**
     * 集群检查时间 ms
     */
    private String clusterCheckinInterval = "2000";

    /**
     * 线程池大小
     */
    private String threadCount = "25";

    /**
     * 线程优先级
     */
    private String threadPriority = "5";

    /**
     * 线程池类型
     */
    private String threadPoolClass = "org.quartz.simpl.SimpleThreadPool";

    /**
     * 获取任务触发器时是否加锁
     */
    private String acquireTriggersWithinLock = "true";

}
