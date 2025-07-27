package com.msa.alarm_service.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.alarm_service.api.entity.AlarmTypes;
import com.msa.alarm_service.api.model.AlarmHistoryRequest;
import com.msa.alarm_service.api.model.UserAlarmsDTO;
import com.msa.alarm_service.api.service.AlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
@Slf4j
public class AlarmController {
    private final AlarmService alarmService;

    @GetMapping("/HelloAlarm")
    public String helloAlarm(){
        return "HELLO ALARM MSA TEST OK";
    }

    /**
     * 모든 사용자 알림 설정 조회
     */
    @GetMapping("/getUserAlarms")
    public List<UserAlarmsDTO> getUserAlarms(){
        return alarmService.getUserAlarms();
    }
    
    /**
     * 특정 사용자의 알림 설정 조회
     */
    @GetMapping("/getUserAlarms/{userIndex}")
    public List<UserAlarmsDTO> getUserAlarmsByUserIndex(@PathVariable("userIndex") Integer userIndex){
        return alarmService.getUserAlarmsByUserIndex(userIndex);
    }
    
    /**
     * 활성화된 알림 설정만 조회
     */
    @GetMapping("/getActiveAlarms")
    public List<UserAlarmsDTO> getActiveAlarms(){
        return alarmService.getActiveAlarms();
    }
    
    /**
     * 알림 내역 저장
     */
    @PostMapping("/saveAlarmHistory")
    public String saveAlarmHistory(@RequestBody AlarmHistoryRequest request){
        return alarmService.saveAlarmHistory(request);
    }
    
    /**
     * 특정 알림 타입 조회
     */
    @GetMapping("/getAlarmType/{alarmTypeId}")
    public AlarmTypes getAlarmType(@PathVariable("alarmTypeId") Integer alarmTypeId){
        return alarmService.getAlarmType(alarmTypeId);
    }
}
