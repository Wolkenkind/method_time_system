package com.t1.openschool.atumanov.homework_aop;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@SpringBootApplication
@EnableAsync
@ComponentScan(
		basePackages = {"org.openapitools", "com.t1.openschool.atumanov.homework_aop" , "org.openapitools.configuration"},
		nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class HomeworkAopApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeworkAopApplication.class, args);
	}

	//taskExecutor will be used in @Async-annotated methods
	public Executor taskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(25);
		executor.setThreadNamePrefix("GitHubLookup-");
		executor.initialize();
		return executor;
	}

	@Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
		return initializer;
	}
}
