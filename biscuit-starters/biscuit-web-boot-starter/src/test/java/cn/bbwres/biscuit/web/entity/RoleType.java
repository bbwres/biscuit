package cn.bbwres.biscuit.web.entity;

import cn.bbwres.biscuit.enums.BaseEnum;

public enum RoleType implements BaseEnum<String> {
    USER("1","用户"),
    POST("2","岗位"),

    ;

    private final String value;
    private final String displayName;

    RoleType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * 枚举value
     *
     * @return 枚举value
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 枚举的显示名字
     *
     * @return 枚举的显示名字
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }
}
