package com.example.omya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentMessFragment extends Fragment {

    private ArrayList<MessModel> recycleList;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private SearchView searchView;
    private MessAdapter recycleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_mess, container, false);

        // Initialize Firebase and UI components
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerview_mess);
        searchView = view.findViewById(R.id.search_mess);
        recycleList = new ArrayList<>();
        recycleAdapter = new MessAdapter(recycleList, getContext());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recycleAdapter);

        // Fetch mess data
        fetchMessData();

        // Set up SearchView listener
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        return view;
    }

    private void fetchMessData() {
        if (firebaseDatabase == null) {
            Toast.makeText(getContext(), "Database not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseDatabase.getReference().child("doormint/student/mess")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recycleList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessModel messModel = dataSnapshot.getValue(MessModel.class);
                            if (messModel != null) {
                                recycleList.add(messModel);
                            }
                        }

                        if (recycleList.isEmpty()) {
                            Toast.makeText(getContext(), "No mess data found.", Toast.LENGTH_SHORT).show();
                        }

                        recycleAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterList(String text) {
        ArrayList<MessModel> filteredList = new ArrayList<>();
        for (MessModel messModel : recycleList) {
            if (messModel.getUpl_mess_name().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getUpl_owner_name().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getUpl_mess_location().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getUpl_mess_price().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getMess_type().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getUpl_facility().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getMess_open_time().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getMess_close_time().toLowerCase().contains(text.toLowerCase()) ||
                    messModel.getUpl_owner_contact().toLowerCase().contains(text.toLowerCase()) ) {
                filteredList.add(messModel);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No data found!", Toast.LENGTH_SHORT).show();
        }

        recycleAdapter.setFilteredList(filteredList);
    }
}
