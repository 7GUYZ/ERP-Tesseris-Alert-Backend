package com.msa.alarm_service.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarmTypes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarmTypes_id")    
    private Integer alarmTypesId;

    @Column(name = "alarmTypes_code")
    private String alarmTypesCode;

    @Column(name = "alarmTypes_label")
    private String alarmTypesLabel;

    @Column(name = "alarmTypes_description")
    private String alarmTypesDescription;

    @Column(name = "alarmTypes_createdAt")
    private LocalDateTime alarmTypesCreatedAt;
}
