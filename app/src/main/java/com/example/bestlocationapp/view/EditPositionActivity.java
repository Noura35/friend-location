package com.example.bestlocationapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class EditPositionActivity extends AppCompatActivity {

    private EditText edtPseudo, edtTelephone;
    private Button btnSave;
    private int num = 0;
    private ImageView backbtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_position);

        // Initialize UI components
        edtPseudo = findViewById(R.id.ed_pseudo_edit);
        edtTelephone = findViewById(R.id.ednumero_edit);
        btnSave = findViewById(R.id.btnEdit_edit);
        backbtn = findViewById(R.id.back_btn);

        // Retrieve data from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            num = extras.getInt("id", 0);  // Default to 0 if no ID is provided
            edtPseudo.setText(extras.getString("pseudo"));
            edtTelephone.setText(extras.getString("telephone"));
        }

        // Set up listener for Save button
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                editData(num);
            }
        });

        // Set up listener for Back button
        backbtn.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(EditPositionActivity.this, MainActivity.class));
        });
    }

    // Method to validate input fields
    private boolean validateInput() {
        String pseudo = edtPseudo.getText().toString().trim();
        String numero = edtTelephone.getText().toString().trim();

        if (TextUtils.isEmpty(pseudo)) {
            edtPseudo.setError("Please enter your pseudo");
            edtPseudo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(numero)) {
            edtTelephone.setError("Please enter your number");
            edtTelephone.requestFocus();
            return false;
        }

        return true;
    }

    // Method to send the updated data to the server
    private void editData(int id) {
        final String token = SessionManager.getInstance(this).getToken().getToken();
        final String pseudo = edtPseudo.getText().toString().trim();
        final String numero = edtTelephone.getText().toString().trim();

        // Show progress dialog while processing the request
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        JSONObject postparams = new JSONObject();
        try {
            postparams.put("pseudo", pseudo);
            postparams.put("numero", numero);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the PUT request to update data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                Url.ALL_DATA_URL + "/" + id, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();  // Dismiss progress dialog
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(EditPositionActivity.this,
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(EditPositionActivity.this, MainActivity.class));

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Failed to update data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();  // Dismiss progress dialog
                        // Log the error and notify user
                        Log.e("EditPositionActivity", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error updating data", Toast.LENGTH_SHORT).show();
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

        // Add request to Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
