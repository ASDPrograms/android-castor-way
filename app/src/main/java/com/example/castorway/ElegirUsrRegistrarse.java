package com.example.castorway;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ElegirUsrRegistrarse extends AppCompatActivity {
    CardView cardViewTutor, cardViewKit;
    ImageView imgRegresarAzul3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_usr_registrarse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            cardViewTutor = findViewById(R.id.cardViewTutor);
            cardViewKit = findViewById(R.id.cardViewKit);
            imgRegresarAzul3 = findViewById(R.id.imgRegresarAzul3);

            cardViewTutor.setOnClickListener(this::registrarTutor);
            cardViewKit.setOnClickListener(this::registrarKit);
            imgRegresarAzul3.setOnClickListener(this::regresarHome);

            return insets;
        });
    }
    private void registrarTutor(View view){
        Intent registrarTutor = new Intent(this, RegistrarTutor.class);
        startActivity(registrarTutor);
    }
    private void registrarKit(View view){
        Intent registrarKit = new Intent(this, RegistrarKit.class);
        startActivity(registrarKit);
    }
    private void regresarHome(View view){
        Intent regresarHome = new Intent(this, MainActivity.class);
        startActivity(regresarHome);
    }
}