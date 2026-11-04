package com.scc.model;

public record ActivityLog(
        int id,
        int userId,
        String username,
        String actionType,
        String description,
        String timestamp
) {
    /** Full action text, e.g. "USER_APPROVED: Admin [1] approved account for User ID 12". */
    public String action() {
        return actionType + ": " + description;
    }

    @Override
    public String toString() {
        return String.format("[%s] User '%s' (ID: %d) -> Action: %s", timestamp, username, userId, action());
    }
}