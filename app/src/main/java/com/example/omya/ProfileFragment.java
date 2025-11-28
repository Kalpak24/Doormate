package com.example.omya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    TextView name, email;
    CardView terms, logout;
    private DatabaseReference databaseReferenceStudent, databaseReferenceAdmin, databaseReferenceOwner;
    ImageView backButton;
    Button feedback;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        logout = view.findViewById(R.id.logout);
        terms = view.findViewById(R.id.terms);
        backButton = view.findViewById(R.id.backButton);
//        feedback = view.findViewById(R.id.feedback);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize references for all paths
        databaseReferenceStudent = FirebaseDatabase.getInstance().getReference("doormint/account/student");
        databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference("doormint/account/admin");
        databaseReferenceOwner = FirebaseDatabase.getInstance().getReference("doormint/account/owner");

        // Add listeners to check all three paths
        checkUserData(databaseReferenceStudent, currentUserId);
        checkUserData(databaseReferenceAdmin, currentUserId);
        checkUserData(databaseReferenceOwner, currentUserId);

        // Get and set email from FirebaseAuth
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail != null) {
            email.setText(userEmail);
        }

//        feedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        // Set up the logout functionality
        logout.setOnClickListener(v -> {
            // Sign out the user from Firebase
            FirebaseAuth.getInstance().signOut();

            // Redirect to the login screen
            Intent intent = new Intent(getActivity(), MainActivity.class); // Update with your login activity if needed
            startActivity(intent);
            getActivity().finish();  // Close the current activity
        });


        terms.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), TermsActivity.class);
            startActivity(i);
        });

        return view;
    }

    private void checkUserData(DatabaseReference databaseReference, String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("Name").getValue(String.class);
                    if (userName != null) {
                        name.setText(userName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Error fetching data", error.toException());
            }
        });
    }
}
