package org.example.core;

/**
 * Q2, Q6, Q7(전략 패턴). OrderService는 이 인터페이스(역할)에만 의존하고
 * 어떤 구현체(구현)가 들어올지는 컨테이너가 결정한다 = IoC / DI.
 */
public interface DiscountPolicy {
    int discount(int price);
}
