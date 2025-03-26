package com.example.castorway;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

        //Inicio del código para mostrar el toast personalizado
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_personalizado, null);

        //Inicio de código para cambiar elementos del toast personalizado

        //Se cambia la imágen
        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(R.drawable.castor_logo_solito);

        //Se cambia el texto
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText("Bienvenido(a) de nuevo :)");

        //Se cambia el color de fondo
        Drawable background = layout.getBackground();
        background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azul_toast_bienvenido), PorterDuff.Mode.SRC_IN);

        // Cambia color del texto
        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        //Fin del código que se encarga de cambiar los elementos del toast personalizado

        //Lo crea y lo pone en la parte de arriba del cel
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
        //Fin del código para mostrar el toast personalizado
        Intent intent = new Intent(LoadingActivityStart.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}