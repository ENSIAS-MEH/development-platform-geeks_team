package com.projtechhub.techhub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author pc
 **/
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(max = 100)
    private String name;          // frontend form field id="name", label="Full Name"

    @NotBlank
    @Email
    private String email;         // frontend form field id="reg-email"

    @NotBlank
    @Size(min = 8)
    private String password;      // frontend form field id="reg-password"

    @NotNull
    private String role;          // frontend <select> id="role" — values: "Student", "Developer",
    // "Event Organizer", "Company"
    // you map "Event Organizer" → UserType.ORGANIZER on save
}
