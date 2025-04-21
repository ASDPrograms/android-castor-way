package com.example.castorway;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.DialogInterface;
import android.widget.Toast;


import com.example.castorway.conexionInternet.ConnectionUtils;

public class LoadingActivityStart extends AppCompatActivity {

    private ProgressBar progressBar;
    private Handler handler;
    private int downloadSpeed = 2;
    private boolean isConnectionActive = true;
    private boolean isErrorShown = false;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_start);

        progressBar = findViewById(R.id.progressBar);

        handler = new Handler();

        checkConnectionAndLoad();

    }

    private void showNoConnectionDialog() {
        // Crea el AlertDialog para mostrar el mensaje
        new AlertDialog.Builder(this)
                .setTitle("Conexión a Internet")
                .setMessage("No hay conexión a Internet. Intentaremos cargar nuevamente.")
                .setCancelable(false) // Evita que el usuario cierre el diálogo sin aceptar
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Restablecer la actividad
                        recreate(); // Esto recarga la actividad actual
                    }
                })
                .show();
        isErrorShown = true;
        isLoading = false; // Detiene el progreso
        handler.removeCallbacksAndMessages(null);

    }

    private void checkConnectionAndLoad() {
        if (ConnectionUtils.isInternetAvailable(this)) {
            // Si hay conexión, simula el progreso y continúa
            startConnectionCheck();
            simulateProgress();
        } else {
            // Si no hay conexión, muestra el modal y espera para intentar nuevamente
            showNoConnectionDialog();
        }
    }

    private void simulateProgress() {
        // Aquí se simula el progreso o el proceso de carga
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if(isLoading){
                    progressBar.setProgress(progress);
                    progress++;

                    // Asegúrate de que el progreso sea suave ajustando el intervalo
                    if (progress <= 100) {
                        handler.postDelayed(this, 50); // Cambié el intervalo de 100ms a 50ms para suavizar
                    } else {
                        // Aquí es donde se va a la siguiente actividad después de que se complete el progreso
                        goToNextActivity();
                    }
                }
            }
        };
        handler.post(runnable);
    }


    private void startConnectionCheck() {
        // Verifica la conexión cada 500ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!ConnectionUtils.isInternetAvailable(LoadingActivityStart.this)) {
                    // Si no hay conexión, muestra el diálogo de error y detiene el progreso
                    isConnectionActive = false;
                    showNoConnectionDialog();
                } else {
                    // Si hay conexión activa, sigue verificando cada 500ms
                    isConnectionActive = true;
                }

                // Vuelve a verificar la conexión después de 500ms solo si no se ha mostrado el error
                if (!isErrorShown) {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }


    private void goToNextActivity() {
        // Después de la carga exitosa, puedes redirigir al usuario

        Toast.makeText(this, "Bienvenido de nuevo :)", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(LoadingActivityStart.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}