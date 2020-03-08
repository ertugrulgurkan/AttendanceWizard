package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.ArrayList;
import java.util.List;

public class UserLogin implements UserInterface {

    private String email;
    private String fullName;
    private List<String> courseIds;
    private String userType;


    public UserLogin() {
    }

    public UserLogin(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
        this.courseIds = new ArrayList<>();
    }

    public UserLogin(String email, String fullName, String userType) {
        this.email = email;
        this.fullName = fullName;
        this.courseIds = new ArrayList<>();
        this.userType = userType;
    }

    public UserLogin(String email, String fullName, List<String> courseIds) {
        this.email = email;
        this.fullName = fullName;
        this.courseIds = courseIds;
    }

    public UserLogin(String email, String fullName, List<String> courseIds, String userType) {
        this.email = email;
        this.fullName = fullName;
        this.courseIds = courseIds;
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", courseIds=" + courseIds +
                '}';
    }

}
