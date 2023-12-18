package com.example.justwalk;

import java.util.Map;

public class Place {
    public String Name;
    public double Latitude;
    public double Longitude;
    public String Description;
    public Map<String, String> Timestamp;

    public Place(String name, Double latitude, Double longitude, String description, Map<String, String> timestamp){
        Name = name;
        Latitude = latitude;
        Longitude = longitude;
        Description = description;
        Timestamp = timestamp;
    }
}
