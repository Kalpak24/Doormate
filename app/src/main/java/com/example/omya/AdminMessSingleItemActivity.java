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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminMessSingleItemActivity extends AppCompatActivity {

    TextView mess_name, mess_price, mess_type, mess_location, mess_facility, mess_contact, mess_ow_name, mess_open_time, mess_close_time;
    ImageView mess_image;
    Button accept, decline;
    ImageView backButton;
//    Spinner mess_type;
    private FirebaseDatabase database;
    private ProgressDialog dialog;
    String mess_key = "";
    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;
    String imageUrl = "";

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_mess_single_item);

        SharedPreferences sharedPreferences = getSharedPreferences("mess_email", MODE_PRIVATE);
        String mess_email = sharedPreferences.getString("mess_email1", "DefaultName");

        // Initialize UI components
        mess_image = findViewById(R.id.mess_image);
        mess_price = findViewById(R.id.mess_price);
        mess_location = findViewById(R.id.mess_location);
        mess_type = findViewById(R.id.mess_type);
        mess_facility = findViewById(R.id.mess_facility);
        mess_contact = findViewById(R.id.mess_contact);
        mess_ow_name = findViewById(R.id.mess_ow_name);
        mess_open_time = findViewById(R.id.mess_open_time);
        mess_close_time = findViewById(R.id.mess_close_time);
        mess_name = findViewById(R.id.mess_name);
        accept = findViewById(R.id.accept);
        decline = findViewById(R.id.decline);
        backButton = findViewById(R.id.backButton);


        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
//        checkBox4 = findViewById(R.id.CheckBox4);
//        checkBox5 = findViewById(R.id.CheckBox5);
//        checkBox6 = findViewById(R.id.CheckBox6);

        // Load the room image using Picasso
        Picasso.get().load(getIntent().getStringExtra("mess_image")).placeholder(R.drawable.ic_launcher_foreground).into(mess_image);

        // Set other room details from Intent
        mess_price.setText(getIntent().getStringExtra("mess_price"));
        mess_location.setText(getIntent().getStringExtra("mess_location"));
        mess_type.setText(getIntent().getStringExtra("mess_type"));
        mess_facility.setText(getIntent().getStringExtra("mess_facility"));
        mess_contact.setText(getIntent().getStringExtra("mess_contact"));
        mess_ow_name.setText(getIntent().getStringExtra("mess_ow_name"));
        mess_open_time.setText(getIntent().getStringExtra("mess_open_time"));
        mess_close_time.setText(getIntent().getStringExtra("mess_close_time"));
        mess_name.setText(getIntent().getStringExtra("mess_name"));

        checkBox1.setChecked(getIntent().getBooleanExtra("checkbox1",false));
        checkBox2.setChecked(getIntent().getBooleanExtra("checkbox2",false));
        checkBox3.setChecked(getIntent().getBooleanExtra("checkbox3",false));
//        checkBox4.setChecked(getIntent().getBooleanExtra("checkbox4",false));
//        checkBox5.setChecked(getIntent().getBooleanExtra("checkbox5",false));
//        checkBox6.setChecked(getIntent().getBooleanExtra("checkbox6",false));

        // Get the room key and image URL from the Intent
        mess_key = getIntent().getStringExtra("mess_key");
        imageUrl = getIntent().getStringExtra("mess_image");

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
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/mess/" + mess_email);

            // Remove the room from Firebase Realtime Database
            reference.child(mess_key).removeValue().addOnSuccessListener(aVoid -> {
                Toast.makeText(AdminMessSingleItemActivity.this, "Mess declined and removed successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminMessSingleItemActivity.this, AdminMainActivity.class));
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(AdminMessSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });


        // Handle the Accept button click
        accept.setOnClickListener(v -> {
            dialog.show();

            // Create RoomModel from the received data
            MessModel model = new MessModel();
            model.setUpl_mess_location(getIntent().getStringExtra("mess_location"));
            model.setUpl_mess_price(getIntent().getStringExtra("mess_price"));
            model.setMess_type(getIntent().getStringExtra("mess_type"));
            model.setUpl_mess_name(getIntent().getStringExtra("mess_name"));
            model.setUpl_facility(getIntent().getStringExtra("mess_facility"));
            model.setUpl_owner_name(getIntent().getStringExtra("mess_ow_name"));
            model.setUpl_owner_contact(getIntent().getStringExtra("mess_contact"));
            model.setMess_open_time(getIntent().getStringExtra("mess_open_time"));
            model.setMess_close_time(getIntent().getStringExtra("mess_close_time"));
            model.setUpl_mess_image(getIntent().getStringExtra("mess_image"));

            model.setCheckBox1(getIntent().getBooleanExtra("checkbox1",false));
            model.setCheckBox2(getIntent().getBooleanExtra("checkbox2",false));
            model.setCheckBox3(getIntent().getBooleanExtra("checkbox3",false));
//            model.setCheckBox4(getIntent().getBooleanExtra("checkbox4",false));
//            model.setCheckBox5(getIntent().getBooleanExtra("checkbox5",false));
//            model.setCheckBox6(getIntent().getBooleanExtra("checkbox6",false));

            // Push the room details to Firebase under the "student" node
            database.getReference().child("doormint/student/mess").push().setValue(model).addOnSuccessListener(unused -> {
                dialog.dismiss();
                Toast.makeText(AdminMessSingleItemActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminMessSingleItemActivity.this, AdminMainActivity.class));
                finish();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/mess/" + mess_email);

                // Remove the room from Firebase Realtime Database
                reference.child(mess_key).removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminMessSingleItemActivity.this, "Mess declined and removed successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminMessSingleItemActivity.this, AdminMainActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(AdminMessSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(AdminMessSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.squareup.picasso.Picasso;
//
//public class AdminMessSingleItemActivity extends AppCompatActivity {
//
//    TextView mess_name, mess_price, mess_location, mess_type, mess_facility, mess_contact, mess_ow_name, mess_open_time, mess_close_time;
//    ImageView mess_image;
//    Button accept, decline;
//    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;
//    private FirebaseDatabase database;
//    private ProgressDialog dialog;
//    String mess_key = "";
//    String imageUrl = "";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_admin_mess_single_item);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("mess_email", MODE_PRIVATE);
//        String mess_email = sharedPreferences.getString("mess_email1", "DefaultName");
//
//        // Initialize UI components
//        checkBox1 = findViewById(R.id.CheckBox1);
//        checkBox2 = findViewById(R.id.CheckBox2);
//        checkBox3 = findViewById(R.id.CheckBox3);
//        checkBox4 = findViewById(R.id.CheckBox4);
//        checkBox5 = findViewById(R.id.CheckBox5);
//        checkBox6 = findViewById(R.id.CheckBox6);
//
//
//        mess_image = findViewById(R.id.mess_image);
//        mess_price = findViewById(R.id.mess_price);
//        mess_location = findViewById(R.id.mess_location);
////        mess_type = findViewById(R.id.mess_type);
//        mess_facility = findViewById(R.id.mess_facility);
//        mess_contact = findViewById(R.id.mess_contact);
//        mess_ow_name = findViewById(R.id.mess_ow_name);
//        mess_open_time = findViewById(R.id.mess_open_time);
//        mess_close_time = findViewById(R.id.mess_close_time);
//        mess_name = findViewById(R.id.mess_name);
//        accept = findViewById(R.id.accept);
//        decline = findViewById(R.id.decline);
//
//        // Load the room image using Picasso
//        Picasso.get().load(getIntent().getStringExtra("mess_image")).placeholder(R.drawable.ic_launcher_foreground).into(mess_image);
//
//        // Set other room details from Intent
//        mess_price.setText(getIntent().getStringExtra("mess_price"));
//        mess_location.setText(getIntent().getStringExtra("mess_location"));
//        mess_type.setText(getIntent().getStringExtra("mess_type"));
//        mess_facility.setText(getIntent().getStringExtra("mess_facility"));
//        mess_contact.setText(getIntent().getStringExtra("mess_contact"));
//        mess_ow_name.setText(getIntent().getStringExtra("mess_ow_name"));
//        mess_open_time.setText(getIntent().getStringExtra("mess_open_time"));
//        mess_close_time.setText(getIntent().getStringExtra("mess_close_time"));
//        mess_name.setText(getIntent().getStringExtra("mess_name"));
//
//        // Get the room key and image URL from the Intent
//        mess_key = getIntent().getStringExtra("mess_key");
//        imageUrl = getIntent().getStringExtra("mess_image");
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
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CheckboxStates");
//
//        checkBox1.setChecked(getIntent().getBooleanExtra("checkbox1",false));
//        checkBox2.setChecked(getIntent().getBooleanExtra("checkbox2",false));
//        checkBox3.setChecked(getIntent().getBooleanExtra("checkbox3",false));
//        checkBox4.setChecked(getIntent().getBooleanExtra("checkbox4",false));
//        checkBox5.setChecked(getIntent().getBooleanExtra("checkbox5",false));
//        checkBox6.setChecked(getIntent().getBooleanExtra("checkbox6",false));
//
//        // Handle the Decline button click
//        decline.setOnClickListener(v -> {
//            // Correct Firebase reference with '/' between room and email
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/mess/" + mess_email);
//
//            // Remove the room from Firebase Realtime Database
//            reference.child(mess_key).removeValue().addOnSuccessListener(aVoid -> {
//                Toast.makeText(AdminMessSingleItemActivity.this, "Mess declined and removed successfully!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(AdminMessSingleItemActivity.this, AdminMainActivity.class));
//                finish();
//            }).addOnFailureListener(e -> {
//                Toast.makeText(AdminMessSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        });
//
//
//        // Handle the Accept button click
//        accept.setOnClickListener(v -> {
//            dialog.show();
//
//            // Create RoomModel from the received data
//            MessModel model = new MessModel();
//            model.setUpl_mess_location(getIntent().getStringExtra("mess_location"));
//            model.setUpl_mess_price(getIntent().getStringExtra("mess_price"));
//            model.setMess_type(getIntent().getStringExtra("mess_type"));
//            model.setUpl_mess_name(getIntent().getStringExtra("mess_name"));
//            model.setUpl_facility(getIntent().getStringExtra("mess_facility"));
//            model.setUpl_owner_name(getIntent().getStringExtra("mess_ow_name"));
//            model.setUpl_owner_contact(getIntent().getStringExtra("mess_contact"));
//            model.setMess_open_time(getIntent().getStringExtra("mess_open_time"));
//            model.setMess_close_time(getIntent().getStringExtra("mess_close_time"));
//            model.setUpl_mess_image(getIntent().getStringExtra("mess_image"));
//
//            // Push the room details to Firebase under the "student" node
//            database.getReference().child("doormint/student/mess").push().setValue(model).addOnSuccessListener(unused -> {
//                dialog.dismiss();
//                Toast.makeText(AdminMessSingleItemActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(AdminMessSingleItemActivity.this, AdminMainActivity.class));
//                finish();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("doormint/mess/" + mess_email);
//
//                // Remove the room from Firebase Realtime Database
//                reference.child(mess_key).removeValue().addOnSuccessListener(aVoid -> {
//                    Toast.makeText(AdminMessSingleItemActivity.this, "Mess declined and removed successfully!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(AdminMessSingleItemActivity.this, AdminMainActivity.class));
//                    finish();
//                }).addOnFailureListener(e -> {
//                    Toast.makeText(AdminMessSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//
//            }).addOnFailureListener(e -> {
//                dialog.dismiss();
//                Toast.makeText(AdminMessSingleItemActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        });
//    }
//}
