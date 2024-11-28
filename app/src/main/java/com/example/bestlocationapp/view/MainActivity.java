package com.example.bestlocationapp.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bestlocationapp.R;
import com.example.bestlocationapp.controller.SessionManager;
import com.example.bestlocationapp.fragments.chat.ChatFragment;
import com.example.bestlocationapp.fragments.map.MapsFragment;
import com.example.bestlocationapp.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    private String token;
    private SessionManager sessionManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Assurez-vous que cette ligne fonctionne correctement
        setContentView(R.layout.activity_main);

        // Récupération du token de session
        token = getTokenFromSession();
        if (token == null) {
            return; // Quitter si le token est nul
        }






        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_map);
        bottomNav.setOnItemSelectedListener(navListener);

        // Initialiser le fragment de départ (MapsFragment)
        Fragment selectedFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();




        // Vérification des permissions de localisation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        // Gérer les insets pour les fenêtres (bordures)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Méthode pour récupérer le token de session
    private String getTokenFromSession() {
        sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoginIn()) {
            return sessionManager.getToken() != null ? sessionManager.getToken().getToken() : null;
        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class)); // Redirection vers la page de connexion
            return null;
        }
    }












    // Gestion du menu de navigation avec if-else
    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId(); // ID de l'élément sélectionné

                if (itemId == R.id.nav_chat) {
                    selectedFragment = new ChatFragment(); // Naviguer vers ProfileFragment
                } else if (itemId == R.id.nav_map) {
                    selectedFragment = new MapsFragment(); // Naviguer vers MapsFragment
                } else if (itemId == R.id.nav_logout) {
                    SessionManager.getInstance(MainActivity.this).UserloginOut();
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return true;

                } else {
                    selectedFragment = new MapsFragment(); // Si aucun ID ne correspond, on affiche le MapsFragment par défaut
                }

                // Remplacement du fragment sélectionné
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            };


}
