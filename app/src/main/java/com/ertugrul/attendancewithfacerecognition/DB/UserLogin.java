package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.List;

public class UserLogin {
    private String userType;
    private String userId;
    private String schoolCode;
    private List<String> courseIds;

    public UserLogin() {
    }

    public UserLogin(String userType, String userId,String schoolCode,List<String> courseIds) {
        this.userType = userType;
        this.userId = userId;
        this.schoolCode = schoolCode;
        this.courseIds = courseIds;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }
}
