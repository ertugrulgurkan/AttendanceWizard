package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.List;

public class TeacherLogin{

    private String userId;
    private String schoolCode;
    private String email;
    private String fullName;
    private List<String> courseIds;
    private String title;

    public TeacherLogin() {
    }

    public TeacherLogin(String userId, String schoolCode, String email, String fullName, List<String> courseIds, String title) {
        this.userId = userId;
        this.schoolCode = schoolCode;
        this.email = email;
        this.fullName = fullName;
        this.courseIds = courseIds;
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
