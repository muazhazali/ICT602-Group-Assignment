package com.example.medilocate;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.medilocate.databinding.ActivityMapsBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import com.android.volley.Request;
import com.android.volley.Response;

import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    MarkerOptions marker;
    private String URL = "http://192.168.1.105/ICT602_2/all.php";
    RequestQueue requestQueue;
    Gson gson;
    LocationMarker[] locationMarkers;
    private LatLng malaysiaLocation = new LatLng(3.139, 101.686); // Kuala Lumpur, Malaysia coordinates

    Vector<MarkerOptions> markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gson = new GsonBuilder().create();

        markerOptions = new Vector<MarkerOptions>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (MarkerOptions mark : markerOptions) {
            mMap.addMarker(mark);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysiaLocation, 7));
        enableMyLocation();
        sendRequest();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Get the username from SharedPreferences
                                SharedPreferences sharedPref = getSharedPreferences("userSession", Context.MODE_PRIVATE);
                                String username = sharedPref.getString("username", null);

                                // Check if username is not null
                                if (username != null) {
                                    updateUserLocation(username, latitude, longitude);
                                } else {
                                    Log.e("MapsActivity", "Username is null.");
                                }
                            }
                        }
                    });
        }
    }

    private void updateUserLocation(String username, double latitude, double longitude) {
        // Generate timestamp
        long timestamp = System.currentTimeMillis() / 1000; // Convert milliseconds to seconds

        // Build the URL with username, timestamp, latitude, and longitude
        String updateLocationURL = "https://192.168.1.105/update_location.php?username=" + Uri.encode(username, "@") +
                "&latitude=" + latitude + "&longitude=" + longitude + "&timestamp=" + timestamp;

        // Make a request to update user location
        StringRequest locationRequest = new StringRequest(Request.Method.GET, updateLocationURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response if needed
                        Log.d("MapsActivity", "Location updated successfully.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error if needed
                        Log.e("MapsActivity", "Failed to update location.");
                    }
                });

        // Add the request to the Volley queue
        requestQueue.add(locationRequest);
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Log.d("MapsActivity", "Permission granted") ;
            return;
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Log.d("MapsActivity", "Permission not granted") ;
            return;
        }
    }

    public void sendRequest () {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, onSuccess, onError);
        requestQueue.add(stringRequest);

    }

    public Response.Listener<String> onSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            locationMarkers = gson.fromJson(response, LocationMarker[].class);

            Log.d("MapsActivity", "Number of Location Markers: " + locationMarkers.length);

            if (locationMarkers.length < 1) {
                Toast.makeText(getApplicationContext(), "Problem retrieving JSON data", Toast.LENGTH_LONG).show();
                return;
            }

            for (LocationMarker locationMarker : locationMarkers) {
                Double lat = Double.parseDouble(locationMarker.lat);
                Double lng = Double.parseDouble(locationMarker.lng);
                String name = locationMarker.name;
                String snippet = locationMarker.description;

                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng))
                        .title(name)
                        .snippet(snippet);

                mMap.addMarker(marker);
            }
        }
    };

    public Response.ErrorListener onError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void saveUsername(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.apply();
    }

}