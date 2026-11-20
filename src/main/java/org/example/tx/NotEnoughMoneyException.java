package org.example.tx;

/** Q20. 체크 예외 (기본 설정에서는 롤백되지 않고 커밋된다) */
public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
