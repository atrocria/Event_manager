package model;

import java.time.LocalDateTime;
// import java.util.ArrayList;
import java.util.List;

public class ConcertEvent extends EventModel {
    private final String artistName;
    // private List<Performance> performances = new ArrayList<>();
    private final GenreModel genre;

    public ConcertEvent(int id, String title, String description, String venue, String date, LocalDateTime startTime, String organizer, int durationMin, String registrationDeadLine, int max_attendees, String status, String creationTime, List<UserModel> attendees, String artistName, GenreModel genre) {
        super(id, title, description, venue, date, startTime, organizer, durationMin, registrationDeadLine, max_attendees, status, creationTime, attendees);
        this.artistName = artistName;
        this.genre = genre;
    }

    //todo performance schedule segmentation, check total performance time over or equal to total performance time
    //todo an artist can be a guest only if there're more than 1 artist and an organizer
    public void addPerformance() {

    }

    public void removePerformance() {

    }

    public String getArtistName() {return artistName;}
    public GenreModel getGenre() {return genre;}

    //! do not understand
    public List<ConcertEvent> filterPop(List<ConcertEvent> all) {
        return all.stream()
                .filter(c -> c.getGenre() == GenreModel.POP)
                .toList();
    }

    // check whether the performance can be created, only used by services
    public bool canAddPerformance(int performanceTime){
        if(performanceTime <= 0)
            return false;

        if(performanceTime > startTime.plusMinutes(durationMin)){
            int alreadyScheduledTime = performanceTime
        }
    }

    @Override
    public double calculateTicketPrice(){
        return
    }
}