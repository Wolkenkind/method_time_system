# Spring AOP Homework Project

This project is a homework for [T1 Open School of Java development][1], demonstrating use of [Spring AOP][2] Aspects as a core of method execution time measurement system. The code consists of [Spring Boot][3] service, providing system's REST API to get statistical data of method execution time. API complies with [OpenAPI Specification][4] v.[3.0.0][5] and the code is generated with help of [OpenAPI Generator plugin for Gradle][6]. The API definition file can be found at: `/scr/main/resources/api_definition.yaml`

API documentation is generated using [springdoc-openapi][7] java library and can be accessible locally as [JSON][8] or [Swagger UI][9] after running the application. 

Execution time of methods annotated with `@TrackTime` and `@TrackTimeAsync` annotations is measured by means of aspects and stored in [PostgreSQL][10] database. [Project Reactor][11] is used to ensure that writing to database happens in asynchronous, non-blocking manner.

There is also an "internal" API that can be used to call methods, annotated for measurement, for example, to populate the database with data.

## Prerequisites

In order to run this project...

- you must have access to PostgreSQL instance and create database named `mdb` with owner `aopuser` (also database named `test` for running tests). `aopuser` must have password `springaop` (or you can modify application.properties as needed with your own credentials)
- you should populate the database with some data first. You can add new method and call annotated methods from code, or you can use "internal" APIs that provide access to the annotated methods

## Method execution time API

Most comfortable way to read the API documentation is through Swagger UI. Start the application and use the [link][9].

---
- `GET` /api/v3/stat/total/sync

Returns total execution time for all methods, marked as synchronous.
  - parameters:
    - `unit` *(optional)* Unit of time measurement, can be 'minutes', 'seconds', 'milliseconds' or 'nanoseconds'. Default: **milliseconds**
  - responses:
    - `200` successful operation
      - **string** `methodType`:\[sync, async\]  example: *sync*
      - **string** `statisticResultType`:\[avg, total\]	 example: *total*
      - **number** `statisticResult` example: *332.6*
      - **string** `resultUnits` example: *seconds*
    - `500` internal error

---
- `GET` /api/v3/stat/total/async

Returns total execution time for all methods, marked as asynchronous.
  - parameters:
    - `unit` *(optional)* Unit of time measurement, can be 'minutes', 'seconds', 'milliseconds' or 'nanoseconds'. Default: **milliseconds**
  - responses:
    - `200` successful operation
      - **string** `methodType`:\[sync, async\]  example: *sync*
      - **string** `statisticResultType`:\[avg, total\]	 example: *total*
      - **number** `statisticResult` example: *332.6*
      - **string** `resultUnits` example: *seconds*
    - `500` internal error

---
- `GET` /api/v3/stat/avg/sync

Returns average execution time for all methods, marked as synchronous.
  - parameters:
    - `unit` *(optional)* Unit of time measurement, can be 'minutes', 'seconds', 'milliseconds' or 'nanoseconds'. Default: **milliseconds**
  - responses:
    - `200` successful operation
      - **string** `methodType`:\[sync, async\]  example: *sync*
      - **string** `statisticResultType`:\[avg, total\]	 example: *total*
      - **number** `statisticResult` example: *332.6*
      - **string** `resultUnits` example: *seconds*
    - `500` internal error

---
- `GET` /api/v3/stat/avg/async

Returns average execution time for all methods, marked as asynchronous.
- parameters:
  - `unit` *(optional)* Unit of time measurement, can be 'minutes', 'seconds', 'milliseconds' or 'nanoseconds'. Default: **milliseconds**
- responses:
  - `200` successful operation
    - **string** `methodType`:\[sync, async\]  example: *sync*
    - **string** `statisticResultType`:\[avg, total\]	 example: *total*
    - **number** `statisticResult` example: *332.6*
    - **string** `resultUnits` example: *seconds*
  - `500` internal error

---
- `GET` /api/v3/stat/total/method
 
Returns total execution time for a method with specified name

Fully qualified method name must be specified as a parameter, e.g. *com.app.service.Method*
- parameters:
  - `name` ****required***  Fully qualified name of the method to provide statistics for
  - `sync` *(optional)* Synchronicity filter for method executions, can be 'all', 'sync' or 'async'. Default: **all**
  - `unit` *(optional)* Unit of time measurement, can be 'minutes', 'seconds', 'milliseconds' or 'nanoseconds'. Default: **milliseconds**
- responses:
  - `200` successful operation
    - **string** `methodName` example: *com.app.service.Method*
    - **string** `methodType`:\[all, sync, async\]  example: *sync*
    - **string** `statisticResultType`:\[avg, total\]	 example: *total*
    - **number** `statisticResult` example: *332.6*
    - **string** `resultUnits` example: *seconds*
  - `500` internal error

---
- `GET` /api/v3/stat/avg/method

Returns average execution time for a method with specified name

Fully qualified method name must be specified as a parameter, e.g. *com.app.service.Method*
- parameters:
  - `name` ****required***  Fully qualified name of the method to provide statistics for
  - `sync` *(optional)* Synchronicity filter for method executions, can be 'all', 'sync' or 'async'. Default: **all**
  - `unit` *(optional)* Unit of time measurement, can be 'minutes', 'seconds', 'milliseconds' or 'nanoseconds'. Default: **milliseconds**
- responses:
  - `200` successful operation
    - **string** `methodName` example: *com.app.service.Method*
    - **string** `methodType`:\[all, sync, async\]  example: *sync*
    - **string** `statisticResultType`:\[avg, total\]	 example: *total*
    - **number** `statisticResult` example: *332.6*
    - **string** `resultUnits` example: *seconds*
  - `500` internal error

---

## Internal API

This API may be used to populate database with method execution data. It uses services which call annotated methods in the application.

---
- `GET` /api/sleep

Calls method Thread.sleep() for a defined number of seconds

- parameters:
  - `seconds` ****required***  Amount of time to sleep in seconds
- responses:
  - `200` successful operation

- `GET` /api/fibo

---
Returns number from Fibonacci sequence

- parameters:
  - `number` ****required***  Number in sequence
- responses:
  - `200` successful operation
    -  "Result: " **number** `fibonacciNumber`  

- `GET` /api/gitUser

---
Performs a lookup of a GitHub user through GitHub API. This method is called asynchronously, so several parallel lookups are possible

- parameters:
  - `users` ****required*** GitHub username(s)
- responses:
  - `200` successful operation
    - **array of objects**
      - **string** `name` example: *Wolkenkind*
      - **string** `blog` example: *http://pivotal.io*

---































[1]: https://t1.ru/internship/item/otkrytaya-shkola-dlya-java-razrabotchikov/
[2]: https://docs.spring.io/spring-framework/reference/core/aop.html
[3]: https://spring.io/projects/spring-boot
[4]: https://www.openapis.org/
[5]: https://swagger.io/specification/v3/
[6]: https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin
[7]: https://springdoc.org/
[8]: http://localhost:8080/v3/api-docs
[9]: http://localhost:8080/swagger-ui/index.html
[10]: https://www.postgresql.org/
[11]: https://projectreactor.io/
