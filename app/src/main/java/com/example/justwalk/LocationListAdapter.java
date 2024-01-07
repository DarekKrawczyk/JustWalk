package com.example.justwalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LocationListAdapter extends ArrayAdapter<WalkModel> {
    public LocationListAdapter(@NonNull Context context, ArrayList<WalkModel> dataArrayList) {
        super(context, R.layout.walk_list_item, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        WalkModel walk = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.location_item, parent, false);
        }
        TextView dayName = view.findViewById(R.id.LocationDayName);
        TextView dayDate = view.findViewById(R.id.LocationDayDate);
        ListView LocationLocations = view.findViewById(R.id.LocationLocations);

        List<String> stringList = Utility.SplitString(walk.Places);

        String Date = Utility.ExtractDate(walk.Date);

        dayDate.setText(Date);

        LocalDate givenDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            givenDate = LocalDate.parse(Date, DateTimeFormatter.ISO_LOCAL_DATE);

            LocalDate currentDate = LocalDate.now();

            if (givenDate.isEqual(currentDate)) {
                dayName.setText("Today");
            }
            else if (givenDate.isEqual(currentDate.minusDays(1))) {
                dayName.setText("Yesterday");
            } else {
                LocalDate date = LocalDate.parse(Date, DateTimeFormatter.ISO_LOCAL_DATE);

                dayName.setText(Utility.GetDayName(date));
            }
        }
        else dayName.setText("Day");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, stringList);

        LocationLocations.setAdapter(adapter);

        return view;
    }
}