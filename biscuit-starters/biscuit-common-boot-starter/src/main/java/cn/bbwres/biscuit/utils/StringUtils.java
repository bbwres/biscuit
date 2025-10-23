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

package cn.bbwres.biscuit.utils;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串工具
 *
 * @author zhanglinfeng
 */
public class StringUtils {

    /**
     * 数组分隔符
     */
    public static final String ARRAY_SPLIT = ",";


    /**
     * 数组字符串转换为list
     *
     * @param arrayStr 数组字符串
     * @return list
     */
    public static List<String> arrayStr2List(String arrayStr) {
        if (ObjectUtils.isEmpty(arrayStr)) {
            return new ArrayList<>(0);
        }
        return Arrays.asList(arrayStr.split(ARRAY_SPLIT));
    }


    /**
     * list 转数组字符串
     *
     * @return String
     */
    public static String list2ArrayStr(List<String> stringList) {
        if (CollectionUtils.isEmpty(stringList)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : stringList) {
            stringBuilder.append(item).append(ARRAY_SPLIT);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

}
