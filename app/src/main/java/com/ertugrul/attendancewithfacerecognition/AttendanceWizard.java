package com.ertugrul.attendancewithfacerecognition;

import android.app.Application;

public class AttendanceWizard extends Application {
    public String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
