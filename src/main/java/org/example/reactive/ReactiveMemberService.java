package org.example.reactive;

import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** Q41. Mono = 0~1개, Flux = 0~N개를 발행하는 Reactor 타입 */
@Service
public class ReactiveMemberService {

    private final Map<Long, String> store = new TreeMap<>(Map.of(1L, "kim", 2L, "lee", 3L, "park"));

    public Mono<String> findName(Long id) {
        return Mono.justOrEmpty(store.get(id));
    }

    public Flux<String> findAllUpperCase() {
        return Flux.fromIterable(store.values()).map(String::toUpperCase);
    }
}
