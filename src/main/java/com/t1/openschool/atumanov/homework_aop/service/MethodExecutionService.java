package com.t1.openschool.atumanov.homework_aop.service;

import com.t1.openschool.atumanov.homework_aop.model.MethodExecution;
import com.t1.openschool.atumanov.homework_aop.repository.MethodExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MethodExecutionService {

    private final MethodExecutionRepository methodExecutionRepository;

    public Flux<MethodExecution> findByName(String name) {
        return methodExecutionRepository.findByFullyQualifiedName(name);
    }

    public Mono<MethodExecution> save(MethodExecution execution) {
        return methodExecutionRepository.save(execution);
    }

    public Mono<MethodExecution> update(long id, MethodExecution execution){
        return methodExecutionRepository.findById(id).map(Optional::of).defaultIfEmpty(Optional.empty())
                .flatMap(optionalMethodExecution -> {
                   if (optionalMethodExecution.isPresent()) {
                       execution.setId(id);
                       return methodExecutionRepository.save(execution);
                   }

                   return Mono.empty();
                });
    }

    public Flux<MethodExecution> findBySynchronicity(boolean async) {
        return methodExecutionRepository.findByIsAsync(async);
    }

    public Flux<MethodExecution> findByNameAndSynchronicity(String name, boolean async) {
        return methodExecutionRepository.findByFullyQualifiedNameAndIsAsync(name, async);
    }
}
