package com.example.omya;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class LoginAdminActivity extends AppCompatActivity {

    private Button signin;
    private TextView resetpass, button_signup;
    private EditText inputemail, inputpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private DatabaseReference db_ref;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        db_ref = FirebaseDatabase.getInstance().getReference("doormint/account/admin");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        if (mAuth.getCurrentUser() != null) {
            checkUserRole(mAuth.getCurrentUser().getUid());
        }

        inputemail = findViewById(R.id.input_username);
        inputpassword = findViewById(R.id.input_password);
        signin = findViewById(R.id.button_login);
        button_signup = findViewById(R.id.button_signup);
        resetpass = findViewById(R.id.button_forgot_password);

        button_signup.setOnClickListener(v -> {
            Intent i = new Intent(LoginAdminActivity.this, CreateAdminActivity.class);
            startActivity(i);
        });

        signin.setOnClickListener(v -> {
            final String email = inputemail.getText().toString().trim();
            final String password = inputpassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                pd.show();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginAdminActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                if (!task.isSuccessful()) {
                                    showErrorDialog("Authentication Failed", task.getException().getMessage());
                                } else {
                                    checkUserRole(mAuth.getCurrentUser().getUid());
                                }
                            }
                        });
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("admin_email", email);
                editor.apply();
            } else {
                showErrorDialog("Input Error", "Please fill all the fields.");
            }
        });

        resetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RestoreAdminActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserRole(String userId) {
        pd.show();
        db_ref.child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                if (snapshot.exists()) {
                    String role = snapshot.getValue(String.class);
                    if ("admin".equals(role)) {
                        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showAccessDeniedDialog();
                        FirebaseAuth.getInstance().signOut();
                    }
                } else {
                    showErrorDialog("Error", "Role not defined for user");
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                showErrorDialog("Database Error", error.getMessage());
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showAccessDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Access Denied")
                .setMessage("You are not authorized to log in as an admin.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
