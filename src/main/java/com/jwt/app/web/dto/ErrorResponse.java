package com.jwt.app.web.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String code;
    private String message;
}
