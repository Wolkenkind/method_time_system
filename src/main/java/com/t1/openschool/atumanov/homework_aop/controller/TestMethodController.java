package com.t1.openschool.atumanov.homework_aop.controller;

import com.t1.openschool.atumanov.homework_aop.model.User;
import com.t1.openschool.atumanov.homework_aop.service.FibonacciService;
import com.t1.openschool.atumanov.homework_aop.service.GitHubLookupService;
import com.t1.openschool.atumanov.homework_aop.service.SleepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TestMethodController {

    private final FibonacciService fibonacciService;
    private final GitHubLookupService gitHubLookupService;
    private final SleepService sleepService;

    @GetMapping("/fibo")
    public ResponseEntity<String> getFiboResult(@RequestParam(name = "number") String parameter) {
        try {
            int i = Integer.parseInt(parameter);
            long result = fibonacciService.getFibonacciNumber(i);
            return new ResponseEntity<>("Result: " + result, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Wrong parameter format, must be integer number", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/gitUser")
    public ResponseEntity<List<User>> getGitHubUser(@RequestParam List<String> users) {
        try {
            List<CompletableFuture<User>> futures = new ArrayList<>();
            for (String user: users) {
                futures.add(gitHubLookupService.findUser(user));
            }
            @SuppressWarnings("unchecked")
            CompletableFuture<User>[] futuresArray = new CompletableFuture[futures.size()];
            futuresArray = futures.toArray(futuresArray);
            CompletableFuture<List<User>> listFuture = CompletableFuture.allOf(futuresArray)
                    .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            return new ResponseEntity<>(listFuture.join(), HttpStatus.OK);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sleep")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executeSleep(@RequestParam int seconds) {
        try {
            sleepService.executeSleep(seconds);
        } catch (InterruptedException e) {
            //do nothing
        }
    }
}
