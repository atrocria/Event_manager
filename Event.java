public class Event {
    private int id;
    private String name;
    private String date;

    public Event(String name, String date) {
        this.name = name;
        this.date = date;
    }

    //get setter
    public int getID() { return id;}
    public String getName() { return name;}
    public void setName(String name) { this.name = name;}
    public String getDate() { return date;}
    public void setDate(String date) { this.date = date;}

}
