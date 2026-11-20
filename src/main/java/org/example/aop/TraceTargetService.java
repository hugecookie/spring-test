package org.example.aop;

import org.springframework.stereotype.Service;

@Service
public class TraceTargetService {

    @Trace
    public String hello(String name) {
        return "hello " + name;
    }

    /**
     * Q17. 내부 호출 문제: this.hello() 는 프록시가 아닌 실제 객체의 메서드를 직접 부르므로
     * Aspect 가 적용되지 않는다. (@Transactional, @Async, @Cacheable 도 동일)
     */
    public String callInternal() {
        return this.hello("internal");
    }
}
