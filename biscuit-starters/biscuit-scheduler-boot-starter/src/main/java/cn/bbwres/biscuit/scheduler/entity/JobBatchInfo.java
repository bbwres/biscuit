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

package cn.bbwres.biscuit.scheduler.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 批处理信息
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@Data
public class JobBatchInfo implements Serializable {
    private static final long serialVersionUID = 1133248207627964064L;

    /**
     * id
     */
    private String id;

    /**
     * 批次编码
     */
    private String batchNo;

    /**
     * 状态
     */
    private String status;

    /**
     * 批次名称
     */
    private String batchName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 组名
     */
    private String groupName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
