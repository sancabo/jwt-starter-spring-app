package com.jwt.app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequestDTO {
    private String user;
    private String password;
}
