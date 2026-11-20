package org.example.bean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Q8, Q10. prototype 스코프: 조회할 때마다 새 인스턴스.
 * 컨테이너는 생성·주입·초기화까지만 관여하고 @PreDestroy 는 호출하지 않는다.
 */
@Component
@Scope("prototype")
public class PrototypeBean {

    private int count;
    private boolean initialized;
    private boolean destroyed;

    @PostConstruct
    void init() {
        initialized = true;
    }

    @PreDestroy
    void destroy() {
        destroyed = true; // prototype 이므로 컨테이너가 호출하지 않음
    }

    public int addCount() {
        return ++count;
    }

    public boolean isInitialized() { return initialized; }
    public boolean isDestroyed() { return destroyed; }
}
