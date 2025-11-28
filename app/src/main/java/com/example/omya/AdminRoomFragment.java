package com.example.omya;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminRoomFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminRoomAdapter adminRoomAdapter;
    private List<String> nodeNames;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_room, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_room);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nodeNames = new ArrayList<>();

        // Pass the context (getContext()) to the adapter constructor
        adminRoomAdapter = new AdminRoomAdapter(nodeNames, getContext());
        recyclerView.setAdapter(adminRoomAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("doormint/room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous node names
                nodeNames.clear();

                // Iterate through the children and get the keys (node names)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nodeName = snapshot.getKey();
                    RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                    if (nodeName != null) {
                        roomModel.setKey(snapshot.getKey());
                        nodeNames.add(nodeName);
                    }
                }

                // Notify the adapter that the data has changed
                adminRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
