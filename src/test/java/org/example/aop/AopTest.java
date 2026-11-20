package org.example.aop;

import static org.assertj.core.api.Assertions.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** 3. AOP (Q13 ~ Q17) */
@SpringBootTest
class AopTest {

    @Autowired TraceTargetService traceTargetService;
    @Autowired TraceCallerService traceCallerService;
    @Autowired TraceLog traceLog;

    @BeforeEach
    void setUp() {
        traceLog.clear();
    }

    @Test
    @DisplayName("Q13~Q15. @Trace 메서드 호출 전후로 Advice 가 실행된다 (런타임 프록시 위빙)")
    void q13_aspectApplied() {
        assertThat(traceTargetService.hello("kim")).isEqualTo("hello kim");
        assertThat(traceLog.getLogs()).containsExactly(
                "START TraceTargetService.hello(..)",
                "END TraceTargetService.hello(..)");
    }

    @Test
    @DisplayName("Q16. Spring Boot 는 기본으로 CGLIB 프록시를 주입한다")
    void q16_bootUsesCglib() {
        assertThat(AopUtils.isCglibProxy(traceTargetService)).isTrue();
        assertThat(traceTargetService.getClass().getName()).contains("$$SpringCGLIB$$");
    }

    interface Greeter {
        String greet();
    }

    static class GreeterImpl implements Greeter {
        public String greet() { return "hi"; }
    }

    @Test
    @DisplayName("Q16. JDK 동적 프록시는 인터페이스 구현, CGLIB 는 클래스 상속")
    void q16_jdkProxy_vs_cglib() {
        MethodInterceptor advice = invocation -> "[proxy] " + invocation.proceed();

        ProxyFactory jdkFactory = new ProxyFactory(new GreeterImpl()); // 인터페이스 자동 감지
        jdkFactory.addAdvice(advice);
        Object jdkProxy = jdkFactory.getProxy();
        assertThat(AopUtils.isJdkDynamicProxy(jdkProxy)).isTrue();
        assertThat(jdkProxy).isInstanceOf(Greeter.class).isNotInstanceOf(GreeterImpl.class); // 구현 클래스로 캐스팅 불가
        assertThat(((Greeter) jdkProxy).greet()).isEqualTo("[proxy] hi");

        ProxyFactory cglibFactory = new ProxyFactory(new GreeterImpl());
        cglibFactory.setProxyTargetClass(true);
        cglibFactory.addAdvice(advice);
        Object cglibProxy = cglibFactory.getProxy();
        assertThat(AopUtils.isCglibProxy(cglibProxy)).isTrue();
        assertThat(cglibProxy).isInstanceOf(GreeterImpl.class); // 상속했으므로 구현 클래스 타입
    }

    @Test
    @DisplayName("Q17. this.hello() 내부 호출은 프록시를 거치지 않아 Advice 가 적용되지 않는다")
    void q17_selfInvocation() {
        traceTargetService.callInternal();
        assertThat(traceLog.getLogs()).isEmpty();
    }

    @Test
    @DisplayName("Q17 해결. 별도 빈에서 호출하면 프록시를 거쳐 적용된다")
    void q17_fixedBySeparateBean() {
        traceCallerService.call();
        assertThat(traceLog.getLogs()).containsExactly(
                "START TraceTargetService.hello(..)",
                "END TraceTargetService.hello(..)");
    }
}
