// package com.techhub.teamservice.entity.enums;

// public enum TeamStatus {
//     OPEN,
//     FULL,
//     CLOSED
// }

package com.techhub.teamservice.entity.enums;

/**
 * Lifecycle status of a {@link com.techhub.teamservice.entity.Team}.
 *
 * <p>Transitions:
 * <pre>
 *   OPEN  ──→  FULL   (currentMembers == maxMembers)
 *   FULL  ──→  OPEN   (a member leaves)
 *   OPEN  ──→  CLOSED (owner closes the team)
 *   FULL  ──→  CLOSED (owner closes the team)
 * </pre>
 */
public enum TeamStatus {

    /** Team has capacity and is accepting new members via invitation. */
    OPEN,

    /** Team has reached maxMembers; no new members can join until someone leaves. */
    FULL,

    /** Team is permanently closed by the owner; no invitations accepted. */
    CLOSED
}