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

package cn.bbwres.biscuit.web.file.service;

import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.web.file.controller.vo.FileInfoVO;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;

import java.util.List;

/**
 * 文件上传与下载的服务
 *
 * @author zhanglinfeng
 */
public interface FileUploadAndDownloadService {


    /**
     * 保存临时文件和记录
     *
     * @param fileInfo 文件信息
     * @return 文件信息
     */
    TempFileInfo saveTempFileInfo(TempFileInfo fileInfo);


    /**
     * 根据
     *
     * @param businessType
     * @param businessId
     * @param fileId  文件id
     * @return
     */
    List<FileInfo> findByBusinessAndId(String businessType, String businessId, String... fileId);

    /**
     * 检查当前用户对当前业务文件的权限
     *
     * @param businessType 业务类型
     * @param businessId   业务id
     * @param requestUser  请求用户
     * @param fileId       文件id 为空时不校验文件
     * @return true  有权限，false 无权限
     */
    boolean checkFilePermission(String businessType, String businessId, UserBaseInfo requestUser, String... fileId);

    /**
     * 根据业务类型和业务id查询出关联的附件列表信息
     * @param businessType
     * @param businessId
     * @return
     */
    List<FileInfoVO> findByBusiness(String businessType, String businessId);

}
