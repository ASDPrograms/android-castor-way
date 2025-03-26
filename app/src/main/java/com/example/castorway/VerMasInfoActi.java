package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.retrofit.RetrofitClient;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerMasInfoActi extends AppCompatActivity {

    ImageView btnSalirAddActi;
    TextView titNombreActi, txtTipoHabito, txtHoras, txtFechas, numRamitas;
    ImageView btnBurbujaMasInfo, imgRaudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_mas_info);

        btnSalirAddActi = findViewById(R.id.btnSalirAddActi);
        btnSalirAddActi.setOnClickListener(v1 -> mostrarModalCerrarView("¡Atención!", "Si das click en aceptar saldrás del formulario y no se guardará la información ingresada."));

        //declaración de elementos que van a establecer su valor para que se vea la info
        titNombreActi = findViewById(R.id.titNombreActi);
        txtTipoHabito = findViewById(R.id.txtTipoHabito);
        txtHoras = findViewById(R.id.txtHoras);
        txtFechas = findViewById(R.id.txtFechas);
        numRamitas = findViewById(R.id.numRamitas);

        establecerValores();


        btnBurbujaMasInfo = findViewById(R.id.btnBurbujaMasInfo);
        imgRaudy = findViewById(R.id.imgRaudy);

        btnBurbujaMasInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método para mostrar el modal
                showBubbleDialog();
                imgRaudy.setAlpha(1.0f);
            }
        });

    }
    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir, si das click en aceptar saldrás y no se actualizará la actividad con los nuevos datos.");
    }
    private void showBubbleDialog() {

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {

                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");

                    for (Actividad actividad : actividades) {
                        SharedPreferences sharedPreferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                        int idActividad = sharedPreferences.getInt("idActividad", 0);
                        Log.e("DEBUG", "Id acti: " + idActividad);
                        if (actividad.getIdActividad() == idActividad) {
                            // Crear un View con el diseño del modal
                            View dialogView = getLayoutInflater().inflate(R.layout.modal_mas_info_acti, null);

                            // Obtener referencias a los elementos
                            LinearLayout linearLayout = dialogView.findViewById(R.id.layoutModalMasInfo); // Asegúrate de que tenga un ID en el XML
                            ScrollView scrollView = dialogView.findViewById(R.id.scrollModalMasInfo); // Asegúrate de que tenga un ID en el XML
                            TextView txtDescripcion = dialogView.findViewById(R.id.txtDescripcion);

                            // Configurar el texto
                            String infoExtra = String.valueOf(actividad.getInfoExtraHabito());
                            txtDescripcion.setText(infoExtra);

                            // Configurar el AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(VerMasInfoActi.this);
                            builder.setView(dialogView);
                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();

                            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                            Display display = windowManager.getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            int screenHeight = size.y;

                            // Definir un límite de altura
                            int maxHeight = (int) (screenHeight * 0.5);

                            ViewGroup.LayoutParams linearLayoutParams = linearLayout.getLayoutParams();
                            linearLayoutParams.height = maxHeight; // Limitar la altura del LinearLayout
                            linearLayout.setLayoutParams(linearLayoutParams);


                            int scrollHeight = (int) (maxHeight * 0.6); // 20% del máximo del LinearLayout

                            ViewGroup.LayoutParams scrollViewParams = scrollView.getLayoutParams();
                            scrollViewParams.height = scrollHeight; // Establecer la altura del ScrollView
                            scrollView.setLayoutParams(scrollViewParams);

                            // Ajustar la posición del modal
                            Window window = dialog.getWindow();
                            if (window != null) {
                                window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);  // Para que esté arriba
                                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            }
                        }

                    }
                }

            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void mostrarModalCerrarView(String titulo, String mensaje) {
        //Se crea el modal al momento de hacer click en el botón
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_cerrar_view_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        // Se obtienen los elementos del modal
        TextView txtTitle = dialog.findViewById(R.id.txtDialogTitle);
        TextView txtMessage = dialog.findViewById(R.id.txtDialogMessage);
        Button btnClose = dialog.findViewById(R.id.btnCerrarModal);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        // Asignar valores personalizados
        txtTitle.setText(titulo);
        txtMessage.setText(mensaje);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .commit();

        });

        // Mostrar el modal
        dialog.show();
    }

    private void establecerValores(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");

                    for (Actividad actividad : actividades) {
                        SharedPreferences sharedPreferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                        int idActividad = sharedPreferences.getInt("idActividad", 0);
                        Log.e("DEBUG", "Id acti: " + idActividad);
                        if(actividad.getIdActividad() == idActividad){

                            //Nombre del hábito
                            String nombreActi = String.valueOf(actividad.getNombreHabito());
                            titNombreActi.setText(nombreActi);

                            //Tipo del hábito
                            String tipoHabit = String.valueOf(actividad.getTipoHabito());
                            txtTipoHabito.setText(tipoHabit);

                            //Fechas del hábito:
                            String fechaInicial = String.valueOf(actividad.getDiaInicioHabito());
                            String fechaFinal = String.valueOf(actividad.getDiaMetaHabito());

                            txtFechas.setText(fechaInicial + "  -  " + fechaFinal);

                            //Horas del hábito:
                            String horaInicial = String.valueOf(actividad.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividad.getHoraFinHabito());

                            txtHoras.setText(horaInicial + "  -  " + horaFinal);


                            //ramitas del hábito:
                            String numeroRamitas = String.valueOf(actividad.getNumRamitas());
                            numRamitas.setText(numeroRamitas + " ramitas");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }

}