package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.justwalk.databinding.ActivityWalksBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WalksActivity extends DashboardBaseActivity {

    ActivityWalksBinding _activityBinding;
    WalkListAdapter _listAdapter;
    ArrayList<WalkModel> _walkArrayList = new ArrayList<>();
    ArrayList<Walk> _tempDBList = new ArrayList<>();
    WalkModel walk;
    private final String TAG = "WalksActivity";
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "HERER");
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityWalksBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Walks");
        /*
        Log.d(TAG, "HERER2");
        String[] Date={"18.12.2023", "20.12.2023"};
        String[] StartingTime={"17:30", "18:20"};
        String[] EndTime={"19:22", "20:00"};
        String[] Duration={"2:30", "1:40"};
        double[] Distance={2100, 3000};
        int[] Points={994, 1244};
        int[] Steps={2900, 4100};
        double[] CaloriesBurned={244, 190};
        String[] Places={"AEI * Rynek * Kawiarnia", "ZMITAC * Monopolowy * Ławeczka na rynku"};

        for(int i = 0; i < Date.length; i++){
            walk = new WalkModel(Date[i],StartingTime[i],EndTime[i],Duration[i],Distance[i],Points[i],Steps[i],CaloriesBurned[i],Places[i]);
            _walkArrayList.add(walk);
        }
         */

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("walks");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle data changes
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Walk walk = snapshot.getValue(Walk.class);

                    if (walk != null) {
                        // Add more fields as needed
                        _tempDBList.add(walk);
                    }
                }
                UpdateWalks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        _listAdapter = new WalkListAdapter(WalksActivity.this, _walkArrayList);
        _activityBinding.listview.setAdapter(_listAdapter);
        _activityBinding.listview.setClickable(true);

        _activityBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WalksActivity.this, DetailedWalkActivity.class);
                intent.putExtra("Date", _walkArrayList.get(position).Date);
                intent.putExtra("StartingTime", _walkArrayList.get(position).StartingTime);
                intent.putExtra("EndTime", _walkArrayList.get(position).EndTime);
                intent.putExtra("Distance", _walkArrayList.get(position).Distance);
                intent.putExtra("Duration", _walkArrayList.get(position).Duration);
                intent.putExtra("CaloriesBurned", _walkArrayList.get(position).CaloriesBurned);
                intent.putExtra("Steps", _walkArrayList.get(position).Steps);
                intent.putExtra("Points", _walkArrayList.get(position).Points);
                intent.putExtra("Places", _walkArrayList.get(position).Places);
                startActivity(intent);
            }
        });
    }

    private void UpdateWalks(){
        _walkArrayList.clear();
        for(int i = 0; i < _tempDBList.size(); i++){
            Walk tempWalk = _tempDBList.get(i);

            String date = Utility.ConvertTimestampToString(tempWalk.Timestamp);
            String startTime = Utility.ConvertTimestampToHoursAndMinutes(tempWalk.StartingTimeInMilis);
            String endTime = Utility.ConvertTimestampToHoursAndMinutes(tempWalk.EndingTimeInMilis);
            String duration = Utility.CalculateDuration(startTime, endTime);
            double distance = tempWalk.Distance;
            int points = tempWalk.Points;
            int steps = tempWalk.Steps;
            double calories = tempWalk.CaloriesBurned;
            String places = Utility.ParsePlacesToString(tempWalk.Places);
            WalkModel vm = new WalkModel(date, startTime, endTime, duration, distance, points, steps, calories, places);
            _walkArrayList.add(vm);
        }
        _listAdapter.notifyDataSetChanged();
        _tempDBList.clear();
    }
}