package org.example.common;

/** Q27. 모든 API 가 같은 구조로 내려주는 공통 에러 응답 */
public record ErrorResponse(String code, String message) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
