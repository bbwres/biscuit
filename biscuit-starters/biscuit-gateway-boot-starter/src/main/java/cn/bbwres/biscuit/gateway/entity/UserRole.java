package cn.bbwres.biscuit.gateway.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户角色信息
 *
 * @author zhanglinfeng12
 */
@Accessors(chain = true)
@Data
public class UserRole {

    public UserRole() {
    }

    public UserRole(String clientId, String roleCode, String tenantId) {
        this.clientId = clientId;
        this.roleCode = roleCode;
        this.tenantId = tenantId;
    }

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 租户id
     */
    private String tenantId;
}
