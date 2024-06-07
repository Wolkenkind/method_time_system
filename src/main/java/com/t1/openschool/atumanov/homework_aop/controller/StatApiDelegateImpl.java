package com.t1.openschool.atumanov.homework_aop.controller;

import com.t1.openschool.atumanov.homework_aop.ExecutionUnits;
import com.t1.openschool.atumanov.homework_aop.GroupStatResult;
import com.t1.openschool.atumanov.homework_aop.MethodStatResult;
import com.t1.openschool.atumanov.homework_aop.SynchronicityFilter;
import com.t1.openschool.atumanov.homework_aop.model.MethodExecution;
import com.t1.openschool.atumanov.homework_aop.service.MethodExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.OptionalDouble;

@RequiredArgsConstructor
@Service
public class StatApiDelegateImpl implements StatApiDelegate {

    private final MethodExecutionService service;

    //utility method to convert from nanoseconds to given unit
    private static long getUnitDivider(ExecutionUnits unit) {
        switch (unit) {
            case MINUTES: return 60_000_000_000L;
            case SECONDS: return 1_000_000_000;
            case MILLISECONDS: return 1_000_000;
            case NANOSECONDS: return 1;
            default: throw new IllegalArgumentException("Illegal conversion unit argument: " + unit.getValue());
        }
    }

    @Override
    public ResponseEntity<GroupStatResult> getAvgForAsynchronous(ExecutionUnits unit) {
        try {
            List<MethodExecution> executionList = service.findBySynchronicity(true).toStream().toList();
            OptionalDouble avg = executionList.stream().mapToLong(MethodExecution::getNanoDuration).average();
            return new ResponseEntity<>(
                    new GroupStatResult(GroupStatResult.MethodTypeEnum.ASYNC,
                            GroupStatResult.StatisticResultTypeEnum.AVG,
                            BigDecimal.valueOf(avg.orElse(0) / getUnitDivider(unit)),
                            unit.toString()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<MethodStatResult> getAvgForMethod(String name, SynchronicityFilter sync, ExecutionUnits unit) {
        try {
            boolean asyncExecution = sync.getValue().toUpperCase().equals("ASYNC");
            List<MethodExecution> executionList;
            if (sync.getValue().toUpperCase().equals("ALL")) {
                executionList = service.findByName(name).toStream().toList();
            } else {
                executionList = service.findByNameAndSynchronicity(name, asyncExecution).toStream().toList();
            }
            OptionalDouble avg = executionList.stream().mapToLong(MethodExecution::getNanoDuration).average();
            return new ResponseEntity<>(
                    new MethodStatResult(name,
                            MethodStatResult.MethodTypeEnum.fromValue(sync.getValue()),
                            MethodStatResult.StatisticResultTypeEnum.AVG,
                            BigDecimal.valueOf(avg.orElse(0) / getUnitDivider(unit)),
                            unit.toString()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GroupStatResult> getAvgForSynchronous(ExecutionUnits unit) {
        try {
            List<MethodExecution> executionList = service.findBySynchronicity(false).toStream().toList();
            OptionalDouble avg = executionList.stream().mapToLong(MethodExecution::getNanoDuration).average();
            return new ResponseEntity<>(
                    new GroupStatResult(GroupStatResult.MethodTypeEnum.SYNC,
                            GroupStatResult.StatisticResultTypeEnum.AVG,
                            BigDecimal.valueOf(avg.orElse(0) / getUnitDivider(unit)),
                            unit.toString()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GroupStatResult> getTotalForAsynchronous(ExecutionUnits unit) {
        try {
            List<MethodExecution> executionList = service.findBySynchronicity(true).toStream().toList();
            long total = executionList.stream().mapToLong(MethodExecution::getNanoDuration).sum();
            return new ResponseEntity<>(
                    new GroupStatResult(GroupStatResult.MethodTypeEnum.ASYNC,
                            GroupStatResult.StatisticResultTypeEnum.TOTAL,
                            BigDecimal.valueOf(total / getUnitDivider(unit)),
                            unit.toString()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<MethodStatResult> getTotalForMethod(String name, SynchronicityFilter sync, ExecutionUnits unit) {
        try {
            boolean asyncExecution = sync.getValue().toUpperCase().equals("ASYNC");
            List<MethodExecution> executionList;
            if (sync.getValue().toUpperCase().equals("ALL")) {
                executionList = service.findByName(name).toStream().toList();
            } else {
                executionList = service.findByNameAndSynchronicity(name, asyncExecution).toStream().toList();
            }
            long total = executionList.stream().mapToLong(MethodExecution::getNanoDuration).sum();
            return new ResponseEntity<>(
                    new MethodStatResult(name,
                            MethodStatResult.MethodTypeEnum.fromValue(sync.getValue()),
                            MethodStatResult.StatisticResultTypeEnum.TOTAL,
                            BigDecimal.valueOf(total / getUnitDivider(unit)),
                            unit.toString()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<GroupStatResult> getTotalForSynchronous(ExecutionUnits unit) {
        try {
            List<MethodExecution> executionList = service.findBySynchronicity(false).toStream().toList();
            long total = executionList.stream().mapToLong(MethodExecution::getNanoDuration).sum();
            return new ResponseEntity<>(
                    new GroupStatResult(GroupStatResult.MethodTypeEnum.SYNC,
                            GroupStatResult.StatisticResultTypeEnum.TOTAL,
                            BigDecimal.valueOf(total / getUnitDivider(unit)),
                            unit.toString()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
