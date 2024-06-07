package com.t1.openschool.atumanov.homework_aop.service;

import com.t1.openschool.atumanov.homework_aop.annotation.TrackTime;
import org.springframework.stereotype.Service;

@Service
public class FibonacciService {
    @TrackTime
    public long getFibonacciNumber(long n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return getFibonacciNumber(n - 1) + getFibonacciNumber(n - 2);
    }
}
