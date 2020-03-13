package com.ertugrul.attendancewithfacerecognition.DB;

public class UserLogin {
    private String userType;
    private String userId;
    private String schoolCode;



    public UserLogin() {
    }

    public UserLogin(String userType, String userId,String schoolCode) {
        this.userType = userType;
        this.userId = userId;
        this.schoolCode = schoolCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUser() {
        return userId;
    }

    public void setUser(String user) {
        this.userId = user;
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
}
