package org.example.tx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.member.Member;
import org.example.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 4. 트랜잭션 (Q18 ~ Q22)
 * 주의: 테스트 메서드에 @Transactional 을 붙이지 않는다. (붙이면 테스트가 끝날 때 전부 롤백되어 커밋 여부를 확인할 수 없음)
 */
@SpringBootTest
class TransactionTest {

    @Autowired TxMemberService txMemberService;
    @Autowired TxProbeService txProbeService;
    @Autowired MemberRepository memberRepository;
    @Autowired TxLogRepository txLogRepository;
    @Autowired TransactionTemplate transactionTemplate;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
        txLogRepository.deleteAll();
    }

    @Test
    @DisplayName("Q18. 프로그래밍 방식 트랜잭션: TransactionTemplate")
    void q18_transactionTemplate() {
        transactionTemplate.executeWithoutResult(status -> {
            memberRepository.save(new Member("programmatic"));
            status.setRollbackOnly(); // 직접 롤백 지시
        });
        assertThat(memberRepository.count()).isZero();
    }

    @Test
    @DisplayName("Q19. @Transactional 은 프록시가 트랜잭션을 걸고, 내부 호출엔 적용되지 않는다")
    void q19_proxy() {
        assertThat(AopUtils.isAopProxy(txProbeService)).isTrue();
        assertThat(txProbeService.isTxActive()).isTrue();
        assertThat(txProbeService.callInternally()).isFalse();
    }

    @Test
    @DisplayName("Q20. 런타임 예외 → 롤백")
    void q20_runtimeException_rollback() {
        assertThatThrownBy(() -> txMemberService.saveThenRuntimeException("runtime"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(memberRepository.findByName("runtime")).isEmpty();
    }

    @Test
    @DisplayName("Q20. 체크 예외 → 기본은 커밋")
    void q20_checkedException_commit() {
        assertThatThrownBy(() -> txMemberService.saveThenCheckedException("checked"))
                .isInstanceOf(NotEnoughMoneyException.class);
        assertThat(memberRepository.findByName("checked")).isPresent();
    }

    @Test
    @DisplayName("Q20. rollbackFor = Exception.class → 체크 예외도 롤백")
    void q20_checkedException_rollbackFor() {
        assertThatThrownBy(() -> txMemberService.saveThenCheckedExceptionWithRollbackFor("rollbackFor"))
                .isInstanceOf(NotEnoughMoneyException.class);
        assertThat(memberRepository.findByName("rollbackFor")).isEmpty();
    }

    @Test
    @DisplayName("Q20. 예외를 catch 로 삼키면 프록시가 모르므로 커밋")
    void q20_swallowedException_commit() {
        txMemberService.saveThenSwallowException("swallow");
        assertThat(memberRepository.findByName("swallow")).isPresent();
    }

    @Test
    @DisplayName("Q21. REQUIRED: 내부 예외를 바깥에서 잡아도 rollback-only → UnexpectedRollbackException, 전부 롤백")
    void q21_required() {
        assertThatThrownBy(() -> txMemberService.joinWithRequired("required"))
                .isInstanceOf(UnexpectedRollbackException.class);
        assertThat(memberRepository.findByName("required")).isEmpty();
        assertThat(txLogRepository.count()).isZero();
    }

    @Test
    @DisplayName("Q21. REQUIRES_NEW: 내부만 롤백되고 바깥(회원)은 커밋")
    void q21_requiresNew() {
        txMemberService.joinWithRequiresNew("requiresNew");
        assertThat(memberRepository.findByName("requiresNew")).isPresent();
        assertThat(txLogRepository.count()).isZero();
    }

    @Test
    @DisplayName("Q22. 격리 수준을 지정한 읽기 전용 트랜잭션")
    void q22_isolation() {
        memberRepository.save(new Member("isolation"));
        assertThat(txMemberService.countMembers()).isEqualTo(1);
    }
}
