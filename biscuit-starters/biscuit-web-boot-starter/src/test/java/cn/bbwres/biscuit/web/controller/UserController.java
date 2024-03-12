package cn.bbwres.biscuit.web.controller;

import cn.bbwres.biscuit.web.entity.Role;
import cn.bbwres.biscuit.web.entity.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "用户管理")
@RestController
public class UserController {


    @Operation(summary = "用户管理")
    @ApiResponse(description = "用户信息")
    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @Operation(summary = "用户角色管理")
    @ApiResponse(description = "角色信息")
    @GetMapping("/userRole")
    public Role userRole(Role role1) {
        Role role = new Role();
        role.setRoleType(RoleType.USER);
        return role;
    }


}
