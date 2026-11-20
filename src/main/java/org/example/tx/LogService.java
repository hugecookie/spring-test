package org.example.tx;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Q21. 내부 트랜잭션 쪽. 일부러 항상 실패하게 만들었다. */
@Service
public class LogService {

    private final TxLogRepository txLogRepository;

    public LogService(TxLogRepository txLogRepository) {
        this.txLogRepository = txLogRepository;
    }

    /** REQUIRED(기본): 바깥 트랜잭션에 참여 → 여기서 터지면 전체가 rollback-only 로 표시됨 */
    @Transactional
    public void saveRequired(String message) {
        txLogRepository.save(new TxLog(message));
        throw new IllegalStateException("로그 저장 실패");
    }

    /** REQUIRES_NEW: 바깥 트랜잭션을 보류하고 별도 커넥션으로 새 트랜잭션 → 서로 영향 없음 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRequiresNew(String message) {
        txLogRepository.save(new TxLog(message));
        throw new IllegalStateException("로그 저장 실패");
    }
}
