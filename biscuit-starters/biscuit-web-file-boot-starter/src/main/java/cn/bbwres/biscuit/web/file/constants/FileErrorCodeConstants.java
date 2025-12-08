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

package cn.bbwres.biscuit.web.file.constants;

import cn.bbwres.biscuit.exception.constants.ErrorCode;

/**
 * 文件上传错误码
 *
 * @author zhanglinfeng
 */
public interface FileErrorCodeConstants {

    /**
     * 数据已经存在
     */
    ErrorCode DATA_ALREADY_EXISTS_ERROR = new ErrorCode("901001001", "file.data_already_exists_error");


}
