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
@Table(name = "alarms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alarms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer alarmId;

    @ManyToOne
    @JoinColumn(name = "alarmTypes_id")
    private AlarmTypes alarmTypes;

    @Column(name = "alarm_message", length = 200)
    private String alarmMessage;

    @Column(name = "alarm_createdAt")
    private LocalDateTime alarmCreatedAt;

    @Column(name = "alarm_sender_index")
    private Integer alarmSenderIndex;

    @Column(name = "alarm_receiver_index")
    private Integer alarmReceiverIndex;

    @Column(name = "is_receiver_read")
    private Integer isReceiverRead;
}
