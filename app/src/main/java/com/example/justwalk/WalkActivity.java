package com.example.justwalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justwalk.databinding.ActivityDashboardBinding;
import com.example.justwalk.databinding.ActivityWalkBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class WalkActivity extends DashboardBaseActivity implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        DirectionsResultListener,
        DataFetchedResultListener,LocationUpdateListener {

    private LocationBroadcastReceiver locationReceiver;
    // TIME COUNTING
    Intent locationServiceIntent;
    private float CurrentZoom = -1;
    private Button startButton;
    private Button stopButton;
    private Button pauseButton;
    private TextView PointsTV;
    private TextView timerTextView;
    private TextView distanceTextView;
    private boolean _isNavigating = false;
    private int USER_POINTS = 0;

    private boolean isTrackingWalking = false;
    //HEADER
    private Switch AssistantSwitch;
    private Button NavigateButton;
    private boolean firstRun;
    private Button CancelButton;
    //POLULINE
    private Polyline SINGLE_ASSISTANT_POLYLINE;
    private List<Polyline> ASSISTANT_POLYLINES;
    private List<Polyline> WALK_POLYLINES;
    private Location previousLocation;
    private List<LatLng> polylinePoints;
    private Polyline polyline;
    //END POLTLINE
    private long _walkStartTime = 0;
    private long _endWalkingTime = 0;
    private double DISTANCE_FROM_MARKER_CAP = 0.0001;
    private final int TimerDistanceMeasurementLimit = 5;
    private int CurrentTimerSecs = 0;
    private int PrevSeconds = 0;
    private double _totalDistance;
    private boolean isTimerRunning = false;
    private boolean isTimerPaused = false;
    private long startTimeInMillis;
    private long pausedTimeInMillis;
    private Handler handler;
    private Runnable runnable;
    // END
    private final LatLng defaultLocation = new LatLng(-20, 170);
    private Location lastKnownLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap _googleMap;
    private PlacesClient placesClient;
    private FusedLocationProviderClient _location;
    private boolean _locationPermissionGranted;
    private static final String TAG = "WalkActivity";
    private MapPointsCollection _mapPoints;
    private GeoApiContext _geoApiContext = null;
    private ActivityWalkBinding _activityBinding;
    private com.example.justwalk.databinding.ActivityDashboardBinding activityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _activityBinding = ActivityWalkBinding.inflate(getLayoutInflater());
        setContentView(_activityBinding.getRoot());
        allocateActivityTitle("Walk");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.Map, mapFragment)
                .commit();


        if(mapFragment == null){
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        }
        mapFragment.getMapAsync(this);
        //InitMap();
        polylinePoints = new ArrayList<>();
        ASSISTANT_POLYLINES = new ArrayList<>();
        WALK_POLYLINES = new ArrayList<>();
        PrevSeconds = 0;
        locationReceiver = new LocationBroadcastReceiver(this);
        _totalDistance = 0;
        CurrentTimerSecs = 0;
        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.GOOGLE_MAPS_API_KEY));
        placesClient = Places.createClient(this);

        /*
        _mapPoints = new MapPointsCollection(_googleMap);
        _mapPoints.setDirectionsResultListener(this);
        _mapPoints.setFetchResultListener(this);
*/
        // Construct a FusedLocationProviderClient.
        _location = LocationServices.getFusedLocationProviderClient(this);

        //Buttons -> Start
        startButton = _activityBinding.BtnStart;
        stopButton = _activityBinding.getRoot().findViewById(R.id.BtnStop);
        pauseButton = _activityBinding.getRoot().findViewById(R.id.BtnPause);
        timerTextView = _activityBinding.getRoot().findViewById(R.id.TvTime);
        distanceTextView = _activityBinding.getRoot().findViewById(R.id.TvDistance);
        PointsTV = _activityBinding.getRoot().findViewById(R.id.WalkingActPointsTV);

        if(startButton == null){
            Toast.makeText(this, "BUTTON NULL", Toast.LENGTH_SHORT).show();
        }

        AssistantSwitch = _activityBinding.getRoot().findViewById(R.id.SwthEnabled);
        CancelButton = _activityBinding.getRoot().findViewById(R.id.BtnCancel);
        NavigateButton = _activityBinding.getRoot().findViewById(R.id.BtnNavigate);

        // Set initial state
        UpdateAssistanUI();

        // Add a listener to the switch to handle changes
        AssistantSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update button state when the switch state changes
                NavigateButton.setEnabled(isChecked);
                if(isChecked == true){

                } else{
                    if(_mapPoints != null){
                        ClearAssistantWalkData();
                    }
                    _isNavigating = false;
                }
            }
        });

        NavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_isNavigating == false){
                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    sb.append("location=" + lastKnownLocation.getLatitude() + ","+lastKnownLocation.getLongitude());
                    sb.append("&radius=2000");
                    sb.append("&type=park");
                    sb.append("&mode=transit");
                    sb.append("&key=" + getResources().getString(R.string.GOOGLE_MAPS_API_KEY));

                    String url  = sb.toString();


                    // Add starting point
                    MapPoint startingMP = new MapPoint("STARTING", lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), null);
                    _mapPoints.SetStartingPoint(startingMP);

                    Object dataFetch[] = new Object[2];
                    dataFetch[0] = _mapPoints;
                    dataFetch[1] = url;
                    //dataFetch[2] = _mapPoints;


                    // Define an Executor (you can use AsyncTask.THREAD_POOL_EXECUTOR as a simple replacement)
                    Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;

                    // Execute the background task
                    executor.execute(() -> {
                        // Your background task code goes here

                        // For example, if you want to perform network operations, you might do something like this:
                        FetchData fData = new FetchData();
                        fData.execute(dataFetch);
                    });
                    _isNavigating = true;
                }
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuitDialog();
            }
        });

        handler = new Handler();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    resumeTimer();
                } else {
                    startTimer();
                }
                startLocationService();
                registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION_UPDATE));
                isTrackingWalking = true;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStopTimerDialog();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        if(_geoApiContext == null){
            _geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.GOOGLE_MAPS_API_KEY)).build();
        }

        //startLocationService();
        startLocationService();
        registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION_UPDATE));
    }

    public void endWalk(){
        // TODO: Save data in database and quit activity to main screen

        /*
        Map map = new HashMap();
        map.put("timestamp", ServerValue.TIMESTAMP);
         */
        long time = System.currentTimeMillis();
        double strideLength = 0.7;
        double weight = 80;
        double METvalue = 4.5; // Moderate walk
        double minutes = Utility.ConvertToTotalSeconds(timerTextView.getText().toString());

        double distanceKM = _totalDistance/1000;
        int steps = Utility.CalculateSteps(_totalDistance, strideLength);
        double calories = Utility.CalculateCaloriesBurned(weight, distanceKM, METvalue);

        long startingWalkTime = _walkStartTime;
        long endingWalkTime = _endWalkingTime;

        long durationTimeInSecs = 0;

        List<Place> places = _mapPoints.exportVisitedPlaces();

        Walk walkToSave = new Walk(time, startingWalkTime, time, durationTimeInSecs, _totalDistance, steps, USER_POINTS, calories, places);

        try{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference walksRef = database.getReference("walks");

            String walkId = walksRef.push().getKey();
            walksRef.child(walkId).setValue(walkToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "DATA ADDED TO DB");
                            } else {
                                Log.d(TAG, "DB ERROR");
                            }
                        }
                    });

        } catch (Exception ex){
            Log.d(TAG, "DB EXCEPTION");
        }


        isTrackingWalking = false;
        handler.removeCallbacks(runnable);
        timerTextView.setText("00:00:000");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);
        isTimerRunning = false;
        isTimerPaused = false;
        PrevSeconds = 0;
        CurrentTimerSecs = 0;
        _totalDistance = 0;
        distanceTextView.setText("0[m]");
        clearPolyline();

        try{
            unregisterReceiver(locationReceiver);
        } catch (Exception ex){

        }
        try{
            StopLocationService();
        } catch (Exception ex){

        }

        Intent intent = new Intent(WalkActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void resumeTimer() {
        pausedTimeInMillis = 0;
        startTimer();
    }
    private void UpdateAssistanUI() {
        // Enable or disable buttons based on the switch state
        boolean switchState = AssistantSwitch.isChecked();
        NavigateButton.setEnabled(switchState);
    }
    public void ClearAssistantWalkData(){
        // Clear Data
        _mapPoints.ClearData();
        ClearAssistantPolylines();
    }
    public void ClearAssistantPolylines(){
        for(Polyline poli : ASSISTANT_POLYLINES){
            poli.remove();
        }
        ASSISTANT_POLYLINES.clear();
    }
    private void calculateTotalDistance() {
        if (polylinePoints.size() >= 2) {
            double totalDistance = 0;
            for (int i = 1; i < polylinePoints.size(); i++) {
                LatLng startPoint = polylinePoints.get(i - 1);
                LatLng endPoint = polylinePoints.get(i);

                Location startLocation = new Location("start");
                startLocation.setLatitude(startPoint.latitude);
                startLocation.setLongitude(startPoint.longitude);

                Location endLocation = new Location("end");
                endLocation.setLatitude(endPoint.latitude);
                endLocation.setLongitude(endPoint.longitude);

                totalDistance += startLocation.distanceTo(endLocation);
            }
            _totalDistance = totalDistance;

            // Display total distance (you can format it as needed)
            // You can use distanceText as needed (e.g., display it in a TextView)
        }
    }
    private void UpdateTotalDistanceUI(){
        calculateTotalDistance();
        DecimalFormat df = new DecimalFormat("#.##");
        String distanceText = df.format(_totalDistance) + "[m]";
        distanceTextView.setText(distanceText);

        String pointsString = String.format("Points: %01d", USER_POINTS);
        PointsTV.setText(pointsString);

    }
    private void startTimer() {
        getDeviceLocation();
        previousLocation = lastKnownLocation;

        if (!isTimerRunning) {
            if (isTimerPaused) {
                startTimeInMillis = System.currentTimeMillis() - pausedTimeInMillis;
            } else {
                startTimeInMillis = System.currentTimeMillis();
                if(_walkStartTime == 0){
                    _walkStartTime = System.currentTimeMillis();
                }
            }

            // TIMER - this is responsible for counting time of current walk.
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isTimerPaused) {
                        long currentTimeInMillis = System.currentTimeMillis();
                        long elapsedTimeInMillis = currentTimeInMillis - startTimeInMillis;

                        int minutes = (int) (elapsedTimeInMillis / (60 * 1000));
                        int seconds = (int) ((elapsedTimeInMillis % (60 * 1000)) / 1000);
                        int milliseconds = (int) (elapsedTimeInMillis % 1000);

                        int secs = ((minutes * 60) + seconds);

                        String timeString = String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
                        timerTextView.setText(timeString);
                    }

                    handler.postDelayed(this, 10); // Update every 10 milliseconds
                }
            };

            handler.postDelayed(runnable, 10); // Start the timer
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(true);
            isTimerRunning = true;
            isTimerPaused = false;
        }
    }


    public void FinishAssistantWalk(){
        // TODO: IMPLEMENT THIS
        Toast.makeText(this, "You have gathered: "+USER_POINTS+" points!", Toast.LENGTH_SHORT).show();

        _isNavigating = false;
        // CLEAR POLYLINES AFTER WALK WITH ASSISTANT
        ClearAssistantWalkData();
    }

    public void FinishNormalWalk(){
        // TODO: IMPLEMENT
        ClearAssistantWalkData();
    }

    private void AddRouteToMap(final DirectionsRoute route, int color){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + route);

                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    PolylineOptions options = new PolylineOptions()
                            .width(25)
                            .color(Color.BLUE)
                            .geodesic(true)
                            .clickable(true)
                            .visible(true);

                    List<PatternItem> pattern;

                    pattern = Arrays.asList(new Dot(), new Gap(10));
                    options.jointType(JointType.ROUND);
                    options.pattern(pattern);
                    options.addAll(newDecodedPath);

                    Polyline polyline = _googleMap.addPolyline(options);
                    polyline.setColor(color);
                    polyline.setClickable(true);
                    ASSISTANT_POLYLINES.add(polyline);
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result, int color){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    PolylineOptions options = new PolylineOptions()
                            .width(25)
                            .color(Color.BLUE)
                            .geodesic(true)
                            .clickable(true)
                            .visible(true);

                    List<PatternItem> pattern;

                    pattern = Arrays.asList(new Dot(), new Gap(10));
                    options.jointType(JointType.ROUND);
                    options.pattern(pattern);
                    options.addAll(newDecodedPath);

                    Polyline polyline = _googleMap.addPolyline(options);
                    polyline.setColor(color);
                    polyline.setClickable(true);
                    ASSISTANT_POLYLINES.add(polyline);
                }
            }
        });
    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit dialog");
        builder.setMessage("Do you want to end your walk?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endWalk();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showStopTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop Timer");
        builder.setMessage("Do you want to stop the timer?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopTimer();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void stopTimer() {
        _endWalkingTime = System.currentTimeMillis();
        isTrackingWalking = false;
        unregisterReceiver(locationReceiver);
        handler.removeCallbacks(runnable);
        timerTextView.setText("00:00:000");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);
        isTimerRunning = false;
        isTimerPaused = false;
        PrevSeconds = 0;
        CurrentTimerSecs = 0;
        _totalDistance = 0;
        distanceTextView.setText("0[m]");
        clearPolyline();
        StopLocationService();
    }

    private void pauseTimer() {
        if (isTimerRunning && !isTimerPaused) {
            isTrackingWalking = true;
            handler.removeCallbacks(runnable);
            pausedTimeInMillis = System.currentTimeMillis() - startTimeInMillis;
            startButton.setEnabled(true);
            stopButton.setEnabled(true);
            pauseButton.setEnabled(false);
            isTimerRunning = false;
            isTimerPaused = true;
            unregisterReceiver(locationReceiver);
        }
    }

    private void ClearWalkPolylines(){
        for(Polyline poli : WALK_POLYLINES){
            poli.remove();
        }
        WALK_POLYLINES.clear();
    }
    private void DrawPolylineTo(LatLng location) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(location)
                .color(Color.RED)
                .width(10);
        polylineOptions.add(location);

        Polyline polyline = _googleMap.addPolyline(polylineOptions);
        WALK_POLYLINES.add(polyline);
    }

    private void drawPolyline() {
        // Clear previous polylines
        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(polylinePoints)
                .color(Color.RED)
                .width(10);

        polyline = _googleMap.addPolyline(polylineOptions);
    }

    private void clearPolyline() {
        // Clear all polylines on the map
        if (polyline != null) {
            polyline.remove();
            polylinePoints.clear();
        }
    }

    private void calculateDirections(List<MapPoint> destinations) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng originLatLng = new com.google.maps.model.LatLng(
                lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude()
        );

        com.google.maps.model.LatLng previousPoint = new com.google.maps.model.LatLng(originLatLng.lat, originLatLng.lng);

        for (MapPoint mp : destinations) {
            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    mp.GetLatLng().latitude,
                    mp.GetLatLng().longitude
            );

            DirectionsApiRequest directions = new DirectionsApiRequest(_geoApiContext);
            directions.alternatives(false);
            directions.origin(previousPoint);
            Log.d(TAG, "calculateDirections: destination: " + destination.toString());
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    _mapPoints.AddDirectionResult(result);
                    String Routes = result.routes[0].toString();
                    String duration = result.routes[0].legs[0].duration.toString();
                    String distance = result.routes[0].legs[0].distance.toString();
                    String geocodedWayPoints = result.geocodedWaypoints[0].toString();
                    Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                    Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                    Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                    Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
                }
            });
            previousPoint = new com.google.maps.model.LatLng(mp.GetLatLng().latitude, mp.GetLatLng().longitude);
        }
    }

    private void GetLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        Log.d(TAG, "GetLocationPermission: run method");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            _locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (_googleMap == null) {
            return;
        }
        try {
            if (_locationPermissionGranted) {
                _googleMap.setMyLocationEnabled(true);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                _googleMap.setMyLocationEnabled(false);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                GetLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        _locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                _locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _googleMap = googleMap;

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        _googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Nullable
            @Override
            public View getInfoContents(@NonNull Marker marker) {
                return null;
            }

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

        });

        _googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Update the local variable with the current camera zoom
                CurrentZoom = _googleMap.getCameraPosition().zoom;
            }
        });
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        GetLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        _mapPoints = new MapPointsCollection(_googleMap);
        _mapPoints.setDirectionsResultListener(this);
        _mapPoints.setFetchResultListener(this);

        _googleMap.setOnPolylineClickListener(this);
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (_locationPermissionGranted) {
                Task<Location> locationResult = _location.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), CurrentZoom));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            _googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, CurrentZoom));
                            _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            locationServiceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                WalkActivity.this.startForegroundService(locationServiceIntent);
            }else{
                startService(locationServiceIntent);
            }
        }
    }

    private void StopLocationService(){
        if(locationServiceIntent != null){
            stopService(locationServiceIntent);
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.codingwithmitch.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    public void getWalkingDirections(GeoApiContext geo, LatLng origin, LatLng destination, int color) {
        new Handler(Looper.getMainLooper()).post(() -> {
        GeoApiContext geoApiContext = geo;

        DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.WALKING)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        if (result.routes != null && result.routes.length > 0) {
                            //Toast.makeText(WalkActivity.this, "DATA RRREDY", Toast.LENGTH_SHORT).show();
                            addPolylinesToMap(result, color);
                            Log.e(TAG, "Directions drawn: ");
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        //Toast.makeText(WalkActivity.this, "DATA ERROR", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Directions request failed: " + e.getMessage());
                    }
                });
        });
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        polyline.setColor(Color.BLUE);
    }

    @Override
    public void onDirectionsResultChanged(boolean areDirectionsOK) {
        if (areDirectionsOK == true) {
            //Toast.makeText(this, "DIRECTIONS READY", Toast.LENGTH_SHORT).show();
            //boolean areok = _mapPoints.AreDirResultsOK();
            boolean areok = true;
            if(areok == true){

            /*
            MapPoint startingMP = new MapPoint("STARTING", lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            _mapPoints.SetStartingPoint(startingMP);
            _mapPoints.CalculateBestPath();
            */

                // TODO: TTUAJ
                //LatLng one = _mapPoints.GetMapPoints().get(0).GetLatLng();
                //LatLng twa = _mapPoints.GetMapPoints().get(3).GetLatLng();
                //Toast.makeText(this, "FIRST SETTING", Toast.LENGTH_SHORT).show();
                //LatLng firstMP = _mapPoints.GetStartingMapPoint().GetLatLng();
                //LatLng secondMP = _mapPoints.GetDestinationMarker().getPosition();

                //getWalkingDirections(_geoApiContext, firstMP, secondMP, Color.RED);

                /*
                _mapPoints.NormalizedResults();
                List<DirectionsResult> dr = _mapPoints.GetDirResults();
                AddRouteToMap(dr.get(1).routes[0], Color.GRAY);
                Toast.makeText(this, String.valueOf(dr.get(1).routes.length), Toast.LENGTH_SHORT).show();
                for(int i = 0; i < dr.size() - 1; i++){

                    LatLng one = _mapPoints.GetMapPoints().get(i).GetLatLng();
                    LatLng twa = _mapPoints.GetMapPoints().get(i+1).GetLatLng();
                    getWalkingDirections(_geoApiContext,one, twa, Color.RED);

                    //if(i == 0) addPolylinesToMap(dr.get(i), Color.RED);
                    //else addPolylinesToMap(dr.get(i), Color.GRAY);

                    //addPolylinesToMap(dr.get(i), Color.GRAY); BYLO
                    //AddRouteToMap(dr.get(i).routes[0], Color.GRAY);
                }
                */
            }
        }
    }

    // This gets triggered whenever data is fetched.
    @Override
    public void onDatFetchedResultChanged(boolean isDataFetched) {
        if(isDataFetched == true){
            _mapPoints.CalculateBestPath();
            _mapPoints.LimitPlacesTo(3);
            MapPoint mp = _mapPoints.GetFirstMapPoint();
            if(mp != null){
                calculateDirections(_mapPoints.GetMapPoints());
            }

            _mapPoints.ClearMapPointIterator();
            _mapPoints.DeleteMarkers();
            _mapPoints.SetMapMarkers();
            LatLng firstMP = _mapPoints.GetStartingMapPoint().GetLatLng();
            LatLng secondMP = _mapPoints.GetDestinationMarker().getPosition();

            if(firstMP == null || secondMP == null){
                Log.d(TAG, "onDatFetchedResultChanged() -> nulls");
            } else{
                getWalkingDirections(_geoApiContext, firstMP, secondMP, Color.RED);
            }
        }
    }

    // This updates whenever location is updated from background service.
    @Override
    public void onLocationUpdate(double latitude, double longitude) {
        //Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();

        // Get location and update it
        LatLng currentLatLng = new LatLng(latitude, longitude);
        if(lastKnownLocation != null){
            lastKnownLocation.setLatitude(currentLatLng.latitude);
            lastKnownLocation.setLongitude(currentLatLng.longitude);
        }

        // Draw polylines if user is tracking walking!
        if(isTrackingWalking == true){
            polylinePoints.add(currentLatLng);
            drawPolyline();
        }

        // Is navigation assistan turned on
        if(_isNavigating == true){
            // If result is != -1 then user has hit something!
            boolean index = _mapPoints.HasArrived(currentLatLng, DISTANCE_FROM_MARKER_CAP);
            if(index == true) {
                    // USER HAS ARRIVED!
                    Marker hitMarker = _mapPoints.GetDestinationMarker();
                    String arrivedPlaceName = hitMarker.getTitle();
                    LatLng markerLatLng = hitMarker.getPosition();


                    int points = _mapPoints.CalculatePoints(currentLatLng, hitMarker.getPosition());

                    _mapPoints.DeleteMarkers();

                    ClearAssistantPolylines();
                    if(arrivedPlaceName.contains("STARTING")){
                        // TODO: HANDLE ENERING STARTING POSITION
                        points += 100;
                        Toast.makeText(WalkActivity.this, "YOU HAVE FINISHED! Points: " + points, Toast.LENGTH_SHORT).show();
                        _isNavigating = false;
                        //_mapPoints.RemoveMarkerAt(index);
                    } else {
                        Toast.makeText(WalkActivity.this, "YOU HAVE ARRIVED: " + arrivedPlaceName + ", points: "+points, Toast.LENGTH_SHORT).show();
                        _mapPoints.IncrementMapPointIterator();

                        _mapPoints.SetMapNextMarkers();

                        // Update next place to go to
                        boolean lastMPPassed = _mapPoints.LastMapPointPassed();

                        MapPoint firstMP = _mapPoints.GetStartingMapPoint();
                        LatLng secondMP;
                        if(lastMPPassed){
                            secondMP = _mapPoints.GetStartingMapPoint().GetLatLng();
                        } else{
                            secondMP = _mapPoints.GetDestinationMarker().getPosition();
                        }


                        getWalkingDirections(_geoApiContext, firstMP.GetLatLng(), secondMP, Color.RED);
                    }
                    USER_POINTS += points;

                }


            /*int markercCount = _mapPoints.GerMarker().size() - 1;
            if(markercCount == 0){
                // ALL OF THE POINTS HAVE BEEN HIT
                // TODO: IMPLEMENT LOGIC FOR THAT
                Toast.makeText(WalkActivity.this, "YOU HAVE FINISHED YOUR WALK!", Toast.LENGTH_SHORT).show();
                FinishAssistantWalk();
            }*/
        }

        UpdateTotalDistanceUI();

        CurrentTimerSecs = 0;
    }
}