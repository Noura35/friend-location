package com.example.bestlocationapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bestlocationapp.R;
import com.example.bestlocationapp.Utils.Url;
import com.example.bestlocationapp.controller.SessionManager;
import com.example.bestlocationapp.controller.VolleySingleton;
import com.example.bestlocationapp.fragments.map.MapsFragment;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.app.AlertDialog;


public class ShowPositionActivity extends AppCompatActivity {

    private TextView tvaddress, tvpseudo, tvtelephone;
    private Button delete, edit;
    private Bundle extras;
    private int num = 0;
    double latitude, longitude;
    ImageView backbtn ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Assurez-vous que cette ligne est correcte pour votre projet
        setContentView(R.layout.activity_show_position);

        tvpseudo = findViewById(R.id.tvpseudo_show);
        tvtelephone = findViewById(R.id.tv_num_show);
        tvaddress = findViewById(R.id.address_show);
        edit = findViewById(R.id.btnedit);
        delete = findViewById(R.id.btnDelete);

        extras = getIntent().getExtras();
        if (extras != null) {
            tvpseudo.setText(extras.getString("pseudo"));
            tvtelephone.setText(extras.getString("telephone"));
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
            getAddressFromLocation(latitude, longitude);

            Float testNum = extras.getFloat("id");
            num = Math.round(testNum);


        }

        // Listener pour le bouton éditer
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num != 0) {
                    Intent intent = new Intent(ShowPositionActivity.this, EditPositionActivity.class);
                    intent.putExtra("id", num);
                    intent.putExtra("pseudo", tvpseudo.getText().toString());
                    intent.putExtra("telephone", tvtelephone.getText().toString());
                    startActivity(intent);
                }
            }
        });


        // Listener pour le bouton supprimer
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num != 0) {

                    new AlertDialog.Builder(ShowPositionActivity.this)
                            .setTitle("Confirm deletion\n")
                            .setMessage("Are you sure you want to delete this position?\n")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteData(num);  // Suppression de l'entrée
                                    startActivity(new Intent(ShowPositionActivity.this, MainActivity.class));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Fermer la boîte de dialogue
                                }
                            })
                            .setCancelable(false) // Empêcher de fermer le dialogue en dehors de celui-ci
                            .show(); // Afficher la boîte de dialogue
                }
            }
        });




        backbtn=findViewById(R.id.back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ShowPositionActivity.this,MainActivity.class));

            }
        });





    }

    // Suppression des données
    public void deleteData(int id) {
        final String token = SessionManager.getInstance(this).getToken().getToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                Url.ALL_DATA_URL + "/" + id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(ShowPositionActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();  // Retour à l'écran précédent après suppression
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ShowPositionActivity.this, "Error deleting data. Please try again.", Toast.LENGTH_SHORT).show();
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

    // Méthode pour obtenir l'adresse à partir de la latitude et longitude
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressObj = addresses.get(0);
                String addressText = addressObj.getAddressLine(0);
                tvaddress.setText(addressText);
            } else {
                tvaddress.setText("No address found for this location.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            tvaddress.setText("Unable to get address for this location.");
        }
    }
}
