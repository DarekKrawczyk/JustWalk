package com.example.justwalk;

import com.google.android.gms.maps.model.LatLng;

public class MapPoint {
    private String _name;
    private String _lat;
    private String _lng;
    private double _latDbl;
    private double _lngDbl;
    public MapPoint(String name, String lat, String lng){
        _name = name;
        _lat = lat;
        _lng = lng;
        _latDbl = Double.parseDouble(_lat);
        _lngDbl = Double.parseDouble(_lng);
    }

    public MapPoint(String name, double lat, double lng){
        _name = name;
        _lat = "";
        _lng = "";
        _latDbl = lat;
        _lngDbl = lng;
    }

    public LatLng GetLatLng(){
        return new LatLng(_latDbl, _lngDbl);
    }
    public String GetName(){
        return _name;
    }
}
