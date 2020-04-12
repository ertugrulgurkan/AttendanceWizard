package com.ertugrul.attendancewithfacerecognition;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ertugrul.attendancewithfacerecognition.Utilities.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPhoto extends AppCompatActivity {

    String studentName;
    String regNo;

    ImageView takenImageForStudent;
    private static final int PICK_IMAGE_ID = 200;
    Bitmap bitmap;
    FirebaseDatabase d;
    DatabaseReference dbRef;
    boolean imageTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(100);

        takenImageForStudent = findViewById(R.id.takenImageForStudent);
        d = FirebaseDatabase.getInstance();
        dbRef = d.getReference();

        ((EditText)findViewById(R.id.regNo)).setText(Prefs.getString("userId", ""), TextView.BufferType.EDITABLE);
        ((EditText)findViewById(R.id.studentName)).setText(Prefs.getString("selectedCourseId", ""), TextView.BufferType.EDITABLE);
        takenImageForStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext(), getString(R.string.pick_image_intent_text_student));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });

        (findViewById(R.id.rotate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = ImagePicker.rotate(bitmap, 90);
                takenImageForStudent.setImageBitmap(bitmap);
            }
        });

        final Button upload = findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (alreadyHasPermission()){
                    //studentName = ((EditText)findViewById(R.id.studentName)).getText().toString();
                    //regNo = ((EditText)findViewById(R.id.regNo)).getText().toString();

                    if(!imageTaken){
                        Toast.makeText(UploadPhoto.this, "Select an image for the student", Toast.LENGTH_SHORT).show();
                    }
                    /*else if (studentName.equals("")){
                        ((EditText)findViewById(R.id.studentName)).setError("Please enter a Student Name");
                        (findViewById(R.id.studentName)).requestFocus();
                    }
                    else if (regNo.equals("")){
                        ((EditText)findViewById(R.id.regNo)).setError("Please enter a Registration Number");
                        (findViewById(R.id.regNo)).requestFocus();
                    }
                    else if (database.studentDao().checkRegNoUnique(regNo)==1){
                        ((EditText)findViewById(R.id.regNo)).setError("Registration number should be unique");
                        (findViewById(R.id.regNo)).requestFocus();
                    }
                    */
                    else{
                        new AddPersonTask().execute(Prefs.getString("selectedCourseId", ""), Prefs.getString("userName", ""), Prefs.getString("userId", ""));
                    }
                }
                else{
                    ActivityCompat.requestPermissions(UploadPhoto.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

            }
        });




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
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(UploadPhoto.this, MainActivity.class));
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder ab = new AlertDialog.Builder(UploadPhoto.this);
                ab.setMessage("Are you sure you want to Logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(UploadPhoto.this, ShowCourses.class));
    }





    private boolean alreadyHasPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    Toast.makeText(getApplicationContext(), "Permission denied to write your External storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                takenImageForStudent.setImageBitmap(bitmap);

                imageTaken = true;

                if (bitmap == null){
                    takenImageForStudent.setImageDrawable(getDrawable(R.drawable.circle_icon));
                    imageTaken = false;
                }
                else{
                    (findViewById(R.id.rotate)).setVisibility(View.VISIBLE);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    // Background task of adding a person to person group.
    class AddPersonTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try{
                publishProgress("Syncing with server to add person...");
                Log.v("","Request: Creating Person in person group" + params[0]);

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0], //personGroupID
                        params[1], //name
                        params[2]); //userData or regNo
                return createPersonResult.personId.toString();


            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.v("",e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String personId) {

            if (personId != null) {
                Log.v("","Response: Success. Person " + personId + " created.");
                String userId = Prefs.getString("userId", "");
                String selectedCourseId = Prefs.getString("selectedCourseId", "");
                //Toast.makeText(AddStudent.this, "Person with personId "+personId+" successfully created", Toast.LENGTH_SHORT).show();
                Toast.makeText(UploadPhoto.this, "Student was successfully created", Toast.LENGTH_SHORT).show();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + userId + "/courseIds/"+ selectedCourseId, selectedCourseId);
                childUpdates.put("/students/" + userId + "/courseIds/"+ selectedCourseId, selectedCourseId);
                childUpdates.put("/courses/" + selectedCourseId + "/studentIds/"+ userId, personId);
                dbRef.updateChildren(childUpdates);
                new AddFaceTask().execute(personId);
            }
        }
    }

    class AddFaceTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try{
                Log.v("", "Adding face...");
                UUID personId = UUID.fromString(params[0]);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());

                AddPersistedFaceResult result = faceServiceClient.addPersonFaceInLargePersonGroup(
                        Prefs.getString("selectedCourseId", ""),
                        personId,
                        imageInputStream,
                        "",
                        null);


                File folder = new File(Environment.getExternalStorageDirectory(), "/Faces/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                File photo = new File(Environment.getExternalStorageDirectory(), "/Faces/"+result.persistedFaceId.toString()+".jpg");
                if (photo.exists()) {
                    photo.delete();
                }

                try {



                    FileOutputStream fos= new FileOutputStream(photo.getPath());

                    fos.write(stream.toByteArray());
                    fos.close();

                    Log.v("Store face in storage", "Face stored with name "+photo.getName()+" and path "+photo.getAbsolutePath());
                }
                catch (java.io.IOException e) {
                    Log.e("Store face in storage", "Exception in photoCallback", e);
                }

                return result.persistedFaceId.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String persistedFaceId) {
            Log.v("", "Successfully added face with persistence id "+persistedFaceId);

            Toast.makeText(UploadPhoto.this, "Face was successfully added to the student", Toast.LENGTH_SHORT).show();

            //Toast.makeText(AddStudent.this, "Face with persistedFaceId "+persistedFaceId+" successfully created", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UploadPhoto.this, ShowCourses.class));
        }
    }
}
