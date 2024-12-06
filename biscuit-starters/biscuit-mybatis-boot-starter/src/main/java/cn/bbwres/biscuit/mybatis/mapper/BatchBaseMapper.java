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

package cn.bbwres.biscuit.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * 增加了批量插入的baseMapper
 *
 * @author zhanglinfeng
 */
public interface BatchBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入数据. 部分数据库不支持
     *
     * @param entityList
     * @return
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);

    /**
     * 分批插入数据,避免sql过长。
     * 部分数据库不支持
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    default Integer insertBatchSomeColumnSplit(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return 0;
        }
        List<List<T>> splits = CollectionUtils.split(entityList, batchSize);
        int resultNum = 0;
        for (List<T> split : splits) {
            resultNum += insertBatchSomeColumn(split);
        }
        return resultNum;
    }

}
