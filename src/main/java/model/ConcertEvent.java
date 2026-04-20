package model;

import java.time.LocalDateTime;
// import java.util.ArrayList;
import java.util.List;

public class ConcertEvent extends EventModel {
    private String artistName;
    // private List<Performance> performances = new ArrayList<>();
    private GenreModel genre;
    private String ticketType;

    public ConcertEvent() {
    }

    public ConcertEvent(int id, String title, String description, int venue, String date, LocalDateTime startTime,
            String organizer, int durationMin, String registrationDeadLine, int max_attendees, String status,
            String creationTime, String type, List<UserModel> attendees, String artistName, GenreModel genre,
            String ticketType) {
        super(id, title, description, venue, date, startTime, organizer, durationMin, registrationDeadLine,
                max_attendees, status, creationTime, type, attendees);
        this.artistName = artistName;
        this.genre = genre;
        this.ticketType = ticketType;
    }

    //todo performance schedule segmentation, check total performance time over or equal to total performance time
    //todo an artist can be a guest only if there're more than 1 artist and an organizer
    public void addPerformance() {

    }

    public void removePerformance() {

    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String var1) {
        this.artistName = var1;
    }

    public GenreModel getGenre() {
        return genre;
    }

    public void setGenre(GenreModel var1) {
        this.genre = var1;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String var1){this.ticketType = var1;}

    //! do not understand
    public List<ConcertEvent> filterPop(List<ConcertEvent> all) {
        return all.stream()
                .filter(c -> c.getGenre() == GenreModel.POP)
                .toList();
    }

    // // check whether the performance can be created, only used by services
    // public bool canAddPerformance(int performanceTime){
    //     if(performanceTime <= 0)
    //         return false;

    //     if(performanceTime > startTime.plusMinutes(durationMin)){
    //         int alreadyScheduledTime = performanceTime
    //     }
    // }
    


    // @Override
    // public double calculateTicketPrice(){
    //! early bird prices
    //     return
    // }


}