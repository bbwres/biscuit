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

import java.io.Serial;

/**
 * 包含创建人姓名和更新人姓名的实体
 *
 * @author zhanglinfeng
 */
public class BaseNameEntity extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -2162625487235707805L;

    /**
     * 创建人姓名
     */
    @TableField(value = "creator_name", fill = FieldFill.INSERT)
    private String creatorName;

    /**
     * 更新人姓名
     */
    @TableField(value = "updater_name", fill = FieldFill.UPDATE)
    private String updaterName;


    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getUpdaterName() {
        return updaterName;
    }

    public void setUpdaterName(String updaterName) {
        this.updaterName = updaterName;
    }
}
