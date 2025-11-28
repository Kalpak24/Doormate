package com.example.omya;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginStudentActivity extends AppCompatActivity {

    private Button signin;
    private TextView button_signup, resetpass;
    private EditText inputemail, inputpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        // Initialize Firebase database reference
        dbRef = FirebaseDatabase.getInstance().getReference("doormint/account/student");

        // Show progress dialog to check if the user is logged in
        pd.show();

        if (mAuth.getCurrentUser() != null) {
            // If the user is already logged in, navigate to the StudentMainActivity directly
            validateUserRole();
        } else {
            // If not logged in, dismiss the progress dialog
            pd.dismiss();
        }

        inputemail = findViewById(R.id.input_username);
        inputpassword = findViewById(R.id.input_password);

        signin = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signup);
        resetpass = findViewById(R.id.button_forgot_password);

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginStudentActivity.this, CreateStudentActivity.class);
                startActivity(i);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputemail.getText().toString().trim();
                final String password = inputpassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    pd.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginStudentActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                "Authentication Failed",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("error", task.getException().getMessage());
                                    } else {
                                        // Validate the user role before proceeding
                                        validateUserRole();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
                }
            }
        });

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestoreStudentActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Validates the user's role after login.
     * If the role is "Student", navigate to the StudentMainActivity.
     */
    private void validateUserRole() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        dbRef.child(currentUserId).child("Role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);
                if ("Student".equals(role)) {
                    // Role is Student, proceed to the StudentMainActivity
                    pd.dismiss();
                    Intent intent = new Intent(LoginStudentActivity.this, StudentMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Role is not Student, show an error or direct to a different activity
                    pd.dismiss();
                    showUnauthorizedDialog();
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pd.dismiss();
                showErrorDialog("Error validating user role");
            }
        });
    }

    /**
     * Shows a dialog when the user is not authorized.
     */
    private void showUnauthorizedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unauthorized Access")
                .setMessage("You are not authorized to access this section.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * Shows an error dialog.
     * @param message The message to be displayed in the dialog.
     */
    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
