package com.t1.openschool.atumanov.homework_aop.repository;

import com.t1.openschool.atumanov.homework_aop.model.MethodExecution;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface MethodExecutionRepository extends ReactiveCrudRepository<MethodExecution, Long> {
    Flux<MethodExecution> findByFullyQualifiedName(String name);
    Flux<MethodExecution> findByIsAsync(boolean async);
    Flux<MethodExecution> findByFullyQualifiedNameAndIsAsync(String name, boolean async);
}
