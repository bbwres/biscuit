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

package cn.bbwres.biscuit.web.file.api;

import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessRefOldParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;

import java.util.List;

/**
 * 文件与业务关系操作
 *
 * @author zhanglinfeng
 */
public interface FileBusinessOperation {

    /**
     * 1.文件与业务id绑定
     *
     * @param fileBindBusinessExpandParams 文件业务绑定参数
     */
    void fileBindBusiness(FileBindBusinessExpandParams fileBindBusinessExpandParams);


    /**
     * 2. 文件绑定的业务变更（用于将原文件绑定到新业务上）
     *
     * @param fileBindBusinessRefOldParams 文件业务绑定参数
     */
    void fileBindBusinessChange(FileBindBusinessRefOldParams fileBindBusinessRefOldParams);


    /**
     * 3. 文件绑定复制（用于将原业务关联的附件 复制到新的业务上）
     *
     * @param fileBindBusinessRefOldParams 文件业务绑定参数
     */
    void fileBindBusinessCopy(FileBindBusinessRefOldParams fileBindBusinessRefOldParams);

    /**
     * 4. 根据业务id删除附件（支持删除单个或者多个）
     *
     * @param fileBindBusinessParams 文件业务绑定参数
     */
    void deleteFileByBusinessInfo(FileBindBusinessParams fileBindBusinessParams);

    /**
     * 5. 删除过期没有关联的临时文件
     */
    void deleteTempFile();


    /**
     * 6. 获取文件信息
     *
     * @param fileBindBusinessParams 业务信息
     * @return 文件列表
     */
    List<FileInfo> findFileInfoByBusiness(FileBindBusinessParams fileBindBusinessParams);


    /**
     * 解析文件,获取文件内容，支持excel
     *
     * @param fileBindBusinessParams 要解析的文件信息，文件id必须并且只能是一个
     * @return 文件内容
     */
    Object parseFile(FileBindBusinessParams fileBindBusinessParams);
}
