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

package cn.bbwres.biscuit.mongodb.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.mongodb.dao.ExtendMongoRepository;

import java.util.Collection;

/**
 * 扩展的mongodb操作服务
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public interface ExtendMongoService<T, Q, ID, D extends ExtendMongoRepository<T, ID>> {


    /**
     * 获取操作类
     *
     * @return a D object
     */
    D getMongodbDao();


    /**
     * 批量插入
     *
     * @param objectsToSave a {@link java.util.Collection} object
     * @return a {@link java.util.Collection} object
     */
    Collection<T> insertAll(Collection<? extends T> objectsToSave);

    /**
     * 保存单条数据
     *
     * @param entity a T object
     * @return a T object
     */
    T insert(T entity);

    /**
     * 保存数据,如果不存在则插入，如果存在则更新
     *
     * @param entity a T object
     * @return a T object
     */
    T insertOrUpdate(T entity);


    /**
     * 根据id查询
     *
     * @param id a ID object
     * @return a T object
     */
    T findById(ID id);


    /**
     * 根据id删除
     *
     * @param id a ID object
     */
    void deleteById(ID id);


    /**
     * 分页查询
     *
     * @param page a {@link cn.bbwres.biscuit.dto.Page} object
     * @return a {@link cn.bbwres.biscuit.dto.Page} object
     */
    Page<T, Q> pageList(Page<T, Q> page);


}
