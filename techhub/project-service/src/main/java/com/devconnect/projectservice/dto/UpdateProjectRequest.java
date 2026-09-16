package com.devconnect.projectservice.dto;

import com.devconnect.projectservice.enums.ProjectStatus;
import com.devconnect.projectservice.enums.ProjectType;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateProjectRequest {
    @Size(min = 3, max = 200) private String title;
    private String description;
    private ProjectType type;
    private Set<String> technologies;
    private Set<String> skillsNeeded;
    private ProjectStatus status;
    private String githubUrl;
}
