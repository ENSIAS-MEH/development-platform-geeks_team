package com.techhub.teamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * TechHub — Team Service entry point.
 *
 * <p>Port: 8084 (governance §2.3, Member 3)
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Team lifecycle (create / read / delete)</li>
 *   <li>Membership management (join via invitation, leave, capacity check)</li>
 *   <li>Invitation flow (send, accept, decline, auto-expire via scheduler)</li>
 *   <li>Kafka event production → Notification Service (port 8086)</li>
 *   <li>Redis second-level cache for team and invitation reads</li>
 * </ul>
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class TeamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamServiceApplication.class, args);
    }
}