package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.justwalk.databinding.ActivityDashboardBinding;

public class DashboardActivity extends DashboardBaseActivity {

    ActivityDashboardBinding _activityBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Dashboard");
    }
}