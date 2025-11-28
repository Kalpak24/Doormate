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

public class AdminRoomAdapterPermission extends RecyclerView.Adapter<AdminRoomAdapterPermission.ViewHolderRoom> {

    private ArrayList<RoomModel> room_list;
    private Context room_context;

    public AdminRoomAdapterPermission(ArrayList<RoomModel> room_list, Context room_context) {
        this.room_list = room_list;
        this.room_context = room_context;
    }

    @NonNull
    @Override
    public ViewHolderRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(room_context).inflate(R.layout.admin_room_recycler_look, parent, false);
        return new ViewHolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRoom holder, int position) {
        RoomModel model = room_list.get(position);
        String nodeKey = model.getRoomKey(); // Get the unique node key

        Picasso.get().load(model.getUpl_room_image()).placeholder(R.drawable.ic_launcher_background).into(holder.show_room_image);
        holder.show_room_name.setText(model.getUpl_room_name());
        holder.show_room_price.setText(model.getUpl_room_price());
        holder.show_room_type.setText(model.getRoom_type());



        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(room_context, AdminRoomSingleItemActivity.class);
            i.putExtra("room_key", nodeKey); // Pass the node key
            i.putExtra("room_image", model.getUpl_room_image());
            i.putExtra("room_price", model.getUpl_room_price());
            i.putExtra("room_location", model.getUpl_room_location());
            i.putExtra("room_type", model.getRoom_type());
            i.putExtra("room_facility", model.getUpl_facility());
            i.putExtra("room_contact", model.getUpl_owner_contact());
            i.putExtra("room_ow_name", model.getUpl_owner_name());
            i.putExtra("room_open_time", model.getRoom_open_time());
            i.putExtra("room_close_time", model.getRoom_close_time());
            i.putExtra("room_name", model.getUpl_room_name());
            i.putExtra("room_key",model.getRoomKey());

            i.putExtra("checkbox1", model.isCheckBox1());
            i.putExtra("checkbox2", model.isCheckBox2());
            i.putExtra("checkbox3", model.isCheckBox3());
            i.putExtra("checkbox4", model.isCheckBox4());
            i.putExtra("checkbox5", model.isCheckBox5());
            i.putExtra("checkbox6", model.isCheckBox6());

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            room_context.startActivity(i);
        });
    }


    @Override
    public int getItemCount() {
        return room_list.size();
    }

    public static class ViewHolderRoom extends RecyclerView.ViewHolder {
        TextView show_room_name, show_room_price, show_room_type;
        ImageView show_room_image;

        public ViewHolderRoom(@NonNull View itemView) {
            super(itemView);
            show_room_name = itemView.findViewById(R.id.show_room_name);
            show_room_price = itemView.findViewById(R.id.show_room_price);
            show_room_image = itemView.findViewById(R.id.show_room_image);
            show_room_type = itemView.findViewById(R.id.show_room_type);
        }
    }
}