package com.example.omya;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SingleRoomAdminActivity extends AppCompatActivity {

    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
    ImageView room_image, add;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    MaterialButton dialButton;
    ImageView backButton;
    private FirebaseDatabase database;
    private ProgressDialog dialog;

    // Request permission to make a call
    private final ActivityResultLauncher<String> requestCallPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    makeCall();
                } else {
                    Toast.makeText(SingleRoomAdminActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_room_admin);

        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.setCancelable(false);

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
        add = findViewById(R.id.add);
        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
        checkBox4 = findViewById(R.id.CheckBox4);
        checkBox5 = findViewById(R.id.CheckBox5);
        checkBox6 = findViewById(R.id.CheckBox6);
        dialButton = findViewById(R.id.dialButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        dialButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(SingleRoomAdminActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestCallPermission.launch(Manifest.permission.CALL_PHONE);
            } else {
                makeCall();
            }
        });

        Intent intent = getIntent();

        String imageUrl = intent.getStringExtra("room_image");
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(room_image);
        }

        room_price.setText(intent.getStringExtra("room_price"));
        room_location.setText(intent.getStringExtra("room_location"));
        room_type.setText(intent.getStringExtra("room_type"));
        room_facility.setText(intent.getStringExtra("room_facility"));
        room_contact.setText(intent.getStringExtra("room_contact"));
        room_ow_name.setText(intent.getStringExtra("room_ow_name"));
        room_open_time.setText(intent.getStringExtra("room_open_time"));
        room_close_time.setText(intent.getStringExtra("room_close_time"));
        room_name.setText(intent.getStringExtra("room_name"));
        checkBox1.setChecked(intent.getBooleanExtra("checkbox1", false));
        checkBox2.setChecked(intent.getBooleanExtra("checkbox2", false));
        checkBox3.setChecked(intent.getBooleanExtra("checkbox3", false));
        checkBox4.setChecked(intent.getBooleanExtra("checkbox4", false));
        checkBox5.setChecked(intent.getBooleanExtra("checkbox5", false));
        checkBox6.setChecked(intent.getBooleanExtra("checkbox6", false));

        add.setOnClickListener(v -> {
            String key = intent.getStringExtra("unique_key");
            uploadRoomData(key);
        });
    }

    private void makeCall() {
        String phoneNumber = room_contact.getText().toString();
        if (!phoneNumber.isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "Phone number is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadRoomData(String key) {
        if (key != null && !key.isEmpty()) {
            DatabaseReference bookRef = database.getReference("doormint/student/room/" + key);
            DatabaseReference studentRoomRef = database.getReference("doormint/book/" + key);

            dialog.setMessage("Removing and archiving room...");
            dialog.show();

            RoomModel room = new RoomModel();
            room.setUpl_room_name(room_name.getText().toString());
            room.setUpl_room_price(room_price.getText().toString());
            room.setUpl_room_location(room_location.getText().toString());
            room.setRoom_type(room_type.getText().toString());
            room.setUpl_facility(room_facility.getText().toString());
            room.setUpl_owner_contact(room_contact.getText().toString());
            room.setUpl_owner_name(room_ow_name.getText().toString());
            room.setRoom_open_time(room_open_time.getText().toString());
            room.setRoom_close_time(room_close_time.getText().toString());
            room.setUpl_room_image(getIntent().getStringExtra("room_image"));
            room.setCheckBox1(checkBox1.isChecked());
            room.setCheckBox2(checkBox2.isChecked());
            room.setCheckBox3(checkBox3.isChecked());
            room.setCheckBox4(checkBox4.isChecked());
            room.setCheckBox5(checkBox5.isChecked());
            room.setCheckBox6(checkBox6.isChecked());


            studentRoomRef.setValue(room).addOnCompleteListener(uploadTask -> {
                if (uploadTask.isSuccessful()) {
                    bookRef.removeValue().addOnCompleteListener(removeTask -> {
                        dialog.dismiss();
                        if (removeTask.isSuccessful()) {
                            Toast.makeText(SingleRoomAdminActivity.this, "Room moved successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SingleRoomAdminActivity.this, "Failed to remove room!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    dialog.dismiss();
                    Toast.makeText(SingleRoomAdminActivity.this, "Failed to archive room!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Invalid key, deletion failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
