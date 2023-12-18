package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.justwalk.databinding.ActivityDetailedWalkBinding;

public class DetailedWalkActivity extends AppCompatActivity {

    ActivityDetailedWalkBinding _binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityDetailedWalkBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        Intent intent = this.getIntent();
        if(intent != null){
            String Date = intent.getStringExtra("Date");
            String StartingTime = intent.getStringExtra("StartingTime");
            String EndTime = intent.getStringExtra("EndTime");
            String Duration = intent.getStringExtra("Duration");
            String Distance = String.valueOf(intent.getDoubleExtra("Distance",0));
            String Points = String.valueOf(intent.getIntExtra("Points", 0));
            String Steps = String.valueOf(intent.getIntExtra("Steps", 0));
            String CaloriesBurned = String.valueOf(intent.getDoubleExtra("CaloriesBurned", 0));
            String Places = intent.getStringExtra("Places");

            _binding.detailWalkDate.setText(Date);
            _binding.detailWalkStartingTime.setText(StartingTime);
            _binding.detailWalkEndTime.setText(EndTime);
            _binding.detailWalkDuration.setText(Duration);
            _binding.detailWalkDistance.setText(Distance);
            _binding.detailWalkPoints.setText(Points);
            _binding.detailWalkSteps.setText(Steps);
            _binding.detailWalkCalories.setText(CaloriesBurned);
            _binding.detailWalkLocations.setText(Places);

        }
    }
}