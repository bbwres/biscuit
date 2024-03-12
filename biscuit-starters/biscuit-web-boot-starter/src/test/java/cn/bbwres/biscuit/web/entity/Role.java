package cn.bbwres.biscuit.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "角色信息")
public class Role implements Serializable {
    private static final long serialVersionUID = -1177562663203381113L;

    @Schema(description = "角色类型")
    private RoleType roleType;


    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
