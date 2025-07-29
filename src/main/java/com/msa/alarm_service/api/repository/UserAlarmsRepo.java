package com.msa.alarm_service.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.msa.alarm_service.api.entity.UserAlarms;

@Repository
public interface UserAlarmsRepo extends JpaRepository<UserAlarms, Integer> {
    
    /**
     * 특정 사용자의 알림 설정 조회
     */
    List<UserAlarms> findByUserIndex(Integer userIndex);
    
    /**
     * 특정 알림 타입이 활성화된 사용자들의 user_index 조회
     */
    @Query("SELECT ua.userIndex FROM UserAlarms ua WHERE ua.alarmTypes.alarmTypesId = :alarmTypeId AND ua.isActive = 1")
    List<Integer> findActiveUserIndexesByAlarmType(@Param("alarmTypeId") Integer alarmTypeId);
    
    /**
     * 특정 사용자의 특정 알림 타입 설정 조회
     */
    UserAlarms findByUserIndexAndAlarmTypes_AlarmTypesId(Integer userIndex, Integer alarmTypesId);
    
    /**
     * 특정 사용자의 특정 알림 타입 설정 존재 여부 확인
     */
    boolean existsByUserIndexAndAlarmTypes_AlarmTypesId(Integer userIndex, Integer alarmTypesId);
    
    /**
     * 알림 설정이 활성화된 사용자 목록 조회
     */
    @Query("SELECT ua FROM UserAlarms ua WHERE ua.isActive = 1")
    List<UserAlarms> findActiveAlarms();
}
