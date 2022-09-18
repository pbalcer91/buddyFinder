package pl.com.wfiis.android.buddyfinder.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String uid;
    private String userName;
    private String email;
    private String password;
    private ArrayList<Event> createdEvents;
    private ArrayList<Event> joinedEvents;
    //private Byte [] image;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createdEvents = new ArrayList<>();
        this.joinedEvents = new ArrayList<>();
    }

    public User(String uid,String email, String password,String userName) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createdEvents = new ArrayList<>();
        this.joinedEvents = new ArrayList<>();
    }
    public User(){
        this.createdEvents = new ArrayList<>();
        this.joinedEvents = new ArrayList<>();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return uid;
    }

    public void setId(String uid) {
        this.uid = uid;
    }

    public ArrayList<Event> getJoinedEvents() {
        return joinedEvents;
    }

    public void addJoinedEvent(Event event) {
        joinedEvents.add(event);
    }

    public void removeJoinedEventById(String id) {
        for (Event event : joinedEvents) {
            if (event.getId().equals(id))
                joinedEvents.remove(event);
        }
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(ArrayList<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public void setJoinedEvents(ArrayList<Event> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public void addCreatedEvent(Event event) {
        createdEvents.add(event);
    }

    public void removeCreatedEventById(String id) {
        for (Event event : joinedEvents) {
            if (event.getId().equals(id))
                createdEvents.remove(event);
        }
    }

    public User(Parcel source) {
        uid = source.readString();
        userName = source.readString();
        email = source.readString();
        password = source.readString();
        createdEvents = source.createTypedArrayList(Event.CREATOR);
        joinedEvents = source.createTypedArrayList(Event.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeTypedList(createdEvents);
        parcel.writeTypedList(joinedEvents);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
    };
}
