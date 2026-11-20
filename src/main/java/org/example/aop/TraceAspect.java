package org.example.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Q13, Q14. 로깅이라는 횡단 관심사를 한 곳에 모듈화한 Aspect.
 *  - Aspect   : 이 클래스 (Advice + Pointcut)
 *  - Pointcut : traced()  → "@Trace 가 붙은 메서드"만 골라냄
 *  - Advice   : around()  → 실제 실행할 부가 기능 (@Before/@AfterReturning/@AfterThrowing/@After 도 있음)
 *  - JoinPoint: Spring AOP 에서는 "메서드 실행" 시점만 가능
 * Q15. Spring AOP 는 런타임에 프록시를 만들어 위빙한다.
 */
@Aspect
@Component
public class TraceAspect {

    private final TraceLog traceLog;

    public TraceAspect(TraceLog traceLog) {
        this.traceLog = traceLog;
    }

    @Pointcut("@annotation(org.example.aop.Trace)")
    void traced() {
    }

    @Around("traced()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        traceLog.add("START " + method);
        try {
            Object result = joinPoint.proceed(); // 실제 대상 메서드 호출
            traceLog.add("END " + method);
            return result;
        } catch (Throwable e) {
            traceLog.add("EXCEPTION " + method);
            throw e;
        }
    }
}
