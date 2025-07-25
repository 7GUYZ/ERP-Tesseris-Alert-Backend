package com.msa.alarm_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {

        // 결과를 Map 형식으로 구성
        Map<String, Object> response = new HashMap<>();

        // ErrorCode에서 resultCode를 가져오고, message는 ex.getMessage()에서 받아오기
        response.put("resultCode", ex.getResultCode());  // 에러 코드
        response.put("message", ex.getErrorMessage());  // 에러 메시지
        response.put("data", null);  // data는 null로 고정

        return ResponseEntity.badRequest().body(response);
    }    
}
