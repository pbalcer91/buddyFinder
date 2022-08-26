package pl.com.wfiis.android.buddyfinder.models;

import java.util.ArrayList;


public class Offer {

    public Offer(String title, User author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    private String title;
    //private String description;
    //private Date date;
    //private int minParticipants;
    //private int maxParticipants;
    //private location
    final private User author;
    private ArrayList<User> participants;
}
