package org.example.tx;

import org.example.member.Member;
import org.example.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Q18~Q22. 선언적 트랜잭션(@Transactional) 예제
 * Q19. 프록시가 호출을 가로채 → 트랜잭션 시작(커넥션을 ThreadLocal 에 바인딩) → 정상: 커밋 / 예외: 롤백
 */
@Service
public class TxMemberService {

    private final MemberRepository memberRepository;
    private final LogService logService;

    public TxMemberService(MemberRepository memberRepository, LogService logService) {
        this.memberRepository = memberRepository;
        this.logService = logService;
    }

    /** Q20. 런타임 예외 → 롤백 */
    @Transactional
    public void saveThenRuntimeException(String name) {
        memberRepository.save(new Member(name));
        throw new IllegalStateException("런타임 예외");
    }

    /** Q20. 체크 예외 → 기본은 커밋! */
    @Transactional
    public void saveThenCheckedException(String name) throws NotEnoughMoneyException {
        memberRepository.save(new Member(name));
        throw new NotEnoughMoneyException("잔고 부족");
    }

    /** Q20. rollbackFor 지정 → 체크 예외도 롤백 */
    @Transactional(rollbackFor = Exception.class)
    public void saveThenCheckedExceptionWithRollbackFor(String name) throws NotEnoughMoneyException {
        memberRepository.save(new Member(name));
        throw new NotEnoughMoneyException("잔고 부족");
    }

    /** Q20. 예외를 catch 로 삼키면 프록시는 예외를 모르므로 커밋된다 */
    @Transactional
    public void saveThenSwallowException(String name) {
        memberRepository.save(new Member(name));
        try {
            throw new IllegalStateException("삼켜진 예외");
        } catch (IllegalStateException ignored) {
        }
    }

    /** Q21. REQUIRED 내부 실패를 바깥에서 잡아도 → 커밋 시 UnexpectedRollbackException */
    @Transactional
    public void joinWithRequired(String name) {
        memberRepository.save(new Member(name));
        try {
            logService.saveRequired(name);
        } catch (IllegalStateException ignored) {
        }
    }

    /** Q21. REQUIRES_NEW 내부 실패는 바깥에서 잡으면 → 회원은 커밋, 로그만 롤백 */
    @Transactional
    public void joinWithRequiresNew(String name) {
        memberRepository.save(new Member(name));
        try {
            logService.saveRequiresNew(name);
        } catch (IllegalStateException ignored) {
        }
    }

    /** Q22. 격리 수준 지정 예 (기본은 DB 기본값: MySQL=REPEATABLE READ, PostgreSQL=READ COMMITTED) */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public long countMembers() {
        return memberRepository.count();
    }
}
