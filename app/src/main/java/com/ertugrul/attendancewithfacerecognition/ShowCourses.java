package com.ertugrul.attendancewithfacerecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ertugrul.attendancewithfacerecognition.DB.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.LargePersonGroup;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowCourses extends AppCompatActivity {

    ListView courseListView;
    CoursesListAdapter coursesListAdapter;
    DatabaseReference mDatabase;
    String schoolCode;
    List<String> courseIds;
    FirebaseDatabase d;
    DatabaseReference dbRef;
    String userName;
    String userId;
    String selectedCourseId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_courses);
        d = FirebaseDatabase.getInstance();
        dbRef = d.getReference();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(100);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        schoolCode = Prefs.getString("schoolCode", "");
        courseIds = new ArrayList<>(Arrays.asList(((new Gson()).fromJson(Prefs.getString("userCourseIds", ""), String[].class))));
        courseListView = findViewById(R.id.courseList);


        //Log.v("EditCourses", Prefs.getString("UserID", "A"));
        //Log.v("EditCourses", Prefs.getString("UserEmail", "A"));
        //Log.v("EditCourses", Prefs.getString("UserDisplayName", "A"));
        //Log.v("EditCourses", Prefs.getString("userCourseIds", "A"));
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
                                startActivity(new Intent(ShowCourses.this, MainActivity.class));
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(ShowCourses.this);
                ab.setMessage("Are you sure you want to Logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            courseIds = new GetAllCourses().execute(courseIds).get();
            new GetStudentName().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GetPersonGroupList().execute();
    }


    public class CoursesListAdapter extends ArrayAdapter<Course> {

        private Context context;

        public CoursesListAdapter(Activity context, int resource, List<Course> courses) {
            super(context, resource, courses);
            this.context = context;
        }


        @Override
        public long getItemId(int i) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            final Course course = getItem(position);
            selectedCourseId = course.courseId;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_all_course_row, parent, false);
            }


            TextView courseNameAndYear = convertView.findViewById(R.id.courseNameAndYear);
            TextView courseCode = convertView.findViewById(R.id.courseCode);

            courseNameAndYear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog = ProgressDialog.show(ShowCourses.this,
                            "Please Wait",
                            "Loading...");
                        Prefs.putString("selectedCourseId", course.courseId);
                        Log.i("LOG", "Student full name is : " + userName);
                        Log.i("LOG", "Selected Course id is : " + selectedCourseId);
                    DatabaseReference databaseReference = dbRef.child("courses").child(course.courseId).child("studentIds");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Boolean isEnrolled = false;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getValue().equals(userId))
                                    isEnrolled = true;
                            }
                            progressDialog.dismiss();
                            if (isEnrolled){
                                Toast.makeText(ShowCourses.this, "Already enrolled this course.", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Intent intent = new Intent(ShowCourses.this, UploadPhoto.class);
                                startActivity(intent);
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });




                    /////





                    //Boolean isEnrolled;
                    //try {
                    //    isEnrolled = new checkEnrolled().execute().get();
                    //    if (isEnrolled){
                    //        /// buraya bir async task konup boolean değerle enroll olunup olunmadığı kontol edilebilir. eğer olunmussa toast mesajı ile you are already enrolled this course yazılabilir.
                    //        Intent intent = new Intent(ShowCourses.this, UploadPhoto.class);
                    //        startActivity(intent);
                    //    }
                    //    else{
                    //        Toast.makeText(ShowCourses.this, "Already enrolled this course.", Toast.LENGTH_LONG).show();
                    //    }
                    //} catch (ExecutionException e) {
                    //    e.printStackTrace();
                    //} catch (InterruptedException e) {
                    //    e.printStackTrace();
                    //}


                }
            });

            assert course != null;
            courseNameAndYear.setText(course.getCourseName() + " - Year " + course.getYear());
            courseCode.setText(course.getCourseCode());

            return convertView;

        }
    }

    class GetAllCourses extends AsyncTask<List<String>, Void, List<String>> {
        List<String> list = new ArrayList<>();

        protected List<String> doInBackground(List<String>... lists) {
            schoolCode = Prefs.getString("schoolCode", "");
            list = lists[0];

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference schoolRef = dbRef.child("courses");
            schoolRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("schoolCode").getValue().equals(schoolCode))
                            list.add((String) ds.child("courseId").getValue());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            return list;
        }
    }


    class GetPersonGroupList extends AsyncTask<Void, Void, LargePersonGroup[]> {

        @Override
        protected LargePersonGroup[] doInBackground(Void... params) {
            Log.v("", "Generate list of all person groups");

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try {
                Log.v("", ("Syncing with server to add person group..."));

                return faceServiceClient.listLargePersonGroups();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(LargePersonGroup[] allLpgs) {

            //if lpg course of not user remove
            if (allLpgs.length == 0) {
                Toast.makeText(ShowCourses.this, "No Courses Created Yet", Toast.LENGTH_LONG).show();
                (findViewById(R.id.classListProgress)).setVisibility(View.GONE);
                return;
            }

            if (courseIds.equals("")) {
                Toast.makeText(ShowCourses.this, "No Courses Created Yet", Toast.LENGTH_LONG).show();
                (findViewById(R.id.classListProgress)).setVisibility(View.GONE);
                return;
            }

            List<LargePersonGroup> courseLpgs = new ArrayList<>();

            for (LargePersonGroup lpg : allLpgs) {
                if (courseIds.contains(lpg.largePersonGroupId))
                    courseLpgs.add(lpg);
            }

            if (courseLpgs.size() == 0) {
                Toast.makeText(ShowCourses.this, "No Courses Created Yet", Toast.LENGTH_LONG).show();
                (findViewById(R.id.classListProgress)).setVisibility(View.GONE);
                return;
            }

            List<Course> courseList = new ArrayList<>();
            for (LargePersonGroup lpg : courseLpgs) {
                Course course = (new Gson()).fromJson(lpg.userData, Course.class);
                courseList.add(course);
            }

            (findViewById(R.id.classListProgress)).setVisibility(View.GONE);

            coursesListAdapter = new CoursesListAdapter(ShowCourses.this, R.layout.list_course_row, courseList);
            courseListView.setAdapter(coursesListAdapter);


        }
    }

    class GetStudentName extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            userId = Prefs.getString("userId", "");
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference studentRef = dbRef.child("students");
            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey().equals(userId)){
                            userName = ds.child("fullName").getValue(String.class);
                            Prefs.putString("userName", userName);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return null;
        }
    }
    private class checkEnrolled extends AsyncTask<Void, Void, Boolean>{

        boolean running;
        boolean isEnrolled = false;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;

            progressDialog = ProgressDialog.show(ShowCourses.this,
                    "Please Wait",
                    "Loading...");

            //Toast.makeText(ShowCourses.this,"Progress Start",Toast.LENGTH_LONG).show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            DatabaseReference databaseReference = dbRef.child("courses").child(selectedCourseId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("studentIds").exists() && ds.child("studentIds").getValue().equals(userId))
                            isEnrolled = true;
                    }
                    //progressDialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return isEnrolled;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            //Toast.makeText(ShowCourses.this,"Progress Ended",Toast.LENGTH_LONG).show();
            //progressDialog.dismiss();
        }

    }

}
    /*class DeletePersonGroupTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.v("","Request: Delete person group " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try{
                Log.v("",("Deleting person group..."));
                faceServiceClient.deleteLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(final String deletedCourseId) {
            if(!deletedCourseId.equals("")){
                Toast.makeText(ShowCourses.this, "Course successfully deleted", Toast.LENGTH_LONG).show();

                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase d = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference = d.getReference().child("users").child(user.getUid()).child("courseIds");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        List<String> courseIds = new ArrayList<>();
                        for (DataSnapshot courseData: ds.getChildren()){
                            if (!courseData.getValue().equals(deletedCourseId)){
                                courseIds.add((String)courseData.getValue());
                            }
                        }
                        databaseReference.setValue(courseIds);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                onStart();
            }
            else{
                Toast.makeText(ShowCourses.this, "Course could not be deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
*/


