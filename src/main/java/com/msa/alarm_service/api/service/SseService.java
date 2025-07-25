package com.msa.alarm_service.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.msa.alarm_service.util.SseEmiters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SSE(Server-Sent Events) 서비스 클래스
 * 
 * SSE 연결 관리와 알림 전송을 담당하는 비즈니스 로직을 처리합니다.
 * SseEmiters 유틸리티를 사용하여 실제 SSE 기능을 구현합니다.
 * 
 * @author MSA Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {
    
    // SSE 연결을 관리하는 유틸리티
    private final SseEmiters sseEmiters;
    
    /**
     * 사용자의 SSE 연결을 생성합니다.
     * 
     * @param userId 연결할 사용자의 ID
     * @return 생성된 SseEmitter 객체
     * @throws IllegalArgumentException 사용자 ID가 유효하지 않은 경우
     */
    public SseEmitter connect(String userId) {
        // 1. 사용자 ID 검증
        validateUserId(userId);
        
        // 2. 연결 시작 로깅
        log.info("사용자 {} SSE 연결 시작", userId);
        
        // 3. 중복 연결 체크 및 경고
        if (sseEmiters.isConnected(userId)) {
            log.warn("사용자 {} 이미 연결되어 있음 - 기존 연결을 대체합니다", userId);
        }
        
        try {
            // 4. 실제 SSE 연결 생성
            SseEmitter emitter = sseEmiters.connect(userId);
            
            // 5. 연결 성공 로깅
            log.info("사용자 {} SSE 연결 성공", userId);
            
            return emitter;
            
        } catch (Exception e) {
            // 6. 연결 실패 시 로깅
            log.error("사용자 {} SSE 연결 실패: {}", userId, e.getMessage());
            throw new RuntimeException("SSE 연결 생성에 실패했습니다.", e);
        }
    }
    
    /**
     * 특정 사용자에게 알림을 전송합니다.
     * 
     * @param userId 알림을 받을 사용자의 ID
     * @param message 전송할 알림 메시지
     */
    public void sendNotification(String userId, String message) {
        // 1. 파라미터 검증
        validateUserId(userId);
        validateMessage(message);
        
        // 2. 연결 상태 확인
        if (!sseEmiters.isConnected(userId)) {
            log.warn("사용자 {}가 연결되어 있지 않아 알림을 전송할 수 없습니다", userId);
            return;
        }
        
        // 3. 알림 전송 시작 로깅
        log.info("사용자 {}에게 알림 전송 시작: {}", userId, message);
        
        try {
            // 4. 실제 알림 전송
            sseEmiters.send(userId, message);
            
            // 5. 전송 성공 로깅
            log.info("사용자 {} 알림 전송 완료", userId);
            
        } catch (Exception e) {
            // 6. 전송 실패 시 로깅
            log.error("사용자 {} 알림 전송 실패: {}", userId, e.getMessage());
        }
    }
    
    /**
     * 모든 연결된 사용자에게 알림을 전송합니다.
     * 
     * @param message 전송할 알림 메시지
     */
    public void sendNotificationToAll(String message) {
        // 1. 메시지 검증
        validateMessage(message);
        
        // 2. 연결된 사용자 수 확인
        int connectedCount = sseEmiters.getConnectedUserCount();
        if (connectedCount == 0) {
            log.warn("연결된 사용자가 없어 알림을 전송할 수 없습니다");
            return;
        }
        
        // 3. 전체 전송 시작 로깅
        log.info("모든 사용자({}명)에게 알림 전송 시작: {}", connectedCount, message);
        
        try {
            // 4. 실제 전체 전송
            sseEmiters.sendToAll(message);
            
            // 5. 전송 성공 로깅
            log.info("모든 사용자 알림 전송 완료");
            
        } catch (Exception e) {
            // 6. 전송 실패 시 로깅
            log.error("전체 알림 전송 실패: {}", e.getMessage());
        }
    }
    
    /**
     * 특정 사용자의 연결을 강제로 해제합니다.
     * 
     * @param userId 연결을 해제할 사용자의 ID
     */
    public void disconnect(String userId) {
        // 1. 사용자 ID 검증
        validateUserId(userId);
        
        // 2. 연결 해제 시작 로깅
        log.info("사용자 {} SSE 연결 해제 시작", userId);
        
        try {
            // 3. 실제 연결 해제
            sseEmiters.disconnect(userId);
            
            // 4. 해제 성공 로깅
            log.info("사용자 {} SSE 연결 해제 완료", userId);
            
        } catch (Exception e) {
            // 5. 해제 실패 시 로깅
            log.error("사용자 {} SSE 연결 해제 실패: {}", userId, e.getMessage());
        }
    }
    
    /**
     * 현재 연결된 사용자 수를 반환합니다.
     * 
     * @return 연결된 사용자 수
     */
    public int getConnectedUserCount() {
        return sseEmiters.getConnectedUserCount();
    }
    
    /**
     * 특정 사용자가 연결되어 있는지 확인합니다.
     * 
     * @param userId 확인할 사용자의 ID
     * @return 연결 여부
     */
    public boolean isConnected(String userId) {
        return sseEmiters.isConnected(userId);
    }
    
    /**
     * 사용자 ID의 유효성을 검증합니다.
     * 
     * @param userId 검증할 사용자 ID
     * @throws IllegalArgumentException 유효하지 않은 사용자 ID인 경우
     */
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.error("유효하지 않은 사용자 ID: {}", userId);
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }
    }
    
    /**
     * 메시지의 유효성을 검증합니다.
     * 
     * @param message 검증할 메시지
     * @throws IllegalArgumentException 유효하지 않은 메시지인 경우
     */
    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            log.error("유효하지 않은 메시지: {}", message);
            throw new IllegalArgumentException("메시지가 필요합니다.");
        }
    }
}
