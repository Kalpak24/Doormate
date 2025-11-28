package com.example.omya;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;
    private long backPressedTime = 0;
    private Toast backToast;
    FloatingActionButton feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        userdb_ref = user_db.getReference("doormint/account/student");
        cur_user = mAuth.getCurrentUser();
        feed = findViewById(R.id.feed);
        if (cur_user == null) {
            startActivity(new Intent(StudentMainActivity.this, LoginStudentActivity.class));
            finish();
            return;
        }
        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentMainActivity.this,FeedBackActivity.class);
                startActivity(i);
            }
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.mess) {
                    selectedFragment = new StudentMessFragment();
                } else if (item.getItemId() == R.id.room) {
                    selectedFragment = new StudentRoomFragment();
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.fav_room) {
                    selectedFragment = new FavRoomFragment();
                } else if (item.getItemId() == R.id.fav_mess) {
                    selectedFragment = new FavMessFragment();
                }

                return loadFragment(selectedFragment);
            }
        });

        // Load default fragment (Room Fragment) when app starts
        if (savedInstanceState == null) {
            loadFragment(new StudentRoomFragment());
            bottomNavigationView.setSelectedItemId(R.id.room);
        }
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
}
