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

package cn.bbwres.biscuit.enums;

/**
 * 通用的数据状态枚举
 *
 * @author zhanglinfeng
 */

public enum DataStatusEnum implements BaseEnum<Integer> {
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    /**
     * 禁用
     */
    DISABLED(0, "禁用"),
    ;

    DataStatusEnum(Integer value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    private final Integer value;

    private final String displayName;

    /**
     * 枚举value
     *
     * @return 枚举value
     */
    @Override
    public Integer getValue() {
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
