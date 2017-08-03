package com.yaxon.vndp.dcap.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * Author: 游锋锋
 * Time: 2016-02-01 17:51
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class JsonRedisSerializer {
    public static final String EMPTY_JSON = "{}";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    protected ObjectMapper objectMapper;

    public JsonRedisSerializer(){
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(long.class, new ToStringSerializer());
        objectMapper.registerModule(module);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 序列化函数
     * @param object 目标对象
     * @return
     */
    public String seriazileAsString(Object object){
        if (object== null) {
            return EMPTY_JSON;
        }
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    /**
     * 反序列函数
     * @param str 反序列化目标字符   clazz 目标类
     * @return
     */
    public <T> T deserializeAsObject(String str, Class<T> clazz){
        if(str == null || clazz == null){
            return null;
        }
        try{
            return this.objectMapper.readValue(str, clazz);
        }catch (Exception ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }
}
