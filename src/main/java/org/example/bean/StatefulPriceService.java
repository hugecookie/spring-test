package org.example.bean;

import org.springframework.stereotype.Service;

/**
 * Q9. 잘못된 예: 싱글톤에 변경 가능한 인스턴스 필드(price)를 둔 상태 유지(stateful) 설계.
 * 모든 요청 스레드가 같은 객체를 공유하므로 사용자 A 의 값이 B 에 의해 덮어써진다.
 */
@Service
public class StatefulPriceService {

    private int price; // 공유 필드!

    public void order(String user, int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
