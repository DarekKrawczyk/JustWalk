package com.example.justwalk;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;

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
        TextView listPoints = view.findViewById(R.id.listPoints);
        ImageView image = view.findViewById(R.id.listImage);

        listDate.setText(walk.Date);
        listPoints.setText(String.valueOf(walk.Points));
        image.setImageResource(walk.Image);

        return view;
    }
}
