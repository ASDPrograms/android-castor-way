package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CalendarioFragmentTutor extends Fragment {

    private LinearLayout layoutDiasSemana, layoutActividades, layout_no_usr_kit_seleccionado, layout_no_dia_seleccionado;
    private ImageView flechaIzquierda, flechaDerecha, btnAgregarActi;
    private Calendar fechaInicioSemana, diaSeleccionado;
    private TextView txtRangoFecha;

    public CalendarioFragmentTutor() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutDiasSemana = view.findViewById(R.id.contenedor_dias_semana);
        layoutActividades = view.findViewById(R.id.contenedor_actividades);
        layout_no_dia_seleccionado = view.findViewById(R.id.layout_no_dia_seleccionado);
        layout_no_usr_kit_seleccionado = view.findViewById(R.id.layout_no_usr_kit_seleccionado);
        btnAgregarActi = view.findViewById(R.id.btnAgregarActi);
        txtRangoFecha = view.findViewById(R.id.txtRangoFecha);
        flechaIzquierda = view.findViewById(R.id.imgFlechaBotonAnterior);
        flechaDerecha = view.findViewById(R.id.imgFlechaBotonSiguiente);

        fechaInicioSemana = Calendar.getInstance();

        int diaInicioSemana = fechaInicioSemana.get(Calendar.DAY_OF_WEEK);
        int diferencia = Calendar.SUNDAY - diaInicioSemana;
        fechaInicioSemana.add(Calendar.DAY_OF_MONTH, diferencia);

        mostrarDiasSemana(fechaInicioSemana);

        flechaIzquierda.setOnClickListener(v -> {
            fechaInicioSemana.add(Calendar.WEEK_OF_YEAR, -1);
            actualizarTextoRangoFecha(fechaInicioSemana);
            mostrarDiasSemana(fechaInicioSemana);
        });

        flechaDerecha.setOnClickListener(v -> {
            fechaInicioSemana.add(Calendar.WEEK_OF_YEAR, 1);
            actualizarTextoRangoFecha(fechaInicioSemana);
            mostrarDiasSemana(fechaInicioSemana);
        });

        confirmUsrKitSeleccionado();
        actualizarTextoRangoFecha(fechaInicioSemana);
        btnAgregarActi.setOnClickListener(v -> {
            int num = confirmUsrKitSeleccionado();
            if(num != 0){
                Intent intent = new Intent(requireActivity(), AgregarActiTutor.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void mostrarDiasSemana(Calendar inicioSemanaOriginal) {
        layoutDiasSemana.removeAllViews();

        Calendar hoy = Calendar.getInstance();
        Calendar dia = (Calendar) inicioSemanaOriginal.clone();

        for (int i = 0; i < 7; i++) {
            Calendar diaActual = (Calendar) dia.clone();
            TextView diaView = new TextView(getContext());

            int diaDelMes = diaActual.get(Calendar.DAY_OF_MONTH);
            diaView.setText(String.valueOf(diaDelMes));
            diaView.setTag(diaActual);

            diaView.setTextColor(Color.BLACK);
            diaView.setGravity(Gravity.CENTER);
            diaView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            diaView.setTextSize(30);
            diaView.setPadding(2,2,2,2);
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.dongle);
            diaView.setTypeface(typeface);
            diaView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics()),
                    1f
            ));

            // Fondo por defecto
            diaView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.fondo_dias_semana));

            // Resaltar si es hoy
            if (esMismoDia(diaActual, hoy)) {
                diaView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dia_hoy));
                diaView.setTextColor(Color.WHITE);
            }

            // Resaltar si es el día seleccionado
            if (diaSeleccionado != null && esMismoDia(diaActual, diaSeleccionado)) {
                diaView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dia_seleccionado));
            }

            if(diaSeleccionado != null && esMismoDia(diaActual, diaSeleccionado) && esMismoDia(diaActual, hoy)){
                diaView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dia_hoy));
                diaView.setTextColor(Color.WHITE);
            }

            //pa buscar cada fechita
            diaView.setOnClickListener(v -> {
                diaSeleccionado = (Calendar) v.getTag();
                hayDiaSeleccionado(diaSeleccionado);
                mostrarDiasSemana(inicioSemanaOriginal);
                desplegActis(diaSeleccionado);

                int dia1 = diaSeleccionado.get(Calendar.DAY_OF_MONTH);
                int mes = diaSeleccionado.get(Calendar.MONTH) + 1;
                int anio1 = diaSeleccionado.get(Calendar.YEAR);

                Log.d("fechitaGuardada", "Día seleccionado: " + dia1 + "/" + mes + "/" + anio1);
            });

            layoutDiasSemana.addView(diaView);
            dia.add(Calendar.DAY_OF_MONTH, 1);
            hayDiaSeleccionado(diaSeleccionado);
        }

    }
    private boolean esMismoDia(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private int confirmUsrKitSeleccionado(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        if(idKit == 0){
            layoutActividades.setVisibility(View.GONE);
            layout_no_usr_kit_seleccionado.setVisibility(View.VISIBLE);
        }else{
            layout_no_usr_kit_seleccionado.setVisibility(View.GONE);
            layoutActividades.setVisibility(View.VISIBLE);
        }
        return idKit;
    }

    private void hayDiaSeleccionado(Calendar diaSeleccionado){
        if(diaSeleccionado == null){
            layout_no_dia_seleccionado.setVisibility(View.VISIBLE);
            layoutActividades.setVisibility(View.GONE);
        }else{
            layout_no_dia_seleccionado.setVisibility(View.GONE);
            layoutActividades.setVisibility(View.VISIBLE);
        }
    }
    private void actualizarTextoRangoFecha(Calendar inicio) {
        Calendar fin = (Calendar) inicio.clone();
        fin.add(Calendar.DAY_OF_MONTH, 6);

        String[] meses = {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};

        int mesInicio = inicio.get(Calendar.MONTH);
        int mesFin = fin.get(Calendar.MONTH);
        int anioInicio = inicio.get(Calendar.YEAR);
        int anioFin = fin.get(Calendar.YEAR);

        String texto;

        if (anioInicio == anioFin) {
            if (mesInicio == mesFin) {
                texto = meses[mesInicio] + " " + anioInicio;
            } else {
                texto = meses[mesInicio] + " - " + meses[mesFin] + " " + anioInicio;
            }
        } else {
            texto = meses[mesInicio] + " " + anioInicio + " - " + meses[mesFin] + " " + anioFin;
        }
        txtRangoFecha.setText(texto);
    }

    private void desplegActis(Calendar diaSeleccionado) {

        layoutActividades.removeAllViews();
        int idKit = confirmUsrKitSeleccionado();

        if (idKit != 0) {
            Log.d("DEBUG", "Si hay idKit");
            ApiService apiService2 = RetrofitClient.getApiService();
            Call<List<Actividad>> call2 = apiService2.getAllActividades();
            int finalIdKit = idKit;
            call2.enqueue(new Callback<List<Actividad>>() {
                @Override
                public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                    List<Actividad> actividades = response.body();

                    if (actividades != null) {
                        Log.e("DEBUG", "Hay actis");
                        for (Actividad actividad : actividades) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String fechaSeleccionadaStr = sdf.format(diaSeleccionado.getTime());
                            Log.e("DEBUG", String.valueOf(actividad.getIdKit() == finalIdKit));
                            if (actividad.getIdKit() == finalIdKit) {
                                String[] fechasArray = actividad.getFechasActividad().split(",");
                                String fechasActis = String.valueOf(actividad.getFechasActividad());
                                for (String fecha : fechasArray) {
                                    if (fecha.trim().equals(fechaSeleccionadaStr)) {
                                    Log.d("DEBUG", "Entró al if de acti");

                                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                                    View actividadView = inflater.inflate(R.layout.item_list_actividades, layoutActividades, false);

                                    //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                                    ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                                    TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                                    TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                                    TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                                    String hraInicial = String.valueOf(actividad.getHoraInicioHabito());
                                    String horaFinal = String.valueOf(actividad.getHoraFinHabito());

                                    txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                                    Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                                    String imgBd = actividad.getRutaImagenHabito();
                                    Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                                    String imageName = doesImageExist(requireContext(), imgBd);
                                    if (imageName != null) {
                                        InputStream inputStream = null;
                                        String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                        Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                        try {
                                            inputStream = getContext().getAssets().open(assetPath);
                                            Log.d("DEBUG", "InputStream abierto correctamente.");

                                            // Crear un objeto SVG desde el InputStream
                                            SVG svg = SVG.getFromInputStream(inputStream);
                                            if (svg != null) {
                                                // Convertir el SVG a un Drawable y mostrarlo
                                                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                                imgActividad.setImageDrawable(drawable);
                                                Log.d("DEBUG", "Imagen SVG cargada correctamente.");
                                            } else {
                                                Log.e("DEBUG", "Error al crear el objeto SVG.");
                                            }
                                            Log.d("DEBUG", "InputStream cerrado.");
                                        } catch (IOException | SVGParseException e) {
                                            Log.e("DEBUG", "Error al cargar el archivo SVG: " + e.getMessage());
                                        }
                                    } else {
                                        Log.e("DEBUG", "Error al encontrar la ruta de la imagen");
                                    }
                                    //Fin del código de cargar imágen svg desde asset


                                    String nombreHabito = String.valueOf(actividad.getNombreHabito());
                                    int numRamitas = actividad.getNumRamitas();
                                    String stringNumRamitas = String.valueOf(numRamitas);

                                    txtNombre.setText(nombreHabito);
                                    txtRamitasActi.setText(stringNumRamitas + " ramitas");

                                    //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                                    //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                                    btn_ir.setOnClickListener(v -> {

                                        BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                        View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                        modal.setContentView(view);

                                        // Obtener referencias de los elementos
                                        TextView txtTitle = view.findViewById(R.id.txtTitle);
                                        TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                        ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                        TextView numRamitasModal = view.findViewById(R.id.numRamitas);

                                        LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                        LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                        btnEditActi.setOnClickListener(v1 -> {
                                            //Aquí va lo que pasa cuando quiere editar el modal
                                        });

                                        //Aquí se abre el modal que confirma borrar la acti cuando da click en el boton de borrar acti
//                                        btnBorrarActi.setOnClickListener(v1 -> {
//                                            Dialog modalBorrar = new Dialog(requireContext());
//                                            View viewBorrar = getLayoutInflater().inflate(R.layout.modal_cerrar_view_confirm, null);
//                                            modalBorrar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                            modalBorrar.setContentView(viewBorrar);
//
//                                            TextView txtDialogTitle = viewBorrar.findViewById(R.id.txtDialogTitle);
//                                            TextView txtDialogMessage = viewBorrar.findViewById(R.id.txtDialogMessage);
//                                            Button btnCerrarModal = viewBorrar.findViewById(R.id.btnCerrarModal);
//                                            Button btnConfirm = viewBorrar.findViewById(R.id.btnConfirm);
//
//                                            btnConfirm.setOnClickListener(v3 -> {
//                                                modalBorrar.dismiss();
//                                                modal.dismiss();
//                                                borrarActividad(actividad.getIdActividad());
//
//                                            });
//
//
//                                            txtDialogTitle.setText("¡Atención!");
//                                            txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividad.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");
//
//                                            btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
//                                            modalBorrar.show();
//                                        });

                                        String nomHabito = String.valueOf(actividad.getNombreHabito());
                                        String tipHabit = String.valueOf(actividad.getTipoHabito());
                                        int numeroRamitas = actividad.getNumRamitas();
                                        String strgNumRamitas = String.valueOf(numeroRamitas);

                                        txtTitle.setText(nomHabito);
                                        txtTipoHabito.setText(tipHabit);
                                        numRamitasModal.setText(strgNumRamitas + " ramitas");

                                        String imageName2 = doesImageExist(requireContext(), imgBd);
                                        InputStream inputStream2 = null;
                                        String assetPath2 = "img/img_actividades/" + imageName2;
                                        InputStream inputStream3 = null;
                                        try {
                                            inputStream3 = getContext().getAssets().open(assetPath2);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        SVG svg = null;
                                        try {
                                            svg = SVG.getFromInputStream(inputStream3);
                                        } catch (SVGParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        Drawable drawable2 = new PictureDrawable(svg.renderToPicture());
                                        imgActiModal.setImageDrawable(drawable2);
                                        modal.show();

                                        SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt("idActividad", actividad.getIdActividad());
                                        editor.apply();
                                    });
                                    //Fin de la lógica de desplegar modal

                                    // Agregar la vista al contenedor
                                    layoutActividades.addView(actividadView);

                                    Log.e("DEBUG", "Cont: " + layoutActividades);
                                }
                            }}
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Actividad>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Hubo un error al conectar al servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public static String doesImageExist(Context context, String inputPath) {
        // Se obtiene solo el nombre del archivo desde la ruta
        String fileName = extractFileName(inputPath);
        return fileName;
    }
private static String extractFileName(String path) {
    // Eliminar la parte de la ruta antes del nombre del archivo
    int lastSlash = path.lastIndexOf('/');
    if (lastSlash != -1) {
        path = path.substring(lastSlash + 1);
    }
    return path;
}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario_tutor, container, false);
    }
}