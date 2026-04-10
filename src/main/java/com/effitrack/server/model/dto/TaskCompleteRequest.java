package com.effitrack.server.model.dto;

import lombok.Data;

@Data
public class TaskCompleteRequest {
    private int actualMinutes;
    private String operatorComment;
}
