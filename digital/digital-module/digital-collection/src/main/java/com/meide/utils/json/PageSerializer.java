package com.meide.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.pagehelper.Page;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 用于复杂js对象的序列化，防止Array序列化成Map
 */
public class PageSerializer extends JsonSerializer<Page> {
    @Override
    public void serialize(Page value,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("pageNum", value.getPageNum());
        jsonGenerator.writeNumberField("pageSize", value.getPageSize());
        jsonGenerator.writeNumberField("pages", value.getPages());
        jsonGenerator.writeNumberField("total", value.getTotal());
        ArrayList list = new ArrayList(value);
        jsonGenerator.writeObjectField("list", list);
        jsonGenerator.writeEndObject();
    }

    /**
     * 注册js的特殊序列化
     *
     * @param mapper
     */
    public static void register(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Page.class, new PageSerializer());
        mapper.registerModule(module);
    }

}
