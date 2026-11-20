package org.example.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Q6. @Qualifier 는 @Primary 보다 우선한다. */
@Service
public class FixOrderService {

    private final DiscountPolicy discountPolicy;

    public FixOrderService(@Qualifier("fixDiscountPolicy") DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public int order(int price) {
        return price - discountPolicy.discount(price);
    }
}
