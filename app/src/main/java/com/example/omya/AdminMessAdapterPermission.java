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

public class AdminMessAdapterPermission extends RecyclerView.Adapter<AdminMessAdapterPermission.ViewHolderMess> {

    private ArrayList<MessModel> mess_list;
    private Context mess_context;

    public AdminMessAdapterPermission(ArrayList<MessModel> mess_list, Context mess_context) {
        this.mess_list = mess_list;
        this.mess_context = mess_context;
    }

    @NonNull
    @Override
    public AdminMessAdapterPermission.ViewHolderMess onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mess_context).inflate(R.layout.admin_mess_recycler_look, parent, false);
        return new AdminMessAdapterPermission.ViewHolderMess(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminMessAdapterPermission.ViewHolderMess holder, int position) {
        MessModel model = mess_list.get(position);
        String nodeKey = model.getMess_key(); // Get the unique node key

        Picasso.get().load(model.getUpl_mess_image()).placeholder(R.drawable.ic_launcher_background).into(holder.show_mess_image);
        holder.show_mess_name.setText(model.getUpl_mess_name());
        holder.show_mess_price.setText(model.getUpl_mess_price());
        holder.show_mess_type.setText(model.getMess_type());


        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(mess_context, AdminMessSingleItemActivity.class);
            i.putExtra("mess_key", nodeKey); // Pass the node key
            i.putExtra("mess_image", model.getUpl_mess_image());
            i.putExtra("mess_price", model.getUpl_mess_price());
            i.putExtra("mess_location", model.getUpl_mess_location());
            i.putExtra("mess_type", model.getMess_type());
            i.putExtra("mess_facility", model.getUpl_facility());
            i.putExtra("mess_contact", model.getUpl_owner_contact());
            i.putExtra("mess_ow_name", model.getUpl_owner_name());
            i.putExtra("mess_open_time", model.getMess_open_time());
            i.putExtra("mess_close_time", model.getMess_close_time());
            i.putExtra("mess_name", model.getUpl_mess_name());
            i.putExtra("mess_key",model.getMess_key());

            i.putExtra("checkbox1", model.isCheckBox1());
            i.putExtra("checkbox2", model.isCheckBox2());
            i.putExtra("checkbox3", model.isCheckBox3());
//            i.putExtra("checkbox4", model.isCheckBox4());
//            i.putExtra("checkbox5", model.isCheckBox5());
//            i.putExtra("checkbox6", model.isCheckBox6());

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mess_context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return mess_list.size();
    }

    public static class ViewHolderMess extends RecyclerView.ViewHolder {
        TextView show_mess_name, show_mess_price, show_mess_type;
        ImageView show_mess_image;

        public ViewHolderMess(@NonNull View itemView) {
            super(itemView);
            show_mess_name = itemView.findViewById(R.id.show_mess_name);
            show_mess_price = itemView.findViewById(R.id.show_mess_price);
            show_mess_image = itemView.findViewById(R.id.show_mess_image);
            show_mess_type = itemView.findViewById(R.id.show_mess_type);
        }
    }
}