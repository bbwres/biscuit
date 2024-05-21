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

package cn.bbwres.biscuit.mongodb.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.SortInfo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

/**
 * 扩展的MongoRepository
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
@NoRepositoryBean
public interface ExtendMongoRepository<T, ID> extends MongoRepository<T, ID> {


    /**
     * 批量插入
     *
     * @param objectsToSave a {@link java.util.Collection} object
     * @return a {@link java.util.Collection} object
     */
    Collection<T> insertAll(Collection<? extends T> objectsToSave);


    /**
     * 根据查询条件查询一条记录
     *
     * @param query a {@link org.springframework.data.mongodb.core.query.Criteria} object
     * @return a T object
     */
    T findOne(Criteria query);


    /**
     * 查询多条数据
     *
     * @param query a {@link org.springframework.data.mongodb.core.query.Query} object
     * @return a {@link java.util.List} object
     */
    List<T> find(Query query);


    /**
     * 根据条件删除
     *
     * @param query a {@link org.springframework.data.mongodb.core.query.Query} object
     * @return a {@link com.mongodb.client.result.DeleteResult} object
     */
    DeleteResult delete(Query query);

    /**
     * 更新数据
     *
     * @param update a {@link org.springframework.data.mongodb.core.query.Update} object
     * @param query a {@link org.springframework.data.mongodb.core.query.Query} object
     * @return a {@link com.mongodb.client.result.UpdateResult} object
     */
    UpdateResult update(Update update, Query query);


    /**
     * 分页查询
     *
     * @param criteria a {@link java.util.List} object
     * @param pageSize a int
     * @param current a long
     * @param sortInfos 排序
     * @param <Q> a Q class
     * @return a {@link cn.bbwres.biscuit.dto.Page} object
     */
    <Q> Page<T, Q> pageList(List<CriteriaDefinition> criteria, int pageSize, long current, List<SortInfo> sortInfos);

    /**
     * 获取总条数
     *
     * @param query a {@link org.springframework.data.mongodb.core.query.Query} object
     * @return a {@link java.lang.Long} object
     */
    Long count(Query query);


}
