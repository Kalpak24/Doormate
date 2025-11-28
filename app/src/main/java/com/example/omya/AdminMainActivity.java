package com.example.omya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private TextView getUserEmail;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;
    private ProgressDialog pd;
    private long backPressedTime = 0;
    private Toast backToast;
    FloatingActionButton see;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        userdb_ref = user_db.getReference("doormint/account/admin"); // Initialize userdb_ref
        cur_user = mAuth.getCurrentUser();
        see = findViewById(R.id.see);
        if (cur_user == null) {
            startActivity(new Intent(AdminMainActivity.this, LoginAdminActivity.class));
            finish();
            return;
        }

        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMainActivity.this,FeedbackListActivity.class);
                startActivity(i);
            }
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        View header = navigationView.getHeaderView(0);
//
//        getUserEmail = header.findViewById(R.id.UserEmailView);
//        getUserName = header.findViewById(R.id.UserNameView);

//        Query singleUserQuery = userdb_ref.child(cur_user.getUid());
//        pd.show();
//
//        singleUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override

//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    OwnerData ownerData = dataSnapshot.getValue(OwnerData.class);
//                    if (ownerData != null) {
//                        getUserName.setText(ownerData.getName());
//                    }
//                    getUserEmail.setText(cur_user.getEmail());
//                } else {
//                    Log.d("User", "No data found for user");
//                }
//                pd.dismiss();
//            }

//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("User", databaseError.getMessage());
//                pd.dismiss();
//            }
//        });


        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load the default fragment when activity is created
        if (savedInstanceState == null) {
            loadFragment(new AdminRoomFragment());
        }

        // Handle bottom navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.mess) { // Menu item for Mess
                    selectedFragment = new AdminMessFragment();
                } else if (item.getItemId() == R.id.room) { // Menu item for Room
                    selectedFragment = new AdminRoomFragment();
                } else if (item.getItemId() == R.id.profile) { // Menu item for Room
                    selectedFragment = new ProfileFragment();
                }
                    else if (item.getItemId() == R.id.all_room) { // Menu item for Room
                    selectedFragment = new AllRoomFragment();
                }else if (item.getItemId() == R.id.book_room) { // Menu item for Room
                    selectedFragment = new AdminBookFragment();
                }

                return loadFragment(selectedFragment);
            }
        });
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.home) {
//        } else if (id == R.id.logout) {
//            startActivity(new Intent(AdminMainActivity.this, AdminMainActivity.class));
//            mAuth.signOut();
//            Intent intent = new Intent(getApplicationContext(), LoginAdminActivity.class);
//            startActivity(intent);
//            finish();
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginAdminActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginAdminActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Method to load the specified fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    // Method to retrieve email from SharedPreferences
    private String getEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("admin_email", null); // Default is null if not found
    }
}
