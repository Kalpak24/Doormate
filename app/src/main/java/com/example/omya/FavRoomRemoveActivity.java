package com.example.omya;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

public class FavRoomRemoveActivity extends AppCompatActivity {

    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
    ImageView room_image, remove;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    MaterialButton dialButton;
    ImageView backButton;

    private FirebaseDatabase database;
    private ProgressDialog dialog;

    // Variables
    private Uri room_imageUri;

    // Request permission to make a call
    private final ActivityResultLauncher<String> requestCallPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    makeCall();
                } else {
                    Toast.makeText(FavRoomRemoveActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_room_remove);

        database = FirebaseDatabase.getInstance();

        // Initialize ProgressDialog
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
        remove = findViewById(R.id.remove);

        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
        checkBox4 = findViewById(R.id.CheckBox4);
        checkBox5 = findViewById(R.id.CheckBox5);
        checkBox6 = findViewById(R.id.CheckBox6);
        dialButton = findViewById(R.id.dialButton);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());


        // Set up the dial button to make the call
        dialButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(FavRoomRemoveActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestCallPermission.launch(Manifest.permission.CALL_PHONE);
            } else {
                makeCall();
            }
        });

        // Get data from Intent and set to UI
        Intent intent = getIntent();

        String imageUrl = intent.getStringExtra("room_image");
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(room_image);
        }


        String key = intent.getStringExtra("unique_key");
        String modEmail = getIntent().getStringExtra("modEmail");
        room_price.setText(intent.getStringExtra("room_price"));
        room_location.setText(intent.getStringExtra("room_location"));
        room_type.setText(intent.getStringExtra("room_type"));
        room_facility.setText(intent.getStringExtra("room_facility"));
        room_contact.setText(intent.getStringExtra("room_contact"));
        room_ow_name.setText(intent.getStringExtra("room_ow_name"));
        room_open_time.setText(intent.getStringExtra("room_open_time"));
        room_close_time.setText(intent.getStringExtra("room_close_time"));
        room_name.setText(intent.getStringExtra("room_name"));
        String image = intent.getStringExtra("room_image");
        checkBox1.setChecked(intent.getBooleanExtra("checkbox1", false));
        checkBox2.setChecked(intent.getBooleanExtra("checkbox2", false));
        checkBox3.setChecked(intent.getBooleanExtra("checkbox3", false));
        checkBox4.setChecked(intent.getBooleanExtra("checkbox4", false));
        checkBox5.setChecked(intent.getBooleanExtra("checkbox5", false));
        checkBox6.setChecked(intent.getBooleanExtra("checkbox6", false));

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key != null && !key.isEmpty()) {
                    // Reference to the exact path
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("doormint/fav/"+modEmail+"/room/").child(key);

//                    Toast.makeText(SingleOwnerRoomActivity.this, databaseRef, Toast.LENGTH_SHORT).show();
                    // Show Progress Dialog
                    dialog.setMessage("Removing room...");
                    dialog.show();

                    // Remove key from Firebase
                    databaseRef.removeValue().addOnCompleteListener(task -> {
                        dialog.dismiss(); // Dismiss progress dialog
                        if (task.isSuccessful()) {
                            Toast.makeText(FavRoomRemoveActivity.this, "Room Remove successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity after deletion
                        } else {
                            Toast.makeText(FavRoomRemoveActivity.this, "Failed to delete room!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(FavRoomRemoveActivity.this, "Invalid key, deletion failed!", Toast.LENGTH_SHORT).show();
                }
            }
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

}
