package com.effitrack.server.model;

import lombok.Data;
import java.util.Date;

@Data
public class AppError {
    private int statusCode;
    private String message;
    private Date timestamp;

    public AppError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = new Date();
    }
}
