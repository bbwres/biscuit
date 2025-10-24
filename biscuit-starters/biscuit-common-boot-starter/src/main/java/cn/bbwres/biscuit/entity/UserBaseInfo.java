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

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户基础信息对象
 *
 * @author zhanglinfeng
 * @version $Id: $Id
 */
public class UserBaseInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2330611822113249402L;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户中文名称
     */
    private String zhName;

    /**
     * 用户名称
     */
    private String username;


    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 用户信息
     */
    private Object userInfo;

    /**
     * 用户权限信息
     */
    private List<String> authorities;


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
    public Object getUserInfo() {
        return userInfo;
    }

    /**
     * <p>Setter for the field <code>userInfo</code>.</p>
     *
     * @param userInfo a T object
     */
    public void setUserInfo(Object userInfo) {
        this.userInfo = userInfo;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
