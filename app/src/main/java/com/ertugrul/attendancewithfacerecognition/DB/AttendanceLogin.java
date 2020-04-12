package com.ertugrul.attendancewithfacerecognition.DB;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class AttendanceLogin {
    private String studentId;
    private String courseId;
    private int attendanceNumber;

    public AttendanceLogin(String studentId, String courseId, int attendanceNumber) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceNumber = attendanceNumber;
    }

    public AttendanceLogin() {
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getAttendanceNumber() {
        return attendanceNumber;
    }

    public void setAttendanceNumber(int attendanceNumber) {
        this.attendanceNumber = attendanceNumber;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("courseId", courseId);
        result.put("attendanceNumber", attendanceNumber);
        return result;
    }
}
