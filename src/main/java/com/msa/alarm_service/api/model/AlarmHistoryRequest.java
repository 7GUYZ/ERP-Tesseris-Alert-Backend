package com.msa.alarm_service.api.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmHistoryRequest {
    private Integer alarmTypesId;
    private String alarmMessage;
    private Integer senderIndex;
    private List<Integer> receiverIndexes;
    private String alarmType; // 추가 정보용
    private String title;     // 추가 정보용
} 