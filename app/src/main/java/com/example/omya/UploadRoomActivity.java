package com.example.omya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText upl_room_name, upl_room_location, upl_room_price, upl_facility, upl_owner_name, upl_owner_contact;
    private ImageView upl_room_image;
    private Button upl_room_upload;
    private Spinner spinner;
    private TimePicker room_close_time, room_open_time;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;

    // Firebase
    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog dialog;

    // Variables
    private Uri room_imageUri;
    private String selectedRoomType;
    private String owner_email;
    private String open_time, close_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_room);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        owner_email = sharedPreferences.getString("owner_email", null);

        // Firebase initialization
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        // Progress dialog
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setTitle("Uploading");
        dialog.setCanceledOnTouchOutside(false);

        // UI Components initialization
        upl_room_name = findViewById(R.id.upl_room_name);
        upl_room_location = findViewById(R.id.upl_room_location);
        upl_room_price = findViewById(R.id.upl_room_price);
        upl_room_image = findViewById(R.id.upl_room_image);
        upl_room_upload = findViewById(R.id.upl_room_upload);
        upl_facility = findViewById(R.id.upl_facility);
        upl_owner_name = findViewById(R.id.upl_owner_name);
        upl_owner_contact = findViewById(R.id.upl_owner_contact);
        room_close_time = findViewById(R.id.room_close_time);
        room_open_time = findViewById(R.id.room_open_time);
        spinner = findViewById(R.id.room_type);

        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
        checkBox4 = findViewById(R.id.CheckBox4);
        checkBox5 = findViewById(R.id.CheckBox5);
        checkBox6 = findViewById(R.id.CheckBox6);

        // Setup TimePickers
        room_close_time.setOnTimeChangedListener((view, hourOfDay, minute) -> close_time = String.format("%02d:%02d", hourOfDay, minute));

        room_open_time.setOnTimeChangedListener((view, hourOfDay, minute) -> open_time = String.format("%02d:%02d", hourOfDay, minute));

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.room_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoomType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoomType = null;
            }
        });

        // Image Selection
        upl_room_image.setOnClickListener(v -> uploadRoomImage());

        // Upload Button
        upl_room_upload.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadRoomData();
            }
        });
    }

    private void uploadRoomImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            room_imageUri = data.getData();
            upl_room_image.setImageURI(room_imageUri);
        }
    }

    private boolean validateInputs() {
        if (upl_room_name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Room Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (upl_room_location.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Room Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (upl_room_price.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Room Price", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (room_imageUri == null) {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedRoomType == null || selectedRoomType.isEmpty()) {
            Toast.makeText(this, "Select Room Type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (open_time == null || close_time == null) {
            Toast.makeText(this, "Set room opening and closing time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadRoomData() {
        dialog.show();
        boolean isCheckBox1Checked = checkBox1.isChecked();
        boolean isCheckBox2Checked = checkBox2.isChecked();
        boolean isCheckBox3Checked = checkBox3.isChecked();
        boolean isCheckBox4Checked = checkBox4.isChecked();
        boolean isCheckBox5Checked = checkBox5.isChecked();
        boolean isCheckBox6Checked = checkBox6.isChecked();

        final StorageReference reference = firebaseStorage.getReference().child("room").child(System.currentTimeMillis() + ".jpg");
        reference.putFile(room_imageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            RoomModel model = new RoomModel();
            model.setUpl_room_image(uri.toString());
            model.setUpl_room_location(upl_room_location.getText().toString());
            model.setUpl_room_name(upl_room_name.getText().toString());
            model.setUpl_room_price(upl_room_price.getText().toString());
            model.setUpl_facility(upl_facility.getText().toString());
            model.setUpl_owner_name(upl_owner_name.getText().toString());
            model.setUpl_owner_contact(upl_owner_contact.getText().toString());
            model.setRoom_open_time(open_time);
            model.setRoom_close_time(close_time);
            model.setRoom_type(selectedRoomType);

            model.setCheckBox1(isCheckBox1Checked);
            model.setCheckBox2(isCheckBox2Checked);
            model.setCheckBox3(isCheckBox3Checked);
            model.setCheckBox4(isCheckBox4Checked);
            model.setCheckBox5(isCheckBox5Checked);
            model.setCheckBox6(isCheckBox6Checked);

            String sanitizedEmail = owner_email.replace(".", "_");

            database.getReference().child("doormint/room/" + sanitizedEmail).push().setValue(model).addOnSuccessListener(unused -> {
                Toast.makeText(UploadRoomActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(UploadRoomActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        })).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(UploadRoomActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
