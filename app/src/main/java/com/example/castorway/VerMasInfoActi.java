package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerMasInfoActi extends AppCompatActivity {
    TextView titNombreActi, txtTipoHabito, txtHoras, txtFechas, numRamitas, txtDiasCompletados,txtEstatusActi;
    ImageView btnBurbujaMasInfo, imgRaudy, imgPlantaDiasProg, btnSalirAddActi, btnComprAvance;
    LinearLayout linLayEstatusActi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_mas_info);

        btnSalirAddActi = findViewById(R.id.btnSalirAddActi);
        btnSalirAddActi.setOnClickListener(v -> {
            Intent intent = new Intent(VerMasInfoActi.this, HomeTutor.class);
            intent.putExtra("fragmentActiCrear", "ActividadesFragmentTutor");
            startActivity(intent);
        });

        //declaración de elementos que van a establecer su valor para que se vea la info
        titNombreActi = findViewById(R.id.titNombreActi);
        txtTipoHabito = findViewById(R.id.txtTipoHabito);
        txtHoras = findViewById(R.id.txtHoras);
        txtFechas = findViewById(R.id.txtFechas);
        numRamitas = findViewById(R.id.numRamitas);
        txtDiasCompletados = findViewById(R.id.txtDiasCompletados);
        imgPlantaDiasProg = findViewById(R.id.imgPlantaDiasProg);
        txtEstatusActi = findViewById(R.id.txtEstatusActi);
        linLayEstatusActi = findViewById(R.id.linLayEstatusActi);
        btnComprAvance = findViewById(R.id.btnComprAvance);
        btnComprAvance.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.modal_progreso_total);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            LinearLayout linearLayout = dialog.findViewById(R.id.containerModalProgress);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(40);
            drawable.setColor(Color.TRANSPARENT);

            linearLayout.setBackground(drawable);

            ImageView cerrarModalProgTotal = dialog.findViewById(R.id.cerrarModalProgTotal);
            cerrarModalProgTotal.setOnClickListener(v1 -> {
                dialog.dismiss();
            });

            obtenerDatosProgresoTotal(dialog);

            dialog.show();
        });

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
        super.onBackPressed();
        Intent intent = new Intent(VerMasInfoActi.this, HomeTutor.class);
        intent.putExtra("fragmentActiCrear", "ActividadesFragmentTutor");
        startActivity(intent);
    }

    private void obtenerDatosProgresoTotal(Dialog dialog) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();

        // Realizamos la llamada Retrofit
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.body() != null) {
                    List<Actividad> actividades = response.body();

                    // Obtener el contenedor y el ProgressBar
                    LinearLayout containerLayoutProgress = dialog.findViewById(R.id.linearLayoutContElementsProgress);
                    ProgressBar progressBarVert = dialog.findViewById(R.id.vertical_progressbar);
                    ScrollView scrollViewProgress = dialog.findViewById(R.id.scrollViewProgress);
                    FrameLayout imagesContainer = dialog.findViewById(R.id.imagesContainer);

                    // Verifica que el ProgressBar y el contenedor no sean nulos
                    if (progressBarVert != null && containerLayoutProgress != null) {
                        // Obtenemos el alto del LinearLayout contenedor
                        int containerHeight = containerLayoutProgress.getHeight();
                        Log.d("DEBUG", "Altura del contenedor: " + containerHeight);

                        // Si la altura del contenedor es 0, salimos
                        if (containerHeight == 0) {
                            Log.e("ERROR", "La altura del contenedor es 0");
                            return;
                        }

                        // Calcular el nuevo alto para el ProgressBar (420% de la altura)
                        int newHeight = (int) (containerHeight * 4.2); // 420%
                        Log.d("DEBUG", "Nuevo alto para ProgressBar: " + newHeight);

                        // Establece el nuevo alto para el ProgressBar
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) progressBarVert.getLayoutParams();
                        params.height = newHeight;
                        progressBarVert.setLayoutParams(params);


                        // Accede a las preferencias compartidas para obtener el id de la actividad
                        SharedPreferences sharedPreferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                        int idActividad = sharedPreferences.getInt("idActividad", 0);

                        //ya que se estableció el alto del linearlayout, se recorre el scrollview hasta abajo
                        scrollViewProgress.post(() -> scrollViewProgress.fullScroll(ScrollView.FOCUS_DOWN));

                        // Iteramos sobre las actividades y obtenemos la correcta
                        // Iteramos sobre las actividades y obtenemos la correcta
                        for (Actividad actividad : actividades) {
                            if (actividad.getIdActividad() == idActividad) {
                                // Calculamos el progreso en base a los días completados
                                int numDiasComple = actividad.getDiasCompletados();
                                Log.d("DEBUG", "Días completados: " + numDiasComple);

                                // Establecemos el progreso del ProgressBar
                                progressBarVert.setProgress(numDiasComple * 100 / 21);

                                // Posición Y en porcentaje de las hojas
                                LayoutInflater inflater = LayoutInflater.from(VerMasInfoActi.this);
                                for (int i = 1; i <= 21; i++) {
                                    // Inflamos el diseño del LinearLayout
                                    LinearLayout progressItemLayout = (LinearLayout) inflater.inflate(R.layout.diseno_hojas_progreso_actis, imagesContainer, false);

                                    ImageView imageView = progressItemLayout.findViewById(R.id.progress_item_image);
                                    TextView textView = progressItemLayout.findViewById(R.id.progress_item_text);

                                    textView.setText(String.valueOf(i));

                                    int finalI = i;
                                    imageView.setOnClickListener(v -> {
                                        mostrarModalPorDiaActi(finalI);
                                    });

                                    //para que el 1 esté hasta abajo y el 21 hasta arriba
                                    int topMargin = 0;
                                   if(i == 21){
                                       topMargin = 0;
                                   }else{
                                       float posicionY = 100 - (i * 100 / 21);
                                       topMargin = (int) (posicionY * newHeight / 100) - 40;
                                   }

                                    // Usamos FrameLayout.LayoutParams para asegurar la compatibilidad con el FrameLayout
                                    FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                                            FrameLayout.LayoutParams.WRAP_CONTENT,
                                            FrameLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params2.topMargin = topMargin;
                                    params2.gravity = Gravity.CENTER_HORIZONTAL;

                                    // Asignamos el FrameLayout.LayoutParams al progressItemLayout
                                    progressItemLayout.setLayoutParams(params2);

                                    // Agregamos el layout inflado al FrameLayout (imagesContainer)
                                    imagesContainer.addView(progressItemLayout);
                                }

                                FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT
                                );

                                //los íconos de las plantas en los lados a la barra de progreso
                                //se declaran para que se encuentren en la posición que deben de acuerdo al tamaño
                                //de la barra de progreso

                                ImageView img_planta_prog_1 = dialog.findViewById(R.id.img_planta_prog_1);
                                ImageView img_planta_prog_2 = dialog.findViewById(R.id.img_planta_prog_2);
                                ImageView img_planta_prog_3 = dialog.findViewById(R.id.img_planta_prog_3);
                                ImageView img_planta_prog_4 = dialog.findViewById(R.id.img_planta_prog_4);

                                float posicionYUno = 100 - (4 * 100 / 21);
                                int topMarginUno = (int) (posicionYUno * newHeight / 100) - 40;
                                FrameLayout.LayoutParams paramsPlanta1 = new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT
                                );
                                paramsPlanta1.topMargin = topMarginUno;
                                paramsPlanta1.gravity = Gravity.CENTER_HORIZONTAL;
                                img_planta_prog_1.setLayoutParams(paramsPlanta1);

                                float posicionYDos = 100 - (9 * 100 / 21);
                                int topMarginDos = (int) (posicionYDos * newHeight / 100) - 40;
                                FrameLayout.LayoutParams paramsPlanta2 = new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT
                                );
                                paramsPlanta2.topMargin = topMarginDos;
                                paramsPlanta2.gravity = Gravity.CENTER_HORIZONTAL;
                                img_planta_prog_2.setLayoutParams(paramsPlanta2);

                                float posicionYTres = 100 - (14 * 100 / 21);
                                int topMarginTres = (int) (posicionYTres * newHeight / 100) - 40;
                                FrameLayout.LayoutParams paramsPlanta3 = new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT
                                );
                                paramsPlanta3.topMargin = topMarginTres;
                                paramsPlanta3.gravity = Gravity.CENTER_HORIZONTAL;
                                img_planta_prog_3.setLayoutParams(paramsPlanta3);

                                float posicionYCuatro = 100 - (20 * 100 / 21);
                                int topMarginCuatro = (int) (posicionYCuatro * newHeight / 100) - 40;
                                FrameLayout.LayoutParams paramsPlanta4 = new FrameLayout.LayoutParams(
                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT
                                );
                                paramsPlanta4.topMargin = topMarginCuatro;
                                paramsPlanta4.gravity = Gravity.CENTER_HORIZONTAL;
                                img_planta_prog_4.setLayoutParams(paramsPlanta4);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.e("ERROR", "Falló la obtención de datos de progreso", t);
            }
        });
    }

    private void mostrarModalPorDiaActi(int numDia){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();

        // Realizamos la llamada Retrofit
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();

                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                    for (Actividad actividad : actividades) {
                        int idActividad = sharedPreferences.getInt("idActividad", 0);
                        Log.e("DEBUG", "Id acti: " + idActividad);
                        if (actividad.getIdActividad() == idActividad) {

                            //Se infla y crea el modal que sale del fondo, para cada día de la acti
                            View view = LayoutInflater.from(VerMasInfoActi.this).inflate(R.layout.bottom_modal_cada_dia_acti, null);
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(VerMasInfoActi.this);
                            bottomSheetDialog.setContentView(view);

                            bottomSheetDialog.show();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
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

                            //número de días completados
                            String diasCompletados = String.valueOf(actividad.getDiasCompletados());
                            txtDiasCompletados.setText(diasCompletados);
                            int numDiasComple = Integer.parseInt(diasCompletados);

                            if(numDiasComple >= 0 && numDiasComple < 5){
                                Drawable imgPlanta = ContextCompat.getDrawable(VerMasInfoActi.this, R.drawable.img_semilla);
                                imgPlantaDiasProg.setImageDrawable(imgPlanta);
                                int color = ContextCompat.getColor(VerMasInfoActi.this, R.color.cafe_ramita);
                                txtDiasCompletados.setTextColor(color);
                            }else if(numDiasComple >= 5 && numDiasComple < 10){
                                Drawable imgPlanta = ContextCompat.getDrawable(VerMasInfoActi.this, R.drawable.img_planta_1);
                                imgPlantaDiasProg.setImageDrawable(imgPlanta);
                            }else if(numDiasComple >= 10 && numDiasComple < 15){
                                Drawable imgPlanta = ContextCompat.getDrawable(VerMasInfoActi.this, R.drawable.img_planta_2);
                                imgPlantaDiasProg.setImageDrawable(imgPlanta);
                            }
                            else if(numDiasComple >= 15 && numDiasComple < 20) {
                                Drawable imgPlanta = ContextCompat.getDrawable(VerMasInfoActi.this, R.drawable.img_planta_3);
                                imgPlantaDiasProg.setImageDrawable(imgPlanta);
                            }
                            else if(numDiasComple == 21){
                                Drawable imgPlanta = ContextCompat.getDrawable(VerMasInfoActi.this, R.drawable.img_planta_4);
                                imgPlantaDiasProg.setImageDrawable(imgPlanta);
                            }

                            // estatus de la acti:
                            String estadosActi = String.valueOf(actividad.getEstadosActi());

                            String[] estadosActiArray = estadosActi.split(",");

                            boolean todosSonDos = true;
                            for (String valor : estadosActiArray) {
                                if (Integer.parseInt(valor) != 2) {
                                    todosSonDos = false;
                                    break;
                                }
                            }

                            int colorEstatusTxt = 0;
                            int colorEstatusFondo = 0;
                            String txtEstatus = "";

                            if(todosSonDos){
                                colorEstatusTxt = ContextCompat.getColor(VerMasInfoActi.this, R.color.verde_estatus_texto);
                                colorEstatusFondo = ContextCompat.getColor(VerMasInfoActi.this, R.color.verde_estatus_fondo);
                                txtEstatus = "Completada";
                            }else{
                                colorEstatusTxt = ContextCompat.getColor(VerMasInfoActi.this, R.color.amarillo_estatus_texto);
                                colorEstatusFondo = ContextCompat.getColor(VerMasInfoActi.this, R.color.amarillo_estatus_fondo);
                                txtEstatus = "En proceso";
                            }

                            Drawable drawable = ContextCompat.getDrawable(VerMasInfoActi.this, R.drawable.border_estatus_acti);
                            drawable.setColorFilter(colorEstatusFondo, PorterDuff.Mode.SRC_IN);

                            linLayEstatusActi.setBackground(drawable);
                            txtEstatusActi.setTextColor(colorEstatusTxt);
                            txtEstatusActi.setText(txtEstatus);
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