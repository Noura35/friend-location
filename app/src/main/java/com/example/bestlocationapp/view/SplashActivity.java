package com.example.bestlocationapp.view;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bestlocationapp.R;
import android.content.Intent;
import android.os.Handler;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 3000; // Durée de 3 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Après le délai, démarrer l'activité principale
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Ferme l'activité du splash screen
            }
        }, SPLASH_DELAY);






    }

}