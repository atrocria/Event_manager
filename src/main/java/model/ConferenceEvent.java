package model;

import java.time.LocalDateTime;
import java.util.List;

public class ConferenceEvent extends EventModel {
    private final List<String> sessions;
    private final String speakerBio;

    public  ConferenceEvent(int id, String name, String venue, String date, LocalDateTime startTime, int durationMin, int ticketCapacity, List<UserModel> attendees,List<String> sessions, String speakerBio){
        super(id, name, venue, date, startTime, durationMin, ticketCapacity, attendees);
        this.sessions = sessions;
        this.speakerBio = speakerBio;
    }

    public List<String> getSessions() {return sessions;}
    public String getSpeakerBio() {return speakerBio;}
}
