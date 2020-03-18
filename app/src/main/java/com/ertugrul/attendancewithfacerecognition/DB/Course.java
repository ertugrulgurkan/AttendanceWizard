package com.ertugrul.attendancewithfacerecognition.DB;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Course {

    public String courseId;
    //largePersonGroupId
    private String courseName;
    private String year;
    private String numberOfClasses;
    private String courseCode;
    private String schoolCode;
    private List<String> studentIds;

    public Course(String courseId,String courseName, String year, String numberOfClasses, String courseCode,String schoolCode,List<String> studentIds) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.year = year;
        this.numberOfClasses = numberOfClasses;
        this.courseCode = courseCode;
        this.schoolCode=schoolCode;
        this.studentIds = studentIds;
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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
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
        result.put("studentIds",studentIds);
        return result;
    }
}
