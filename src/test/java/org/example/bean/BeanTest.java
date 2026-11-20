package org.example.bean;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** 2. Bean 심화 (Q8 ~ Q12) */
@SpringBootTest
class BeanTest {

    @Autowired ApplicationContext ac;
    @Autowired SingletonClient singletonClient;
    @Autowired StatefulPriceService statefulPriceService;
    @Autowired StatelessPriceService statelessPriceService;
    @Autowired NetworkClient networkClient;

    @Test
    @DisplayName("Q8. 기본 스코프 singleton, @Scope(\"prototype\") 은 조회마다 새 인스턴스")
    void q8_scope() {
        assertThat(ac.getBean(SingletonClient.class)).isSameAs(ac.getBean(SingletonClient.class));
        assertThat(ac.getBean(PrototypeBean.class)).isNotSameAs(ac.getBean(PrototypeBean.class));
    }

    @Test
    @DisplayName("Q9. 싱글톤에 상태를 두면 사용자 A 의 값이 B 에 의해 덮어써진다")
    void q9_statefulSingletonIsNotSafe() {
        statefulPriceService.order("userA", 10000); // A 요청
        statefulPriceService.order("userB", 20000); // 그 사이 B 요청
        assertThat(statefulPriceService.getPrice()).isEqualTo(20000); // A 는 20000 을 보게 된다!

        int priceA = statelessPriceService.order("userA", 10000);    // 무상태 설계
        int priceB = statelessPriceService.order("userB", 20000);
        assertThat(priceA).isEqualTo(10000);
        assertThat(priceB).isEqualTo(20000);
    }

    @Test
    @DisplayName("Q10. 싱글톤에 주입된 prototype 은 공유되고, ObjectProvider 는 매번 새로 받는다")
    void q10_prototypeInSingleton() {
        assertThat(singletonClient.logicWithInjected()).isEqualTo(1);
        assertThat(singletonClient.logicWithInjected()).isEqualTo(2); // 같은 객체 → 누적

        assertThat(singletonClient.logicWithProvider()).isEqualTo(1);
        assertThat(singletonClient.logicWithProvider()).isEqualTo(1); // 매번 새 객체
    }

    @Test
    @DisplayName("Q10, Q11. prototype 은 @PostConstruct 까지만, @PreDestroy 는 호출되지 않는다")
    void q10_prototypeDestroyNotCalled() {
        PrototypeBean prototype;
        NetworkClient singleton;
        try (var context = new AnnotationConfigApplicationContext(PrototypeBean.class, NetworkClient.class)) {
            prototype = context.getBean(PrototypeBean.class);
            singleton = context.getBean(NetworkClient.class);
            assertThat(prototype.isInitialized()).isTrue();
        } // 컨테이너 종료
        assertThat(prototype.isDestroyed()).isFalse();
        assertThat(singleton.getEvents()).last().isEqualTo("preDestroy");
    }

    @Test
    @DisplayName("Q11, Q12. 생성자 시점엔 주입 전(null), @PostConstruct 시점엔 주입 완료")
    void q11_q12_lifecycle() {
        assertThat(networkClient.getEvents()).containsExactly(
                "constructor url=null",
                "postConstruct url=http://localhost:8080/network");
    }
}
