package com.example.castorway_surface;

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

public class ElegirUsrIniciarSesion extends AppCompatActivity {
    CardView cardViewTutor, cardViewKit;
    ImageView imgRegresarAzul;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_usr_iniciar_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            cardViewTutor = findViewById(R.id.cardViewTutor);
            cardViewKit = findViewById(R.id.cardViewKit);
            imgRegresarAzul = findViewById(R.id.imgRegresarAzul);

            cardViewTutor.setOnClickListener(this::iniciarSesionTutor);
            cardViewKit.setOnClickListener(this::iniciarSesionKit);
            imgRegresarAzul.setOnClickListener(this::regresarHome);
            return insets;
        });
    }
    private void iniciarSesionTutor(View view){
        Intent iniciarSesionTutor = new Intent(this, IniciarSesionTutor.class);
        startActivity(iniciarSesionTutor);
    }
    private void iniciarSesionKit(View view){
        Intent iniciarSesionKit = new Intent(this, IniciarSesionKit.class);
        startActivity(iniciarSesionKit);
    }
    private void regresarHome(View view){
        Intent regresarHome = new Intent(this, MainActivity.class);
        startActivity(regresarHome);
    }
}