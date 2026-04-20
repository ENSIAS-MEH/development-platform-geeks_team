package com.projtechhub.techhub.dto.response;

import com.projtechhub.techhub.entities.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * @author pc
 **/

@Builder
@Getter
@AllArgsConstructor
public class UserSummaryDTO {
    private UUID id;
    private String name;
    private String userType;
    private String bio;
    private List<Skill> skills;
    private String location;
}
