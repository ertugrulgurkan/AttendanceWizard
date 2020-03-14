package com.ertugrul.attendancewithfacerecognition;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button signIn;
    EditText email, password;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(100);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.signInEmail);
        password = findViewById(R.id.signInPassword);

        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }
            }
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userType = Prefs.getString("userType","");
        if (user != null) {
            // User is signed in
            if (userType.equals("student"))
                startActivity(new Intent(SignIn.this, UploadPhoto.class));
            else if (userType.equals("teacher"))
                startActivity(new Intent(SignIn.this, EditCourses.class));
        }


    }

    public void signIn() {
        String emailText = email.getText().toString().trim();
        String passText = password.getText().toString().trim();

        if (emailText.isEmpty()) {
            email.setError("Please enter an Email Address");
            email.requestFocus();
            return;
        }

        if (passText.isEmpty()) {
            password.setError("Please enter a Password");
            password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailText, passText).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(SignIn.class.getName(), "signInWithEmail:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseDatabase d = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = d.getReference().child("users").child(user.getUid());

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    boolean isStudent = false;
                                    String schoolCode="";
                                    List<String> courseIds = new ArrayList<>();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.getValue().equals("student"))
                                            isStudent = true;
                                        if (ds.getKey().equals("schoolCode"))
                                            schoolCode = ds.getValue(String.class);
                                        if (ds.getKey().equals("courseIds")){
                                            HashMap<String,String> hashMap = (HashMap<String, String>) ds.getValue();
                                            courseIds = new ArrayList<>(hashMap.values());
                                        }
                                    }
                                    Prefs.putString("schoolCode", schoolCode);
                                    Prefs.putString("UserCourseIds", new Gson().toJson(courseIds));
                                    if (isStudent) {
                                        Prefs.putString("userType","student");
                                        Intent i = new Intent(SignIn.this, UploadPhoto.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        //finish();
                                        startActivity(i);
                                    }
                                    else{
                                        Prefs.putString("userType","teacher");
                                        Intent i = new Intent(SignIn.this, EditCourses.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        //finish();
                                        startActivity(i);

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


                        }
                        else {
                            Toast.makeText(SignIn.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                            Log.i("fail user creation", task.getException().getLocalizedMessage().toString());
                        }
                    }
                });
    }
}
