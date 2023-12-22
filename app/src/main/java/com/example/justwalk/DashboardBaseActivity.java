package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout _drawerLayout;
    private int DELAY_MS = 100;
    private final String TAG = "DashboardBaseActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
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
            }, DELAY_MS);
        } else if (itemID == R.id.nav_walk) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isServicesOK()) {
                        startActivity(new Intent(DashboardBaseActivity.this, WalkActivity.class));
                        overridePendingTransition(0, 0);
                    }
                }
            }, DELAY_MS);
        }   else if (itemID == R.id.nav_settings) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isServicesOK()){
                        startActivity(new Intent(DashboardBaseActivity.this, UserSettingsActivity.class));
                        overridePendingTransition(0, 0);
                    }
                }
            }, DELAY_MS);
        } else if (itemID == R.id.nav_walks) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(DashboardBaseActivity.this, WalksActivity.class));
                    overridePendingTransition(0, 0);
                }
            }, DELAY_MS);
        } else if (itemID == R.id.nav_stats) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isServicesOK()){
                        startActivity(new Intent(DashboardBaseActivity.this, StatisticActivity.class));
                        overridePendingTransition(0, 0);
                    }
                }
            }, DELAY_MS);
        } else if (itemID == R.id.nav_log_out) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    startActivity(new Intent(DashboardBaseActivity.this, LoginActivity.class));
                    overridePendingTransition(0, 0);
                }
            }, DELAY_MS);
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

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK() -> Checking google services version");

        int avaiable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(DashboardBaseActivity.this);

        if(avaiable == ConnectionResult.SUCCESS){
            // Everything OK
            //Log.d(TAG, "isServicesOK: Google Play services are working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(avaiable)){
            // Fixable;
            Log.d(TAG, "Google Play services fixable error!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(DashboardBaseActivity.this, avaiable, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Google Play services error!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}