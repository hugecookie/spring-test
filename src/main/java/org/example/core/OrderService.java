package org.example.core;

import org.springframework.stereotype.Service;

/**
 * Q3. 생성자 주입 (권장)
 *  - final 로 불변 + 누락 시 컴파일/생성 시점에 발견
 *  - 생성자가 하나면 @Autowired 생략 가능 (Spring 4.3+)
 *  - Spring 없이 new OrderService(가짜정책) 로 테스트 가능
 *
 * 비교용 (권장하지 않음):
 *   @Autowired private DiscountPolicy discountPolicy;          // 필드 주입: final 불가, 순수 자바 테스트 불가
 *   @Autowired public void setDiscountPolicy(DiscountPolicy p) // 수정자 주입: 선택적 의존성에만
 */
@Service
public class OrderService {

    private final DiscountPolicy discountPolicy; // @Primary → RateDiscountPolicy 가 주입됨

    public OrderService(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public int order(int price) {
        return price - discountPolicy.discount(price);
    }
}
