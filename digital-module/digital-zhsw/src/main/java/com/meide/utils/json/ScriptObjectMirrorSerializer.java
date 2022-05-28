package com.meide.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.util.List;

/**
 * 用于复杂js对象的序列化，防止Array序列化成Map
 */
public class ScriptObjectMirrorSerializer extends JsonSerializer<ScriptObjectMirror> {
    @Override
    public void serialize(ScriptObjectMirror value,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        if (value.isArray()) {
            jsonGenerator.writeObject(value.to(List.class));
        } else {
            jsonGenerator.writeStartObject();
            for (String key : value.keySet()) {
                Object val = value.get(key);
                jsonGenerator.writeObjectField(key, val);
            }
            jsonGenerator.writeEndObject();
        }
    }

    /**
     * 注册js的特殊序列化
     *
     * @param mapper
     */
    public static void register(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScriptObjectMirror.class, new ScriptObjectMirrorSerializer());
        mapper.registerModule(module);
    }

}
