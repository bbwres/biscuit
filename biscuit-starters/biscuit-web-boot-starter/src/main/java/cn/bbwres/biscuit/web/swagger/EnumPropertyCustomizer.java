package cn.bbwres.biscuit.web.swagger;

import cn.bbwres.biscuit.enums.BaseEnum;
import com.fasterxml.jackson.databind.type.SimpleType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举属性自定义
 *
 * @author zhanglinfeng
 */
public class EnumPropertyCustomizer implements PropertyCustomizer {


    /**
     * 扩展枚举参数
     *
     * @param property
     * @param type
     * @return
     */
    @Override
    public Schema<?> customize(Schema property, AnnotatedType type) {
        // 检查实例并转换
        if (type.getType() instanceof SimpleType) {
            SimpleType fieldType = (SimpleType) type.getType();
            // 获取字段class
            Class<?> fieldClazz = fieldType.getRawClass();
            // 是否是枚举
            if (BaseEnum.class.isAssignableFrom(fieldClazz)) {
                // 获取父接口
                if (fieldClazz.getGenericInterfaces()[0] instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) fieldClazz.getGenericInterfaces()[0];
                    // 通过父接口获取泛型中枚举值的class类型
                    Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                    Schema<?> schema = getSchemaByType(actualTypeArgument, property);

                    // 获取字段注释
                    String description = this.getDescription(fieldClazz);


                    // 重置字段注释和标题为从枚举中提取的
                    if (ObjectUtils.isEmpty(property.getTitle())) {
                        schema.setTitle(description);
                    } else {
                        schema.setTitle(property.getTitle() + ":" + description);
                    }
                    if (ObjectUtils.isEmpty(property.getDescription())) {
                        schema.setDescription(description);
                    } else {
                        schema.setDescription(property.getDescription() + ":" + description);
                    }
                    return schema;
                }
            }
        }
        return property;
    }

    private String getDescription(Class<?> fieldClazz) {
        BaseEnum<?>[] values = (BaseEnum<?>[]) fieldClazz.getEnumConstants();
        List<String> displayValues = Arrays.stream(values)
                .map(codedEnum -> {
                    if (codedEnum instanceof Enum) {
                        return ((Enum<?>) codedEnum).name() + " - " + codedEnum.getDisplayName();
                    }
                    return codedEnum.getValue() + " - " + codedEnum.getDisplayName();
                })
                .collect(Collectors.toList());
        StringBuilder str = new StringBuilder();
        if (!CollectionUtils.isEmpty(displayValues)) {
            str.append(displayValues.get(0));
            for (int i = 1; i < displayValues.size(); i++) {
                str.append(",").append(displayValues.get(i));
            }
        }
        return str.toString();
    }


    private Schema<?> getSchemaByType(Type type, Schema<?> sourceSchema) {
        Schema<?> schema;
        PrimitiveType item = PrimitiveType.fromType(type);

        if (item == null) {
            schema = new ObjectSchema();
        } else {
            schema = item.createProperty();
        }

        // 获取schema的type和format
        String schemaType = schema.getType();
        String format = schema.getFormat();
        // 复制原schema的其它属性
        BeanUtils.copyProperties(sourceSchema, schema);

        // 使用根据枚举值类型获取到的schema
        return schema.type(schemaType).format(format);
    }


}
