package com.ertugrul.attendancewithfacerecognition;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ertugrul.attendancewithfacerecognition.DB.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCourse extends AppCompatActivity {

    String courseName;
    String year;
    String numberOfClassesInCourses;
    String courseCode;
    DatabaseReference mDatabase;
    String schoolCode;
    List<String> courseIds;
    List<String> studentIds;
    String courseIdFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        schoolCode = Prefs.getString("schoolCode", "");
        courseIds = Arrays.asList(((new Gson()).fromJson(Prefs.getString("userCourseIds",""), String[].class)));
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(100);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        (findViewById(R.id.addCourse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                courseName = ((EditText)findViewById(R.id.courseName)).getText().toString();
                year = ((EditText)findViewById(R.id.year)).getText().toString();
                numberOfClassesInCourses = ((EditText)findViewById(R.id.numberOfClasses)).getText().toString();
                courseCode = ((EditText)findViewById(R.id.courseCode)).getText().toString();
                courseIdFromDB = mDatabase.child("courses").push().getKey().toLowerCase();

                if (courseName.equals("")){
                    ((EditText)findViewById(R.id.courseName)).setError("Please enter a Course Name");
                    (findViewById(R.id.courseName)).requestFocus();
                    return;
                }
                else if (year.equals("")){
                    ((EditText)findViewById(R.id.year)).setError("Please enter the Year which the course is in");
                    (findViewById(R.id.year)).requestFocus();
                    return;
                }
                else if (numberOfClassesInCourses.equals("")){
                    ((EditText)findViewById(R.id.numberOfClasses)).setError("Please enter the Number of Classes/Lectures in the course");
                    (findViewById(R.id.numberOfClasses)).requestFocus();
                    return;
                }
                else if (courseCode.equals("")){
                    ((EditText)findViewById(R.id.courseCode)).setError("Please enter the Course Code");
                    (findViewById(R.id.courseCode)).requestFocus();
                    return;
                }
                Course course = new Course(courseIdFromDB,courseName, year, numberOfClassesInCourses, courseCode ,schoolCode,studentIds);
                new AddPersonGroupTask().execute(courseIdFromDB, courseName, (new Gson()).toJson(course)); //largeGroupId, name, userInfo

                findViewById(R.id.addCourseProgress).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddCourse.this, EditCourses.class));
    }



    class AddPersonGroupTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.v("","Request: Creating person group " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try{
                Log.v("",("Syncing with server to add person group..."));

                // Start creating person group in server.
                faceServiceClient.createLargePersonGroup(
                        params[0],
                        params[1],
                        params[2]);

                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(final String result) {
            findViewById(R.id.addCourseProgress).setVisibility(View.INVISIBLE);
            if (result != null) {
                Log.v("", "Response: Success. Person group " + result + " created");

                Toast.makeText(getApplicationContext(), "Course successfully created", Toast.LENGTH_LONG).show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Course course = new Course(courseIdFromDB,courseName,year,numberOfClassesInCourses,courseCode,schoolCode,studentIds);
                Map<String, Object> courseValues = course.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/courses/" + courseIdFromDB, courseValues);
                childUpdates.put("/users/" + user.getUid() + "/courseIds/"+ courseIdFromDB, courseIdFromDB);
                childUpdates.put("/teachers/" + user.getUid() + "/courseIds/"+ courseIdFromDB, courseIdFromDB);
                mDatabase.updateChildren(childUpdates);
                List<String> userCourseIds = new ArrayList<>(Arrays.asList(((new Gson()).fromJson(Prefs.getString("userCourseIds", ""), String[].class))));
                userCourseIds.add(courseIdFromDB);
                Prefs.putString("userCourseIds", new Gson().toJson(userCourseIds));


                startActivity(new Intent(AddCourse.this, EditCourses.class));
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "The course could not be created at this time", Toast.LENGTH_LONG).show();
            }
        }
    }
}
