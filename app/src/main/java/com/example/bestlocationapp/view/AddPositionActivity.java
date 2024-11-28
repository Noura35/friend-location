package com.example.bestlocationapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bestlocationapp.R;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bestlocationapp.Utils.Url;
import com.example.bestlocationapp.controller.SessionManager;
import com.example.bestlocationapp.controller.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class AddPositionActivity extends AppCompatActivity {

    private EditText edpseudo, edtelephone;
    private TextView address;
    private Button btnSave;
    private double latitude, longitude;
    ImageView backbtn ;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ensure this utility method is defined in your project
        setContentView(R.layout.activity_add_position);

        edpseudo = findViewById(R.id.edpseudo_add);
        edtelephone = findViewById(R.id.ednumero_add);
        address = findViewById(R.id.address_show);
        btnSave = findViewById(R.id.Savebtn);

        extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
            // Convert latitude and longitude to a human-readable address
            getAddressFromLocation(latitude, longitude);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });


        backbtn=findViewById(R.id.back_btn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(AddPositionActivity.this,MainActivity.class));
            }
        });








        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveData() {
        final String token = SessionManager.getInstance(this).getToken().getToken();
        final String pseudo = edpseudo.getText().toString().trim();
        final String numero = edtelephone.getText().toString().trim();

        if (TextUtils.isEmpty(pseudo)) {
            edpseudo.setError("Please enter your pseudo");
            edpseudo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(numero)) {
            edtelephone.setError("Please enter your number");
            edtelephone.requestFocus();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("pseudo", pseudo);
            postparams.put("numero", numero);
            postparams.put("latitude", latitude);
            postparams.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Url.ALL_DATA_URL,
                postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(AddPositionActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                // After saving data, navigate to MapsActivity
                                Intent intent = new Intent(AddPositionActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Method to get address from latitude and longitude using Geocoder
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address addressObj = addresses.get(0);
                String addressText = addressObj.getAddressLine(0); // Full address
                address.setText(addressText);
            } else {
                address.setText("No address found for this location.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            address.setText("Unable to get address for this location.");
        }
    }
}
