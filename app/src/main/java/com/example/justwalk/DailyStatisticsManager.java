package com.example.justwalk;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DailyStatisticsManager {

    private DatabaseReference dailyStatsRef;

    public DailyStatisticsManager() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dailyStatsRef = FirebaseDatabase.getInstance().getReference().child("DailyStatistics").child(uid);
        }
    }

    public void getAllDailyStatistics(final OnDailyStatisticsListListener listener) {
        if (dailyStatsRef == null) {
            // Handle the case where the user is not authenticated
            return;
        }

        dailyStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DailyStatistics> dailyStatisticsList = new ArrayList<>();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    DailyStatistics dailyStats = dateSnapshot.getValue(DailyStatistics.class);
                    if (dailyStats != null) {
                        dailyStatisticsList.add(dailyStats);
                    }
                }

                if (listener != null) {
                    listener.onDailyStatisticsList(dailyStatisticsList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public interface OnDailyStatisticsListListener {
        void onDailyStatisticsList(List<DailyStatistics> dailyStatisticsList);
    }
}