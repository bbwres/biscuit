package cn.bbwres.biscuit.scheduler.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 串行的任务
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulBeanInvokingJob extends BeanInvokingJob {

}
