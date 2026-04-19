package model;

import java.time.LocalDateTime;
import java.util.List;

public class WorkshopEvent extends EventModel{
    private String presenter;
    private String discussionTopics;
    private String materialList;

    public WorkshopEvent() {}

    public WorkshopEvent(int id, String title, String description, int venue, String date, LocalDateTime startTime,
            String organizer, int durationMin, String registrationDeadLine, int max_attendees, String status,
            String creationTime, String type, List<UserModel> attendees, String presenter, String discussionTopics, String materialList) {
        super(id, title, description, venue, date, startTime, organizer, durationMin, registrationDeadLine, max_attendees, status, creationTime, type, attendees);
        this.presenter = presenter;
        this.discussionTopics = discussionTopics;
        this.materialList = materialList;
    }
    
    public String getPresenter() {return presenter;}
    public String getDiscussionTopics() {return discussionTopics;}
    public String getMaterialList() {return materialList;}
    
    public void setPresenter(String var1) {this.presenter = var1;}
    public void setDiscussionTopics(String var1) {this.discussionTopics = var1;}
    public void setMaterialList(String var1) {this.materialList = var1;}
}