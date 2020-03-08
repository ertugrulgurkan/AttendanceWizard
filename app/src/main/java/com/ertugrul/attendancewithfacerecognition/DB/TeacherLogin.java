package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.List;

public class TeacherLogin extends UserLogin implements UserInterface{

    private String title;

    public TeacherLogin(String title) {
        this.title = title;
    }

    public TeacherLogin(String email, String fullName, String title) {
        super(email, fullName);
        this.title = title;
    }

    public TeacherLogin(String email, String fullName, String userType, String title) {
        super(email, fullName, userType);
        this.title = title;
    }

    public TeacherLogin(String email, String fullName, List<String> courseIds, String title) {
        super(email, fullName, courseIds);
        this.title = title;
    }

    public TeacherLogin(String email, String fullName, List<String> courseIds, String userType, String title) {
        super(email, fullName, courseIds, userType);
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
