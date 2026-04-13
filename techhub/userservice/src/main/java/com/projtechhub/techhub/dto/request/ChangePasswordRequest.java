package com.projtechhub.techhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author pc
 **/
@AllArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank
    private String currentPassword;   // you verify this against DB before changing

    @NotBlank
    @Size(min = 8)
    private String newPassword;

    @NotBlank
    private String confirmPassword;   // verify equals newPassword in service, not here
}