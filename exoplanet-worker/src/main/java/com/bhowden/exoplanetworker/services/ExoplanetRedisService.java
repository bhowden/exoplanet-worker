package com.bhowden.exoplanetworker.services;

import com.bhowden.exoplanetworker.model.Exoplanet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExoplanetRedisService {

    private static final String EXOPLANET_KEY_PREFIX = "exoplanet:";

    @Autowired
    private RedisTemplate<String, Exoplanet> redisTemplate;

    public void save(Exoplanet exoplanet) {
        String key = generateKey(exoplanet.getName());
        redisTemplate.opsForHash().put(EXOPLANET_KEY_PREFIX + key, key, exoplanet);
    }

    public Exoplanet get(String name) {
        String key = generateKey(name);
        return (Exoplanet) redisTemplate.opsForHash().get(EXOPLANET_KEY_PREFIX + key, key);
    }

    public void delete(String name) {
        String key = generateKey(name);
        redisTemplate.opsForHash().delete(EXOPLANET_KEY_PREFIX + key, key);
    }

    public List<Exoplanet> getAll() {
        Set<String> keys = redisTemplate.keys(EXOPLANET_KEY_PREFIX + "*");
        List<Exoplanet> exoplanetList = new ArrayList<>();

        for (String key : keys) {
            Exoplanet exoplanet = (Exoplanet) redisTemplate.opsForHash().get(key, key.substring(EXOPLANET_KEY_PREFIX.length()));
            if (exoplanet != null) {
                exoplanetList.add(exoplanet);
            }
        }

        return exoplanetList;
    }

    private String generateKey(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
}
