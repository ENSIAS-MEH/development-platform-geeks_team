// package com.techhub.teamservice.entity.enums;

// public enum MemberRole {
//     OWNER,
//     MEMBER
// }

package com.techhub.teamservice.entity.enums;

/**
 * Role of a user within a {@link com.techhub.teamservice.entity.Team}.
 *
 * <p>Business rules:
 * <ul>
 *   <li>Every team has exactly one {@code OWNER} — the creator.</li>
 *   <li>Only the {@code OWNER} can delete the team or invite members.</li>
 *   <li>A {@code MEMBER} can leave at any time.</li>
 * </ul>
 */
public enum MemberRole {

    /** The user who created the team. Has full administrative rights. */
    OWNER,

    /** A regular team member. Can view and participate; cannot manage the team. */
    MEMBER
}