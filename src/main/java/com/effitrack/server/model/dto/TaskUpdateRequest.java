package com.effitrack.server.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskUpdateRequest {
    private LocalDateTime plannedDate;
    private Integer actualMinutes;
    private String operatorComment;
}
