package com.example.omya;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SingleMessOwnerActivity extends AppCompatActivity {

    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
    ImageView room_image;
    CheckBox checkBox1,checkBox2,checkBox3;
    String key;
    ImageView backButton,edit,remove;

    private FirebaseDatabase database;
    private ProgressDialog dialog;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_mess_owner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        backButton = findViewById(R.id.backButton);

        edit = findViewById(R.id.edit);
        remove = findViewById(R.id.remove);
//        accept = findViewById(R.id.accept);
//        decline = findViewById(R.id.decline);

        // Initialize Firebase and ProgressDialog
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setTitle("Uploading");
        dialog.setCanceledOnTouchOutside(false);

        // Fetch checkbox states from Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CheckboxStates");

        // Set data to UI components
        Picasso.get()
                .load(getIntent().getStringExtra("room_image"))
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(room_image);

        checkBox1.setChecked(getIntent().getBooleanExtra("checkbox1",false));
        checkBox2.setChecked(getIntent().getBooleanExtra("checkbox2",false));
        checkBox3.setChecked(getIntent().getBooleanExtra("checkbox3",false));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        room_price.setText(getIntent().getStringExtra("room_price"));
        room_location.setText(getIntent().getStringExtra("room_location"));
        room_type.setText(getIntent().getStringExtra("room_type"));
        room_facility.setText(getIntent().getStringExtra("room_facility"));
        room_contact.setText(getIntent().getStringExtra("room_contact"));
        room_ow_name.setText(getIntent().getStringExtra("room_ow_name"));
        room_open_time.setText(getIntent().getStringExtra("room_open_time"));
        room_close_time.setText(getIntent().getStringExtra("room_close_time"));
        room_name.setText(getIntent().getStringExtra("room_name"));

        key = getIntent().getStringExtra("unique_key");
        String modEmail = getIntent().getStringExtra("modEmail");

        room_price.setText(getIntent().getStringExtra("room_price"));
        room_location.setText(getIntent().getStringExtra("room_location"));
        room_type.setText(getIntent().getStringExtra("room_type"));
        room_facility.setText(getIntent().getStringExtra("room_facility"));
        room_contact.setText(getIntent().getStringExtra("room_contact"));
        room_ow_name.setText(getIntent().getStringExtra("room_ow_name"));
        room_open_time.setText(getIntent().getStringExtra("room_open_time"));
        room_close_time.setText(getIntent().getStringExtra("room_close_time"));
        room_name.setText(getIntent().getStringExtra("room_name"));


        String room_price1 = getIntent().getStringExtra("room_price");
        String room_location1 = getIntent().getStringExtra("room_location");
        String room_type1 = getIntent().getStringExtra("room_type");
        String room_facility1 = getIntent().getStringExtra("room_facility");
        String room_contact1 = getIntent().getStringExtra("room_contact");
        String room_ow_name1 = getIntent().getStringExtra("room_ow_name");
        String room_open_time1 = getIntent().getStringExtra("room_open_time");
        String room_close_time1 = getIntent().getStringExtra("room_close_time");
        String room_name1 = getIntent().getStringExtra("room_name");

        String image1 = getIntent().getStringExtra("image");


        Boolean checkBox11 = getIntent().getBooleanExtra("checkbox1",false);
        Boolean checkBox21 = getIntent().getBooleanExtra("checkbox2",false);
        Boolean checkBox31 = getIntent().getBooleanExtra("checkbox3",false);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SingleMessOwnerActivity.this,EditMessActivity.class);


                i.putExtra("key",key);
                i.putExtra("modEmail",modEmail);

                i.putExtra("mess_price",room_price1);
                i.putExtra("mess_location",room_location1);
                i.putExtra("mess_type",room_type1);
                i.putExtra("mess_facility",room_facility1);
                i.putExtra("mess_contact",room_contact1);
                i.putExtra("mess_ow_name",room_ow_name1);
                i.putExtra("mess_open_time",room_open_time1);
                i.putExtra("mess_close_time",room_close_time1);
                i.putExtra("mess_name",room_name1);

                i.putExtra("mess_image",image1);

                i.putExtra("checkbox1",checkBox11);
                i.putExtra("checkbox2",checkBox21);
                i.putExtra("checkbox3",checkBox31);
                startActivity(i);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key != null && !key.isEmpty()) {
                    // Reference to the exact path
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("doormint/mess/"+modEmail).child(key);

//                    Toast.makeText(SingleOwnerRoomActivity.this, databaseRef, Toast.LENGTH_SHORT).show();
                    // Show Progress Dialog
                    dialog.setMessage("Removing room...");
                    dialog.show();
//                    Toast.makeText(SingleMessOwnerActivity.this, key, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(SingleMessOwnerActivity.this, modEmail, Toast.LENGTH_SHORT).show();
                    // Remove key from Firebase
                    databaseRef.removeValue().addOnCompleteListener(task -> {
                        dialog.dismiss(); // Dismiss progress dialog
                        if (task.isSuccessful()) {
                            Toast.makeText(SingleMessOwnerActivity.this, "Room deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity after deletion
                        } else {
                            Toast.makeText(SingleMessOwnerActivity.this, "Failed to delete room!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SingleMessOwnerActivity.this, "Invalid key, deletion failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
