package com.msa.alarm_service.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseResponse<T> {
    private int resultCode;
    private String message;
    @JsonProperty("data")
    private T data;


    public BaseResponse(int resultCode, String message, T data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    // 성공 응답 생성
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "Success", data);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(200, message, data);
    }

    // 에러 응답 생성
    public static <T> BaseResponse<T> error(int resultCode, String message) {
        return new BaseResponse<>(resultCode, message, null);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(-1, message, null);
    }
}
