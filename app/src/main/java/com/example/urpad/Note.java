package com.example.urpad;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Note implements Parcelable {
    private String title;
    private String content;
    private boolean completed;
    private String time;
    private String userid;
    private String date;
    private String noteID;
    private String search;


    public Note(String title, String content, boolean completed, String date, String time, String userid, String search) {
        this.title = title;
        this.content = content;
        this.completed = completed;
        this.time = time;
        this.date = date;
        this.userid = userid;
        this.search = search;
    }

    public Note(String title, String content, boolean completed, String time, String date) {
        this.title = title;
        this.content = content;
        this.completed = completed;
        this.time = time;
        this.date = date;
    }

    public Note(String title, String time, String date) {
        this.title = title;
        this.time = time;
        this.date = date;
    }

    public Note() {
    }

    protected Note(Parcel in) {
        title = in.readString();
        content = in.readString();
        completed = in.readByte() != 0;
        time = in.readString();
        userid = in.readString();
        date = in.readString();
        noteID = in.readString();
        search = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getisCompleted() {
        return completed;
    }

    public void setisCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(time);
        dest.writeString(userid);
        dest.writeString(date);
        dest.writeString(noteID);
        dest.writeString(search);
    }
}
