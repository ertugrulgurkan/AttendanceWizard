package com.ertugrul.attendancewithfacerecognition.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import static androidx.room.ForeignKey.CASCADE;


@Entity(primaryKeys = {"regNo", "courseId"})
public class Attendance {
    @ForeignKey(entity = Student.class, parentColumns = "regNo", childColumns = "regNo", onDelete = CASCADE)
    @NonNull
    public String regNo;

    @ForeignKey(entity = Course.class, parentColumns = "courseId", childColumns = "courseId", onDelete = CASCADE)
    @NonNull
    public String courseId;

    public int attendanceNumber;

    public Attendance(String regNo, String courseId, int attendanceNumber) {
        this.regNo = regNo;
        this.courseId = courseId;
        this.attendanceNumber = attendanceNumber;
    }
}
