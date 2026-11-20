package org.example.core;

import org.springframework.stereotype.Component;

/** Q5. @Component: 컴포넌트 스캔으로 자동 등록 (빈 이름: fixDiscountPolicy) */
@Component
public class FixDiscountPolicy implements DiscountPolicy {
    @Override
    public int discount(int price) {
        return 1000;
    }
}
