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

package cn.bbwres.biscuit.mybatis.config;

import cn.bbwres.biscuit.entity.UserBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * mybatis 配置
 *
 * @author zhanglinfeng
 */
@ConfigurationProperties(prefix = Constants.MYBATIS_PLUS)
public class MybatisProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 获取用户信息
     */
    private Class<Supplier<UserBaseInfo<?>>> userInfo;

    /**
     * mappers 扫描包配置
     */
    private MapperBasePackage mapper;


    private Supplier<UserBaseInfo<?>> userInfoSupplier;

    public Class<Supplier<UserBaseInfo<?>>> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Class<Supplier<UserBaseInfo<?>>> userInfo) {
        this.userInfo = userInfo;
        if (ObjectUtils.isEmpty(userInfo)) {
            userInfoSupplier = null;
            return;
        }
        try {
            userInfoSupplier = userInfo.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            userInfoSupplier = null;
        }
    }

    public Supplier<UserBaseInfo<?>> getUserInfoSupplier() {
        return userInfoSupplier;
    }

    public MapperBasePackage getMapper() {
        return mapper;
    }

    public void setMapper(MapperBasePackage mapper) {
        this.mapper = mapper;
    }


    /**
     * 获取当前用户信息
     *
     * @return
     */
    public String obtainUserInfo(Function<UserBaseInfo<?>, String> function) {
        if (ObjectUtils.isEmpty(userInfoSupplier)) {
            return null;
        }
        UserBaseInfo<?> userBaseInfo = userInfoSupplier.get();
        if (Objects.isNull(userBaseInfo)) {
            return null;
        }
        return function.apply(userBaseInfo);

    }

}
