package com.ertugrul.attendancewithfacerecognition;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ertugrul.attendancewithfacerecognition.DB.School;
import com.ertugrul.attendancewithfacerecognition.DB.StudentLogin;
import com.ertugrul.attendancewithfacerecognition.DB.TeacherLogin;
import com.ertugrul.attendancewithfacerecognition.DB.UserLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUp extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    private Button signUp;
    private EditText email, password1, password2, fullName,studentNo,title,schoolCode;
    private TextView alreadyAccount;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String fullNameText = "", emailText = "", pass1Text ="",pass2Text = "",studentNoText="",titleText="",schoolCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(100);

        if (getString(R.string.subscription_key).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        }

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        // Firebase
        schoolCode = findViewById(R.id.schoolCode);
        email = findViewById(R.id.email);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        fullName = findViewById(R.id.fullName);
        title = findViewById(R.id.title);
        studentNo = findViewById(R.id.studentNo);
        signUp = findViewById(R.id.signUp);
        alreadyAccount = findViewById(R.id.alreadyAccount);
        radioGroup = findViewById(R.id.radioGroup);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUp();

            }
        });

        alreadyAccount.setText(fromHtml(getString(R.string.already_account)));
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp.this, SignIn.class);
                startActivity(i);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            startActivity(new Intent(SignUp.this, EditCourses.class));
        }

    }


    private boolean fieldsValidation() {
        emailText = email.getText().toString().trim();
        fullNameText = fullName.getText().toString().trim();
        pass1Text = password1.getText().toString().trim();
        pass2Text = password2.getText().toString().trim();
        schoolCodeText = schoolCode.getText().toString().trim();

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);

        if (radioButton.getText().equals("Teacher")){
            titleText = title.getText().toString().trim();
            if(titleText.isEmpty()){
                title.setError("Please enter your title");
                title.requestFocus();
                return false;
            }
        }
        else{
            studentNoText = studentNo.getText().toString().trim();
            if(studentNoText.isEmpty()){
                studentNo.setError("Please enter your student number");
                studentNo.requestFocus();
                return false;
            }
        }
        if(schoolCodeText.isEmpty()){
            schoolCode.setError("Please enter the school code");
            schoolCode.requestFocus();
            return false;
        }
        if(emailText.isEmpty()){
            email.setError("Please enter an Email Address");
            email.requestFocus();
            return false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Please enter a valid Email Address");
            email.requestFocus();
            return false;
        }

        if(pass1Text.isEmpty()){
            password1.setError("Please enter a Password");
            password1.requestFocus();
            return false;
        }
        if(pass2Text.isEmpty()){
            password2.setError("Please enter a Repeated Password");
            password2.requestFocus();
            return false;
        }
        if(!pass1Text.equals(pass2Text)){
            password2.setError("Both the passwords must match");
            password2.requestFocus();
            return false;
        }
        if(fullNameText.isEmpty()){
            fullName.setError("Please enter your Full Name");
            fullName.requestFocus();
            return false;
        }

        return true;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        TextInputLayout input_layout_title = findViewById(R.id.input_layout_title);
        TextInputLayout input_layout_studentNo = findViewById(R.id.input_layout_studentNo);

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.teacher:
                if (checked){
                    input_layout_studentNo.setVisibility(View.GONE);
                    input_layout_title.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.student:
                if (checked){
                    input_layout_title.setVisibility(View.GONE);
                    input_layout_studentNo.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    void signUp(){
        if (!fieldsValidation()) return;

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailText, pass1Text)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            int radioId = radioGroup.getCheckedRadioButtonId();
                            radioButton = findViewById(radioId);
                            FirebaseDatabase d = FirebaseDatabase.getInstance();
                            DatabaseReference mDatabase = d.getReference();
                            List<String> courseIds = new ArrayList<>();
                            if (radioButton.getText().equals("Teacher")){
                                TeacherLogin teacher = new TeacherLogin(user.getUid(),schoolCodeText,emailText,fullNameText,null, titleText);
                                UserLogin userLogin = new UserLogin("teacher", user.getUid(),schoolCodeText);
                                School school = new School(schoolCodeText);

                                String key = mDatabase.child("schools").push().getKey();
                                Map<String, Object> schoolValues = school.toMap();
                                Map<String, Object> childUpdate = new HashMap<>();
                                childUpdate.put("/schools/" + key, schoolValues);
                                mDatabase.updateChildren(childUpdate);

                                mDatabase.child("teachers").child(user.getUid()).setValue(teacher);
                                mDatabase.child("users").child(user.getUid()).setValue(userLogin);

                                //ref.child("users").child(userId).child("username").setValue(name);
                                //ref.child(user.getUid()).setValue(currentUser);
                                Toast.makeText(SignUp.this, "Teacher was successfully created", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUp.this, EditCourses.class);

                                i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Prefs.putString("email", emailText);
                                Prefs.putString("fullName", fullNameText);
                                Prefs.putString("schoolCode", schoolCodeText);
                                Prefs.putString("title", titleText);
                                //Prefs.putString("courseIds", new Gson().toJson(teacher.getCourseIds()));
                                finish();
                                startActivity(i);
                            }
                            else{
                                StudentLogin student = new StudentLogin(user.getUid(),fullNameText,studentNoText,schoolCodeText,emailText,null,null);
                                UserLogin userLogin = new UserLogin("student", user.getUid(),schoolCodeText);

                                School school = new School(schoolCodeText);

                                String key = mDatabase.child("schools").push().getKey();
                                Map<String, Object> schoolValues = school.toMap();
                                Map<String, Object> childUpdate = new HashMap<>();
                                childUpdate.put("/schools/" + key, schoolValues);
                                mDatabase.updateChildren(childUpdate);

                                mDatabase.child("students").child(user.getUid()).setValue(student);
                                mDatabase.child("users").child(user.getUid()).setValue(userLogin);
                                Toast.makeText(SignUp.this, "Student was successfully created", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUp.this, UploadPhoto.class);

                                i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                System.out.println("SignUp " + student.toString());
                                Prefs.putString("email", emailText);
                                Prefs.putString("fullName", fullNameText);
                                Prefs.putString("schoolCode", schoolCodeText);
                                Prefs.putString("schoolId", studentNoText);
                                //Prefs.putString("courseIds", new Gson().toJson(student.getCourseIds()));

                                startActivity(i);

                            }

                            //User currentUser = new User(user.getEmail(), fullNameText, (String) radioButton.getText());






                        }
                        else{
                            Toast.makeText(SignUp.this, "User could not be created "+task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                            Log.i("fail user creation", task.getException().getLocalizedMessage().toString());
                        }
                    }
                });
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
