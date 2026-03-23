package com.projtechhub.techhub.dto.response;

import lombok.Builder;

import java.util.List;

/**
 * @author pc
 **/
@Builder
public class UserResponse {

    private String id;            // UUID.toString() — frontend User interface: id: string

    private String name;          // frontend User interface: name: string
    // mapped FROM your entity's displayName field
    // this MUST be "name" — if you return "displayName"
    // the frontend gets undefined everywhere it reads user.name

    private String email;         // frontend User interface: email?: string

    private String role;          // frontend User interface:
    // role: 'Student'|'Developer'|'Organizer'|'Company'
    // you collapsed userType + security role into one field here
    // logic: if ROLE_ADMIN → "Admin" (future)
    //        else → capitalize(userType) → "Student" etc.

    private List<String> skills;  // frontend User interface: skills: string[]
    // FLAT list of skill names only — ["React", "Java"]
    // NOT objects — if you return [{name:"React", level:"EXPERT"}]
    // the frontend will render "[object Object]" everywhere

    private String location;      // frontend User interface: location?: string
    private String bio;           // frontend User interface: bio?: string
    private String avatarUrl;     // frontend User interface: avatarUrl?: string  ← camelCase
    private String githubUrl;     // frontend User interface: githubUrl?: string  ← camelCase
    private String linkedinUrl;   // frontend User interface: linkedinUrl?: string ← camelCase
    private String portfolioUrl;  // frontend User interface: portfolioUrl?: string ← camelCase
    private String joinedAt;      // frontend User interface: joinedAt?: string
                                    // format: ISO 8601 — "2025-01-15T10:30:00Z"
                                    // return createdAt.toString() — already ISO from Instant/ZonedDateTime
}