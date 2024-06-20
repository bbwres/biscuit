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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 375884071908914508L;
    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    /**
     * 创建人
     */
    @TableField(value = "creator",fill = FieldFill.INSERT)
    private String creator;

    /**
     * 更新人
     */
    @TableField(value = "updater",fill = FieldFill.UPDATE)
    private String updater;


    /**
     * <p>Getter for the field <code>createTime</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * <p>Setter for the field <code>createTime</code>.</p>
     *
     * @param createTime a {@link java.time.LocalDateTime} object
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * <p>Getter for the field <code>updateTime</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * <p>Setter for the field <code>updateTime</code>.</p>
     *
     * @param updateTime a {@link java.time.LocalDateTime} object
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * <p>Getter for the field <code>creator</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCreator() {
        return creator;
    }

    /**
     * <p>Setter for the field <code>creator</code>.</p>
     *
     * @param creator a {@link java.lang.String} object
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * <p>Getter for the field <code>updater</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * <p>Setter for the field <code>updater</code>.</p>
     *
     * @param updater a {@link java.lang.String} object
     */
    public void setUpdater(String updater) {
        this.updater = updater;
    }

}
