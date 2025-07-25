package com.msa.alarm_service.exception;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getResultCode() {
        return errorCode.getCode(); // ErrorCode에서 해당하는 코드값을 반환
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }
}
