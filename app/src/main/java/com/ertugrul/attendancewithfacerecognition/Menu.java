package com.ertugrul.attendancewithfacerecognition;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.ertugrul.attendancewithfacerecognition.DB.StudentLogin;
import com.ertugrul.attendancewithfacerecognition.Utilities.StudentAttendanceEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Menu extends AppCompatActivity {

    FloatingActionButton takeAttendance;
    FloatingActionButton takeExport;
    ProgressDialog progressDialog;
    List<StudentLogin> allStudents;
    List<StudentAttendanceEntity> studentAttendance;
    String courseId;
    String courseName;
    int maxAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(100);

        allStudents = new ArrayList<>(Arrays.asList(((new Gson()).fromJson(Prefs.getString("students", ""), StudentLogin[].class))));
        studentAttendance = new ArrayList<>(Arrays.asList(((new Gson()).fromJson(Prefs.getString("studentAttendance", ""), StudentAttendanceEntity[].class))));
        maxAttendance = Integer.parseInt(Prefs.getString("selectedCourseMaxAttendance", ""));
        courseId = Prefs.getString("courseId", "");
        courseName = Prefs.getString("courseName", "");
        takeAttendance = findViewById(R.id.takeAttendance);

        takeExport = findViewById(R.id.export);


        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Menu.this, TakeAttendance.class));
            }
        });

        takeExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(Menu.this,
                        "Please Wait",
                        "Loading...");
                export(view);
                progressDialog.dismiss();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Menu.this, MainActivity.class));
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(Menu.this);
                ab.setMessage("Are you sure you want to Logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Menu.this, EditCourses.class));
    }

    public void export(View view) {
        try {

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String fileName = courseName+"_AttendanceList.csv";
            String filePath = path + File.separator + fileName;
            File f = new File(filePath);
            CSVWriter writer;
            if (f.exists() && !f.isDirectory()) {
                FileWriter mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
            } else {
                writer = new CSVWriter(new FileWriter(filePath),
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
            }
            String[] headerRecord = {"Course Name", "Student ID", "School Code", "Full Name", "Email", "Attendance", "NumberOfClasses"};
            writer.writeNext(headerRecord);
            if (!allStudents.isEmpty()) {
                for (StudentLogin student : allStudents) {
                    int attendanceNumber = 0;
                    for (StudentAttendanceEntity stu : studentAttendance) {
                        if (stu.getStudentNumber().equals(student.getUserId()))
                            attendanceNumber = stu.getAttendanceNumber();
                    }
                    writer.writeNext(new String[]{courseName, student.getStudentId(), student.getSchoolCode(), student.getFullName(), student.getEmail(), String.valueOf(attendanceNumber), String.valueOf(maxAttendance)});
                }

            }
            writer.close();
            Toast.makeText(Menu.this, "File was exported successfully", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.d("Exception", e.getMessage());
        }

    }
}
