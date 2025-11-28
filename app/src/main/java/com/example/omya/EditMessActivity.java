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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditMessActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText upl_mess_name, upl_mess_location, upl_mess_price, upl_facility, upl_owner_name, upl_owner_contact;
    private ImageView upl_mess_image;
    private Button upl_mess_upload;
    private Spinner spinner;

    private TimePicker mess_close_time, mess_open_time;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    // Firebase
    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog dialog;

    // Variables
    private String open_time, close_time;
    private Uri mess_imageUri;
    private String selectedMessType,owner_email,key,existingRoomImageUrl; // For storing the selected spinner item
//    private String ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mess);



        // UI Components initialization
        upl_mess_name = findViewById(R.id.upl_mess_name);
        upl_mess_location = findViewById(R.id.upl_mess_location);
        upl_mess_price = findViewById(R.id.upl_mess_price);
        upl_mess_image = findViewById(R.id.upl_mess_image);
        upl_mess_upload = findViewById(R.id.upl_mess_upload);

        upl_facility = findViewById(R.id.upl_facility);
        upl_owner_name = findViewById(R.id.upl_owner_name);
        upl_owner_contact = findViewById(R.id.upl_owner_contact);
        mess_close_time = findViewById(R.id.mess_close_time);
        mess_open_time = findViewById(R.id.mess_open_time);
        spinner = findViewById(R.id.mess_type);


        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
//        checkBox4 = findViewById(R.id.CheckBox4);
//        checkBox5 = findViewById(R.id.CheckBox5);
//        checkBox6 = findViewById(R.id.CheckBox6);

        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        mess_close_time.setOnTimeChangedListener((view, hourOfDay, minute) -> close_time = String.format("%02d:%02d", hourOfDay, minute));
        mess_open_time.setOnTimeChangedListener((view, hourOfDay, minute) -> open_time = String.format("%02d:%02d", hourOfDay, minute));

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
        dialog.setTitle("Updating Mess");


        Intent intent = getIntent();
        existingRoomImageUrl = intent.getStringExtra("room_image");
        key = intent.getStringExtra("key");

        if (existingRoomImageUrl != null) {
            Picasso.get().load(existingRoomImageUrl).placeholder(R.drawable.ic_launcher_foreground).into(upl_mess_image);
        }

        upl_mess_price.setText(intent.getStringExtra("mess_price"));
        upl_mess_location.setText(intent.getStringExtra("mess_location"));
        upl_facility.setText(intent.getStringExtra("mess_facility"));
        upl_owner_contact.setText(intent.getStringExtra("mess_contact"));
        upl_owner_name.setText(intent.getStringExtra("mess_ow_name"));
        upl_mess_name.setText(intent.getStringExtra("mess_name"));

        checkBox1.setChecked(intent.getBooleanExtra("checkbox1", false));
        checkBox2.setChecked(intent.getBooleanExtra("checkbox2", false));
        checkBox3.setChecked(intent.getBooleanExtra("checkbox3", false));

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mess_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMessType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMessType = null;
            }
        });

        // Set OnClickListener for Image Selection
        upl_mess_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMessImage();
            }
        });

        // Set OnClickListener for Upload Button
        upl_mess_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    uploadMessData();
                }
            }
        });
    }

    // Method to open gallery for image selection
    private void uploadMessImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mess_imageUri = data.getData();
            upl_mess_image.setImageURI(mess_imageUri); // Display the selected image
        }
    }

    // Validate all input fields before uploading
    private boolean validateInputs() {
        if (upl_mess_name.getText().toString().isEmpty() || upl_mess_location.getText().toString().isEmpty() ||
                upl_mess_price.getText().toString().isEmpty() || selectedMessType == null) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Method to upload mess data to Firebase
    private void uploadMessData() {
        dialog.show();
        if (mess_imageUri != null) {
            StorageReference reference = firebaseStorage.getReference().child("room").child(System.currentTimeMillis() + ".jpg");
            reference.putFile(mess_imageUri).addOnSuccessListener(taskSnapshot ->
                    reference.getDownloadUrl().addOnSuccessListener(this::updateMessInDatabase));
        } else {
            updateMessInDatabase(Uri.parse(existingRoomImageUrl));
        }
    }
    private void updateMessInDatabase(Uri imageUrl) {
        boolean isCheckBox1Checked = checkBox1.isChecked();
        boolean isCheckBox2Checked = checkBox2.isChecked();
        boolean isCheckBox3Checked = checkBox3.isChecked();

        String sanitizedEmail = owner_email.replace(".", "_");
        DatabaseReference roomRef = database.getReference().child("doormint/mess/" + sanitizedEmail + "/" + key);

        roomRef.child("upl_mess_image").setValue(imageUrl.toString());
        roomRef.child("upl_mess_location").setValue(upl_mess_location.getText().toString());
        roomRef.child("upl_mess_name").setValue(upl_mess_name.getText().toString());
        roomRef.child("upl_mess_price").setValue(upl_mess_price.getText().toString());
        roomRef.child("upl_facility").setValue(upl_facility.getText().toString());
        roomRef.child("upl_owner_name").setValue(upl_owner_name.getText().toString());
        roomRef.child("upl_owner_contact").setValue(upl_owner_contact.getText().toString());
        roomRef.child("mess_type").setValue(selectedMessType);
        roomRef.child("mess_close_time").setValue(close_time);
        roomRef.child("mess_open_time").setValue(open_time);


        roomRef.child("checkBox1").setValue(isCheckBox1Checked);
        roomRef.child("checkBox2").setValue(isCheckBox2Checked);
        roomRef.child("checkBox3").setValue(isCheckBox3Checked);

        dialog.dismiss();
        Toast.makeText(EditMessActivity.this, "Update successful!", Toast.LENGTH_SHORT).show();
    }
}
