package org.example.bean;

import org.springframework.stereotype.Service;

/** Q9. 올바른 예: 무상태(stateless). 상태는 지역 변수/파라미터/반환값으로 다룬다. */
@Service
public class StatelessPriceService {

    public int order(String user, int price) {
        int discounted = price; // 지역 변수는 스레드마다 스택에 따로 생긴다
        return discounted;
    }
}
