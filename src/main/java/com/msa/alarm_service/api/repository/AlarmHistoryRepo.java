package com.msa.alarm_service.api.repository;

import com.msa.alarm_service.api.entity.Alarms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlarmHistoryRepo extends JpaRepository<Alarms, Integer> {
    
    // 사용자별 알림 내역 조회 (최근 한달, 최신순)
    @Query("SELECT a FROM Alarms a WHERE a.alarmReceiverIndex = :userIndex " +
           "AND a.alarmCreatedAt >= :oneMonthAgo " +
           "ORDER BY a.alarmCreatedAt DESC")
    List<Alarms> findByUserIndexOrderByCreatedAtDesc(
        @Param("userIndex") Integer userIndex,
        @Param("oneMonthAgo") LocalDateTime oneMonthAgo
    );
} 