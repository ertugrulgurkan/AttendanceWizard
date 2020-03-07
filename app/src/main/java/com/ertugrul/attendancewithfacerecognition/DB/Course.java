package com.ertugrul.attendancewithfacerecognition.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Course {
    @PrimaryKey
    @NonNull
    public String courseId; //largePersonGroupId

    public String courseName;
    public String year;
    public int numberOfClasses;

    public String courseCode;

    public Course(String courseId, String courseName, String year, int numberOfClasses, String courseCode) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.year = year;
        this.numberOfClasses = numberOfClasses;
        this.courseCode = courseCode;
    }
}
