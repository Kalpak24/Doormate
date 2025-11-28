package com.example.omya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwnerRoomFragment extends Fragment {

    private ArrayList<RoomModel> recycleList;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private ImageView upload;
    private String owner_email;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_owner_room, container, false);

        // Initialize Firebase and other components
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerview_room);
        upload = view.findViewById(R.id.upload_room_btn);
        recycleList = new ArrayList<>();

        // Get owner_email from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getContext().MODE_PRIVATE);
        owner_email = sharedPreferences.getString("owner_email", null);

        // Check if owner_email is available
        if (owner_email != null) {
            String sanitizedEmail = owner_email.replace(".", "_");

            // Set up RecyclerView Adapter
            RoomOwnerAdapter recycleAdapter = new RoomOwnerAdapter(recycleList, getContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(recycleAdapter);

            // Fetch data from Firebase using the owner's email
            firebaseDatabase.getReference().child("doormint/room/" + sanitizedEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Clear the list before adding new data
                            recycleList.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                                if (roomModel != null) {
                                    recycleList.add(roomModel); // Add room data to list
                                }
                            }
                            // Notify the adapter that data has changed
                            recycleAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle potential errors
                            // Log the error or show a user-friendly message
                        }
                    });
        }

        // Set onClickListener for upload button (Ensure upload button is not null)
        if (upload != null) {
            upload.setOnClickListener(v -> {
                Intent i = new Intent(getActivity(), UploadRoomActivity.class);
                startActivity(i);
            });
        }

        return view;
    }
}
