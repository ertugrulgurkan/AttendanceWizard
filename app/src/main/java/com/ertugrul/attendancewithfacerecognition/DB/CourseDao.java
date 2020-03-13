package com.ertugrul.attendancewithfacerecognition.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;



@Dao
public interface CourseDao {
    @Query("SELECT * FROM Course")
    List<Course> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Course... courses);

    @Query("SELECT COUNT(*) from Course")
    int countCourses();

    @Query("SELECT numberOfClasses from Course where courseName = :courseId")
    int getNumberOfClasses(String courseId);

    @Query("SELECT courseName from Course where courseName = :courseId")
    String getCourseNameById(String courseId);

    @Query("DELETE from Course where courseName=:courseId")
    void deleteByCourseId(String courseId);
}
