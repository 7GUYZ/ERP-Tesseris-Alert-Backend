package com.msa.alarm_service.api.service;

import com.msa.alarm_service.api.entity.Alarms;
import com.msa.alarm_service.api.entity.AlarmTypes;
import com.msa.alarm_service.api.model.AlarmHistoryResponseDTO;
import com.msa.alarm_service.api.repository.AlarmHistoryRepo;
import com.msa.alarm_service.api.repository.AlarmTypesRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmHistoryService {
    
    private final AlarmHistoryRepo alarmHistoryRepo;
    private final AlarmTypesRepo alarmTypesRepo;
    
    /**
     * 사용자별 알림 내역 조회
     */
    public List<AlarmHistoryResponseDTO> getUserAlarmHistory(Integer userIndex) {
        log.info("알림 내역 조회 시작 - userIndex: {}", userIndex);
        
        List<Alarms> alarms = alarmHistoryRepo.findByUserIndexOrderByCreatedAtDesc(userIndex);
        
        List<AlarmHistoryResponseDTO> result = alarms.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        log.info("알림 내역 조회 완료 - 개수: {}", result.size());
        return result;
    }
    
    /**
     * 알림 읽음 처리
     */
    public void markAsRead(Integer alarmId) {
        log.info("알림 읽음 처리 시작 - alarmId: {}", alarmId);
        
        Alarms alarm = alarmHistoryRepo.findById(alarmId)
            .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다. alarmId: " + alarmId));
        
        // is_receiver_read를 1로 변경
        alarm.setIsReceiverRead(1);
        alarmHistoryRepo.save(alarm);
        
        log.info("알림 읽음 처리 완료 - alarmId: {}", alarmId);
    }
    
    /**
     * Alarms 엔티티를 AlarmHistoryResponseDTO로 변환
     */
    private AlarmHistoryResponseDTO convertToResponseDTO(Alarms alarm) {
        AlarmTypes alarmType = alarm.getAlarmTypes();
        
        return AlarmHistoryResponseDTO.builder()
            .alarmId(alarm.getAlarmId())
            .title(alarmType != null ? alarmType.getAlarmTypesLabel() : "알림")
            .message(alarm.getAlarmMessage())
            .createdAt(alarm.getAlarmCreatedAt())
            .alarmTypeLabel(alarmType != null ? alarmType.getAlarmTypesLabel() : null)
            .alarmTypeCode(alarmType != null ? alarmType.getAlarmTypesCode() : null)
            .isRead(alarm.getIsReceiverRead())
            .build();
    }
} 