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

package cn.bbwres.biscuit.utils.desensitize;

import cn.bbwres.biscuit.utils.desensitize.annotation.Desensitize;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;

/**
 * 序列化时脱敏数据
 *
 * @author zhanglinfeng
 */
public class DesensitizeSerializer extends StdSerializer<Object> implements ContextualSerializer {
    @Serial
    private static final long serialVersionUID = -8184665584108286739L;

    private transient Desensitization<Object> desensitization;
    private transient int prefixLen;
    private transient int suffixLen;

    protected DesensitizeSerializer() {
        super(Object.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonSerializer<Object> createContextual(SerializerProvider prov, BeanProperty property) {
        //获取属性注解
        Desensitize annotation = property.getAnnotation(Desensitize.class);
        Class<? extends Desensitization<?>> clazz = annotation.desensitization();
        DesensitizeSerializer serializer = new DesensitizeSerializer();
        if (clazz != DefaultDesensitization.class) {
            serializer.setDesensitization((Desensitization<Object>) DesensitizationFactory.getDesensitization(clazz));
            serializer.setPrefixLen(annotation.prefixLen());
            serializer.setSuffixLen(annotation.suffixLen());
        }
        return serializer;
    }


    /**
     * 序列化处理
     *
     * @param value
     * @param gen
     * @param provider
     * @throws IOException
     */
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Desensitization<Object> objectDesensitization = getDesensitization();
        if (objectDesensitization != null) {
            try {
                gen.writeObject(objectDesensitization.desensitize(value, this.getPrefixLen(), this.getSuffixLen()));
            } catch (Exception e) {
                gen.writeObject(value);
            }
        } else {
            gen.writeObject(value);
        }
    }


    public Desensitization<Object> getDesensitization() {
        return desensitization;
    }

    public void setDesensitization(Desensitization<Object> desensitization) {
        this.desensitization = desensitization;
    }

    public int getPrefixLen() {
        return prefixLen;
    }

    public void setPrefixLen(int prefixLen) {
        this.prefixLen = prefixLen;
    }

    public int getSuffixLen() {
        return suffixLen;
    }

    public void setSuffixLen(int suffixLen) {
        this.suffixLen = suffixLen;
    }

}
