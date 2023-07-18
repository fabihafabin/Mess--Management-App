package com.example.messmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeMemberActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
TextView welcomeTV;
Button logoutBtn;
FirebaseAuth firebaseAuth;
DrawerLayout drawerLayout;
ActionBarDrawerToggle toggle;
NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigationView);
//
//
        toggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        //logoutBtn=findViewById(R.id.logoutBtn);
        //welcomeTV=findViewById(R.id.welcomeTV);
//        firebaseAuth=FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
//        welcomeTV.setText(firebaseUser.getEmail());
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.signOut();
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//                finish();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int item1=item.getItemId();
        if(item1==R.id.offlineNotes){
            startActivity(new Intent(getApplicationContext(),OfflineNotesActivity.class));
        }
        return true;
    }
}