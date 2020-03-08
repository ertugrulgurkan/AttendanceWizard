package com.ertugrul.attendancewithfacerecognition.DB;

import java.util.List;

public interface UserInterface {

    String getEmail();

    String getFullName();

    String getUserType();

    List<String> getCourseIds();

}
