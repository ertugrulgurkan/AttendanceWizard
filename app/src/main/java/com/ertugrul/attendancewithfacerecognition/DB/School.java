package com.ertugrul.attendancewithfacerecognition.DB;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class School {

    String schoolCode;

    public School() {
    }

    public School(String schoolCode) {
        this.schoolCode = schoolCode;
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
        result.put("schoolCode", schoolCode);
        return result;
    }
}

