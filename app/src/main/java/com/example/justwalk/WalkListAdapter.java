package com.example.justwalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class WalkListAdapter extends ArrayAdapter<WalkModel> {
    public WalkListAdapter(@NonNull Context context, ArrayList<WalkModel> dataArrayList) {
        super(context, R.layout.walk_list_item, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        WalkModel walk = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.walk_list_item, parent, false);
        }
        TextView listDate = view.findViewById(R.id.listDate);

        listDate.setText(walk.Date);

        return view;
    }
}
