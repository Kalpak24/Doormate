package com.example.omya;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class AdminMessFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminMessAdapter adminMessAdapter;
    private List<String> nodeNames;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_mess, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_mess);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nodeNames = new ArrayList<>();

        // Pass the context (getContext()) to the adapter constructor
        adminMessAdapter = new AdminMessAdapter(nodeNames, getContext());
        recyclerView.setAdapter(adminMessAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("doormint/mess");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear previous node names
                nodeNames.clear();

                // Iterate through the children and get the keys (node names)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nodeName = snapshot.getKey();
                    MessModel messModel = dataSnapshot.getValue(MessModel.class);
                    if (nodeName != null) {
                        messModel.setKey(snapshot.getKey());
                        nodeNames.add(nodeName);
                    }
                }

                // Notify the adapter that the data has changed
                adminMessAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
