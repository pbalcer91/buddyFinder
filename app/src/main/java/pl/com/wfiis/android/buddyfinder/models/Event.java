package pl.com.wfiis.android.buddyfinder.models;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Event implements Parcelable {
    @DocumentId
    private String id;
    private String title;
    private String description;
    private Date date;
    private Address location;
    private User author;
    private ArrayList<User> members;

    public Event(User author) {
        this.id = "test";
        this.author = author;
        this.members = new ArrayList<>();
        addMember(author);
        this.title = "";
        this.date = Calendar.getInstance().getTime();
        this.location = null;
        this.description = "";
    }

    public Event(User author, String description) {
        this(author);
        this.description = description;
    }

    public Event() {
    }

    protected Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        author = in.readParcelable(User.class.getClassLoader());
        members = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public boolean isMember(User user) {

        for (User tempUser : members) {
            if (user.getId().equals(tempUser.getId()))
                return true;
        }

        return false;
    }

    public boolean addMember(User member) {
        if (member == null)
            return (false);

        return members.add(member);
    }

    public boolean removeMemberById(String id) {
        if (members.isEmpty())
            return (false);

        for (User user : members) {
            if (user.getId().equals(id))
                members.remove(user);
            return (true);
        }

        return (false);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeParcelable(author, i);
        parcel.writeTypedList(members);
    }

}
