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

package cn.bbwres.biscuit.web.supplier;

import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;

import java.util.function.Supplier;

/**
 * 获取当前登录用户的 Supplier
 * 主要用于数据库的自动填充
 *
 * @author zhanglinfeng
 */
public class UserInfoSupplier implements Supplier<UserBaseInfo<?>> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public UserBaseInfo<?> get() {
        return WebFrameworkUtils.getRequestUser(true);
    }
}
