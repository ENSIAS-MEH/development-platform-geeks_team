package com.projtechhub.techhub.dto.response;

import com.projtechhub.techhub.entities.Skill;
import lombok.Builder;

import java.util.List;

/**
 * @author pc
 **/
@Builder
public class UserProfileResponse {

    private String id;
    private String name;
    private String email;
    private String role;
    private String bio;
    private String avatarUrl;
    private String location;
    private String joinedAt;

    private String portfolioUrl;
    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;    // extra field not in base User — full profile only
    private String headline;      // extra field — "Full-stack dev | Open to collabs"

    private List<String> skills;   // HERE you can use the rich object
    // because MyProfilePage will display level etc.
    // this is different from UserResponse.skills
}