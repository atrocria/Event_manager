package model;

import java.time.LocalDateTime;
import java.util.List;

public class WorkshopEvent extends EventModel{
    private String keynoteSpeaker;

    public WorkshopEvent(int id, String title, String description, String venue, String date, LocalDateTime startTime, String organizer, int durationMin, String registrationDeadLine, int max_attendees, String status, String creationTime, List<UserModel> attendees, String keynoteSpeaker) {
        super(id, title, description, venue, date, startTime, organizer, durationMin, registrationDeadLine, max_attendees, status, creationTime, attendees);
    }
}
