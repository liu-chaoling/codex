package com.personal.site.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 64)
    private String password;

    @NotBlank
    @Size(max = 80)
    private String displayName;
}
