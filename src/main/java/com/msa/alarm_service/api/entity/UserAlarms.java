package com.msa.alarm_service.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userAlarms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAlarms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userAlarms_id")
    private Integer userAlarmsId;

    @Column(name = "user_index")
    private Integer userIndex;

    @ManyToOne
    @JoinColumn(name = "alarmTypes_id")
    private AlarmTypes alarmTypes;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

} 
