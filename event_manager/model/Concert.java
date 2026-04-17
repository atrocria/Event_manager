package model;

import java.time.LocalDateTime;
// import java.util.ArrayList;
import java.util.List;

public class Concert extends Event {
    private final String artistName;
    // private List<Performance> performances = new ArrayList<>();
    private final Genre genre;

    public Concert(int id, String name, String venue, String date, LocalDateTime startTime, int endTime, int durationMin, int ticketCapacity, List<User> attendees, String artistName, Genre genre) {
        super(id, name, venue, date, startTime, durationMin, ticketCapacity, attendees);
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
    public Genre getGenre() {return genre;}

    //! do not understand
    public List<Concert> filterPop(List<Concert> all) {
        return all.stream()
                .filter(c -> c.getGenre() == Genre.POP)
                .toList();
    }

    // check whether the performance can be created, only used by services
    // public bool canAddPerformance(int performanceTime){
    //     if(performanceTime <= 0)
    //         return false;

    //     if(performanceTime > startTime.plusMinutes(durationMin)){
    //         int alreadyScheduledTime = performanceTime
    //     }
    // }

    // @Override
    // public double calculateTicketPrice(){
    //     return
    // }
}