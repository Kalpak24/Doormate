package com.example.omya;

import android.annotation.SuppressLint;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditRoomActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText upl_room_name, upl_room_location, upl_room_price, upl_facility, upl_owner_name, upl_owner_contact;
    private ImageView upl_room_image;
    private Button upl_room_upload;
    private Spinner spinner;
    private TimePicker room_close_time, room_open_time;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;

    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog dialog;
    private String open_time, close_time;
    private Uri room_imageUri;
    private String selectedRoomType, owner_email, key, existingRoomImageUrl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        upl_room_image = findViewById(R.id.upl_room_image);
        upl_room_price = findViewById(R.id.upl_room_price);
        upl_room_location = findViewById(R.id.upl_room_location);
        upl_facility = findViewById(R.id.upl_facility);
        upl_owner_contact = findViewById(R.id.upl_owner_contact);
        upl_owner_name = findViewById(R.id.upl_owner_name);
        room_open_time = findViewById(R.id.room_open_time);
        room_close_time = findViewById(R.id.room_close_time);
        upl_room_name = findViewById(R.id.upl_room_name);
        upl_room_upload = findViewById(R.id.upl_room_upload);
        spinner = findViewById(R.id.room_type);

        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
        checkBox4 = findViewById(R.id.CheckBox4);
        checkBox5 = findViewById(R.id.CheckBox5);
        checkBox6 = findViewById(R.id.CheckBox6);

        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        room_close_time.setOnTimeChangedListener((view, hourOfDay, minute) -> close_time = String.format("%02d:%02d", hourOfDay, minute));
        room_open_time.setOnTimeChangedListener((view, hourOfDay, minute) -> open_time = String.format("%02d:%02d", hourOfDay, minute));

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        owner_email = sharedPreferences.getString("owner_email", null);
        if (owner_email == null) {
            Toast.makeText(this, "Owner email missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setTitle("Updating Room");

        Intent intent = getIntent();
        existingRoomImageUrl = intent.getStringExtra("room_image");
        key = intent.getStringExtra("key");

        if (existingRoomImageUrl != null) {
            Picasso.get().load(existingRoomImageUrl).placeholder(R.drawable.ic_launcher_foreground).into(upl_room_image);
        }

        upl_room_price.setText(intent.getStringExtra("room_price"));
        upl_room_location.setText(intent.getStringExtra("room_location"));
        upl_facility.setText(intent.getStringExtra("room_facility"));
        upl_owner_contact.setText(intent.getStringExtra("room_contact"));
        upl_owner_name.setText(intent.getStringExtra("room_ow_name"));
        upl_room_name.setText(intent.getStringExtra("room_name"));

        checkBox1.setChecked(intent.getBooleanExtra("checkbox1", false));
        checkBox2.setChecked(intent.getBooleanExtra("checkbox2", false));
        checkBox3.setChecked(intent.getBooleanExtra("checkbox3", false));
        checkBox4.setChecked(intent.getBooleanExtra("checkbox4", false));
        checkBox5.setChecked(intent.getBooleanExtra("checkbox5", false));
        checkBox6.setChecked(intent.getBooleanExtra("checkbox6", false));

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

        upl_room_image.setOnClickListener(v -> uploadRoomImage());

        upl_room_upload.setOnClickListener(v -> {
            if (validateInputs()) {
                updateRoomData();
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
        if (upl_room_name.getText().toString().isEmpty() || upl_room_location.getText().toString().isEmpty() ||
                upl_room_price.getText().toString().isEmpty() || selectedRoomType == null) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateRoomData() {
        dialog.show();
        if (room_imageUri != null) {
            StorageReference reference = firebaseStorage.getReference().child("room").child(System.currentTimeMillis() + ".jpg");
            reference.putFile(room_imageUri).addOnSuccessListener(taskSnapshot ->
                    reference.getDownloadUrl().addOnSuccessListener(this::updateRoomInDatabase));
        } else {
            updateRoomInDatabase(Uri.parse(existingRoomImageUrl));
        }
    }

    private void updateRoomInDatabase(Uri imageUrl) {
        boolean isCheckBox1Checked = checkBox1.isChecked();
        boolean isCheckBox2Checked = checkBox2.isChecked();
        boolean isCheckBox3Checked = checkBox3.isChecked();
        boolean isCheckBox4Checked = checkBox4.isChecked();
        boolean isCheckBox5Checked = checkBox5.isChecked();
        boolean isCheckBox6Checked = checkBox6.isChecked();

        String sanitizedEmail = owner_email.replace(".", "_");
        DatabaseReference roomRef = database.getReference().child("doormint/room/" + sanitizedEmail + "/" + key);

        roomRef.child("upl_room_image").setValue(imageUrl.toString());
        roomRef.child("upl_room_location").setValue(upl_room_location.getText().toString());
        roomRef.child("upl_room_name").setValue(upl_room_name.getText().toString());
        roomRef.child("upl_room_price").setValue(upl_room_price.getText().toString());
        roomRef.child("upl_facility").setValue(upl_facility.getText().toString());
        roomRef.child("upl_owner_name").setValue(upl_owner_name.getText().toString());
        roomRef.child("upl_owner_contact").setValue(upl_owner_contact.getText().toString());
        roomRef.child("room_type").setValue(selectedRoomType);
        roomRef.child("room_close_time").setValue(close_time);
        roomRef.child("room_close_time").setValue(open_time);


        roomRef.child("checkBox1").setValue(isCheckBox1Checked);
        roomRef.child("checkBox2").setValue(isCheckBox2Checked);
        roomRef.child("checkBox3").setValue(isCheckBox3Checked);
        roomRef.child("checkBox4").setValue(isCheckBox4Checked);
        roomRef.child("checkBox5").setValue(isCheckBox5Checked);
        roomRef.child("checkBox6").setValue(isCheckBox6Checked);

        dialog.dismiss();
        Toast.makeText(EditRoomActivity.this, "Update successful!", Toast.LENGTH_SHORT).show();
    }
}
