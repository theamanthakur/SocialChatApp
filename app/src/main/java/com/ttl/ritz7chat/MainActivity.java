package com.ttl.ritz7chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private DatabaseReference ChatsRef, UsersRef;
    boolean doubleBackToExitPressedOnce = false;
    private String currentUserID = "";
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        toolbar = findViewById(R.id.main_page_toolbar);

        toolbar = findViewById(R.id.main_page_toolbar);
        mAuth = FirebaseAuth.getInstance();
//                mAuth.signOut();
//        if (mAuth.getCurrentUser().getUid() != null) {
//            currentUserID = mAuth.getCurrentUser().getUid();
//            Toast.makeText(this, "" + currentUserID, Toast.LENGTH_SHORT).show();
//        }


        databaseReference = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.main_page_toolbar);
//        mAuth = FirebaseAuth.getInstance();
//        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
//        getSupportActionBar().setTitle("Ritz7Chat");

//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle("Ritz7Chat");


    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {


                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_chats:
                            selectedFragment = new ChatsFragment();
                            break;
                        case R.id.navigation_groups:
                            selectedFragment = new GroupsFragment();
                            break;
                        case R.id.navigation_contacts:
                            selectedFragment = new ContactsFragment();
                            break;
                        case R.id.navigation_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                        default:
                            selectedFragment = new ChatsFragment();
                    }
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                selectedFragment).commit();

                        return true;
                    }
            };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser ==  null){
            sendToLoginActivity();
        }
        else {
//            updateUserStatus("online");
//            Toast.makeText(this, ""+currentUserID, Toast.LENGTH_SHORT).show();
            veifyUserExistance();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
//            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
//            updateUserStatus("offline");
        }
    }

    private void veifyUserExistance() {
        String userId = mAuth.getCurrentUser().getUid();
        databaseReference.child("UsersChat").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())){
                    final String retName = snapshot.child("name").getValue().toString();
//                    Toast.makeText(MainActivity.this, "Welcome " + retName, Toast.LENGTH_SHORT).show();
                }else{
                    sendToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendToLoginActivity() {
        Intent intent = new Intent(MainActivity.this,loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_option){
//            updateUserStatus("offline");
            mAuth.signOut();
            sendToLoginActivity();
        }
        if(item.getItemId() == R.id.main_settings_option){
            sendToSettingActivity();
        }
        if(item.getItemId() == R.id.main_create_group_option){
//            startActivity(new Intent(this,CreateGroupActivity.class));
        }
        if(item.getItemId() == R.id.main_find_user_option){
            SendUserToFindFriendsActivity();
        }
        return true;
    }

    private void SendUserToFindFriendsActivity() {
//        Intent findFriendsIntent = new Intent(MainActivity.this, findFriendActivity.class);
//        startActivity(findFriendsIntent);
    }

    private void requesNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light);
        builder.setTitle("Enter Group Name");
        final EditText groupNameText = new EditText(MainActivity.this);
        groupNameText.setHint("e.g. Developer Group");
        builder.setView(groupNameText);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameText.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please Enter group Name...", Toast.LENGTH_SHORT).show();
                }else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createNewGroup(String groupName) {
        databaseReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName+ "group created successfully..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToSettingActivity() {
        Intent intentSetting = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intentSetting);

    }

    private void updateUserStatus(String state)
    {
        currentUserID = mAuth.getCurrentUser().getUid();
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        databaseReference.child("UsersChat").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }



}