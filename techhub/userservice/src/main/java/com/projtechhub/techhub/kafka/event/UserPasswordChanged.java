package com.projtechhub.techhub.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserPasswordChanged {
    private String eventId;
    private String eventType;
    private String timestamp;
    private UUID userId;
    private String displayName;
    private String email;

}
