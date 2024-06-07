package com.t1.openschool.atumanov.homework_aop;

import com.t1.openschool.atumanov.homework_aop.model.MethodExecution;
import com.t1.openschool.atumanov.homework_aop.repository.MethodExecutionRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MethodExecutionRepositoryTest {
    @Autowired
    MethodExecutionRepository repository;

    public static final String TESTNAME = "test";
    public static final String EDITED = "edited";

    @BeforeAll
    static void setup(@Autowired DatabaseClient client) {
        client.sql("INSERT INTO method_execution(name,duration,async) VALUES ('" + TESTNAME + "',100,true)")
                .fetch()
                .rowsUpdated()
                .doOnNext(rowsUpdated -> System.out.println("setup: Rows inserted successfully"))
                .block();
    }

    @Test
    public void createRecords() {
        long count = repository.findAll().toStream().count();
        Flux<MethodExecution> meFlux = Flux.just(new MethodExecution("m1", System.nanoTime(), false), new MethodExecution("m2", System.nanoTime(), true))
                .flatMap(execution -> repository.save(execution));

        System.out.println("createRecords: Going to create records...");
        StepVerifier.create(meFlux).expectNextCount(2).verifyComplete();

        System.out.println("createRecords: Going to read records...");
        Flux<MethodExecution> all = repository.findAll();

        StepVerifier.create(all).expectNextCount(2 + count).verifyComplete();
    }

    @Test
    @Order(1)
    public void readRecord() {
        Mono<MethodExecution> meFlux = repository.findById(1L);
        System.out.println("readRecord: Going to read record...");
        StepVerifier.create(meFlux).expectNextCount(1).verifyComplete();
        System.out.println("readRecord: Going to check record name...");
        StepVerifier.create(meFlux)
                .expectNextMatches(next -> next.isAsync() && next.getNanoDuration() == 100 && next.getFullyQualifiedName().equals(TESTNAME))
                .verifyComplete();
    }

    @Test
    @Order(2)
    public void updateRecord() {
        MethodExecution newExecution = null;
        System.out.println("updateRecord: Going to read record...");
        try {
            newExecution = repository.findById(1L).block(Duration.ofMillis(20000));
        } catch (Exception e) {
            System.out.println("updateRecord: Exception during getting record form database: " + e.getMessage());
            Assertions.fail("Error reading record");
        }
        newExecution.setFullyQualifiedName(EDITED);
        System.out.println("updateRecord: Going to save edited record...");
        Mono<MethodExecution> meMono = repository.save(newExecution);
        StepVerifier.create(meMono).expectNextMatches(next -> next.getFullyQualifiedName().equals(EDITED)).verifyComplete();
    }

    @Test
    @Order(3)
    public void deleteRecord() {
        Mono<Void> meMono = repository.deleteById(1L);
        System.out.println("deleteRecord: Going to delete record...");
        StepVerifier.create(meMono).verifyComplete();
        System.out.println("deleteRecord: Going to check on deleted record...");
        Mono<MethodExecution> meMonoCheck = repository.findById(1L);
        StepVerifier.create(meMonoCheck).verifyComplete();
    }
    
    @AfterAll
    static void tearDown(@Autowired DatabaseClient client) {
        client.sql("DROP TABLE method_execution")
                .fetch()
                .rowsUpdated()
                .doOnNext(rowsUpdated -> System.out.println("tearDown: Table deleted successfully"))
                .block();
    }
}
