package com.msa.alarm_service.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.msa.alarm_service.api.entity.AlarmTypes;
import com.msa.alarm_service.api.entity.Alarms;
import com.msa.alarm_service.api.entity.UserAlarms;
import com.msa.alarm_service.api.model.AlarmHistoryRequest;
import com.msa.alarm_service.api.model.UserAlarmsDTO;
import com.msa.alarm_service.api.repository.AlarmTypesRepo;
import com.msa.alarm_service.api.repository.AlarmsRepo;
import com.msa.alarm_service.api.repository.UserAlarmsRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    
    private final UserAlarmsRepo userAlarmsRepo;
    private final AlarmsRepo alarmsRepo;
    private final AlarmTypesRepo alarmTypesRepo;
    
    /**
     * 모든 사용자 알림 설정 조회
     */
    public List<UserAlarmsDTO> getUserAlarms(){
        List<UserAlarms> userAlarms = userAlarmsRepo.findAll();
        return userAlarms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 사용자의 알림 설정 조회
     */
    public List<UserAlarmsDTO> getUserAlarmsByUserIndex(Integer userIndex){
        List<UserAlarms> userAlarms = userAlarmsRepo.findByUserIndex(userIndex);
        return userAlarms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 활성화된 알림 설정만 조회
     */
    public List<UserAlarmsDTO> getActiveAlarms(){
        List<UserAlarms> userAlarms = userAlarmsRepo.findActiveAlarms();
        return userAlarms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * UserAlarms 엔티티를 UserAlarmsDTO로 변환
     */
    private UserAlarmsDTO convertToDTO(UserAlarms userAlarm) {
        UserAlarmsDTO dto = new UserAlarmsDTO();
        dto.setUserAlarmsId(userAlarm.getUserAlarmsId());
        dto.setUserIndex(userAlarm.getUserIndex());
        dto.setIsActive(userAlarm.getIsActive());
        dto.setUpdatedAt(userAlarm.getUpdatedAt());
        
        // 알림 타입 정보 설정
        if (userAlarm.getAlarmTypes() != null) {
            dto.setAlarmTypesId(userAlarm.getAlarmTypes().getAlarmTypesId());
            dto.setAlarmTypesCode(userAlarm.getAlarmTypes().getAlarmTypesCode());
            dto.setAlarmTypesLabel(userAlarm.getAlarmTypes().getAlarmTypesLabel());
            dto.setAlarmTypesDescription(userAlarm.getAlarmTypes().getAlarmTypesDescription());
        }
        
        return dto;
    }
    
    /**
     * 특정 알림 타입 조회
     */
    public AlarmTypes getAlarmType(Integer alarmTypeId) {
        return alarmTypesRepo.findById(alarmTypeId)
                .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + alarmTypeId));
    }
    
    /**
     * 알림 내역 저장
     */
    public String saveAlarmHistory(AlarmHistoryRequest request) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // AlarmTypes 조회
            AlarmTypes alarmType = alarmTypesRepo.findById(request.getAlarmTypesId())
                    .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + request.getAlarmTypesId()));
            
            // 각 수신자에 대해 알림 내역 저장
            for (Integer receiverIndex : request.getReceiverIndexes()) {
                Alarms alarm = new Alarms();
                alarm.setAlarmTypes(alarmType);
                alarm.setAlarmMessage(request.getAlarmMessage());
                alarm.setAlarmCreatedAt(now);
                alarm.setAlarmSenderIndex(request.getSenderIndex());
                alarm.setAlarmReceiverIndex(receiverIndex);
                alarm.setIsReceiverRead(0); // 기본값: 읽지 않음
                
                alarmsRepo.save(alarm);
            }
            
            log.info("알림 내역 저장 완료 - 수신자 수: {}, 메시지: {}", 
                    request.getReceiverIndexes().size(), request.getAlarmMessage());
            
            return "알림 내역 저장 성공";
        } catch (Exception e) {
            log.error("알림 내역 저장 실패: {}", e.getMessage());
            return "알림 내역 저장 실패: " + e.getMessage();
        }
    }
}
