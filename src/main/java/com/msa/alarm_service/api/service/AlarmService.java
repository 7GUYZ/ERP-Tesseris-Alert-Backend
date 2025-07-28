package com.msa.alarm_service.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
    /**
     * 사용자의 특정 알림 타입 설정 조회
     */
    public Map<String, Object> getUserAlarmSetting(Integer userIndex, Integer alarmTypesId) {
        log.info("🔍 사용자 알림 설정 조회 - userIndex: {}, alarmTypesId: {}", userIndex, alarmTypesId);
        
        try {
            // 특정 사용자의 알림 설정 조회
            List<UserAlarmsDTO> userAlarms = getUserAlarmsByUserIndex(userIndex);
            
            // 특정 알림 타입의 설정 찾기
            Optional<UserAlarmsDTO> targetAlarm = userAlarms.stream()
                    .filter(alarm -> alarmTypesId.equals(alarm.getAlarmTypesId()))
                    .findFirst();
            
            Map<String, Object> response = new HashMap<>();
            
            if (targetAlarm.isPresent()) {
                // 설정이 있는 경우: isActive 값 반환
                UserAlarmsDTO alarm = targetAlarm.get();
                Integer isActive = alarm.getIsActive();
                response.put("hasSetting", true);
                response.put("isActive", isActive);
                response.put("message", isActive == 1 ? "알림 활성화" : "알림 비활성화");
                
                log.info("✅ 알림 설정 조회 완료 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                    userIndex, alarmTypesId, isActive);
            } else {
                // 설정이 없는 경우: 기본값 반환
                response.put("hasSetting", false);
                response.put("isActive", null);
                response.put("message", "알림 설정 없음 (기본값: 활성화)");
                
                log.info("✅ 알림 설정 없음 - userIndex: {}, alarmTypesId: {}", userIndex, alarmTypesId);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ 사용자 알림 설정 조회 실패 - userIndex: {}, alarmTypesId: {}", userIndex, alarmTypesId, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "알림 설정 조회에 실패했습니다.");
            errorResponse.put("message", e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 사용자의 알림 설정 업데이트
     */
    public Map<String, Object> updateUserAlarmSetting(Integer userIndex, Integer alarmTypesId, Integer isActive) {
        log.info("🔧 사용자 알림 설정 업데이트 - userIndex: {}, alarmTypesId: {}, isActive: {}", userIndex, alarmTypesId, isActive);
        
        try {
            // 기존 설정 조회
            UserAlarms existingSetting = userAlarmsRepo.findByUserIndexAndAlarmTypes_AlarmTypesId(userIndex, alarmTypesId);
            
            if (existingSetting != null) {
                // 기존 설정이 있는 경우: 업데이트
                existingSetting.setIsActive(isActive);
                existingSetting.setUpdatedAt(LocalDateTime.now());
                userAlarmsRepo.save(existingSetting);
                
                log.info("✅ 기존 알림 설정 업데이트 완료 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                    userIndex, alarmTypesId, isActive);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "알림 설정이 업데이트되었습니다.");
                response.put("isActive", isActive);
                
                return response;
                
            } else {
                // 기존 설정이 없는 경우: 새로 생성
                AlarmTypes alarmType = alarmTypesRepo.findById(alarmTypesId)
                    .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다: " + alarmTypesId));
                
                UserAlarms newUserAlarm = new UserAlarms();
                newUserAlarm.setUserIndex(userIndex);
                newUserAlarm.setAlarmTypes(alarmType);
                newUserAlarm.setIsActive(isActive);
                newUserAlarm.setUpdatedAt(LocalDateTime.now());
                
                userAlarmsRepo.save(newUserAlarm);
                
                log.info("✅ 새 알림 설정 생성 완료 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                    userIndex, alarmTypesId, isActive);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "새 알림 설정이 생성되었습니다.");
                response.put("isActive", isActive);
                
                return response;
            }
            
        } catch (Exception e) {
            log.error("❌ 사용자 알림 설정 업데이트 실패 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                userIndex, alarmTypesId, isActive, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "알림 설정 업데이트에 실패했습니다.");
            errorResponse.put("message", e.getMessage());
            
            return errorResponse;
        }
    }
}
