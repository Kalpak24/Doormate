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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadMessActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText upl_mess_name, upl_mess_location, upl_mess_price, upl_facility, upl_owner_name, upl_owner_contact;
    private ImageView upl_mess_image;
    private Button upl_mess_upload;
    private Spinner spinner;

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    private TimePicker mess_close_time, mess_open_time;
    // Firebase
    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog dialog;

    // Variables
    private Uri mess_imageUri;
    private String selectedMessType; // For storing the selected spinner item
    private String owner_email;

    private String open_time, close_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_mess);

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


        checkBox1 = findViewById(R.id.CheckBox1);
        checkBox2 = findViewById(R.id.CheckBox2);
        checkBox3 = findViewById(R.id.CheckBox3);
//        checkBox4 = findViewById(R.id.CheckBox4);
//        checkBox5 = findViewById(R.id.CheckBox5);
//        checkBox6 = findViewById(R.id.CheckBox6);

        mess_close_time.setOnTimeChangedListener((view, hourOfDay, minute) -> close_time = String.format("%02d:%02d", hourOfDay, minute));

        mess_open_time.setOnTimeChangedListener((view, hourOfDay, minute) -> open_time = String.format("%02d:%02d", hourOfDay, minute));

        spinner = findViewById(R.id.mess_type);

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
        if (upl_mess_name.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Mess Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (upl_mess_location.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Mess Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (upl_mess_price.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter Mess Price", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mess_imageUri == null) {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedMessType == null || selectedMessType.isEmpty()) {
            Toast.makeText(this, "Select Mess Type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (open_time == null || close_time == null) {
            Toast.makeText(this, "Set room opening and closing time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Method to upload mess data to Firebase
    private void uploadMessData() {
        dialog.show();
        boolean isCheckBox1Checked = checkBox1.isChecked();
        boolean isCheckBox2Checked = checkBox2.isChecked();
        boolean isCheckBox3Checked = checkBox3.isChecked();
//        boolean isCheckBox4Checked = checkBox4.isChecked();
//        boolean isCheckBox5Checked = checkBox5.isChecked();
//        boolean isCheckBox6Checked = checkBox6.isChecked();

        final StorageReference reference = firebaseStorage.getReference().child("mess").child(System.currentTimeMillis() + ".jpg");
        reference.putFile(mess_imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        MessModel model = new MessModel();
                        model.setUpl_mess_image(uri.toString());
                        model.setUpl_mess_location(upl_mess_location.getText().toString());
                        model.setUpl_mess_name(upl_mess_name.getText().toString());
                        model.setUpl_mess_price(upl_mess_price.getText().toString());

                        model.setUpl_facility(upl_facility.getText().toString());
                        model.setUpl_owner_name(upl_owner_name.getText().toString());
                        model.setUpl_owner_contact(upl_owner_contact.getText().toString());
                        model.setMess_open_time(open_time);
                        model.setMess_close_time(close_time);

                        model.setMess_type(selectedMessType);

                        model.setCheckBox1(isCheckBox1Checked);
                        model.setCheckBox2(isCheckBox2Checked);
                        model.setCheckBox3(isCheckBox3Checked);
//                        model.setCheckBox4(isCheckBox4Checked);
//                        model.setCheckBox5(isCheckBox5Checked);
//                        model.setCheckBox6(isCheckBox6Checked);

                        // Sanitize email to use as Firebase Database path
                        String sanitizedEmail = owner_email.replace(".", "_"); // Replace '.' with '_'

                        // Upload to Firebase Database
                        database.getReference().child("doormint/mess/" + sanitizedEmail).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UploadMessActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(UploadMessActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UploadMessActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
