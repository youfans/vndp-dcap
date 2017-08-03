package com.yaxon.vndp.dcap.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Author: 游锋锋
 * Time: 2016-02-01 17:50
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class HessianRedisSerializer implements RedisSerializer<Object> {
    private SerializerFactory serializerFactory = SerializerFactory.createDefault();


    /**
     * 序列化函数
     * @param o 序列化目标对象
     * @return
     * @throws SerializationException
     */
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Hessian2Output ho = new Hessian2Output(os);
            ho.setSerializerFactory(serializerFactory);
            ho.startMessage();
            ho.writeObject(o);
            ho.completeMessage();
            ho.close();
            os.close();
            return os.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("序列化异常", e);
        }
    }

    /**
     * 反序列函数
     * @param bytes 反序列化目标字节
     * @return
     * @throws SerializationException
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            Hessian2Input hi = new Hessian2Input(is);
            hi.setSerializerFactory(serializerFactory);
            hi.startMessage();
            Object o = hi.readObject();
            hi.completeMessage();
            hi.close();
            is.close();
            return o;
        } catch (Exception e) {
            throw new SerializationException("反序列化异常", e);
        }
    }

    /**
     * 反序列化函数
     * @param bytes 反序列化目标字节
     * @param clazz 目标类
     * @return
     * @throws SerializationException
     */
    public Object deserialize(byte[] bytes, Class clazz) throws SerializationException {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            Hessian2Input hi = new Hessian2Input(is);
            hi.setSerializerFactory(serializerFactory);
            hi.startMessage();
            Object o = hi.readObject(clazz);
            hi.completeMessage();
            hi.close();
            is.close();
            return o;
        } catch (Exception e) {
            throw new SerializationException("反序列化异常", e);
        }
    }
}
