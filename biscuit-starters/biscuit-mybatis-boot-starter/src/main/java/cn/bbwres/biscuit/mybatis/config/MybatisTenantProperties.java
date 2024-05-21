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

package cn.bbwres.biscuit.mybatis.config;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

/**
 * 租户配置 属性
 *
 * @author zhanglinfeng
 */
@ConfigurationProperties(prefix = Constants.MYBATIS_PLUS + ".tenant")
public class MybatisTenantProperties implements Serializable {
    private static final long serialVersionUID = 6030955700286744649L;

    private boolean enabled = false;


    /**
     * 忽略租户配置的表
     */
    private List<String> ignoreTenantTables;


    /**
     * 默认租户信息
     */
    private String defaultTenant;

    /**
     * 租户id的数据库表字段
     */
    private String tenantIdColumn;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getIgnoreTenantTables() {
        return ignoreTenantTables;
    }

    public void setIgnoreTenantTables(List<String> ignoreTenantTables) {
        this.ignoreTenantTables = ignoreTenantTables;
    }

    public String getDefaultTenant() {
        return defaultTenant;
    }

    public void setDefaultTenant(String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }
}
