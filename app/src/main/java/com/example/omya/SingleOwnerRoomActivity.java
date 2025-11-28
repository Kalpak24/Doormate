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

public class SingleOwnerRoomActivity extends AppCompatActivity {

    TextView room_name, room_price, room_location, room_type, room_facility, room_contact, room_ow_name, room_open_time, room_close_time;
    ImageView room_image;
    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6;

    String key;
    ImageView backButton,remove,edit;

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
        checkBox4 = findViewById(R.id.CheckBox4);
        checkBox5 = findViewById(R.id.CheckBox5);
        checkBox6 = findViewById(R.id.CheckBox6);
        backButton = findViewById(R.id.backButton);
        edit = findViewById(R.id.edit);

        room_image = findViewById(R.id.room_image);
        room_price = findViewById(R.id.room_price);
        room_location = findViewById(R.id.room_location);
        room_type = findViewById(R.id.room_type);
        room_facility = findViewById(R.id.room_facility);
        remove = findViewById(R.id.remove);
        room_contact = findViewById(R.id.room_contact);
        room_ow_name = findViewById(R.id.room_ow_name);
        room_open_time = findViewById(R.id.room_open_time);
        room_close_time = findViewById(R.id.room_close_time);
        room_name = findViewById(R.id.room_name);
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

//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CheckboxStates");
        // Set data to UI components
        Picasso.get()
                .load(getIntent().getStringExtra("room_image"))
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(room_image);

        // Fetch checkbox states from Firebase
        checkBox1.setChecked(getIntent().getBooleanExtra("checkbox1",false));
        checkBox2.setChecked(getIntent().getBooleanExtra("checkbox2",false));
        checkBox3.setChecked(getIntent().getBooleanExtra("checkbox3",false));
        checkBox4.setChecked(getIntent().getBooleanExtra("checkbox4",false));
        checkBox5.setChecked(getIntent().getBooleanExtra("checkbox5",false));
        checkBox6.setChecked(getIntent().getBooleanExtra("checkbox6",false));


        key = getIntent().getStringExtra("unique_key");
        String modEmail = getIntent().getStringExtra("modEmail");

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key != null && !key.isEmpty()) {
                    // Reference to the exact path
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("doormint/room/"+modEmail).child(key);

//                    Toast.makeText(SingleOwnerRoomActivity.this, databaseRef, Toast.LENGTH_SHORT).show();
                    // Show Progress Dialog
                    dialog.setMessage("Removing room...");
                    dialog.show();

                    // Remove key from Firebase
                    databaseRef.removeValue().addOnCompleteListener(task -> {
                        dialog.dismiss(); // Dismiss progress dialog
                        if (task.isSuccessful()) {
                            Toast.makeText(SingleOwnerRoomActivity.this, "Room deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity after deletion
                        } else {
                            Toast.makeText(SingleOwnerRoomActivity.this, "Failed to delete room!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SingleOwnerRoomActivity.this, "Invalid key, deletion failed!", Toast.LENGTH_SHORT).show();
                }
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
        Boolean checkBox41 = getIntent().getBooleanExtra("checkbox4",false);
        Boolean checkBox51 = getIntent().getBooleanExtra("checkbox5",false);
        Boolean checkBox61 = getIntent().getBooleanExtra("checkbox6",false);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SingleOwnerRoomActivity.this,EditRoomActivity.class);


                i.putExtra("key",key);
                i.putExtra("modEmail",modEmail);

                i.putExtra("room_price",room_price1);
                i.putExtra("room_location",room_location1);
                i.putExtra("room_type",room_type1);
                i.putExtra("room_facility",room_facility1);
                i.putExtra("room_contact",room_contact1);
                i.putExtra("room_ow_name",room_ow_name1);
                i.putExtra("room_open_time",room_open_time1);
                i.putExtra("room_close_time",room_close_time1);
                i.putExtra("room_name",room_name1);

                i.putExtra("room_image",image1);

                i.putExtra("checkbox1",checkBox11);
                i.putExtra("checkbox2",checkBox21);
                i.putExtra("checkbox3",checkBox31);
                i.putExtra("checkbox4",checkBox41);
                i.putExtra("checkbox5",checkBox51);
                i.putExtra("checkbox6",checkBox61);
                startActivity(i);
            }
        });
    }
}
