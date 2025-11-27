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

package cn.bbwres.biscuit.utils.desensitize.annotation;

import cn.bbwres.biscuit.utils.desensitize.DefaultDesensitization;
import cn.bbwres.biscuit.utils.desensitize.Desensitization;
import cn.bbwres.biscuit.utils.desensitize.DesensitizeSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * json脱敏注解
 *
 * @author zhanglinfeng
 */
@Documented
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
//jackson 注解的元注解
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizeSerializer.class)
public @interface Desensitize {

    /**
     * 设置脱敏器类型
     *
     * @return
     */
    Class<? extends Desensitization<?>> desensitization() default DefaultDesensitization.class;

    /**
     * 前面保留的长度
     *
     * @return
     */
    int prefixLen() default -1;

    /**
     * 后面保留的长
     *
     * @return
     */
    int suffixLen() default -1;

}
