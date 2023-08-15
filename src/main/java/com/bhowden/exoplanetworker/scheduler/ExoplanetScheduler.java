package com.bhowden.exoplanetworker.scheduler;

import com.bhowden.exoplanetworker.model.Exoplanet;
import com.bhowden.exoplanetworker.services.ExoplanetRedisService;
import com.bhowden.exoplanetworker.services.SshClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExoplanetScheduler {

    @Autowired
    private ExoplanetRedisService exoplanetRedisService;

    @Autowired
    private SshClientService sshClientService;

    private static final int CHUNK_SIZE = 100;

    @Scheduled(fixedRate = 1000) // every second
    public void fetchAndUpdateExoplanets() {
        List<Exoplanet> exoplanets = exoplanetRedisService.getAll();
        for (int i = 0; i < exoplanets.size(); i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, exoplanets.size());
            List<Exoplanet> chunk = exoplanets.subList(i, end);
            processChunk(chunk);
        }
    }

    @Async("taskExecutor")
    public void processChunk(List<Exoplanet> chunk) {
        sshClientService.processExoplanets(chunk);
    }
}
