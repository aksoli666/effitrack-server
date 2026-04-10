package com.effitrack.server.model.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String tableNumber;
    private String pinCode;
}
