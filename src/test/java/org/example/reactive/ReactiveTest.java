package org.example.reactive;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/** 9. 리액티브 (Q40 ~ Q42) */
@SpringBootTest
class ReactiveTest {

    @Autowired ReactiveMemberService service;
    @Autowired ApplicationContext ac;
    @Autowired WebClient.Builder webClientBuilder;

    @Test
    @DisplayName("Q41. Mono 는 0~1개, Flux 는 0~N개")
    void q41_monoAndFlux() {
        StepVerifier.create(service.findName(1L)).expectNext("kim").verifyComplete();
        StepVerifier.create(service.findName(99L)).verifyComplete(); // 0개
        StepVerifier.create(service.findAllUpperCase()).expectNext("KIM", "LEE", "PARK").verifyComplete();
    }

    @Test
    @DisplayName("Q41. 구독하기 전까지는 아무 일도 일어나지 않는다")
    void q41_lazyUntilSubscribe() {
        AtomicInteger calls = new AtomicInteger();
        Mono<Integer> mono = Mono.fromCallable(calls::incrementAndGet).map(n -> n * 10);
        assertThat(calls.get()).isZero(); // 조립만 했을 뿐

        StepVerifier.create(mono).expectNext(10).verifyComplete(); // 구독!
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Q40. 백프레셔: 소비자가 요청(request)한 만큼만 받는다")
    void q40_backpressure() {
        StepVerifier.create(Flux.range(1, 5), 2) // 처음엔 2개만 요청
                .expectNext(1, 2)
                .thenRequest(3)                  // 3개 더 요청
                .expectNext(3, 4, 5)
                .verifyComplete();
    }

    @Test
    @DisplayName("Q42. MVC 와 WebFlux 의존성이 둘 다 있으면 MVC(서블릿) 가 선택되고, WebClient 는 같이 쓸 수 있다")
    void q42_mvcWins() {
        assertThat(ac).isInstanceOf(WebApplicationContext.class)
                .isNotInstanceOf(ReactiveWebApplicationContext.class);
        assertThat(webClientBuilder.build()).isNotNull();
    }
}
