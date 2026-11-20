package org.example.core;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Q6. 같은 타입 빈이 여러 개일 때 @Primary 가 기본으로 선택된다. */
@Primary
@Component
public class RateDiscountPolicy implements DiscountPolicy {
    @Override
    public int discount(int price) {
        return price * 10 / 100;
    }
}
