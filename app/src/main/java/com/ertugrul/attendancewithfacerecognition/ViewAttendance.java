package com.ertugrul.attendancewithfacerecognition;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.legacy.app.FragmentCompat;

import com.ertugrul.attendancewithfacerecognition.DB.StudentLogin;
import com.ertugrul.attendancewithfacerecognition.Utilities.StudentAttendanceEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAttendance extends Fragment {

    ListView viewAttendanceList;
    StudentListAdapter studentListAdapter;
    List<StudentLogin> allStudents;
    List<StudentAttendanceEntity> studentAttendance;
    List<String> studentIds;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_view_attendance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        viewAttendanceList = view.findViewById(R.id.viewAttendanceList);
    }

    public void getAllStudents() {
        String courseId = Prefs.getString("courseId", "");
        getStudentIds(courseId);
        allStudents = new ArrayList<>(Arrays.asList(((new Gson()).fromJson(Prefs.getString("students", ""), StudentLogin[].class))));
        studentAttendance = new ArrayList<>(Arrays.asList(((new Gson()).fromJson(Prefs.getString("studentAttendance", ""), StudentAttendanceEntity[].class))));



        if (allStudents.isEmpty()) {
            viewAttendanceList.setVisibility(View.GONE);
            (getActivity().findViewById(R.id.noStudentsFoundText)).setVisibility(View.VISIBLE);
            (getActivity().findViewById(R.id.takeAttendance)).setVisibility(View.GONE);

        } else {
            (getActivity().findViewById(R.id.noStudentsFoundText)).setVisibility(View.GONE);
            (getActivity().findViewById(R.id.takeAttendance)).setVisibility(View.VISIBLE);
        }

        Collections.sort(allStudents, new Comparator<StudentLogin>() {
            @Override
            public int compare(StudentLogin t1, StudentLogin t2) {
                return (t1.getFullName()).compareTo(t2.getFullName());
            }
        });


        studentListAdapter = new StudentListAdapter(getActivity(), R.layout.list_view_students_row, allStudents);
        viewAttendanceList.setAdapter(studentListAdapter);
    }

    @Override
    public void onStart() {
        if (alreadyHasPermission()) {
            getAllStudents();
        } else {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        super.onStart();
    }

    private boolean alreadyHasPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onStart();
                } else {
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*public synchronized List<String>[] getStudentIds(final String courseId){
        final CountDownLatch done = new CountDownLatch(1);
        final List<String>[] studentIds = new List[0];
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference courseRef = dbRef.child("courses");
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("courseId").getValue().equals(courseId)){
                        HashMap<String,String> hashMap = (HashMap<String, String>) ds.child("studentIds").getValue();
                        studentIds[0] = new ArrayList<>(hashMap.values());
                    }
                }
                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                done.countDown();
            }
        });
        try {
            done.await();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return  studentIds;
    }*/
    public void getStudentIds(final String courseId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference courseRef = dbRef.child("courses");
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<String> studentIds = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("courseId").getValue().equals(courseId)) {
                        HashMap<String, String> hashMap = (HashMap<String, String>) ds.child("studentIds").getValue();
                        for (Map.Entry me : hashMap.entrySet()) {
                            studentIds.add((String) me.getKey());
                        }
                    }
                }
                Prefs.putString("studentIds", new Gson().toJson(studentIds));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public class StudentListAdapter extends ArrayAdapter<StudentLogin> {

        private Context context;

        public StudentListAdapter(Activity context, int resource, List<StudentLogin> students) {
            super(context, resource, students);
            this.context = context;
        }


        @Override
        public long getItemId(int i) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            final StudentLogin student = getItem(position);

            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_view_students_row, parent, false);
            }


            TextView studentName = convertView.findViewById(R.id.studentName);
            CircleImageView studentFaceImage = convertView.findViewById(R.id.studentFaceImage);
            studentFaceImage.setImageResource(R.drawable.person_icon);
            TextView studentRegNo = convertView.findViewById(R.id.studentRegNo);

            final TextView attendanceText = convertView.findViewById(R.id.attendanceText);
            TextView maxAttendanceText = convertView.findViewById(R.id.maxAttendanceText);

            assert student != null;
            studentName.setText(student.getFullName());
            studentRegNo.setText(student.getStudentId());


            CircleImageView deleteStudentButton = convertView.findViewById(R.id.deleteStudentFromCourseButton);
            deleteStudentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //new DeletePersonGroupTask().execute(course.courseId);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                    ab.setMessage("Are you sure to remove the student from this course?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }
            });
            //AppDatabase db = AppDatabase.getAppDatabase(getActivity());
            //
            //if (db.attendanceDao().getAttendance(student.courseId, student..getStudentId())==null){
            //    db.attendanceDao().insertAll(new Attendance(student.regNo, student.courseId, 0));
            //}


            int attendanceNumber = 0;
            for (StudentAttendanceEntity stu : studentAttendance){
                if (stu.getStudentNumber().equals(student.getUserId()))
                    attendanceNumber = stu.getAttendanceNumber();
            }

            int maxAttendance = Integer.parseInt(Prefs.getString("selectedCourseMaxAttendance", ""));

            attendanceText.setText(""+attendanceNumber);
            maxAttendanceText.setText("/"+maxAttendance);

//
            return convertView;

        }



    }
}
