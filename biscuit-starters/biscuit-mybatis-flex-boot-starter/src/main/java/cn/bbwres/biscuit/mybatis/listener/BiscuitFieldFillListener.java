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

package cn.bbwres.biscuit.mybatis.listener;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.entity.BaseTenantEntity;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.mybatis.config.MybatisProperties;
import cn.bbwres.biscuit.mybatis.config.MybatisTenantProperties;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import org.slf4j.Logger;

import java.time.LocalDateTime;

/**
 * 字段填充监听器
 * 需要注意的是：onInsert 监听中，通过 mybatis 的 xml mapper 插入数据，
 * 或者通过 Db + Row 中插入数据，并不会触发 onInsert 行为，
 * 只有通过 xxxxxMapper 进行插入数据才会触发。
 *
 * @author zhanglinfeng
 */

public class BiscuitFieldFillListener implements InsertListener, UpdateListener {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BiscuitFieldFillListener.class);

    private final MybatisProperties mybatisProperties;
    private final MybatisTenantProperties mybatisTenantProperties;

    public BiscuitFieldFillListener(MybatisProperties mybatisProperties, MybatisTenantProperties mybatisTenantProperties) {
        this.mybatisProperties = mybatisProperties;
        this.mybatisTenantProperties = mybatisTenantProperties;
    }


    /**
     * 插入数据 时自动填充
     *
     * @param entity
     */
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setCreateTime(LocalDateTime.now());
            baseEntity.setCreator(mybatisProperties.obtainUserInfo(UserBaseInfo::getUserId));
            baseEntity.setCreatorName(mybatisProperties.obtainUserInfo(UserBaseInfo::getZhName));
        }

        if (entity instanceof BaseTenantEntity baseTenantEntity) {
            baseTenantEntity.setTenantId(mybatisProperties.obtainUserInfo(UserBaseInfo::getTenantId));
        }


    }

    /**
     * 更新数据 时自动填充
     *
     * @param entity
     */
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setUpdateTime(LocalDateTime.now());
            baseEntity.setUpdater(mybatisProperties.obtainUserInfo(UserBaseInfo::getUserId));
            baseEntity.setUpdaterName(mybatisProperties.obtainUserInfo(UserBaseInfo::getZhName));
        }
    }
}
