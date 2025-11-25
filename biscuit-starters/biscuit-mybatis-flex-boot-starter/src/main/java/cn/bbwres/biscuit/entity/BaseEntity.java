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

import com.mybatisflex.annotation.Column;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 375884071908914508L;
    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @Column(value = "update_time")
    private LocalDateTime updateTime;
    /**
     * 创建人
     */
    @Column(value = "creator")
    private String creator;

    /**
     * 创建人姓名
     */
    @Column(value = "creator_name")
    private String creatorName;

    /**
     * 更新人
     */
    @Column(value = "updater")
    private String updater;

    /**
     * 更新人姓名
     */
    @Column(value = "updater_name")
    private String updaterName;


    /**
     * <p>Getter for the field <code>createTime</code>.</p>
     *
     * @return a {@link LocalDateTime} object
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * <p>Setter for the field <code>createTime</code>.</p>
     *
     * @param createTime a {@link LocalDateTime} object
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * <p>Getter for the field <code>updateTime</code>.</p>
     *
     * @return a {@link LocalDateTime} object
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * <p>Setter for the field <code>updateTime</code>.</p>
     *
     * @param updateTime a {@link LocalDateTime} object
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * <p>Getter for the field <code>creator</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getCreator() {
        return creator;
    }

    /**
     * <p>Setter for the field <code>creator</code>.</p>
     *
     * @param creator a {@link String} object
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * <p>Getter for the field <code>updater</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getUpdater() {
        return updater;
    }

    /**
     * <p>Setter for the field <code>updater</code>.</p>
     *
     * @param updater a {@link String} object
     */
    public void setUpdater(String updater) {
        this.updater = updater;
    }


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
