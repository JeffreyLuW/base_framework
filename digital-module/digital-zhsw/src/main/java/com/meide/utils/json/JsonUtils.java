package com.meide.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonUtils {

    public static ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().createXmlMapper(false).build();
        // 通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
        // Include.Include.ALWAYS 默认
        // Include.NON_DEFAULT 属性为默认值不序列化
        // Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
        // Include.NON_NULL 属性为NULL 不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许出现单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        ScriptObjectMirrorSerializer.register(objectMapper);
        PageSerializer.register(objectMapper);
        return objectMapper;
    }

    public static String toJson(Object object, boolean pretty) {
        if (null == object) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        try {
            if (pretty) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> ArrayList<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, beanType);
        try {
            ArrayList<T> list = objectMapper.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T jsonToBean(String jsonData, Class<T> beanType) {
        try {
           return objectMapper.readValue(jsonData, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Object> T fileToObject(File file, Class<T> beanType) {
        try {
            return objectMapper.readValue(file, beanType);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("target file json format is error!" + file);
        }
        return null;
    }

    public static <T extends Object> ArrayList<T> fileToList(File file, Class<T> beanType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, beanType);
            return objectMapper.readValue(file, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("target file json format is error!" + file);
        }
        return null;
    }

    public static <T extends Object> ArrayList<T> resourceToList(Resource resource, Class<T> beanType) {
        InputStream is = null;
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, beanType);
            is = resource.getInputStream();
            return objectMapper.readValue(is, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    /**
     * 从request中获取参数对象。
     *
     * @param request
     * @param type
     * @return
     */
    public static Object requestJsonToObject(HttpServletRequest request, Type type) {
        InputStream is = null;
        if (!request.getHeader("content-type").contains("application/json")) {
            return null;
        }
        try {
            is = request.getInputStream();
            if (is.available() > 0) {
                if (type instanceof Class) {
                    return objectMapper.readValue(is, (Class) type);
                }
                if (type instanceof JavaType) {
                    return objectMapper.readValue(is, (JavaType) type);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

}
