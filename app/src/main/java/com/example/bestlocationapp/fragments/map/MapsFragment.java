package com.example.bestlocationapp.fragments.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bestlocationapp.R;
import com.example.bestlocationapp.Utils.Url;
import com.example.bestlocationapp.controller.SessionManager;
import com.example.bestlocationapp.controller.VolleySingleton;
import com.example.bestlocationapp.model.Position;
import com.example.bestlocationapp.view.AddPositionActivity;
import com.example.bestlocationapp.view.LoginActivity;
import com.example.bestlocationapp.view.ShowPositionActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private String token;
    private List<Position> placeList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private SessionManager sessionManager;





    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Handler handler = new Handler();
    private Runnable locationUpdateTask;
    private static final long LOCATION_UPDATE_INTERVAL = 60000; // 1 minute (en millisecondes)









    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Vérification du token de session et redirection vers login si non valide
        token = getTokenFromSession();
        if (token == null) {
            return inflater.inflate(R.layout.activity_login, container, false);
        }

        // Gonfler le layout pour ce fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation de la carte
       // initializeMap();

        // Initialisation du client de localisation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Vérifiez les permissions avant d'activer la localisation actuelle
        checkLocationPermission();
        // Initialisation de la carte
        initializeMap();

        // Lancer les mises à jour de la localisation
        startLocationUpdates();




    }



    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableCurrentLocation();
        }
    }



    private void startLocationUpdates() {
        locationUpdateTask = new Runnable() {
            @Override
            public void run() {
                updateCurrentLocation();
                handler.postDelayed(this, LOCATION_UPDATE_INTERVAL); // Relancer après l'intervalle
            }
        };
        handler.post(locationUpdateTask); // Démarrer immédiatement
    }

    private void stopLocationUpdates() {
        if (locationUpdateTask != null) {
            handler.removeCallbacks(locationUpdateTask); // Arrêter les mises à jour
        }
    }
    private void updateCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                // Déplacer la caméra vers la nouvelle position
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                Toast.makeText(getContext(), "Position mise à jour!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }




    private void enableCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mMap != null) {
            mMap.setMyLocationEnabled(true);

            // Obtenez la dernière localisation connue
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                Toast.makeText(getContext(), "Vous êtes ici!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }













    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Définir le callback OnMapReadyCallback
        } else {
            Log.e("MapsFragment", "Map fragment is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Définir les listeners
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        LatLng tunis = new LatLng(36.8065, 10.1815);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tunis, 12));


        // Récupérer les données pour charger les marqueurs
        fetchData();
    }

    @Override
    public void onCameraIdle() {
        addMarkersToMap();
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Gestion du clic sur la carte (optionnel)
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d("MarkerClick", "Marker clicked: " + marker.getTitle());
        Toast.makeText(getActivity(), "Marker clicked: " + marker.getTitle(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), ShowPositionActivity.class);
        intent.putExtra("latitude", marker.getPosition().latitude);
        intent.putExtra("longitude", marker.getPosition().longitude);
        intent.putExtra("pseudo", marker.getTitle());
        intent.putExtra("telephone", marker.getSnippet());
        intent.putExtra("id", marker.getZIndex());
        startActivity(intent);
        return false;
    }

    // Méthode pour récupérer les données depuis le serveur
    private void fetchData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url.ALL_DATA_URL, null,
                response -> {
                    progressDialog.dismiss();
                    handleResponse(response); // Gérer les données reçues
                },
                error -> {
                    progressDialog.dismiss();
                    handleError(error); // Gérer les erreurs
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private void handleResponse(JSONObject response) {
        try {
            JSONArray placeArray = response.getJSONArray("data");
            placeList.clear();

            for (int i = 0; i < placeArray.length(); i++) {
                JSONObject placeObj = placeArray.getJSONObject(i);
                Position place = new Position();
                place.setId(placeObj.getInt("id"));
                place.setPseudo(placeObj.getString("pseudo"));
                place.setNumero(placeObj.getString("numero"));
                place.setLatitude(placeObj.getString("latitude"));
                place.setLongitude(placeObj.getString("longitude"));
                placeList.add(place);
            }

            addMarkersToMap();
            Toast.makeText(getActivity(), "Data fetched successfully!", Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            Log.e("DataFetcher", "JSON Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleError(VolleyError error) {
        Log.e("DataFetcher", "Error response: " + error.getMessage());
        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void addMarkersToMap() {
        if (mMap == null || placeList.isEmpty()) return;

        mMap.clear(); // Effacer les anciens marqueurs

        for (Position place : placeList) {
            LatLng position = new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(place.getPseudo())
                    .snippet(place.getNumero())
                    .zIndex(place.getId()) // Stocker l'ID dans ZIndex
            );
            markerList.add(marker);
        }

        // Déplacer la caméra vers la première position
        if (!placeList.isEmpty()) {
            LatLng firstPosition = new LatLng(Double.parseDouble(placeList.get(0).getLatitude()), Double.parseDouble(placeList.get(0).getLongitude()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPosition, 10));
        }
    }

    private String getTokenFromSession() {
        sessionManager = SessionManager.getInstance(getActivity());
        if (sessionManager.isLoginIn()) {
            return sessionManager.getToken() != null ? sessionManager.getToken().getToken() : null;
        } else {
            getActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return null;
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Position");

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            Log.e("Geocoder", "Error getting address: " + e.getMessage());
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            Address addressObj = addresses.get(0);
            String addressText = addressObj.getAddressLine(0);
            builder.setMessage("Do you want to add a new position at this location?\nAddress: " + addressText);
        }

        builder.setPositiveButton("Add", (dialog, which) -> {
            Intent intent = new Intent(getActivity(), AddPositionActivity.class);
            intent.putExtra("latitude", latLng.latitude);
            intent.putExtra("longitude", latLng.longitude);
            startActivity(intent);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Ajoutez ici la gestion des permissions pour la localisation
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true); // Si la permission est accordée
        }
    }
}
