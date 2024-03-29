package cn.bbwres.biscuit.scheduler.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 串行的任务
 *
 * @author zhanglinfeng
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulBeanInvokingJob extends BeanInvokingJob {

}
