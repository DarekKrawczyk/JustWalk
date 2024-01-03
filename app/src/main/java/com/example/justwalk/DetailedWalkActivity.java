package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.justwalk.databinding.ActivityDetailedWalkBinding;

import java.text.DecimalFormat;
import java.util.List;

public class DetailedWalkActivity extends AppCompatActivity {

    ActivityDetailedWalkBinding _binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _binding = ActivityDetailedWalkBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        Intent intent = this.getIntent();
        if(intent != null){
            String Date = intent.getStringExtra("Date");
            String StartingTime = intent.getStringExtra("StartingTime");
            String EndTime = intent.getStringExtra("EndTime");
            String Duration = intent.getStringExtra("Duration");

            Double distDouble = intent.getDoubleExtra("Distance",0);
            distDouble = Math.round(distDouble * 100.0) / 100.0;

            String Distance = String.valueOf(distDouble) + "[m]";
            String Points = String.valueOf(intent.getIntExtra("Points", 0));
            String Steps = String.valueOf(intent.getIntExtra("Steps", 0));

            Double caloriesDouble = intent.getDoubleExtra("CaloriesBurned",0);
            caloriesDouble = Math.round(caloriesDouble * 100.0) / 100.0;

            String CaloriesBurned = String.valueOf(caloriesDouble);
            String Places = intent.getStringExtra("Places");

            Date = Utility.ExtractDate(Date);
            
            _binding.detailWalkDate.setText(Date);
            _binding.detailWalkStartingTime.setText(StartingTime);
            _binding.detailWalkEndTime.setText(EndTime);
            _binding.detailWalkDuration.setText(Duration);
            _binding.detailWalkDistance.setText(Distance);
            _binding.detailWalkPoints.setText(Points);
            _binding.detailWalkSteps.setText(Steps);
            _binding.detailWalkCalories.setText(CaloriesBurned);
            //_binding.detailWalkLocations.setText(Places);

            // Sample list of strings
            List<String> stringList = Utility.SplitString(Places);

            // Create an ArrayAdapter to display the list of strings
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringList);

            ListView listView = _binding.getRoot().findViewById(R.id.detailWalkLocations_);
            listView.setAdapter(adapter);

        }
    }
}