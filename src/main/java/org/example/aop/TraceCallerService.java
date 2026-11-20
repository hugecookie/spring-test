package org.example.aop;

import org.springframework.stereotype.Service;

/** Q17 해결책: 호출 대상을 별도 빈으로 분리해 "프록시를 통해" 호출한다. */
@Service
public class TraceCallerService {

    private final TraceTargetService traceTargetService; // 주입된 것은 CGLIB 프록시

    public TraceCallerService(TraceTargetService traceTargetService) {
        this.traceTargetService = traceTargetService;
    }

    public String call() {
        return traceTargetService.hello("external");
    }
}
