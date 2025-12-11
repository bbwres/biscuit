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

package cn.bbwres.biscuit.web.file.endpoint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 下载文件请求参数
 *
 * @author zlf
 * @version $Id: $Id
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "下载文件请求参数")
public class DownloadFileInfoParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 5757877101749319838L;
    /**
     * 关联的业务id
     */
    @Schema(description = "关联的业务id")
    @NotBlank(message = "业务id不能为空")
    private String businessId;
    /**
     * 关联的业务类型
     */
    @Schema(description = "关联的业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;


    /**
     * 文件下载的时候的ContentType，
     * 默认为application/octet-stream
     */
    @Schema(description = "文件下载的时候的ContentType，默认为application/octet-stream")
    private String downloadContentType;

    /**
     * 文件ids
     */
    @Schema(description = "文件ids,为空则下载全部文件")
    private String[] fileId;



}
