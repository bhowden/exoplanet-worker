package com.bhowden.exoplanetworker.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.google.gson.Gson;

public class GsonRedisSerializer<T> implements RedisSerializer<T> {

    private Gson gson;
    private final Class<T> clazz;

    // Single argument constructor. Uses default Gson instance.
    public GsonRedisSerializer(Class<T> clazz) {
        this(clazz, new Gson());
    }

    // Two argument constructor. Uses provided Gson instance.
    public GsonRedisSerializer(Class<T> clazz, Gson gson) {
        this.clazz = clazz;
        this.gson = gson;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return gson.toJson(t).getBytes();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return gson.fromJson(new String(bytes), clazz);
    }
}
