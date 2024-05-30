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
package cn.bbwres.biscuit.security.oauth2.vo;

import org.springframework.security.core.GrantedAuthority;


/**
 * 系统权限信息
 *
 * @author zhanglinfeng
 */
public class MyGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 8611348088697699904L;
    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色id
     */
    private String roleId;


    @Override
    public String getAuthority() {
        return roleId;
    }


    public String getRoleCode() {
        return roleCode;
    }

    public MyGrantedAuthority setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public String getRoleId() {
        return roleId;
    }

    public MyGrantedAuthority setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }
}
