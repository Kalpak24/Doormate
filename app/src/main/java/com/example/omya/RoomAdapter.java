package com.example.omya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolderRoom> {
    private ArrayList<RoomModel> roomList;
    private Context roomContext;

    public RoomAdapter(ArrayList<RoomModel> roomList, Context roomContext) {
        this.roomList = roomList;
        this.roomContext = roomContext;
    }

    public void setFilteredList(ArrayList<RoomModel> filteredList) {
        if (filteredList != null) {
            this.roomList = filteredList;
        } else {
            this.roomList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(roomContext).inflate(R.layout.owner_room_look, parent, false);
        return new ViewHolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRoom holder, int position) {
        RoomModel model = roomList.get(position);

        // Load image with Picasso
        Picasso.get()
                .load(model.getUpl_room_image())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.showRoomImage);

        // Set room details
        holder.showRoomName.setText(model.getUpl_room_name());
        holder.showRoomPrice.setText(model.getUpl_room_price());
        holder.showRoomType.setText(model.getRoom_type());

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(roomContext, SingleRoomActivity.class);
            intent.putExtra("room_image", model.getUpl_room_image());
            intent.putExtra("room_price", model.getUpl_room_price());
            intent.putExtra("room_location", model.getUpl_room_location());
            intent.putExtra("room_type", model.getRoom_type());
            intent.putExtra("room_facility", model.getUpl_facility());
            intent.putExtra("room_contact", model.getUpl_owner_contact());
            intent.putExtra("room_ow_name", model.getUpl_owner_name());
            intent.putExtra("room_open_time", model.getRoom_open_time());
            intent.putExtra("room_close_time", model.getRoom_close_time());
            intent.putExtra("room_name", model.getUpl_room_name());

            intent.putExtra("checkbox1", model.isCheckBox1());
            intent.putExtra("checkbox2", model.isCheckBox2());
            intent.putExtra("checkbox3", model.isCheckBox3());
            intent.putExtra("checkbox4", model.isCheckBox4());
            intent.putExtra("checkbox5", model.isCheckBox5());
            intent.putExtra("checkbox6", model.isCheckBox6());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            roomContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (roomList != null) ? roomList.size() : 0;
    }

    public static class ViewHolderRoom extends RecyclerView.ViewHolder {
        TextView showRoomName, showRoomPrice, showRoomType;
        ImageView showRoomImage;

        public ViewHolderRoom(@NonNull View itemView) {
            super(itemView);
            showRoomName = itemView.findViewById(R.id.show_room_name);
            showRoomPrice = itemView.findViewById(R.id.show_room_price);
            showRoomImage = itemView.findViewById(R.id.show_room_image);
            showRoomType = itemView.findViewById(R.id.show_room_type);
        }
    }
}
