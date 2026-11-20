package org.example.bean;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/** Q10. 싱글톤에 prototype 을 주입할 때의 문제와 해결 */
@Component
public class SingletonClient {

    // 문제: 주입 시점에 딱 한 번 생성 → 사실상 싱글톤처럼 계속 같은 객체를 씀
    private final PrototypeBean injectedOnce;

    // 해결: 필요할 때마다 컨테이너에서 새로 꺼낸다 (DL, Dependency Lookup)
    private final ObjectProvider<PrototypeBean> prototypeProvider;

    public SingletonClient(PrototypeBean injectedOnce, ObjectProvider<PrototypeBean> prototypeProvider) {
        this.injectedOnce = injectedOnce;
        this.prototypeProvider = prototypeProvider;
    }

    public int logicWithInjected() {
        return injectedOnce.addCount();
    }

    public int logicWithProvider() {
        return prototypeProvider.getObject().addCount();
    }
}
