package cn.bbwres.biscuit.web.controller;

import cn.bbwres.biscuit.exception.ParamsCheckRuntimeException;
import cn.bbwres.biscuit.web.entity.Role;
import cn.bbwres.biscuit.web.entity.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "用户管理")
@RestController
public class UserController {

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

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


    @Operation(summary = "用户管理异常信息")
    @ApiResponse(description = "用户信息")
    @GetMapping("/userEx")
    public String userEx() {
        throw new ParamsCheckRuntimeException("111", "参数错误");
    }

    @Operation(summary = "i18n")
    @ApiResponse(description = "i18n")
    @GetMapping("/i18n")
    public String i18n() {
        return messageSource.getMessage("111", null, "不知道填写啥",LocaleContextHolder.getLocale());
    }


}
