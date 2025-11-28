package com.example.omya;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminRoomSingleItemActivity extends AppCompatActivity {

    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
    ImageView room_image;
    Button accept, decline;
    ImageView backButton;
    private FirebaseDatabase database;
    private ProgressDialog dialog;
    String room_key = "";
    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;
    String imageUrl = "";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_single_item);

        // Initialize SharedPreferences to get room_email
        SharedPreferences sharedPreferences = getSharedPreferences("room_email", MODE_PRIVATE);
        String room_email = sharedPreferences.getString("room_email1", "DefaultName");

        // Initialize UI components
        room_image = findViewById(R.id.room_image);
        room_price = findViewById(R.id.room_price);
        room_location = findViewById(R.id.room_location);
        room_type = findViewById(R.id.room_type);
        room_facility = findViewById(R.id.room_facility);
        room_contact = findViewById(R.id.room_contact);
        room_ow_name = findViewById(R.id.room_ow_name);
        room_open_time = findViewById(R.id.room_open_time);
        room_close_time = findViewById(R.id.room_close_time);
        room_name = findViewById(R.id.room_name);
        accept = findViewById(R.id.accept);
        decline = findViewById(R.id.decline);
        backButton = findViewById(R.id.backButton);

        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
        checkBox4 = findViewById(R.id.CheckBox4);
        checkBox5 = findViewById(R.id.CheckBox5);
        checkBox6 = findViewById(R.id.CheckBox6);

        // Load the room image using Picasso
        Picasso.get().load(getIntent().getStringExtra("room_image")).placeholder(R.drawable.ic_launcher_foreground).into(room_image);

        // Set other room details from Intent
        room_price.setText(getIntent().getStringExtra("room_price"));
        room_location.setText(getIntent().getStringExtra("room_location"));
        room_type.setText(getIntent().getStringExtra("room_type"));
        room_facility.setText(getIntent().getStringExtra("room_facility"));
        room_contact.setText(getIntent().getStringExtra("room_contact"));
        room_ow_name.setText(getIntent().getStringExtra("room_ow_name"));
        room_open_time.setText(getIntent().getStringExtra("room_open_time"));
        room_close_time.setText(getIntent().getStringExtra("room_close_time"));
        room_name.setText(getIntent().getStringExtra("room_name"));
        checkBox1.setChecked(getIntent().getBooleanExtra("checkbox1",false));
        checkBox2.setChecked(getIntent().getBooleanExtra("checkbox2",false));
        checkBox3.setChecked(getIntent().getBooleanExtra("checkbox3",false));
        checkBox4.setChecked(getIntent().getBooleanExtra("checkbox4",false));
        checkBox5.setChecked(getIntent().getBooleanExtra("checkbox5",false));
        checkBox6.setChecked(getIntent().getBooleanExtra("checkbox6",false));

        // Get the room key and image URL from the Intent
        room_key = getIntent().getStringExtra("room_key");
        imageUrl = getIntent().getStringExtra("room_image");

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setTitle("Uploading");
        dialog.setCanceledOnTouchOutside(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Handle the Decline button click
        decline.setOnClickListener(v -> {
            // Correct Firebase reference with '/' between room and email
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/room/" + room_email);

            // Remove the room from Firebase Realtime Database
            reference.child(room_key).removeValue().addOnSuccessListener(aVoid -> {
                Toast.makeText(AdminRoomSingleItemActivity.this, "Room declined and removed successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminRoomSingleItemActivity.this, AdminMainActivity.class));
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(AdminRoomSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });


        // Handle the Accept button click
        accept.setOnClickListener(v -> {
            dialog.show();

            // Create RoomModel from the received data
            RoomModel model = new RoomModel();
            model.setUpl_room_location(getIntent().getStringExtra("room_location"));
            model.setUpl_room_price(getIntent().getStringExtra("room_price"));
            model.setRoom_type(getIntent().getStringExtra("room_type"));
            model.setUpl_room_name(getIntent().getStringExtra("room_name"));
            model.setUpl_facility(getIntent().getStringExtra("room_facility"));
            model.setUpl_owner_name(getIntent().getStringExtra("room_ow_name"));
            model.setUpl_owner_contact(getIntent().getStringExtra("room_contact"));
            model.setRoom_open_time(getIntent().getStringExtra("room_open_time"));
            model.setRoom_close_time(getIntent().getStringExtra("room_close_time"));
            model.setUpl_room_image(getIntent().getStringExtra("room_image"));

            model.setCheckBox1(getIntent().getBooleanExtra("checkbox1",false));
            model.setCheckBox2(getIntent().getBooleanExtra("checkbox2",false));
            model.setCheckBox3(getIntent().getBooleanExtra("checkbox3",false));
            model.setCheckBox4(getIntent().getBooleanExtra("checkbox4",false));
            model.setCheckBox5(getIntent().getBooleanExtra("checkbox5",false));
            model.setCheckBox6(getIntent().getBooleanExtra("checkbox6",false));

            // Push the room details to Firebase under the "student" node
            database.getReference().child("doormint/student/room").push().setValue(model).addOnSuccessListener(unused -> {
                dialog.dismiss();
                Toast.makeText(AdminRoomSingleItemActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminRoomSingleItemActivity.this, AdminMainActivity.class));
                finish();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/room/" + room_email);

                // Remove the room from Firebase Realtime Database
                reference.child(room_key).removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminRoomSingleItemActivity.this, "Room declined and removed successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminRoomSingleItemActivity.this, AdminMainActivity.class));
                    finish();
                });
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(AdminRoomSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }
}




//package com.namah.doormatefinal;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.picasso.Picasso;
//
//public class AdminRoomSingleItemActivity extends AppCompatActivity {
//
//    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
//    ImageView room_image;
//    Button accept, decline;
//    private FirebaseDatabase database;
//    private ProgressDialog dialog;
//    String room_key = "";
//    String imageUrl = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_room_single_item);
//
//        // Initialize SharedPreferences to get room_email
//        SharedPreferences sharedPreferences = getSharedPreferences("room_email", MODE_PRIVATE);
//        String room_email = sharedPreferences.getString("room_email1", "DefaultName");
//
//        // Initialize UI components
//        room_image = findViewById(R.id.room_image);
//        room_price = findViewById(R.id.room_price);
//        room_location = findViewById(R.id.room_location);
//        room_type = findViewById(R.id.room_type);
//        room_facility = findViewById(R.id.room_facility);
//        room_contact = findViewById(R.id.room_contact);
//        room_ow_name = findViewById(R.id.room_ow_name);
//        room_open_time = findViewById(R.id.room_open_time);
//        room_close_time = findViewById(R.id.room_close_time);
//        room_name = findViewById(R.id.room_name);
//        accept = findViewById(R.id.accept);
//        decline = findViewById(R.id.decline);
//
//        // Load the room image using Picasso
//        Picasso.get().load(getIntent().getStringExtra("room_image")).placeholder(R.drawable.ic_launcher_foreground).into(room_image);
//
//        // Set other room details from Intent
//        room_price.setText(getIntent().getStringExtra("room_price"));
//        room_location.setText(getIntent().getStringExtra("room_location"));
//        room_type.setText(getIntent().getStringExtra("room_type"));
//        room_facility.setText(getIntent().getStringExtra("room_facility"));
//        room_contact.setText(getIntent().getStringExtra("room_contact"));
//        room_ow_name.setText(getIntent().getStringExtra("room_ow_name"));
//        room_open_time.setText(getIntent().getStringExtra("room_open_time"));
//        room_close_time.setText(getIntent().getStringExtra("room_close_time"));
//        room_name.setText(getIntent().getStringExtra("room_name"));
//
//        // Get the room key and image URL from the Intent
//        room_key = getIntent().getStringExtra("room_key");
//        imageUrl = getIntent().getStringExtra("room_image");
//
//        // Initialize Firebase Database
//        database = FirebaseDatabase.getInstance();
//
//        dialog = new ProgressDialog(this);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setMessage("Please wait...");
//        dialog.setCancelable(false);
//        dialog.setTitle("Uploading");
//        dialog.setCanceledOnTouchOutside(false);
//
//        // Handle the Decline button click
//        decline.setOnClickListener(v -> {
//            // Correct Firebase reference with '/' between room and email
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/room/" + room_email);
//
//            // Remove the room from Firebase Realtime Database
//            reference.child(room_key).removeValue().addOnSuccessListener(aVoid -> {
//                Toast.makeText(AdminRoomSingleItemActivity.this, "Room declined and removed successfully!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(AdminRoomSingleItemActivity.this, AdminMainActivity.class));
//                finish();
//            }).addOnFailureListener(e -> {
//                Toast.makeText(AdminRoomSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        });
//
//
//        // Handle the Accept button click
//        accept.setOnClickListener(v -> {
//            dialog.show();
//
//            // Create RoomModel from the received data
//            RoomModel model = new RoomModel();
//            model.setUpl_room_location(getIntent().getStringExtra("room_location"));
//            model.setUpl_room_price(getIntent().getStringExtra("room_price"));
//            model.setRoom_type(getIntent().getStringExtra("room_type"));
//            model.setUpl_room_name(getIntent().getStringExtra("room_name"));
//            model.setUpl_facility(getIntent().getStringExtra("room_facility"));
//            model.setUpl_owner_name(getIntent().getStringExtra("room_ow_name"));
//            model.setUpl_owner_contact(getIntent().getStringExtra("room_contact"));
//            model.setRoom_open_time(getIntent().getStringExtra("room_open_time"));
//            model.setRoom_close_time(getIntent().getStringExtra("room_close_time"));
//            model.setUpl_room_image(getIntent().getStringExtra("room_image"));
//
//            // Push the room details to Firebase under the "student" node
//            database.getReference().child("doormint/student/room").push().setValue(model).addOnSuccessListener(unused -> {
//                dialog.dismiss();
//                Toast.makeText(AdminRoomSingleItemActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(AdminRoomSingleItemActivity.this, AdminMainActivity.class));
//                finish();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/room/" + room_email);
//
//                // Remove the room from Firebase Realtime Database
//                reference.child(room_key).removeValue().addOnSuccessListener(aVoid -> {
//                    Toast.makeText(AdminRoomSingleItemActivity.this, "Room declined and removed successfully!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(AdminRoomSingleItemActivity.this, AdminMainActivity.class));
//                    finish();
//                });
//            }).addOnFailureListener(e -> {
//                dialog.dismiss();
//                Toast.makeText(AdminRoomSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        });
//    }
//}
