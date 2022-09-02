package pl.com.wfiis.android.buddyfinder.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private int id;
    private String userName;
    private String email;
    private ArrayList<Event> createdEvents;
    private ArrayList<Event> joinedEvents;
    //private Byte [] image;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Event> getJoinedEvents() {
        return joinedEvents;
    }

    public void addJoinedEvent(Event event) {
        joinedEvents.add(event);
    }

    public void removeJoinedEvent(Event event) {
        joinedEvents.remove(event);
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void addCreatedEvent(Event event) {
        createdEvents.add(event);
    }

    public void removeCreatedEvent(Event event) {
        createdEvents.remove(event);
    }

    public User(String userName, String email) {
        //TODO: generate id
        this.userName = userName;
        this.email = email;
        this.createdEvents = new ArrayList<>();
        this.joinedEvents = new ArrayList<>();
    }

    public User(Parcel source) {
        id = source.readInt();
        userName = source.readString();
        email = source.readString();
        createdEvents = source.createTypedArrayList(Event.CREATOR);
        joinedEvents = source.createTypedArrayList(Event.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(userName);
        parcel.writeString(email);
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
