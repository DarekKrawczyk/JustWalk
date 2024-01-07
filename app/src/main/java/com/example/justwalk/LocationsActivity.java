package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.justwalk.databinding.ActivityLocationsBinding;
import com.example.justwalk.databinding.ActivityWalksBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationsActivity extends DashboardBaseActivity{

    ActivityLocationsBinding _activityBinding;
    LocationListAdapter _listAdapter;
    ArrayList<WalkModel> _walkArrayList = new ArrayList<>();
    ArrayList<Walk> _tempDBList = new ArrayList<>();
    WalkModel walk;
    private final String TAG = "LocationsActivity";
    FirebaseDatabase database;
    DatabaseReference databaseRef;
    ProgressBar loadingProgressBar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "HERER");
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityLocationsBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Locations");

        loadingProgressBar = _activityBinding.getRoot().findViewById(R.id.LocationsProgressBar);
        listView = _activityBinding.getRoot().findViewById(R.id.LocationsListView);

        listView.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("walks");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String currentUserID = user.getUid();
        databaseRef.orderByChild("UserID").equalTo(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Walk walk = snapshot.getValue(Walk.class);

                            if (walk != null) {
                                _tempDBList.add(walk);
                            }
                        }
                        UpdateWalks();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        _listAdapter = new LocationListAdapter(LocationsActivity.this, _walkArrayList);
        _activityBinding.LocationsListView.setAdapter(_listAdapter);

    }

    private void UpdateWalks(){
        _walkArrayList.clear();
        for(int i = _tempDBList.size()-1; i >= 0; i--){
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

            if(places == null || places.isEmpty()) continue;



            int image = 0;

            WalkModel vm = new WalkModel(date, startTime, endTime, duration, distance, points, steps, calories, places, image);
            _walkArrayList.add(vm);
        }
        _listAdapter.notifyDataSetChanged();
        _tempDBList.clear();

        listView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.INVISIBLE);
    }
}