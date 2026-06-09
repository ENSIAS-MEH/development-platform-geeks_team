package com.projtechhub.techhub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author pc
 **/
@Getter
@Setter
public class LoginRequest {

    @NotBlank
    @Email
    private String email;        // frontend form field id="email"

    @NotBlank
    private String password;     // frontend form field id="password"
}
