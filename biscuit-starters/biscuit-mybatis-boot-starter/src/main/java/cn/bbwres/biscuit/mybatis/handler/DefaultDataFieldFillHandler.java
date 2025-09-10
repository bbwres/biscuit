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

package cn.bbwres.biscuit.mybatis.handler;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.entity.BaseTenantEntity;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.mybatis.config.MybatisProperties;
import cn.bbwres.biscuit.mybatis.config.MybatisTenantProperties;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/**
 * 默认数据填充处理器
 *
 * @author zhanglinfeng
 */
public class DefaultDataFieldFillHandler implements MetaObjectHandler {

    private final MybatisProperties mybatisProperties;
    private final MybatisTenantProperties mybatisTenantProperties;

    public DefaultDataFieldFillHandler(MybatisProperties mybatisProperties,
                                       MybatisTenantProperties mybatisTenantProperties) {
        this.mybatisProperties = mybatisProperties;
        this.mybatisTenantProperties = mybatisTenantProperties;
    }

    private static final String UPDATE_TIME = "updateTime";
    private static final String CREATE_TIME = "createTime";
    private static final String UPDATER = "updater";
    private static final String CREATOR = "creator";
    private static final String TENANT_ID = "tenantId";

    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        if (metaObject.getOriginalObject() instanceof BaseEntity) {
            strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, LocalDateTime.now());
            strictInsertFill(metaObject, CREATOR, () -> mybatisProperties.obtainUserInfo(UserBaseInfo::getUserId), String.class);
        }
        if (mybatisTenantProperties.isEnabled() && metaObject.getOriginalObject() instanceof BaseTenantEntity) {
            //获取租户id
            strictInsertFill(metaObject, TENANT_ID, () -> mybatisProperties.obtainUserInfo(userBaseInfo -> ObjectUtils.isEmpty(userBaseInfo.getTenantId()) ?
                    mybatisTenantProperties.getDefaultTenant() : userBaseInfo.getTenantId()), String.class);
        }

    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.getOriginalObject() instanceof BaseEntity) {
            strictUpdateFill(metaObject, UPDATE_TIME, LocalDateTime.class, LocalDateTime.now());
            strictUpdateFill(metaObject, UPDATER, () -> mybatisProperties.obtainUserInfo(UserBaseInfo::getUserId), String.class);
        }

    }


}
