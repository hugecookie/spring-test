package org.example.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** 1. 핵심 원리 (Q1 ~ Q7) */
@SpringBootTest
class CoreTest {

    @Autowired ApplicationContext ac;
    @Autowired OrderService orderService;
    @Autowired FixOrderService fixOrderService;
    @Autowired DiscountRouter discountRouter;

    @Test
    @DisplayName("Q2. DI: OrderService 는 구현체를 모른 채 컨테이너가 넣어 준 정책을 쓴다")
    void q2_di() {
        assertThat(orderService.order(10000)).isEqualTo(9000); // @Primary RateDiscountPolicy(10%)
    }

    @Test
    @DisplayName("Q3. 생성자 주입이면 Spring 없이 new 로 가짜 객체를 넣어 테스트할 수 있다")
    void q3_constructorInjection_pureJavaTest() {
        OrderService service = new OrderService(price -> 0); // 람다 = 가짜 DiscountPolicy
        assertThat(service.order(10000)).isEqualTo(10000);
    }

    static class LazyBean {
        static int created;
        LazyBean() { created++; }
    }

    static class EagerBean {
        static int created;
        EagerBean() { created++; }
    }

    @Test
    @DisplayName("Q4. BeanFactory 는 요청할 때 생성, ApplicationContext 는 시작 시점에 싱글톤을 미리 생성")
    void q4_beanFactory_vs_applicationContext() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("lazyBean", new RootBeanDefinition(LazyBean.class));
        assertThat(LazyBean.created).isZero();       // 아직 안 만들어짐
        beanFactory.getBean("lazyBean");
        assertThat(LazyBean.created).isEqualTo(1);   // getBean 시점에 생성

        try (var context = new AnnotationConfigApplicationContext(EagerBean.class)) {
            assertThat(EagerBean.created).isEqualTo(1); // getBean 전에 이미 생성 → 설정 오류를 빨리 발견
            assertThat(context.getEnvironment()).isNotNull(); // + 환경·프로필, 이벤트, 메시지, 리소스 기능
        }
    }

    @Test
    @DisplayName("Q5. @Bean 으로 외부 라이브러리 클래스(Clock)를 수동 등록")
    void q5_bean() {
        assertThat(ac.getBean(Clock.class).getZone().getId()).isEqualTo("Asia/Seoul");
        assertThat(ac.getBean("fixDiscountPolicy")).isInstanceOf(FixDiscountPolicy.class); // @Component 자동 등록
    }

    @Test
    @DisplayName("Q6. 같은 타입 빈 여러 개: @Primary / @Qualifier / Map 으로 모두 받기")
    void q6_multipleBeans() {
        assertThat(ac.getBeansOfType(DiscountPolicy.class)).hasSize(2);
        assertThat(ac.getBean(DiscountPolicy.class)).isInstanceOf(RateDiscountPolicy.class); // @Primary
        assertThat(fixOrderService.order(10000)).isEqualTo(9000);                            // @Qualifier 우선
        assertThat(discountRouter.discount("fixDiscountPolicy", 20000)).isEqualTo(1000);
        assertThat(discountRouter.discount("rateDiscountPolicy", 20000)).isEqualTo(2000);
    }

    @Test
    @DisplayName("Q6. @Primary/@Qualifier 가 없으면 NoUniqueBeanDefinitionException")
    void q6_noUniqueBean() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.registerBean("a", DiscountPolicy.class, () -> price -> 1);
            context.registerBean("b", DiscountPolicy.class, () -> price -> 2);
            context.refresh();
            assertThatThrownBy(() -> context.getBean(DiscountPolicy.class))
                    .isInstanceOf(NoUniqueBeanDefinitionException.class);
        }
    }

    @Test
    @DisplayName("Q7. 싱글톤 패턴: 같은 빈을 여러 번 조회해도 같은 인스턴스")
    void q7_singleton() {
        assertThat(ac.getBean(OrderService.class)).isSameAs(ac.getBean(OrderService.class));
    }
}
