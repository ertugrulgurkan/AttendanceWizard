package com.ertugrul.attendancewithfacerecognition.Utilities;

public class StudentAttendanceEntity {
    private String studentNumber;
    private int attendanceNumber;

    public StudentAttendanceEntity(String studentNumber, int attendanceNumber) {
        this.studentNumber = studentNumber;
        this.attendanceNumber = attendanceNumber;
    }

    public StudentAttendanceEntity() {
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public int getAttendanceNumber() {
        return attendanceNumber;
    }

    public void setAttendanceNumber(int attendanceNumber) {
        this.attendanceNumber = attendanceNumber;
    }
}
