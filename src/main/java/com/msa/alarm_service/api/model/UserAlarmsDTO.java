package com.msa.alarm_service.api.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAlarmsDTO {
    private Integer userAlarmsId;
    private Integer userIndex;
    private Integer alarmTypesId;
    private Integer isActive;
    private LocalDateTime updatedAt;
    
    // 알림 타입 정보 (선택적)
    private String alarmTypesCode;
    private String alarmTypesLabel;
    private String alarmTypesDescription;
}
