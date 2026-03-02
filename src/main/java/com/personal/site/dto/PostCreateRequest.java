package com.personal.site.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PostCreateRequest {

    @NotBlank
    @Size(max = 2000)
    private String content;
}
