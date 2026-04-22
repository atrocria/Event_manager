package model;

import java.time.LocalDateTime;
import java.util.List;

public class ConferenceEvent extends EventModel {
    private List<String> sessions;
    private String researchTopic;
    private String keynoteSpeaker;
    private String speakerBio;

    public ConferenceEvent() {}

    public ConferenceEvent(int id, String title, String description, int venue, String date, LocalDateTime startTime,
            int organizer, int durationMin, String registrationDeadLine, int max_attendees, String status,
            LocalDateTime creationTime, String type, List<UserModel> attendees, List<String> sessions, String researchTopic, String keynoteSpeaker, String speakerBio) {
        super(id, title, description, venue, date, startTime, organizer, durationMin, registrationDeadLine, max_attendees, status, creationTime, type, attendees);
        this.sessions = sessions;
        this.researchTopic = researchTopic;
        this.keynoteSpeaker = keynoteSpeaker;
        this.speakerBio = speakerBio;
    }

    public List<String> getSessions() {return sessions;}
    public String getKeynoteSpeaker() {return keynoteSpeaker;}
    public String getSpeakerBio() {return speakerBio;}
    public String getResearchTopic() {return researchTopic;}
    
    public void setSessions(List<String> var1) {this.sessions = var1;}
    public void setKeynoteSpeaker(String var1) {this.keynoteSpeaker = var1;}
    public void setResearchTopic(String var1) {this.researchTopic = var1;}
    public void setSpeakerBio(String var1) {this.speakerBio = var1;}
}