package com.bhowden.exoplanetworker.config;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.google.gson.Gson;

public class GsonRedisSerializer<T> implements RedisSerializer<T> {

    private final Gson gson = new Gson();
    private final Class<T> clazz;

    public GsonRedisSerializer(Class<T> clazz) {
        this.clazz = clazz;
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