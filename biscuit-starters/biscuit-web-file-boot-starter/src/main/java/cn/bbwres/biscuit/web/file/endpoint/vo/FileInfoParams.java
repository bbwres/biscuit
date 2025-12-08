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

import cn.bbwres.biscuit.web.file.entity.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * <p>
 * 附件信息 传输对象
 * </p>
 *
 * @author zlf
 * @Date 2022-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileInfoParams extends FileInfo {

    @Serial
    private static final long serialVersionUID = -6152163496617974233L;

    /**
     * 文件的下载地址
     */
    @Schema(name = "文件的下载地址,调用获取文件下载地址时才有值")
    private String downloadUrl;


    /**
     * 文件的预览地址
     */
    @Schema(name = "文件的预览地址,调用获取文件下载地址时才有值")
    private String previewUrl;

}
