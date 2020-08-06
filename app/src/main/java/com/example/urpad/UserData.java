package com.example.urpad;

public class UserData {
    String fullName,userName,gender,dob;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String userID;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public UserData(String fullName, String userName,String gender, String dob,String userID) {
        this.fullName = fullName;
        this.userName = userName;
        this.userID = userID;
        this.gender = gender;
        this.dob = dob;
    }
}
