package com.devconnect.projectservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data @Builder
public class MatchingProjectResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private ProjectResponse project;
    private long matchScore;
}
