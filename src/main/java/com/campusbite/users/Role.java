package com.campusbite.users;

/**
 * M2 User & Role Administration (owner: Lawk).
 * The three roles of CampusBite. Stored in the users.role column as text.
 */
public enum Role {
    /** Browses the menu, orders food, reviews items. */
    STUDENT,
    /** Works the kitchen order board and keeps today's stock up to date. */
    KITCHEN,
    /** Everything KITCHEN can do, plus menu, slot, user management and reports. */
    ADMIN
}
