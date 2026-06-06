package com.techhub.notification_service.kafka.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamJoinedEvent {

    private UUID teamId;
    private UUID userId;
    private String teamName;
}
