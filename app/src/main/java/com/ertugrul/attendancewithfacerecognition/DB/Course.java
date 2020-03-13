package com.ertugrul.attendancewithfacerecognition.DB;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@Entity
@IgnoreExtraProperties
public class Course {

    @PrimaryKey
    @NonNull
    public String courseId;
    //largePersonGroupId
    private String courseName;
    private String year;
    private String numberOfClasses;
    private String courseCode;
    private String schoolCode;

    public Course(String courseId,String courseName, String year, String numberOfClasses, String courseCode,String schoolCode) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.year = year;
        this.numberOfClasses = numberOfClasses;
        this.courseCode = courseCode;
        this.schoolCode=schoolCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(String numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @NonNull
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(@NonNull String courseId) {
        this.courseId = courseId;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("courseId", courseId);
        result.put("courseCode", courseCode);
        result.put("courseName", courseName);
        result.put("year", year);
        result.put("numberOfClasses", numberOfClasses);
        result.put("schoolCode", schoolCode);

        return result;
    }
}
