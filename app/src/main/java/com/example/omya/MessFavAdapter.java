package com.example.omya;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessFavAdapter extends RecyclerView.Adapter<MessFavAdapter.ViewHolderMess>{
    private ArrayList<MessModel> messList;
    private Context messContext;

    public MessFavAdapter(ArrayList<MessModel> messList, Context messContext) {
        this.messList = messList;
        this.messContext = messContext;
    }

    public void setFilteredList(ArrayList<MessModel> filteredList) {
        if (filteredList != null) {
            this.messList = filteredList;
        } else {
            this.messList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MessFavAdapter.ViewHolderMess onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(messContext).inflate(R.layout.owner_mess_look, parent, false);
        return new MessFavAdapter.ViewHolderMess(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessFavAdapter.ViewHolderMess holder, int position) {
        MessModel model = messList.get(position);

        // Load image with Picasso
        Picasso.get()
                .load(model.getUpl_mess_image())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.showMessImage);

        // Set room details
        holder.showMessName.setText(model.getUpl_mess_name());
        holder.showMessPrice.setText(model.getUpl_mess_price());
        holder.showMessType.setText(model.getMess_type());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(messContext, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = user.getEmail();

//        String modEmail = email.replace(".", "_");
        // Set click listener for each item
        if (email != null) {
            String modEmail = email.replace(".", "_");

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("doormint/fav/" + modEmail+"/mess");

            // Set click listener for each item
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(messContext, FavMessRemoveActivity.class);
                intent.putExtra("room_image", model.getUpl_mess_image());
                intent.putExtra("room_price", model.getUpl_mess_price());
                intent.putExtra("room_location", model.getUpl_mess_location());
                intent.putExtra("room_type", model.getMess_type());
                intent.putExtra("room_facility", model.getUpl_facility());
                intent.putExtra("room_contact", model.getUpl_owner_contact());
                intent.putExtra("room_ow_name", model.getUpl_owner_name());
                intent.putExtra("room_open_time", model.getMess_open_time());
                intent.putExtra("modEmail", modEmail);
                intent.putExtra("room_close_time", model.getMess_close_time());
                intent.putExtra("room_name", model.getUpl_mess_name());
                intent.putExtra("checkbox1", model.isCheckBox1());
                intent.putExtra("checkbox2", model.isCheckBox2());
                intent.putExtra("checkbox3", model.isCheckBox3());

                // Fetch Unique Key from Firebase
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String uniqueKey = snapshot.getKey();
                            Log.d("Firebase", "Unique Key: " + uniqueKey);
                            intent.putExtra("unique_key", uniqueKey);  // Pass unique key to next activity
                            messContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching data", databaseError.toException());
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return (messList != null) ? messList.size() : 0;
    }

    public class ViewHolderMess extends RecyclerView.ViewHolder {

        TextView showMessName, showMessPrice, showMessType;
        ImageView showMessImage;

        public ViewHolderMess(@NonNull View itemView) {
            super(itemView);

            showMessName = itemView.findViewById(R.id.show_mess_name);
            showMessPrice = itemView.findViewById(R.id.show_mess_price);
            showMessImage = itemView.findViewById(R.id.show_mess_image);
            showMessType = itemView.findViewById(R.id.show_mess_type);
        }
    }
}
