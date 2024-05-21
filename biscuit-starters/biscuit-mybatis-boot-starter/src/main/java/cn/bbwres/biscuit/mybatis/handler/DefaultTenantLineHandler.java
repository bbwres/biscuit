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

import cn.bbwres.biscuit.mybatis.config.MybatisProperties;
import cn.bbwres.biscuit.mybatis.config.MybatisTenantProperties;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author zhanglinfeng
 */
public class DefaultTenantLineHandler implements TenantLineHandler {

    private final static Logger log = LoggerFactory.getLogger(DefaultTenantLineHandler.class);

    private final MybatisTenantProperties mybatisTenantProperties;

    private final MybatisProperties mybatisProperties;

    public DefaultTenantLineHandler(MybatisTenantProperties mybatisTenantProperties,
                                    MybatisProperties mybatisProperties) {
        this.mybatisTenantProperties = mybatisTenantProperties;
        this.mybatisProperties = mybatisProperties;
    }

    /**
     * 获取租户 ID 值表达式，只支持单个 ID 值
     * <p>
     *
     * @return 租户 ID 值表达式
     */
    @Override
    public Expression getTenantId() {
        String tenantId = mybatisProperties.obtainUserInfo(userBaseInfo -> ObjectUtils.isEmpty(userBaseInfo.getTenantId()) ?
                mybatisTenantProperties.getDefaultTenant() : userBaseInfo.getTenantId());
        log.debug("obtain tenantId:{}", tenantId);

        return new StringValue(tenantId);
    }

    /**
     * 获取租户字段名
     * <p>
     * 默认字段名叫: tenant_id
     *
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        String tenantIdColumn = mybatisTenantProperties.getTenantIdColumn();
        if (ObjectUtils.isEmpty(tenantIdColumn)) {
            return TenantLineHandler.super.getTenantIdColumn();
        }
        return tenantIdColumn;
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     * <p>
     * 默认都要进行解析并拼接多租户条件
     *
     * @param tableName 表名
     * @return 是否忽略, true:表示忽略，false:需要解析并拼接多租户条件
     */
    @Override
    public boolean ignoreTable(String tableName) {
        if ((!CollectionUtils.isEmpty(mybatisTenantProperties.getIgnoreTenantTables()))
                && mybatisTenantProperties.getIgnoreTenantTables().contains(tableName)) {
            return true;
        }
        return TenantLineHandler.super.ignoreTable(tableName);
    }

}
