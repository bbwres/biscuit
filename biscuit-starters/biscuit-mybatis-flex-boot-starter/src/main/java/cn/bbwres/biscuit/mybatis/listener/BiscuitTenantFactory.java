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

import cn.bbwres.biscuit.mybatis.config.MybatisProperties;
import cn.bbwres.biscuit.mybatis.config.MybatisTenantProperties;
import com.mybatisflex.core.tenant.TenantFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * 租户配置
 *
 * @author zhanglinfeng
 */
public class BiscuitTenantFactory implements TenantFactory {
    private final static Logger log = LoggerFactory.getLogger(BiscuitTenantFactory.class);

    private final MybatisTenantProperties mybatisTenantProperties;

    private final MybatisProperties mybatisProperties;

    public BiscuitTenantFactory(MybatisTenantProperties mybatisTenantProperties, MybatisProperties mybatisProperties) {
        this.mybatisTenantProperties = mybatisTenantProperties;
        this.mybatisProperties = mybatisProperties;
    }

    /**
     * @deprecated
     */
    @Override
    public Object[] getTenantIds() {
        return null;
    }


    @Override
    public Object[] getTenantIds(String tableName) {

        if ((!CollectionUtils.isEmpty(mybatisTenantProperties.getIgnoreTenantTables()))
                && mybatisTenantProperties.getIgnoreTenantTables().contains(tableName)) {
            return null;
        }
        String tenantId = mybatisProperties.obtainUserInfo(userBaseInfo -> ObjectUtils.isEmpty(userBaseInfo.getTenantId()) ?
                mybatisTenantProperties.getDefaultTenant() : userBaseInfo.getTenantId());
        log.debug("obtain tenantId:{}", tenantId);
        if (ObjectUtils.isEmpty(tenantId)) {
            return null;
        }

        return new Object[]{tenantId};
    }
}
