package service;

import model.UserRole;

public class PermissionService {
    public static boolean canCreateEvent(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.STAFF || role == UserRole.ORGANIZER;
    }

    public static boolean canDeleteUser(UserRole role) {
        return role == UserRole.ADMIN; // Organizers can't do this
    }

    public static boolean canRemoveUser(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.STAFF || role == UserRole.ORGANIZER;
    }
}