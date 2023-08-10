package com.bhowden.exoplanetworker.config;

import com.bhowden.exoplanetworker.model.Exoplanet;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private Integer port;

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Exoplanet> redisTemplate(RedisConnectionFactory redisConnectionFactory, Gson gson) {
        RedisTemplate<String, Exoplanet> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Use StringRedisSerializer for keys
        template.setKeySerializer(new StringRedisSerializer());

        // Use custom GsonRedisSerializer for values
        template.setValueSerializer(new GsonRedisSerializer<Exoplanet>(Exoplanet.class, gson));

        return template;
    }

}

