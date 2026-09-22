package com.techhub.community.dto;

import com.techhub.community.enums.Topic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request body for creating or updating a community group.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 120, message = "Group name must be at most 120 characters")
    private String name;

    private String description;

    @NotNull(message = "Topic is required")
    private Topic topic;

    @Builder.Default
    private Boolean isPublic = true;
}
