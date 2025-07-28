package com.msa.alarm_service.api.controller;

import com.msa.alarm_service.api.model.AlarmHistoryResponseDTO;
import com.msa.alarm_service.api.service.AlarmHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarms/alarm-history")
@RequiredArgsConstructor
@Slf4j
public class AlarmHistoryController {
    
    private final AlarmHistoryService alarmHistoryService;
    
    /**
     * 테스트용 엔드포인트
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("AlarmHistoryController 테스트 성공!");
    }
    
    /**
     * 사용자별 알림 내역 조회
     */
    @GetMapping("/user/{userIndex}")
    public ResponseEntity<List<AlarmHistoryResponseDTO>> getUserAlarmHistory(
            @PathVariable("userIndex") Integer userIndex) {
        try {
            log.info("알림 내역 조회 요청 - userIndex: {}", userIndex);
            
            List<AlarmHistoryResponseDTO> alarms = alarmHistoryService.getUserAlarmHistory(userIndex);
            
            log.info("알림 내역 조회 성공 - 개수: {}", alarms.size());
            return ResponseEntity.ok(alarms);
            
        } catch (Exception e) {
            log.error("알림 내역 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of()); // 빈 리스트 반환으로 테스트
        }
    }

    /**
     * 알림 읽음 처리
     */
    @PostMapping("/alarms/{alarmId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable("alarmId") Integer alarmId) {
        try {
            log.info("알림 읽음 처리 요청 - alarmId: {}", alarmId);
            
            alarmHistoryService.markAsRead(alarmId);
            
            log.info("알림 읽음 처리 성공 - alarmId: {}", alarmId);
            return ResponseEntity.ok("읽음 처리 완료");
            
        } catch (Exception e) {
            log.error("알림 읽음 처리 실패 - alarmId: {}, error: {}", alarmId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("읽음 처리 실패");
        }
    }
} 