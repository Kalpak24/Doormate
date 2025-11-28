package com.example.omya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavMessFragment extends Fragment {

    private ArrayList<MessModel> recycleList;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private MessFavAdapter recycleAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fav_mess, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerview_room);

        recycleList = new ArrayList<>();
        recycleAdapter = new MessFavAdapter(recycleList, getContext());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recycleAdapter);

        fetchMessData();
        return view;
    }


    private void fetchMessData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
//            Toast.makeText(roomContext, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();

        String modEmail = email.replace(".", "_");

        firebaseDatabase.getReference().child("doormint/fav/" + modEmail + "/mess").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list before adding new data
                recycleList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessModel messModel = dataSnapshot.getValue(MessModel.class);
                    if (messModel != null) {
                        recycleList.add(messModel); // Add room data to list
                    }
                }

                // Notify the adapter that data has changed
                recycleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}