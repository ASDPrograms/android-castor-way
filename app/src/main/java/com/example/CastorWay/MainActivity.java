package com.example.castorway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
    }@Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
        boolean sesionActiva = preferences.getBoolean("sesionActiva", false);

        if (sesionActiva) {
            String tipoUsuario = preferences.getString("tipoUsuario", "");

            if (tipoUsuario.equals("Castor")) {
                Intent intent = new Intent(this, VerAppWeb.class);
                startActivity(intent);
                finish();
            } else if (tipoUsuario.equals("Kit")) {
                Intent intent = new Intent(this, VerAppWebKit.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(this, HomeUsuarioSesion.class);
            startActivity(intent);
            finish();
        }
    }
}