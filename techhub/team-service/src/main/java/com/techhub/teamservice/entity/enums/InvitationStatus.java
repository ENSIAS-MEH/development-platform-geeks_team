package com.techhub.teamservice.entity.enums;

/**
 * Lifecycle status of a {@link com.techhub.teamservice.entity.TeamInvitation}.
 *
 * <p>Transitions:
 * <pre>
 *   PENDING ──→ ACCEPTED  (receiver accepts before expiry)
 *   PENDING ──→ DECLINED  (receiver declines)
 *   PENDING ──→ EXPIRED   (scheduler runs; now() > expirationTime)
 * </pre>
 * Terminal states: ACCEPTED, DECLINED, EXPIRED — no further transitions.
 */
public enum InvitationStatus {

    /** Invitation sent; waiting for receiver's response. */
    PENDING,

    /** Receiver accepted; member was added to the team. */
    ACCEPTED,

    /** Receiver explicitly declined. */
    DECLINED,

    /** Scheduler set this after expirationTime passed without a response. */
    EXPIRED
}