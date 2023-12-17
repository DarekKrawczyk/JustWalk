package com.example.justwalk;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapPoint {
    //private Marker _marker;
    private String _name;
    private String _lat;
    private String _lng;
    private double _latDbl;
    private double _lngDbl;
    public MapPoint(String name, String lat, String lng, Marker marker){
        _name = name;
        _lat = lat;
        _lng = lng;
        _latDbl = Double.parseDouble(_lat);
        _lngDbl = Double.parseDouble(_lng);
        //_marker = marker;
    }

    public MapPoint(String name, double lat, double lng, Marker marker){
        _name = name;
        _lat = "";
        _lng = "";
        _latDbl = lat;
        _lngDbl = lng;
        //_marker = marker;
    }

    /*
    public void SetMarker(Marker marker){
        _marker = marker;
    }
    public void RemoveMarker(){
        _marker.remove();
    }
    public Marker GetMarker(){
        return _marker;
    }*/
    public LatLng GetLatLng(){
        return new LatLng(_latDbl, _lngDbl);
    }
    public String GetName(){
        return _name;
    }
}
