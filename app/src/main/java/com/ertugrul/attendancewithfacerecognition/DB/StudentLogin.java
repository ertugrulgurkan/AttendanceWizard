package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.List;

public class StudentLogin extends UserLogin implements UserInterface {

    private String studentId; //personID

    public StudentLogin(String studentId) {
        this.studentId = studentId;
    }

    public StudentLogin(String email, String fullName, String studentId) {
        super(email, fullName);
        this.studentId = studentId;
    }

    public StudentLogin(String email, String fullName, String userType, String studentId) {
        super(email, fullName, userType);
        this.studentId = studentId;
    }

    public StudentLogin(String email, String fullName, List<String> courseIds, String studentId) {
        super(email, fullName, courseIds);
        this.studentId = studentId;
    }

    public StudentLogin(String email, String fullName, List<String> courseIds, String userType, String studentId) {
        super(email, fullName, courseIds, userType);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
