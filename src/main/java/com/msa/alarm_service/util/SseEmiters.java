package com.msa.alarm_service.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE(Server-Sent Events) 연결을 관리하는 유틸리티 클래스
 * 
 * 이 클래스는 사용자별 SSE 연결을 관리하고, 실시간 알림을 전송하는 역할을 담당합니다.
 * ConcurrentHashMap을 사용하여 스레드 안전성을 보장합니다.
 * 
 * @author MSA Team
 * @version 1.0
 */
public class SseEmiters {
    
    /**
     * 사용자별 SSE Emitter를 저장하는 Map
     * 
     * Key: 사용자 ID (String)
     * Value: 해당 사용자의 SSE Emitter
     * 
     * ConcurrentHashMap을 사용하여 여러 스레드에서 동시에 접근해도 안전합니다.
     */
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 사용자의 SSE 연결을 생성하고 관리합니다.
     * 
     * @param userId 연결할 사용자의 ID
     * @return 생성된 SseEmitter 객체
     * 
     * 동작 과정:
     * 1. 60초 타임아웃으로 새로운 SseEmitter 생성
     * 2. 사용자 ID와 Emitter를 Map에 저장
     * 3. 연결 완료/타임아웃 시 자동으로 Map에서 제거
     */
    public SseEmitter connect(String userId) {
        // 60초 타임아웃으로 SSE Emitter 생성
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        
        // 사용자 ID와 Emitter를 Map에 저장
        emitters.put(userId, emitter);
        
        // 연결이 완료되면 Map에서 제거 (정상 종료)
        emitter.onCompletion(() -> emitters.remove(userId));
        
        // 타임아웃이 발생하면 Map에서 제거 (비정상 종료)
        emitter.onTimeout(() -> emitters.remove(userId));
        
        return emitter;
    }

    /**
     * 특정 사용자에게 실시간 알림을 전송합니다.
     * 
     * @param userId 알림을 받을 사용자의 ID
     * @param message 전송할 알림 메시지
     * 
     * 동작 과정:
     * 1. 사용자 ID로 저장된 Emitter를 찾음
     * 2. Emitter가 존재하면 알림 메시지 전송
     * 3. 전송 실패 시 Map에서 해당 사용자 제거
     * 
     * 주의사항:
     * - 사용자가 연결되어 있지 않으면 알림이 전송되지 않음
     * - IOException 발생 시 자동으로 연결 해제
     */
    public void send(String userId, String message) {
        // 사용자 ID로 저장된 Emitter를 찾음
        SseEmitter emitter = emitters.get(userId);
        
        // Emitter가 존재하면 알림 전송
        if (emitter != null) {
            try {
                // SSE 이벤트 형태로 알림 전송
                // name: "notification" - 클라이언트에서 구분할 수 있는 이벤트 이름
                // data: message - 실제 전송할 알림 내용
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message));
            } catch (IOException e) {
                // 전송 실패 시 Map에서 해당 사용자 제거
                // (연결이 끊어진 것으로 간주)
                emitters.remove(userId);
            }
        }
    }
    
    /**
     * 사용자가 연결되어 있는지 확인합니다.
     * 
     * @param userId 확인할 사용자의 ID
     * @return 연결 여부 (true: 연결됨, false: 연결되지 않음)
     */
    public boolean isConnected(String userId) {
        return emitters.containsKey(userId);
    }
    
    /**
     * 현재 연결된 사용자 수를 반환합니다.
     * 
     * @return 연결된 사용자 수
     */
    public int getConnectedUserCount() {
        return emitters.size();
    }
    
    /**
     * 특정 사용자의 연결을 강제로 해제합니다.
     * 
     * @param userId 연결을 해제할 사용자의 ID
     */
    public void disconnect(String userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
        }
    }
    
    /**
     * 모든 연결된 사용자에게 알림을 전송합니다.
     * 
     * @param message 전송할 알림 메시지
     */
    public void sendToAll(String message) {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message));
            } catch (IOException e) {
                // 전송 실패 시 해당 사용자 제거
                emitters.remove(userId);
            }
        });
    }
    
    /**
     * 모든 SSE 연결을 해제합니다.
     * 주로 서버 종료 시 사용됩니다.
     */
    public void disconnectAll() {
        emitters.forEach((userId, emitter) -> {
            emitter.complete();
        });
        emitters.clear();
    }
}
