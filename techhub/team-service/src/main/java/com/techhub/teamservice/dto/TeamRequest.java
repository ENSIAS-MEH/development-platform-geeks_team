package com.techhub.teamservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeamRequest(
        @NotBlank
        @Size(max = 120)
        String name,

        String description,

        @Min(2)
        @Max(100)
        Integer maxMembers
) {}

