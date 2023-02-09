package com.jwt.app.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
public class UserProfileDTO {
    private String name;
    private String lastName;
    private String bio;
    private Boolean verified;
    private Date birthDay;
}
