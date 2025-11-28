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

public class CreateOwnerActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, retypePassword, fullName;
    private FirebaseAuth mAuth;
    private Button btnSignup;
    private ProgressDialog pd;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        setContentView(R.layout.activity_create_owner);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Create Owner");
        }

        dbRef = FirebaseDatabase.getInstance().getReference("doormint/account/owner"); // Adjusted path for owners
        mAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.input_userEmail);
        inputPassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);

        btnSignup = findViewById(R.id.button_register);

        pd.dismiss();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String confirmPassword = retypePassword.getText().toString().trim();
                final String name = fullName.getText().toString().trim();

                if (validateInput(name, email, password, confirmPassword)) {
                    registerOwner(email, password, name, sharedPreferences);
                }
            }
        });
    }

    /**
     * Validates user input.
     *
     * @param name            The full name of the owner.
     * @param email           The email address.
     * @param password        The password.
     * @param confirmPassword The password confirmation.
     * @return true if all inputs are valid; false otherwise.
     */
    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty() || name.length() <= 2) {
            Toast.makeText(CreateOwnerActivity.this, "Please, enter a valid Name", Toast.LENGTH_LONG).show();
            fullName.requestFocus();
            return false;
        } else if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(CreateOwnerActivity.this, "Please, enter a valid Email", Toast.LENGTH_LONG).show();
            inputEmail.requestFocus();
            return false;
        } else if (password.isEmpty() || password.length() <= 5) {
            Toast.makeText(CreateOwnerActivity.this, "Please, enter a valid Password", Toast.LENGTH_LONG).show();
            inputPassword.requestFocus();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(CreateOwnerActivity.this, "Password did not match!", Toast.LENGTH_LONG).show();
            retypePassword.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Registers a new owner in Firebase.
     *
     * @param email            The email address.
     * @param password         The password.
     * @param name             The full name.
     * @param sharedPreferences The shared preferences instance for storing owner data.
     */
    private void registerOwner(String email, String password, String name, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("owner_email", email);
        editor.apply();

        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateOwnerActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(CreateOwnerActivity.this, "Registration failed! Try again.", Toast.LENGTH_LONG).show();
                            Log.v("error", task.getException().getMessage());
                        } else {
                            saveOwnerDataToDatabase(name);
                        }
                    }
                });
    }

    /**
     * Saves the owner's data to the Firebase database.
     *
     * @param name The full name of the owner.
     */
    private void saveOwnerDataToDatabase(String name) {
        String userId = mAuth.getCurrentUser().getUid();
        dbRef.child(userId).child("Name").setValue(name);
        dbRef.child(userId).child("Role").setValue("Owner")
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateOwnerActivity.this, "Welcome, your account has been created!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateOwnerActivity.this, OwnerMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.v("error", task.getException().getMessage());
                        Toast.makeText(CreateOwnerActivity.this, "Failed to save data! Try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
