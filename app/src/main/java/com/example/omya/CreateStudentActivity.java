package com.example.omya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateStudentActivity extends AppCompatActivity {

    private EditText inputemail, inputpassword, retypePassword, fullName, input_aadhar;
    private FirebaseAuth mAuth;
    private Button btnSignup;
    private ProgressDialog pd;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_student);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        dbRef = FirebaseDatabase.getInstance().getReference("doormint/account/student");
        mAuth = FirebaseAuth.getInstance();

        inputemail = findViewById(R.id.input_userEmail);
        inputpassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);
        btnSignup = findViewById(R.id.button_register);
        input_aadhar = findViewById(R.id.input_aadhar);

        if (mAuth.getCurrentUser() != null) {
            inputemail.setVisibility(View.GONE);
            inputpassword.setVisibility(View.GONE);
            retypePassword.setVisibility(View.GONE);
            btnSignup.setText("Update Profile");

            fullName.setText("Your name");
            pd.dismiss();
        } else {
            pd.dismiss();
        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputemail.getText().toString().trim();
                final String password = inputpassword.getText().toString().trim();
                final String confirmPassword = retypePassword.getText().toString().trim();
                final String name = fullName.getText().toString().trim();
                final String aadhar = input_aadhar.getText().toString().trim();

                if (name.isEmpty() || name.length() <= 2) {
                    Toast.makeText(CreateStudentActivity.this, "Please, enter a valid Name", Toast.LENGTH_LONG).show();
                    fullName.requestFocusFromTouch();
                } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(CreateStudentActivity.this, "Please, enter a valid Email", Toast.LENGTH_LONG).show();
                    inputemail.requestFocusFromTouch();
                } else if (password.isEmpty() || password.length() <= 5) {
                    Toast.makeText(CreateStudentActivity.this, "Please, enter a valid Password", Toast.LENGTH_LONG).show();
                    inputpassword.requestFocusFromTouch();
                }  else if (aadhar.isEmpty() || aadhar.length() !=12 ) {
                    Toast.makeText(CreateStudentActivity.this, "Please, enter a valid Aadhar", Toast.LENGTH_LONG).show();
                    inputpassword.requestFocusFromTouch();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(CreateStudentActivity.this, "Password did not match!", Toast.LENGTH_LONG).show();
                    retypePassword.requestFocusFromTouch();
                } else {
                    pd.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateStudentActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(CreateStudentActivity.this, "Registration failed! Try again.", Toast.LENGTH_LONG).show();
                                Log.v("error", task.getException().getMessage());
                            } else {
                                String id = mAuth.getCurrentUser().getUid();
                                // Store the role as "Student" and additional details
                                dbRef.child(id).child("Name").setValue(name);
                                dbRef.child(id).child("Role").setValue("Student");
                                dbRef.child(id).child("Aadhar").setValue(aadhar);

                                Toast.makeText(CreateStudentActivity.this, "Welcome, your account has been created!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CreateStudentActivity.this, StudentMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }
}
