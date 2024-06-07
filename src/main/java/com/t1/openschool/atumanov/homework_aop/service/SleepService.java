package com.t1.openschool.atumanov.homework_aop.service;

import com.t1.openschool.atumanov.homework_aop.annotation.TrackTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SleepService {
    @TrackTime
    public void executeSleep(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }
}
