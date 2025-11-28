package com.example.omya;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

public class LoginOwnerActivity extends AppCompatActivity {

    private Button signin;
    private TextView button_signup, resetpass;
    private EditText inputemail, inputpassword;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private ProgressDialog pd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_owner);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("doormint/account/owner");
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        // Check if the user is already logged in
        if (mAuth.getCurrentUser() != null) {
            checkUserRole(mAuth.getCurrentUser().getUid());
        }

        inputemail = findViewById(R.id.input_username);
        inputpassword = findViewById(R.id.input_password);

        signin = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signup);
        resetpass = findViewById(R.id.button_forgot_password);

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginOwnerActivity.this, CreateOwnerActivity.class);
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
                            .addOnCompleteListener(LoginOwnerActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                "Authentication Failed",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("error", task.getException().getMessage());
                                    } else {
                                        // Verify user role before proceeding
                                        checkUserRole(mAuth.getCurrentUser().getUid());
                                    }
                                }
                            });
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("owner_email", email);
                    editor.apply();

                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields.", Toast.LENGTH_LONG).show();
                }
            }
        });

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestoreOwnerActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Checks if the logged-in user has the "owner" role.
     *
     * @param userId The Firebase UID of the logged-in user.
     */
    private void checkUserRole(String userId) {
        pd.show();
        dbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                if (snapshot.exists()) {
                    // User is an owner; navigate to OwnerMainActivity
                    Intent intent = new Intent(getApplicationContext(), OwnerMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is not an owner; sign them out
                    mAuth.signOut();
                    Toast.makeText(LoginOwnerActivity.this,
                            "Access Denied! You do not have Owner privileges.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Log.v("FirebaseError", "Error: " + error.getMessage());
                Toast.makeText(LoginOwnerActivity.this,
                        "An error occurred while verifying your role.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
