package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.List;

public class StudentLogin{

    private String userId;
    private String fullName;
    private String studentId; //personID
    private  String schoolCode;
    private String email;
    private List<String> courseIds;
    private String faceArrayJson;

    public StudentLogin(String userId, String fullName, String studentId, String schoolCode, String email, List<String> courseIds, String faceArrayJson) {
        this.userId = userId;
        this.fullName = fullName;
        this.studentId = studentId;
        this.schoolCode = schoolCode;
        this.email = email;
        this.courseIds = courseIds;
        this.faceArrayJson = faceArrayJson;
    }
    public StudentLogin() {
    }
    public String getFaceArrayJson() {
        return faceArrayJson;
    }

    public void setFaceArrayJson(String faceArrayJson) {
        this.faceArrayJson = faceArrayJson;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public List<String> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }
}
