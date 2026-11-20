package org.example.tx;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TxLogRepository extends JpaRepository<TxLog, Long> {
}
