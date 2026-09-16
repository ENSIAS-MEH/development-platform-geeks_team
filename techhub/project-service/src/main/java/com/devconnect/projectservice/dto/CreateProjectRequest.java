package com.devconnect.projectservice.dto;

import com.devconnect.projectservice.enums.ProjectType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class CreateProjectRequest {
    @NotBlank @Size(min = 3, max = 200) private String title;
    private String description;
    @NotNull private ProjectType type;
    private Set<String> technologies;
    private Set<String> skillsNeeded;
    @Pattern(regexp = "^(https?://.*)?$") private String githubUrl;
}
