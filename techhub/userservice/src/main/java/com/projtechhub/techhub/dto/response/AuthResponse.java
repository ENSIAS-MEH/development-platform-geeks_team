package com.projtechhub.techhub.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author pc
 **/
@Builder
@Data
public class AuthResponse {

    private String accessToken;   // frontend will read this to set Authorization header
    private String refreshToken;  // stored for token rotation
    private String tokenType;     // always "Bearer" — frontend prepends this to header value
    private Long expiresIn;       // seconds until accessToken expires — frontend uses this
    // to know when to call /refresh proactively
    private UserResponse user;    // return the full user object on login so the frontend
    // can populate the UI immediately without a second GET /me call
}