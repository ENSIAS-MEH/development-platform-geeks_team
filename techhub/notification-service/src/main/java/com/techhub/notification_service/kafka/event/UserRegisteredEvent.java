package com.techhub.notification_service.kafka.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // safe if user-service adds fields later
public class UserRegisteredEvent {

    private String eventId;
    private String eventType;
    private String timestamp;
    private UUID userId;
    private String displayName;
    private String email;
    private String role;
}


/// this class represents the structure of the event that will be consumed from Kafka.
//  It uses Lombok annotations to generate boilerplate code like getters, constructors, and builders. 
// The @JsonIgnoreProperties annotation allows the class to ignore any unknown properties in the JSON payload, which is useful for forward compatibility if the user-service adds new fields to the event in the future.