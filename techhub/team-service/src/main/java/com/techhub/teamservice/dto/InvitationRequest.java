package com.techhub.teamservice.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record InvitationRequest(
        @NotNull
        UUID teamId,
        @NotNull
        UUID receiverId
) {}

