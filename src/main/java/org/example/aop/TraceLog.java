package org.example.aop;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** Aspect 가 남긴 로그를 테스트에서 확인하기 위한 저장소 */
@Component
public class TraceLog {

    private final List<String> logs = new CopyOnWriteArrayList<>();

    public void add(String log) { logs.add(log); }
    public List<String> getLogs() { return List.copyOf(logs); }
    public void clear() { logs.clear(); }
}
