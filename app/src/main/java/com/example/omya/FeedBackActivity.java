package com.example.omya;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedBackActivity extends AppCompatActivity {
    private EditText editTextName, editTextMobile, editTextFeedback;
    private Button buttonSubmit, buttonViewFeedbacks;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("doormint/feedback");

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextFeedback = findViewById(R.id.editTextFeedback);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonViewFeedbacks = findViewById(R.id.buttonViewFeedbacks);

        // Submit button click listener
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        // View feedbacks button click listener
        buttonViewFeedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedBackActivity.this, FeedbackListActivity.class));
            }
        });
    }

    private void submitFeedback() {
        // Get input values directly from EditText fields
        String name = editTextName.getText().toString().trim();
        String mobile = editTextMobile.getText().toString().trim();
        String feedbackText = editTextFeedback.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            editTextMobile.setError("Mobile number is required");
            return;
        }

        if (TextUtils.isEmpty(feedbackText)) {
            editTextFeedback.setError("Feedback is required");
            return;
        }

        // Create feedback object with data from EditText fields
        Feedback feedback = new Feedback(name, mobile, feedbackText);

        // Generate unique ID for feedback
        String feedbackId = databaseReference.push().getKey();
        feedback.setId(feedbackId);

        // Save to Firebase
        databaseReference.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(FeedBackActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedBackActivity.this, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        editTextName.setText("");
        editTextMobile.setText("");
        editTextFeedback.setText("");
    }
}