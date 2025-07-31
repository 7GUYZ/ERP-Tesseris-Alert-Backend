package com.msa.alarm_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    /**
     * 루트 경로 핸들러 - 서비스 상태 확인
     */
    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", 200);
        response.put("message", "Alert Backend Service is running");
        response.put("service", "ERP Tesseris Alert Backend");
        response.put("version", "1.0.0");
        response.put("status", "healthy");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "alert-backend");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
} 