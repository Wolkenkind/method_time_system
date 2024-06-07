package com.t1.openschool.atumanov.homework_aop.service;

import com.t1.openschool.atumanov.homework_aop.annotation.TrackAsyncTime;
import com.t1.openschool.atumanov.homework_aop.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GitHubLookupService {
    private final RestTemplate restTemplate;

    public GitHubLookupService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    @TrackAsyncTime
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        String url = String.format("https://api.github.com/users/%s", user);
        User results = restTemplate.getForObject(url, User.class);
        return CompletableFuture.completedFuture(results);
    }
}
