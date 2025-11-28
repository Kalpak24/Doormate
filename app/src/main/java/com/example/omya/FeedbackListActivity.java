package com.example.omya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("doormint/feedback");

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewFeedbacks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize feedback list
        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);

        // Load feedbacks
        loadFeedbacks();
    }

    private void loadFeedbacks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Feedback feedback = snapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FeedbackListActivity.this, "Failed to load feedbacks: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter for RecyclerView
    private static class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
        private List<Feedback> feedbackList;

        public FeedbackAdapter(List<Feedback> feedbackList) {
            this.feedbackList = feedbackList;
        }

        @NonNull
        @Override
        public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item, parent, false);
            return new FeedbackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
            Feedback feedback = feedbackList.get(position);
            holder.textViewName.setText("Name: " + feedback.getUserName());
            holder.textViewMobile.setText("Mobile: " + feedback.getMobileNumber());
            holder.textViewFeedback.setText("Feedback: " + feedback.getFeedbackText());
        }

        @Override
        public int getItemCount() {
            return feedbackList.size();
        }

        static class FeedbackViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewMobile, textViewFeedback;

            public FeedbackViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewMobile = itemView.findViewById(R.id.textViewMobile);
                textViewFeedback = itemView.findViewById(R.id.textViewFeedback);
            }
        }
    }
}