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

package cn.bbwres.biscuit.scheduler.annotation;


import java.lang.annotation.*;

/**
 * 定时批处理任务定义
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface JobBatchDefinition {
    /**
     * 批处理名称
     *
     * @return
     */
    String batchName();

    /**
     * 分组
     *
     * @return
     */
    String group() default "DEFAULT";

    /**
     * 初始化执行的方法
     *
     * @return
     */
    String initMethod();

    /**
     * 初始化的 的cron 表达式
     *
     * @return
     */
    String cron();


    /**
     * 任务描述
     *
     * @return
     */
    String description() default "";


}
