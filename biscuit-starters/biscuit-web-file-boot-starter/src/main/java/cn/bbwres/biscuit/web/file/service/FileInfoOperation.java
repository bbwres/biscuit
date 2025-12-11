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
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.endpoint.vo.FileInfoParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息操作接口
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public interface FileInfoOperation {


    /**
     * 保存临时文件和记录
     *
     * @param fileInfo 文件信息
     * @return 文件信息
     */
    TempFileInfo saveTempFileInfo(TempFileInfo fileInfo);


    /**
     * 根据 文件业务类型和业务id 以及文件id查询文件列表
     *
     * @param businessType a {@link java.lang.String} object
     * @param businessId a {@link java.lang.String} object
     * @param fileId       文件id
     * @return a {@link java.util.List} object
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
     *
     * @param businessType a {@link java.lang.String} object
     * @param businessId a {@link java.lang.String} object
     * @return a {@link java.util.List} object
     */
    List<FileInfoParams> findByBusiness(String businessType, String businessId);

    /**
     * 根据文件hash查询临时文件中是否存在
     *
     * @param fileHash a {@link java.lang.String} object
     * @return a {@link cn.bbwres.biscuit.web.file.entity.FileInfo} object
     */
    FileInfo findByFileHashOne(String fileHash);

    /**
     * 根据ID查询出临时文件
     *
     * @param fileIds a {@link java.util.List} object
     * @return a {@link java.util.List} object
     */
    List<TempFileInfo> findTempFileInfoByIds(List<String> fileIds);

    /**
     * 文件绑定业务信息
     * 1.先删除临时文件信息
     * 2.再保存文件信息
     *
     * @param fileBindBusinessExpandParams 请求参数信息
     * @param fileInfos                    文件信息
     */
    void fileBindBusiness(FileBindBusinessExpandParams fileBindBusinessExpandParams, List<FileInfo> fileInfos);

    /**
     * 更新文件信息
     *
     * @param fileInfos a {@link java.util.List} object
     */
    void updateFileInfo(List<FileInfo> fileInfos);

    /**
     * 保存文件信息
     *
     * @param fileInfos a {@link java.util.List} object
     */
    void saveFileInfo(List<FileInfo> fileInfos);

    /**
     * 根据文件id删除文件信息
     *
     * @param list a {@link java.util.List} object
     */
    void deleteByFileIds(List<String> list);

    /**
     * 查询在指定时间也没有业务关联的临时文件信息
     *
     * @param gtData a {@link java.time.LocalDateTime} object
     * @return a {@link java.util.List} object
     */
    List<TempFileInfo> findByNoBusiness(LocalDateTime gtData);

    /**
     * 根据临时文件信息，删除临时文件
     *
     * @param noBusinessList a {@link java.util.List} object
     */
    void deleteTempFileInfo(List<TempFileInfo> noBusinessList);

}
