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

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class StudentRoomFragment extends Fragment {

    private ArrayList<RoomModel> recycleList;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private SearchView searchView;
    private RoomAdapter recycleAdapter;
    private ImageCarousel imageCarousel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_room, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.recyclerview_room);
        searchView = view.findViewById(R.id.search_room);

        recycleList = new ArrayList<>();
        recycleAdapter = new RoomAdapter(recycleList, getContext());

        imageCarousel = view.findViewById(R.id.imageCarousel);

        List<CarouselItem> carouselItems = new ArrayList<>();
        carouselItems.add(new CarouselItem(R.drawable.ads4));
        carouselItems.add(new CarouselItem(R.drawable.ads2));
        carouselItems.add(new CarouselItem(R.drawable.ads3));
        carouselItems.add(new CarouselItem(R.drawable.ads));

        imageCarousel.addData(carouselItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recycleAdapter);

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

        fetchRoomData();

        return view;
    }

    private void fetchRoomData() {
        firebaseDatabase.getReference().child("doormint/student/room")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recycleList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                            if (roomModel != null) {
                                recycleList.add(roomModel);
                            }
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
        ArrayList<RoomModel> filteredList = new ArrayList<>();
        for (RoomModel roomModel : recycleList) {
            if ((safeString(roomModel.getUpl_room_name()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getUpl_room_price()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getUpl_owner_contact()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getRoom_close_time()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getRoom_open_time()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getUpl_facility()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getUpl_owner_name()).toLowerCase().contains(text.toLowerCase())) ||
                    (safeString(roomModel.getUpl_room_location()).toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(roomModel);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No data found!", Toast.LENGTH_SHORT).show();
        }

        recycleAdapter.setFilteredList(filteredList);
    }

    private String safeString(String input) {
        return input == null ? "" : input;
    }
}
