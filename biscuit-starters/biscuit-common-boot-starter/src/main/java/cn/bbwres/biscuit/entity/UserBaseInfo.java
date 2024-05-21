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

import java.io.Serializable;

/**
 * 用户基础信息对象
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class UserBaseInfo<T> implements Serializable {

    private static final long serialVersionUID = 2330611822113249402L;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String username;


    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 用户信息
     */
    private T userInfo;


    /**
     * <p>Getter for the field <code>userId</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUserId() {
        return userId;
    }

    /**
     * <p>Setter for the field <code>userId</code>.</p>
     *
     * @param userId a {@link java.lang.String} object
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * <p>Getter for the field <code>username</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUsername() {
        return username;
    }

    /**
     * <p>Setter for the field <code>username</code>.</p>
     *
     * @param username a {@link java.lang.String} object
     */
    public void setUsername(String username) {
        this.username = username;
    }

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

    /**
     * <p>Getter for the field <code>userInfo</code>.</p>
     *
     * @return a T object
     */
    public T getUserInfo() {
        return userInfo;
    }

    /**
     * <p>Setter for the field <code>userInfo</code>.</p>
     *
     * @param userInfo a T object
     */
    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }
}
