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

import com.ertugrul.attendancewithfacerecognition.DB.StudentLogin;
import com.ertugrul.attendancewithfacerecognition.DB.TeacherLogin;
import com.ertugrul.attendancewithfacerecognition.DB.UserLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;

public class SignUp extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private Button signUp;
    private EditText email, password1, password2, fullName,studentNo,title;
    private TextView alreadyAccount;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String fullNameText = "", emailText = "", pass1Text ="",pass2Text = "",studentNoText="",titleText="";

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailText, pass1Text)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            int radioId = radioGroup.getCheckedRadioButtonId();
                            radioButton = findViewById(radioId);
                            UserLogin currentUser;
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

                            if (radioButton.getText().equals("Teacher")){
                                currentUser = new TeacherLogin(user.getEmail(),fullNameText, (String) radioButton.getText() ,titleText);
                                ref.child(user.getUid()).setValue(currentUser);
                                Toast.makeText(SignUp.this, "Teacher was successfully created", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUp.this, EditCourses.class);

                                i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Prefs.putString("UserID", user.getUid());
                                Prefs.putString("UserEmail", user.getEmail());
                                Prefs.putString("UserDisplayName", user.getDisplayName());
                                Prefs.putString("UserCourseIds", "");

                                startActivity(i);
                            }
                            else{
                                currentUser = new StudentLogin(user.getEmail(),fullNameText, (String) radioButton.getText() ,studentNoText);
                                ref.child(user.getUid()).setValue(currentUser);
                                Toast.makeText(SignUp.this, "Student was successfully created", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUp.this, AddStudent.class);

                                i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Prefs.putString("UserID", user.getUid());
                                Prefs.putString("UserEmail", user.getEmail());
                                Prefs.putString("UserDisplayName", user.getDisplayName());
                                Prefs.putString("UserCourseIds", "");

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
