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

public class MessOwnerAdapter extends RecyclerView.Adapter<MessOwnerAdapter.ViewHolderMess> {
    private ArrayList<MessModel> mess_list;
    private Context mess_context;

    public MessOwnerAdapter(ArrayList<MessModel> mess_list, Context mess_context){
        this.mess_list = mess_list;
        this.mess_context = mess_context;
    }
    public void setFilteredList(ArrayList<MessModel> filteredList) {
        if (filteredList != null) {
            this.mess_list = filteredList;
        } else {
            this.mess_list = new ArrayList<>();
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MessOwnerAdapter.ViewHolderMess onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mess_context).inflate(R.layout.owner_mess_look, parent, false);
        return new MessOwnerAdapter.ViewHolderMess(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessOwnerAdapter.ViewHolderMess holder, int position) {
        MessModel model = mess_list.get(position);
        Picasso.get().load(model.getUpl_mess_image()).placeholder(R.drawable.ic_launcher_background).into(holder.show_mess_image);
        holder.show_mess_name.setText(model.getUpl_mess_name());
        holder.show_mess_price.setText(model.getUpl_mess_price());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(mess_context, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();
        if (email != null) {
            String modEmail = email.replace(".", "_");

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("doormint/mess/" + modEmail);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mess_context, SingleMessOwnerActivity.class);
                intent.putExtra("room_image", model.getUpl_mess_image());
                intent.putExtra("room_price", model.getUpl_mess_price());
                intent.putExtra("room_location", model.getUpl_mess_location());
                intent.putExtra("room_type", model.getMess_type());
                intent.putExtra("room_facility", model.getUpl_facility());
                intent.putExtra("room_contact", model.getUpl_owner_contact());
                intent.putExtra("room_ow_name", model.getUpl_owner_name());
                intent.putExtra("room_open_time", model.getMess_open_time());
                intent.putExtra("room_close_time", model.getMess_close_time());
                intent.putExtra("room_name", model.getUpl_mess_name());
                intent.putExtra("modEmail", modEmail);
                intent.putExtra("checkbox1", model.isCheckBox1());
                intent.putExtra("checkbox2", model.isCheckBox2());
                intent.putExtra("checkbox3", model.isCheckBox3());
//            intent.putExtra("checkbox4", model.isCheckBox4());
//            intent.putExtra("checkbox5", model.isCheckBox5());
//            intent.putExtra("checkbox6", model.isCheckBox6());

                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String uniqueKey = snapshot.getKey();
                            Log.d("Firebase", "Unique Key: " + uniqueKey);
                            intent.putExtra("unique_key", uniqueKey);  // Pass unique key to next activity
                            mess_context.startActivity(intent);
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
        return mess_list.size();
    }

    public class ViewHolderMess extends RecyclerView.ViewHolder {
        TextView show_mess_name, show_mess_price;
        ImageView show_mess_image;

        public ViewHolderMess(@NonNull View itemView) {
            super(itemView);

            show_mess_name = itemView.findViewById(R.id.show_mess_name);
            show_mess_price = itemView.findViewById(R.id.show_mess_price);
            show_mess_image = itemView.findViewById(R.id.show_mess_image);
        }
    }
}
