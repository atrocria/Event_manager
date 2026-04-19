package model;

// user ranking by permission level, show and hide ui accordingly
public enum UserRole {
    ADMIN(100),
    STAFF(80),
    ORGANIZER(60),
    SPEAKER(40),
    ATTENDEE(20);

    private final int level;

    UserRole(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    // Permission check helper
    public boolean canAccessAdminPanel() {
        return this.level >= 80;
    }
}