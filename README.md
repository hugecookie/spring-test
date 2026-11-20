# Spring 면접 질문 예제 코드

「Spring 면접 질문과 답변」 문서의 Q1~Q42 답변을 **실행 가능한 코드 + 테스트**로 옮긴 프로젝트입니다.
답변을 말로 설명한 뒤, 해당 테스트를 열어 "정말 그렇게 동작하는지" 확인해 보세요.

- Spring Boot 3.5.7 · Java 17 · H2(in-memory) · JUnit 5
- 실행: `./gradlew test` (테스트 메서드 이름 앞의 `qN_` 과 `@DisplayName` 이 질문 번호)

| 챕터 | 질문 | 예제 코드 (`src/main/java/org/example`) | 증명 테스트 (`src/test/java/org/example`) |
|---|---|---|---|
| 1. 핵심 원리 | Q1~Q3 DI·생성자 주입 | `core/OrderService`, `core/DiscountPolicy` | `core/CoreTest` |
| | Q4 BeanFactory vs ApplicationContext | — | `core/CoreTest.q4_*` |
| | Q5 @Component vs @Bean | `core/FixDiscountPolicy`, `core/CoreConfig` | `core/CoreTest.q5_*` |
| | Q6 같은 타입 빈 여러 개 | `core/RateDiscountPolicy`(@Primary), `core/FixOrderService`(@Qualifier), `core/DiscountRouter`(Map) | `core/CoreTest.q6_*` |
| | Q7 디자인 패턴 | 싱글톤·프록시·전략·템플릿 콜백 등 전체 예제 | `core/CoreTest.q7_*` |
| 2. Bean 심화 | Q8 스코프 | `bean/PrototypeBean` | `bean/BeanTest.q8_*` |
| | Q9 싱글톤 스레드 안전성 | `bean/StatefulPriceService`(나쁜 예), `bean/StatelessPriceService` | `bean/BeanTest.q9_*` |
| | Q10 prototype을 싱글톤에 주입 | `bean/SingletonClient` (ObjectProvider) | `bean/BeanTest.q10_*` |
| | Q11~Q12 생명주기·@PostConstruct | `bean/NetworkClient` | `bean/BeanTest.q11_q12_*` |
| 3. AOP | Q13~Q15 Aspect·Advice·Pointcut·Weaving | `aop/Trace`, `aop/TraceAspect` | `aop/AopTest.q13_*` |
| | Q16 JDK 프록시 vs CGLIB | — | `aop/AopTest.q16_*` |
| | Q17 내부 호출 문제와 해결 | `aop/TraceTargetService`, `aop/TraceCallerService` | `aop/AopTest.q17_*` |
| 4. 트랜잭션 | Q18 선언적/프로그래밍 방식 | `tx/TxMemberService`, TransactionTemplate | `tx/TransactionTest.q18_*` |
| | Q19 동작 원리(프록시) | `tx/TxProbeService` | `tx/TransactionTest.q19_*` |
| | Q20 체크 예외 롤백 | `tx/TxMemberService`, `tx/NotEnoughMoneyException` | `tx/TransactionTest.q20_*` |
| | Q21 REQUIRED vs REQUIRES_NEW | `tx/LogService` | `tx/TransactionTest.q21_*` |
| | Q22 격리 수준 | `tx/TxMemberService.countMembers` | `tx/TransactionTest.q22_*` |
| 5. Spring MVC | Q23~Q25 Controller·매핑·요청 흐름 | `web/MemberApiController`, `member/MemberService` | `web/WebMvcTest.q23_*`, `q24_*` |
| | Q26 Filter vs Interceptor | `web/LogFilter`, `web/LoginCheckInterceptor`, `web/WebConfig` | `web/WebMvcTest.q26_*` |
| | Q27 @ControllerAdvice | `web/GlobalExceptionHandler`, `common/*` | `web/WebMvcTest.q27_*` |
| 6. Spring Boot | Q28~Q29 자동 설정 | `boot/autoconfig/GreetingAutoConfiguration`, `resources/META-INF/spring/...imports` | `boot/BootTest.q29_*` |
| | Q30 설정 파일·프로필 | `resources/application.yml`, `application-dev.yml`, `boot/DevOnlyBean` | `boot/BootTest$DevProfile` |
| | Q31 javax → jakarta | 모든 엔티티·필터의 `jakarta.*` import | — |
| 7. 데이터·JPA | Q32~Q33 JdbcTemplate·예외 변환 | `data/MemberJdbcRepository` | `data/DataAccessTest.q32_*`, `q33_*` |
| | Q34 영속성 컨텍스트 | `data/Team.changeName` | `data/DataAccessTest.q34_*` |
| | Q35 N+1 | `data/TeamRepository` (fetch join, @EntityGraph) | `data/DataAccessTest.q35_*` (SQL 개수 측정) |
| | Q36 지연/즉시 로딩 | `data/Player` (LAZY) | `data/DataAccessTest.q36_*` |
| 8. Security | Q37~Q38 설정·인증/인가 흐름 | `security/SecurityConfig`, `security/AuthController` | `security/SecurityTest` |
| | Q39 세션 vs JWT | `security/SimpleTokenProvider` (학습용 HMAC 토큰) | `security/SimpleTokenProviderTest` |
| 9. 리액티브 | Q40~Q41 리액티브·Mono/Flux | `reactive/ReactiveMemberService` | `reactive/ReactiveTest` |
| | Q42 MVC + WebFlux 같이 쓰기 | `build.gradle` (web + webflux 둘 다) | `reactive/ReactiveTest.q42_*` |

> 계정(Security 예제): `user / password` (ROLE_USER), `admin / password` (ROLE_ADMIN)
