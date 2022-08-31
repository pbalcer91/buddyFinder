package pl.com.wfiis.android.buddyfinder.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Event implements Parcelable {
    private int id;
    private String title;
    private String description;

    private Date date;
    //private location
    final private User author;
    private ArrayList<User> members;

    public Event(String title, User author) {
        this.id = 0;
        this.title = title;
        this.author = author;
        this.members = new ArrayList<>();
        this.date = Calendar.getInstance().getTime();
    }

    public Event(String title, User author, String description) {
        this(title, author);
        this.description = description;
    }

    protected Event(Parcel in) {
        id = in.readInt();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public boolean addMember(User member) {
        if (member == null)
            return (false);

        if (!members.add(member))
            return (false);

        return (true);
    }

    public boolean removeMember(User member) {
        if (member == null)
            return (false);

        if (members.isEmpty())
            return (false);

        if (!members.remove(member))
            return (false);

        return (true);
    }

    public boolean removeAllMember() {
        if (members.isEmpty())
            return (false);

        members.clear();
        return (true);
    }

    public int getId() {
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
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeParcelable(author, i);
        parcel.writeTypedList(members);
    }

}
