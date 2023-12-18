package com.example.justwalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.justwalk.databinding.ActivityWalksBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WalksActivity extends DashboardBaseActivity {

    ActivityWalksBinding _activityBinding;
    WalkListAdapter _listAdapter;
    ArrayList<WalkModel> _walkArrayList = new ArrayList<>();
    WalkModel walk;
    private final String TAG = "WalksActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "HERER");
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityWalksBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Walks");
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

        _listAdapter = new WalkListAdapter(WalksActivity.this, _walkArrayList);
        _activityBinding.listview.setAdapter(_listAdapter);
        _activityBinding.listview.setClickable(true);

        _activityBinding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WalksActivity.this, DetailedWalkActivity.class);
                intent.putExtra("Date", Date[position]);
                intent.putExtra("StartingTime", StartingTime[position]);
                intent.putExtra("EndTime", EndTime[position]);
                intent.putExtra("Distance", Distance[position]);
                intent.putExtra("Duration", Duration[position]);
                intent.putExtra("CaloriesBurned", CaloriesBurned[position]);
                intent.putExtra("Steps", Steps[position]);
                intent.putExtra("Points", Points[position]);
                intent.putExtra("Places", Places[position]);
                startActivity(intent);
            }
        });
    }
}