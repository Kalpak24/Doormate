package com.example.omya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminRoomAdapter extends RecyclerView.Adapter<AdminRoomAdapter.ViewHolderRoom> {

    private List<String> nodeNames;
    private Context room_context;

    // Constructor
    public AdminRoomAdapter(List<String> nodeNames, Context room_context) {
        this.nodeNames = nodeNames;
        this.room_context = room_context;
    }


    @NonNull
    @Override
    public ViewHolderRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom item layout (admin_room_look.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_room_look, parent, false);
        return new ViewHolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRoom holder, int position) {
        // Set the node name for each item in RecyclerView
        holder.nodeNameTextView.setText(nodeNames.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start AdminRoomSingleActivity
                Intent i = new Intent(room_context, AdminRoomSingleActivity.class);

                // Pass the node name (owner_email) as an extra
                i.putExtra("admin_owner_email", nodeNames.get(position));

                // Set flags if needed
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Start the activity
                room_context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (nodeNames != null ? nodeNames.size() : 0);  // Handle null case
    }

    public static class ViewHolderRoom extends RecyclerView.ViewHolder {

        TextView nodeNameTextView;

        public ViewHolderRoom(@NonNull View itemView) {
            super(itemView);
            // Bind the TextView from the custom item layout
            nodeNameTextView = itemView.findViewById(R.id.owner_email);  // Correct the ID if needed
        }
    }
}
