package org.example.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Q11, Q12. 빈 생명주기
 * 객체 생성(생성자) → 의존관계 주입(@Value 필드) → @PostConstruct → 사용 → @PreDestroy
 * 생성자 시점에는 필드 주입이 아직 안 됐으므로 url 이 null 이다.
 */
@Component
public class NetworkClient {

    private final List<String> events = new ArrayList<>();

    @Value("${app.network.url:http://localhost:9999}")
    private String url;

    public NetworkClient() {
        events.add("constructor url=" + url); // null
    }

    @PostConstruct
    void connect() {
        events.add("postConstruct url=" + url); // 주입 완료 → 안전하게 사용 가능
    }

    @PreDestroy
    void disconnect() {
        events.add("preDestroy");
    }

    public List<String> getEvents() {
        return events;
    }
}
