package org.example.core;

import java.util.Map;
import org.springframework.stereotype.Service;

/** Q6, Q7. 같은 타입 빈을 Map<빈이름, 빈> 으로 모두 받아 런타임에 골라 쓰는 전략 패턴 */
@Service
public class DiscountRouter {

    private final Map<String, DiscountPolicy> policies;

    public DiscountRouter(Map<String, DiscountPolicy> policies) {
        this.policies = policies;
    }

    public int discount(String policyBeanName, int price) {
        DiscountPolicy policy = policies.get(policyBeanName);
        if (policy == null) {
            throw new IllegalArgumentException("없는 할인 정책: " + policyBeanName);
        }
        return policy.discount(price);
    }

    public Map<String, DiscountPolicy> getPolicies() {
        return policies;
    }
}
