package cn.bbwres.biscuit.entity;

import java.io.Serializable;

/**
 * 用户基础信息对象
 *
 * @author zhanglinfeng
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


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public T getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(T userInfo) {
        this.userInfo = userInfo;
    }
}
