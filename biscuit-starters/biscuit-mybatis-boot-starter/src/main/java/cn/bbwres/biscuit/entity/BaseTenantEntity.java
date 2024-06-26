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

package cn.bbwres.biscuit.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 租户基础类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class BaseTenantEntity extends BaseEntity {

    private static final long serialVersionUID = -6563481654761263760L;
    /**
     * 租户id
     */
    @TableField(value = "tenant_id", fill = FieldFill.INSERT)
    private String tenantId;


    /**
     * <p>Getter for the field <code>tenantId</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * <p>Setter for the field <code>tenantId</code>.</p>
     *
     * @param tenantId a {@link java.lang.String} object
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
