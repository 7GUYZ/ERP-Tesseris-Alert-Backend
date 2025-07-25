package com.msa.alarm_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.msa.alarm_service.util.SseEmiters;

/**
 * SSE 관련 설정 클래스
 * SseEmiters를 Spring Bean으로 등록합니다.
 */
@Configuration
public class SseConfig {

    /**
     * SseEmiters Bean을 생성합니다.
     * 
     * @return SseEmiters 인스턴스
     */
    @Bean
    public SseEmiters sseEmiters() {
        return new SseEmiters();
    }
} 