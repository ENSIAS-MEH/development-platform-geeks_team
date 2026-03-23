package com.projtechhub.techhub.dto.request;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * @author pc
 **/
public class UpdateProfileRequest {

    @Size(max = 100)
    private String name;          // all fields optional — null means "don't update this field"

    @Size(max = 160)
    private String headline;

    @Size(max = 500)
    private String bio;

    private String location;

    @URL
    private String avatarUrl;

    @URL
    private String githubUrl;

    @URL
    private String linkedinUrl;

    @URL
    private String portfolioUrl;

    @URL
    private String websiteUrl;
}