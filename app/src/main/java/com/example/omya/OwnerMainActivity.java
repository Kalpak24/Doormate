package com.example.omya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OwnerMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private TextView getUserEmail;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;
    private long backPressedTime = 0;
    private Toast backToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main);

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        userdb_ref = user_db.getReference("doormint/account/owner"); // Initialize userdb_ref
        cur_user = mAuth.getCurrentUser();

        if (cur_user == null) {
            startActivity(new Intent(OwnerMainActivity.this, LoginStudentActivity.class));
            finish();
            return;
        }
        checkUserAuthentication();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        getUserEmail = findViewById(R.id.UserEmailView);
        getUserName = findViewById(R.id.UserNameView);


        if (savedInstanceState == null) {
            loadFragment(new OwnerRoomFragment());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.mess) {
                selectedFragment = new OwnerMessFragment();
            } else if (item.getItemId() == R.id.room) {
                selectedFragment = new OwnerRoomFragment();
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = new ProfileFragment();
            }


            return loadFragment(selectedFragment);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserAuthentication();
    }

    // TODO:  Warning : Do not Solve Below Error
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity(); // Close the entire app
            System.exit(0); // Optional: Ensures app is fully closed
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserAuthentication();
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginOwnerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private String getEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("owner_email", null);
    }
}
