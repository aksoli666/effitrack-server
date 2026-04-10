package com.effitrack.server.model.dto;

import com.effitrack.server.model.EquipmentStatus;
import lombok.Data;

@Data
public class StatusRequest {
    private EquipmentStatus status;
    private String reason;
}
