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

package cn.bbwres.biscuit.web.file.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件业务关联请求参数
 *
 * @author zhanglinfeng12
 * @version $Id: $Id
 */
@Data
@Accessors(chain = true)
public class FileBusinessParams implements Serializable {

    @Serial
    private static final long serialVersionUID = -3577631671088232735L;
    /**
     * 关联的业务id
     */
    private String businessId;
    /**
     * 关联的业务类型
     */
    private String businessType;


}
