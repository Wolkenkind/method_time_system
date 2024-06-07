package com.t1.openschool.atumanov.homework_aop;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomeworkAopApplicationTests {

	private static final int SLEEP_TIME_SECONDS = 1;
	private static final int FIBO_NUMBER = 20;
	private static final String LOOKUP_USERS = "Wolkenkind, PivotalSoftware, StarKRE";
	private static final String DEFAULT_UNIT_STRING = "milliseconds";
	private static final String UNIT_STRING = "minutes";
	private static final String METHOD_NAME = "com.t1.openschool.atumanov.homework_aop.service.SleepService.executeSleep";

	private static String baseServiceUrl;
	private static String sleepUrl, fiboUrl, lookupUrl;
	private static String baseApiUrl;

	@LocalServerPort
	private int serverTestPort;
	@Autowired
	private TestRestTemplate template;

	@BeforeAll
	static void setup(@Autowired TestRestTemplate template) {

	}

	@Test
	@Order(1)
	void contextLoads() {
		baseServiceUrl = "http://localhost:" + serverTestPort + "/api/";
		sleepUrl = baseServiceUrl + "sleep?seconds={amount}";
		fiboUrl = baseServiceUrl + "fibo?number={number}";
		lookupUrl = baseServiceUrl + "gitUser?users={users}";

		baseApiUrl = "http://localhost:" + serverTestPort + "/api/v3/stat/";

		//Run service APIs to prepare method execution data in the database
		CompletableFuture<Void> sleepFuture = CompletableFuture.runAsync(() -> template.getForEntity(sleepUrl, Void.class, SLEEP_TIME_SECONDS));
		CompletableFuture<Void> fiboFuture = CompletableFuture.runAsync(() -> template.getForEntity(fiboUrl, Void.class, FIBO_NUMBER));
		CompletableFuture<Void> lookupFuture = CompletableFuture.runAsync(() -> template.getForEntity(lookupUrl, Void.class, LOOKUP_USERS));

		CompletableFuture<Void>[] futuresArray = new CompletableFuture[] {sleepFuture, fiboFuture, lookupFuture};
		CompletableFuture.allOf(futuresArray).join();

		System.out.println("contextLoads: Services APIs finished");
	}

	@Test
	@Order(2)
	void testAvgSync() {

		groupTest(baseApiUrl + "avg/sync", GroupStatResult.MethodTypeEnum.SYNC, GroupStatResult.StatisticResultTypeEnum.AVG);
	}

	@Test
	@Order(2)
	void testAvgAsync() {
		groupTest(baseApiUrl + "avg/async", GroupStatResult.MethodTypeEnum.ASYNC, GroupStatResult.StatisticResultTypeEnum.AVG);
	}

	@Test
	@Order(2)
	void testTotalSync() {
		groupTest(baseApiUrl + "total/sync", GroupStatResult.MethodTypeEnum.SYNC, GroupStatResult.StatisticResultTypeEnum.TOTAL);
	}

	@Test
	@Order(2)
	void testTotalAsync() {
		groupTest(baseApiUrl + "total/async", GroupStatResult.MethodTypeEnum.ASYNC, GroupStatResult.StatisticResultTypeEnum.TOTAL);
	}

	@Test
	@Order(2)
	void testAvgMethod() {
		methodTest(baseApiUrl + "avg/method?name=" + METHOD_NAME,  MethodStatResult.StatisticResultTypeEnum.AVG);
	}

	@Test
	@Order(2)
	void testTotalMethod() {
		methodTest(baseApiUrl + "total/method?name=" + METHOD_NAME, MethodStatResult.StatisticResultTypeEnum.TOTAL);
	}

	@AfterAll
	static void tearDown(@Autowired DatabaseClient client) {
		client.sql("DROP TABLE method_execution")
				.fetch()
				.rowsUpdated()
				.doOnNext(rowsUpdated -> System.out.println("tearDown: Table deleted successfully"))
				.block();
	}

	private void groupTest(String url, GroupStatResult.MethodTypeEnum syncType, GroupStatResult.StatisticResultTypeEnum statisticType) {
		ResponseEntity<GroupStatResult> responseEntity = template.getForEntity(url, GroupStatResult.class);
		assert responseEntity.getStatusCode().equals(HttpStatus.OK);
		GroupStatResult result = responseEntity.getBody();
		assert result.getMethodType().equals(syncType);
		assert result.getStatisticResultType().equals(statisticType);
		assert result.getResultUnits().equals(DEFAULT_UNIT_STRING);

		url += "?unit=" + UNIT_STRING;
		responseEntity = template.getForEntity(url, GroupStatResult.class);
		assert responseEntity.getStatusCode().equals(HttpStatus.OK);
		result = responseEntity.getBody();
		assert result.getMethodType().equals(syncType);
		assert result.getStatisticResultType().equals(statisticType);
		assert result.getResultUnits().equals(UNIT_STRING);
	}

	private void methodTest(String url, MethodStatResult.StatisticResultTypeEnum statisticType) {
		ResponseEntity<MethodStatResult> responseEntity = template.getForEntity(url, MethodStatResult.class);
		assert responseEntity.getStatusCode().equals(HttpStatus.OK);
		MethodStatResult result = responseEntity.getBody();
		assert result.getMethodType().equals(MethodStatResult.MethodTypeEnum.ALL);
		assert result.getStatisticResultType().equals(statisticType);
		assert result.getResultUnits().equals(DEFAULT_UNIT_STRING);

		url += "&unit=" + UNIT_STRING;
		System.out.println(url);
		responseEntity = template.getForEntity(url, MethodStatResult.class);
		assert responseEntity.getStatusCode().equals(HttpStatus.OK);
		result = responseEntity.getBody();
		assert result.getMethodType().equals(MethodStatResult.MethodTypeEnum.ALL);
		assert result.getStatisticResultType().equals(statisticType);
		System.out.println(result.getResultUnits());
		assert result.getResultUnits().equals(UNIT_STRING);

		url += "&sync=" + MethodStatResult.MethodTypeEnum.ASYNC;
		responseEntity = template.getForEntity(url, MethodStatResult.class);
		assert responseEntity.getStatusCode().equals(HttpStatus.OK);
		result = responseEntity.getBody();
		assert result.getStatisticResult().intValue() == 0;
	}
}
