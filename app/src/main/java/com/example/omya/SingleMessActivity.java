package com.example.omya;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SingleMessActivity extends AppCompatActivity {

    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
    ImageView room_image;
    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;
    Button accept, decline;
    ImageView backButton,add;
    MaterialButton dialButton;

    private FirebaseDatabase database;
    private ProgressDialog dialog;

    private final ActivityResultLauncher<String> requestCallPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    makeCall();
                } else {
                    Toast.makeText(SingleMessActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mess);

        database = FirebaseDatabase.getInstance();

        // Initialize ProgressDialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.setCancelable(false);

        // Initialize UI components
        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);

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
        add = findViewById(R.id.add);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialButton = findViewById(R.id.dialButton);

        // Set up the dial button to make the call
        dialButton.setOnClickListener(v -> {
            // Check if the call permission is granted
            if (ActivityCompat.checkSelfPermission(SingleMessActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestCallPermission.launch(Manifest.permission.CALL_PHONE);
            } else {
                makeCall(); // Make the call if permission is already granted
            }
        });
        add.setOnClickListener(v -> uploadRoomData());
        Intent intent = getIntent();

        String imageUrl = intent.getStringExtra("room_image");
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(room_image);
        }

        checkBox1.setChecked(getIntent().getBooleanExtra("checkbox1",false));
        checkBox2.setChecked(getIntent().getBooleanExtra("checkbox2",false));
        checkBox3.setChecked(getIntent().getBooleanExtra("checkbox3",false));

        room_price.setText(getIntent().getStringExtra("room_price"));
        room_location.setText(getIntent().getStringExtra("room_location"));
        room_type.setText(getIntent().getStringExtra("room_type"));
        room_facility.setText(getIntent().getStringExtra("room_facility"));
        room_contact.setText(getIntent().getStringExtra("room_contact"));
        room_ow_name.setText(getIntent().getStringExtra("room_ow_name"));
        room_open_time.setText(getIntent().getStringExtra("room_open_time"));
        room_close_time.setText(getIntent().getStringExtra("room_close_time"));
        room_name.setText(getIntent().getStringExtra("room_name"));

    }

    private void makeCall() {
        String phoneNumber = room_contact.getText().toString(); // Get the text from the TextView
        if (!phoneNumber.isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber)); // Make sure to prefix "tel:"
            startActivity(callIntent); // Make the call
        } else {
            Toast.makeText(SingleMessActivity.this, "Phone number is not available", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadRoomData() {
        dialog.show();

        // Create RoomModel object
        MessModel model = new MessModel();
        String image = getIntent().getStringExtra("room_image");
        model.setUpl_mess_price(room_price.getText().toString());
        model.setUpl_mess_location(room_location.getText().toString());
        model.setMess_type(room_type.getText().toString());
        model.setUpl_facility(room_facility.getText().toString());
        model.setUpl_owner_contact(room_contact.getText().toString());
        model.setUpl_owner_name(room_ow_name.getText().toString());
        model.setUpl_mess_name(room_name.getText().toString());
        model.setMess_open_time(room_open_time.getText().toString());
        model.setMess_close_time(room_close_time.getText().toString());
        model.setUpl_mess_image(image);

        // Set CheckBox values
        model.setCheckBox1(checkBox1.isChecked());
        model.setCheckBox2(checkBox2.isChecked());
        model.setCheckBox3(checkBox3.isChecked());


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
//            Toast.makeText(roomContext, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();

        String modEmail = email.replace(".", "_");

        // Upload room data to Firebase
        database.getReference().child("doormint/fav/"+modEmail+"/mess").push().setValue(model)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(SingleMessActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(SingleMessActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
