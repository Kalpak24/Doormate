package com.example.omya;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminRoomSingleActivity extends AppCompatActivity {

    private ArrayList<RoomModel> recycleList;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private String sanitizedEmail;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_single);

        // Initialize Firebase and other components
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyclerview_room);
        recycleList = new ArrayList<>();

        // Retrieve sanitized email from Intent
        sanitizedEmail = getIntent().getStringExtra("admin_owner_email");

        // Save sanitized email to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("room_email", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("room_email1", sanitizedEmail);
        editor.apply();

        if (sanitizedEmail != null && !sanitizedEmail.isEmpty()) {
            // Set up RecyclerView Adapter
            AdminRoomAdapterPermission recycleAdapter = new AdminRoomAdapterPermission(recycleList, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(recycleAdapter);

            // Show a loading indicator
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading rooms...");
            progressDialog.show();

            // Fetch data from Firebase using the sanitized email
            firebaseDatabase.getReference().child("doormint/room/" + sanitizedEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Clear the list before adding new data
                            recycleList.clear();

                            // Iterate through the children and get the data
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                                if (roomModel != null) {
                                    roomModel.setRoomKey(dataSnapshot.getKey());
                                    recycleList.add(roomModel); // Add room data to list
                                }
                            }
                            // Notify the adapter that data has changed
                            recycleAdapter.notifyDataSetChanged();
                            progressDialog.dismiss(); // Dismiss loading indicator
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            progressDialog.dismiss(); // Dismiss loading indicator
                            Toast.makeText(AdminRoomSingleActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle case when sanitizedEmail is null or empty
            Toast.makeText(this, "Invalid or empty email", Toast.LENGTH_SHORT).show();
        }
    }
}
