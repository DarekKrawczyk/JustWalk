package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

public class DashboardBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout _drawerLayout;
    @Override
    public void setContentView(View view){
        _drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_dashboard_base, null);
        FrameLayout container = _drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(_drawerLayout);

        Toolbar toolbar = _drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = _drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,_drawerLayout, toolbar, R.string.menu_drawer_open, R.string.menu_drawer_close);
        _drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        _drawerLayout.closeDrawer(GravityCompat.START);
        int itemID = item.getItemId();

        if (itemID == R.id.nav_home) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(DashboardBaseActivity.this, HomeActivity.class));
                    overridePendingTransition(0, 0);
                }
            }, 300);
        } else if (itemID == R.id.nav_walk) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(DashboardBaseActivity.this, WalkActivity.class));
                    overridePendingTransition(0, 0);
                }
            }, 300);
        } else if (itemID == R.id.nav_walks) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO: IMPLEMENT
                }
            }, 300);
        } else if (itemID == R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO: IMPLEMENT
                }
            }, 300);
        } else if (itemID == R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO: IMPLEMENT LOGOUT!!!
                    
                }
            }, 300);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _drawerLayout.closeDrawer(GravityCompat.START);
            }
        }, 1000); // Adjust the delay time as needed

        return false;
    }

    protected void allocateActivityTitle(String title){
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }
}