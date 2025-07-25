package com.msa.alarm_service.exception;

public enum ErrorCode {
    // 알림 서비스 관련 에러 코드
    ALARM_NOT_FOUND(1001, "알림을 찾을 수 없습니다."),
    SSE_CONNECTION_FAILED(1002, "SSE 연결에 실패했습니다."),
    INVALID_USER_ID(1003, "유효하지 않은 사용자 ID입니다.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
