package cn.bbwres.biscuit.mybatis.handler;

import cn.bbwres.biscuit.entity.UserBaseInfo;

import java.util.function.Supplier;

public class UserSupplier implements Supplier<UserBaseInfo> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public UserBaseInfo get() {
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        userBaseInfo.setUserId("1");
        userBaseInfo.setUsername("张三");
        userBaseInfo.setTenantId("1");
        return userBaseInfo;
    }
}
