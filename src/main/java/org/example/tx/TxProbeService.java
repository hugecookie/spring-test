package org.example.tx;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Q17, Q19. 트랜잭션이 실제로 걸렸는지 확인하는 서비스 */
@Service
public class TxProbeService {

    @Transactional
    public boolean isTxActive() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }

    /** 내부 호출 → 프록시를 거치지 않으므로 트랜잭션이 없다 */
    public boolean callInternally() {
        return isTxActive();
    }
}
