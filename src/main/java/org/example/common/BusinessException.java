package org.example.common;

/** Q27. 비즈니스 예외 (RuntimeException 이므로 @Transactional 에서 롤백 대상) */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
