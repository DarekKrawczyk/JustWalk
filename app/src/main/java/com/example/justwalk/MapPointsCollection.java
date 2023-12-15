package com.example.justwalk;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.List;

public class MapPointsCollection {
    private GoogleMap _googleMap;
    private List<Marker> _markers;
    private MapPoint _startingMapPoint;
    private List<MapPoint> _mapPoints;
    private List<DirectionsResult> _directionResults;
    private boolean _isReady = false;
    private boolean _isDataFetched = false;
    private int _mpIterator =0;
    private DirectionsResultListener dirsOkListner;
    private DataFetchedResultListener _dataFetchedListner;
    public MapPointsCollection(GoogleMap googleMap,MapPoint startingMapPoint, List<MapPoint> mapPoints){
        _googleMap = googleMap;
        _startingMapPoint = startingMapPoint;
        _mapPoints = mapPoints;
        _markers = new ArrayList<>();
        _directionResults = new ArrayList<>();
    }
    public void setDirectionsResultListener(DirectionsResultListener listener) {
        this.dirsOkListner = listener;
    }
    public void setFetchResultListener(DataFetchedResultListener listener) {
        this._dataFetchedListner = listener;
    }
    public List<MapPoint> GetMapPoints(){
        if(_mapPoints != null && _mapPoints.size() > 0){
            _mapPoints.add(0, _startingMapPoint);
            return _mapPoints;
        }
        return null;
    }
    public void AddDirectionResult(DirectionsResult directionsResult){
        _directionResults.add(directionsResult);
        if(_directionResults.size() == _mapPoints.size()){
            SetReady(true);
        }
        SetReady(false);
    }
    public List<DirectionsResult> GetDirResults(){
        return _directionResults;
    }
    public void NormalizedResults(){
        DirectionsResult roadHome = _directionResults.get(_directionResults.size() - 1);
        List<DirectionsResult> road = new ArrayList<>();
        for(int i = _directionResults.size() - 2; i >= 0; i--){
            road.add(_directionResults.get(i));
        }
        road.add(roadHome);
        _directionResults = road;
    }
    public boolean AreDirResultsOK(){
        if(_directionResults.size() == _mapPoints.size()){
            return true;
        }
        return false;
    }
    public MapPointsCollection(GoogleMap googleMap){
        _googleMap = googleMap;
        _startingMapPoint = new MapPoint("DEFAULT", "0","0");
        _mapPoints = new ArrayList<>();
        _directionResults = new ArrayList<>();
        _markers = new ArrayList<>();
    }
    public GoogleMap GetGoogleMap(){
        return _googleMap;
    }
    public void SetStartingPoint(MapPoint mp){
        _startingMapPoint = mp;
    }
    public void AddMapPoint(MapPoint mp){
        _mapPoints.add(mp);
    }
    public List<MapPoint> ReturnAllMapPoints(){
        return _mapPoints;
    }

    public void IncrementMapPointIterator(){
        _mpIterator++;
    }
    public void ClearMapPointIterator(){
        _mpIterator=0;
    }
    public boolean LastMapPointPassed(){
        if(_mpIterator == _mapPoints.size()-1){
            return true;
        }
        else return false;
    }

    public MapPoint GetReturnStartingMapPoint(){
        return _startingMapPoint;
    }

    public MapPoint GetStartingMapPoint(){
        if(_mpIterator < _mapPoints.size()){
            return _mapPoints.get(_mpIterator);
        }
        else{
            return null;
        }
    }

    public MapPoint GetNextMapPoint(){
        if((_mpIterator + 1) < _mapPoints.size()){
            return _mapPoints.get(_mpIterator + 1);
        }
        else{
            return null;
        }
    }

    public void SetReady(boolean ready){
        _isReady = ready;
        dirsOkListner.onDirectionsResultChanged(_isReady);
    }
    public boolean IsReady(){
        return _isReady;
    }
    public void SetDataFetchedReady(boolean ready){
        _isDataFetched = ready;
        _dataFetchedListner.onDatFetchedResultChanged(_isDataFetched);
    }
    public boolean IsDataFetched(){
        return _isDataFetched;
    }
    public void CalculateBestPath(){
        List<MapPoint> newMapPoints = SortMapPoints(_mapPoints, _startingMapPoint);
        _mapPoints = newMapPoints;
    }

    public List<Marker> GerMarker(){
        return _markers;
    }

    public void RemoveMarkerAt(int index){
        int counter = 0;
        for(Marker mark : _markers){
            if(counter == index){
                mark.remove();
                break;
            }
            counter++;
        }
        _markers.remove(index);
    }

    public Marker GetMarkerFromIndex(int index){
        return _markers.get(index);
    }

    public int HasArrivedAtMarker(LatLng userPosition, double distanceCap){
        // If user has arrived at marker return index of that marker, otherwise return -1
        for(int i = 0; i<_markers.size() - 1; i++){
            LatLng markerPos = _markers.get(i).getPosition();
            double distance = MapPointsCollection.CalculateDistance(userPosition, markerPos);
            if(distance < distanceCap){
                return i;
            }
        }
        return -1;
    }

    public void SetMapMarkers(){
        // Starting map point
        MarkerOptions startingMapPointmarkerOptions = new MarkerOptions();
        startingMapPointmarkerOptions.title(_startingMapPoint.GetName());
        startingMapPointmarkerOptions.position(_startingMapPoint.GetLatLng());
        startingMapPointmarkerOptions.snippet(String.valueOf(0));
        Marker startMarker = _googleMap.addMarker(startingMapPointmarkerOptions);
        _markers.add(startMarker);
        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(_startingMapPoint.GetLatLng(), 15));

        for(int i = 0; i < _mapPoints.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(_mapPoints.get(i).GetName());
            markerOptions.position(_mapPoints.get(i).GetLatLng());
            markerOptions.snippet(String.valueOf(i+1));
            Marker newMarker = _googleMap.addMarker(markerOptions);
            _markers.add(newMarker);
            _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(_mapPoints.get(i).GetLatLng(), 15));
        }
    }

    public int CalculatePoints(int index){
        double distance = MapPointsCollection.CalculateDistance(_startingMapPoint.GetLatLng(), _markers.get(index).getPosition());
        int points = (int)(distance * 1000);
        return points;
    }

    public MapPoint GetFirstMapPoint(){
        if(_mapPoints != null && _mapPoints.size() > 0){
            return _mapPoints.get(0);
        }
        return null;
    }

    public static List<MapPoint> SortMapPoints(List<MapPoint> mapPoints, MapPoint startPoint) {
        List<MapPoint> sortedList = new ArrayList<>();
        List<MapPoint> unvisitedPoints = new ArrayList<>(mapPoints);
        MapPoint currentPoint = startPoint;

        while (!unvisitedPoints.isEmpty()) {
            // Find the closest unvisited point to the current point
            MapPoint closestPoint = FindClosestPoint(currentPoint, unvisitedPoints);

            // Add the closest point to the sorted list
            sortedList.add(closestPoint);

            // Update the current point for the next iteration
            currentPoint = closestPoint;

            // Remove the visited point from the unvisited list
            unvisitedPoints.remove(closestPoint);
        }

        // Because we want co come back
        sortedList.add(startPoint);
        return sortedList;
    }

    private static MapPoint FindClosestPoint(MapPoint referencePoint, List<MapPoint> points) {
        // Find the closest point using a simple Euclidean distance formula
        MapPoint closestPoint = null;
        double minDistance = Double.MAX_VALUE;

        for (MapPoint point : points) {
            double distance = CalculateDistance(referencePoint, point);
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = point;
            }
        }

        return closestPoint;
    }

    private static double CalculateDistance(MapPoint point1, MapPoint point2) {
        // Use your preferred formula to calculate the distance between two points
        // For simplicity, let's assume a simple Euclidean distance here
        double latDiff = point1.GetLatLng().latitude - point2.GetLatLng().latitude;
        double lngDiff = point1.GetLatLng().longitude- point2.GetLatLng().longitude;
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff);
    }
    private static double CalculateDistance(LatLng point1, LatLng point2) {
        // Use your preferred formula to calculate the distance between two points
        // For simplicity, let's assume a simple Euclidean distance here
        double latDiff = point1.latitude - point2.latitude;
        double lngDiff = point1.longitude- point2.longitude;
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff);
    }
    public void LimitPlacesTo(int size) {
        if(_mapPoints != null && _mapPoints.size() > size){
            while(_mapPoints.size() > size){
                _mapPoints.remove(_mapPoints.size()-1);
            }
            _mapPoints.add(_startingMapPoint);
        }
    }

    public void ClearData() {
        // Remove markers
        for(Marker marker : _markers){
            marker.remove();
        }

        _isReady = false;
        _isDataFetched = false;
        _startingMapPoint = new MapPoint("DEFAULT", "0","0");
        _mapPoints = new ArrayList<>();
        _directionResults = new ArrayList<>();

    }

}

