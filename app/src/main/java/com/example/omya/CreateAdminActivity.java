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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAdminActivity extends AppCompatActivity {

    private EditText inputemail, inputpassword, retypePassword, fullName, key;
    private FirebaseAuth mAuth;
    private Button btnSignup;
    private ProgressDialog pd;
    private DatabaseReference db_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        setContentView(R.layout.activity_create_admin);

        String ukey = "1241"; // Replace "hello" with your actual admin creation key
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Create Admin");
        }

        db_ref = FirebaseDatabase.getInstance().getReference("doormint/account/admin"); // Path for user data
        mAuth = FirebaseAuth.getInstance();

        inputemail = findViewById(R.id.input_userEmail);
        inputpassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);
        key = findViewById(R.id.key);

        btnSignup = findViewById(R.id.button_register);

        pd.dismiss();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputemail.getText().toString().trim();
                final String password = inputpassword.getText().toString().trim();
                final String confirmPassword = retypePassword.getText().toString().trim();
                final String name = fullName.getText().toString().trim();
                final String enteredKey = key.getText().toString().trim();

                if (name.isEmpty() || name.length() <= 2) {
                    Toast.makeText(CreateAdminActivity.this, "Please, enter a valid Name", Toast.LENGTH_LONG).show();
                    fullName.requestFocusFromTouch();
                } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(CreateAdminActivity.this, "Please, enter a valid Email", Toast.LENGTH_LONG).show();
                    inputemail.requestFocusFromTouch();
                } else if (password.isEmpty() || password.length() <= 5) {
                    Toast.makeText(CreateAdminActivity.this, "Please, enter a valid Password", Toast.LENGTH_LONG).show();
                    inputpassword.requestFocusFromTouch();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(CreateAdminActivity.this, "Password did not match!", Toast.LENGTH_LONG).show();
                    retypePassword.requestFocusFromTouch();
                } else if (!enteredKey.equals(ukey)) {
                    Toast.makeText(CreateAdminActivity.this, "Key does not match", Toast.LENGTH_LONG).show();
                    key.requestFocusFromTouch();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("admin_email", email);
                    editor.apply();
                    pd.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(CreateAdminActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(CreateAdminActivity.this, "Registration failed! Try again.", Toast.LENGTH_LONG).show();
                                        Log.v("error", task.getException().getMessage());
                                    } else {
                                        String id = mAuth.getCurrentUser().getUid();
                                        db_ref.child(id).child("Name").setValue(name);
                                        db_ref.child(id).child("role").setValue("admin"); // Set role as admin
                                        Toast.makeText(CreateAdminActivity.this, "Welcome, your admin account has been created!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CreateAdminActivity.this, AdminMainActivity.class);
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
