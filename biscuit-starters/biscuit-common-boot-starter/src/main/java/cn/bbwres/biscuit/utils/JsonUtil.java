package cn.bbwres.biscuit.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * json 工具类
 *
 * @author zhanglinfeng
 */
public class JsonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));


        OBJECT_MAPPER = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT))
                .registerModule(new JavaTimeModule());
        //设置不序列化为空的字段
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //反序列化未知字段不报错
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化未知字段不报错
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }


    /**
     * 对象转json
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String toJson(T t) {
        try {
            return OBJECT_MAPPER.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            LOG.info("toJson error!,{}", e.getMessage());
        }
        return null;
    }

    /**
     * 对象转换json之后转换base64
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String toJsonBase64(T t) {
        String json = toJson(t);
        if (ObjectUtils.isEmpty(json)) {
            return null;
        }
        return Base64Utils.encodeToUrlSafeString(json.getBytes());
    }

    /**
     * json  转对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOG.info("json  to Object error, {}", e.getMessage());
        }
        return null;
    }


    /**
     * 将base64的json字符串转换为对象
     *
     * @param base64Json base64字符串
     * @param clazz      对象类型
     * @param <T>        类型
     * @return
     */
    public static <T> T toObjectByBase64Json(String base64Json, Class<T> clazz) {
        String json = new String(Base64Utils.decodeFromUrlSafeString(base64Json), StandardCharsets.UTF_8);
        return toObject(json, clazz);
    }


    /**
     * 转换对象
     *
     * @param json
     * @param valueTypeRef
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (JsonProcessingException e) {
            LOG.info("json  to Object error, {}", e.getMessage());
        }
        return null;
    }

    /**
     * 对象转换为map
     *
     * @param obj
     * @return
     */
    public static Map<String, ?> object2Map(Object obj) {
        Map<String, ?> params = null;
        try {
            params = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(obj), new TypeReference<>() {
            });
        } catch (Exception e) {
            LOG.warn("object  to Map error, {}", e.getMessage());
        }
        return params;
    }


    /**
     * 对象转换为map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> jsonString2MapObj(String jsonStr) {
        Map<String, Object> params = null;
        try {
            params = OBJECT_MAPPER.readValue(jsonStr, new TypeReference<>() {
            });
        } catch (Exception e) {
            LOG.warn("json  to Map error, {}", e.getMessage());
        }
        return params;
    }


}
