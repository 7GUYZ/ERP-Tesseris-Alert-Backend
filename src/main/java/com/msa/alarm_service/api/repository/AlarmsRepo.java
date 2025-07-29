package com.msa.alarm_service.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.msa.alarm_service.api.entity.Alarms;

@Repository
public interface AlarmsRepo extends JpaRepository<Alarms, Integer> {
    // 기본 CRUD 메서드들은 JpaRepository에서 제공
} 