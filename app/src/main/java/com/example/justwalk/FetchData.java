package com.example.justwalk;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchData extends AsyncTask<Object,String,String> {
    String googleNearByPlacesData;
    GoogleMap googleMap;
    String url;
    MapPointsCollection mapPoints;

    @Override
    protected String doInBackground(Object... objects) {
        try{
            mapPoints = (MapPointsCollection) objects[0];
            googleMap = mapPoints.GetGoogleMap();
            url = (String) objects[1];
            DownloadURL downloadURL = new DownloadURL();
            googleNearByPlacesData = downloadURL.retrieveURL(url);
        } catch (IOException e){
            e.printStackTrace();
        }

        return googleNearByPlacesData;
    }

    @Override
    protected void onPostExecute(String s){
        try{
            JSONObject jsonObject  = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                JSONObject getLocation = jsonObj.getJSONObject("geometry").getJSONObject("location");
                String lat = getLocation.getString("lat");
                String lng = getLocation.getString("lng");

                JSONObject getName = jsonArray.getJSONObject(i);
                String name = getName.getString("name");

                MapPoint mp = new MapPoint(name, lat, lng);
                mapPoints.AddMapPoint(mp);

                /*LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latLng);
                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));*/

            }
            //mapPoints.LimitPlacesTo(3);
            //mapPoints.SetMapMarkers();
            //mapPoints.SetReady(true);
            mapPoints.SetDataFetchedReady(true);


        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
