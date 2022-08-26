package pl.com.wfiis.android.buddyfinder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userName;
    private String email;
    private int id;
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

    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
        //TODO: generate id
    }

    public User(Parcel source) {
        userName = source.readString();
        email = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeString(email);
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
