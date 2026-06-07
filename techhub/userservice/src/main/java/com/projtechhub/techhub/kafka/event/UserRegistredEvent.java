package com.projtechhub.techhub.kafka.event;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.*;

import java.util.UUID;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistredEvent {
    private String eventId;
    private String eventType;
    private String timestamp;
    private UUID userId;
    private String displayName;
    private String email;
    private String role;


}
