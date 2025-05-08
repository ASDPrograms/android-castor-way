package com.example.castorway;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    private ScrollView scrollActividades;
    private LinearLayout layoutDiasSemana, layoutActividades, layout_no_usr_kit_seleccionado, layout_no_dia_seleccionado;
    private ImageView flechaIzquierda, flechaDerecha, btnAgregarActi;
    private Calendar fechaInicioSemana, diaSeleccionado;
    private TextView txtRangoFecha, textoNoActs, btnHoyFecha, diaLunes, diaMartes, diaMiercoles, diaJueves, diaViernes, diaSabado, diaDomingo;

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
        scrollActividades = view.findViewById(R.id.scroll_actividades);
        textoNoActs = null;
        flechaIzquierda = view.findViewById(R.id.imgFlechaBotonAnterior);
        flechaDerecha = view.findViewById(R.id.imgFlechaBotonSiguiente);
        btnHoyFecha = view.findViewById(R.id.btnHoyFecha);

        diaLunes = view.findViewById(R.id.dia_lunes);
        diaMartes = view.findViewById(R.id.dia_martes);
        diaMiercoles = view.findViewById(R.id.dia_miercoles);
        diaJueves = view.findViewById(R.id.dia_jueves);
        diaViernes = view.findViewById(R.id.dia_viernes);
        diaSabado = view.findViewById(R.id.dia_sabado);
        diaDomingo = view.findViewById(R.id.dia_domingo);

        fechaInicioSemana = Calendar.getInstance();
        String[] dias = {"domingo", "lunes", "martes", "miercoles", "jueves", "viernes", "sabado"};

        int diaInicioSemana = fechaInicioSemana.get(Calendar.DAY_OF_WEEK);
        String nombreDia = dias[diaInicioSemana - 1];

        String idTexto = "dia_" + nombreDia;
        int idDia = getResources().getIdentifier(idTexto, "id", getContext().getPackageName());

        if (idDia != 0) {
            TextView diaView = getView().findViewById(idDia);
            if (diaView != null) {
                diaView.setTextColor(ContextCompat.getColor(requireContext(), R.color.cafe_ramita)); // Usa tu color
            }
        }
        int diferencia = Calendar.SUNDAY - diaInicioSemana;
        fechaInicioSemana.add(Calendar.DAY_OF_MONTH, diferencia);
        diaSeleccionado = null;
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

        btnHoyFecha.setOnClickListener(v -> {
            Calendar hoy = Calendar.getInstance();
            diaSeleccionado = (Calendar) hoy.clone();

            fechaInicioSemana = Calendar.getInstance();

            int diaInicioSemana2 = fechaInicioSemana.get(Calendar.DAY_OF_WEEK);
            int diferencia2 = Calendar.SUNDAY - diaInicioSemana2;
            fechaInicioSemana.add(Calendar.DAY_OF_MONTH, diferencia2);
            actualizarTextoRangoFecha(fechaInicioSemana);
            scrollActividades.setVisibility(View.VISIBLE);
            layout_no_dia_seleccionado.setVisibility(View.GONE);
            mostrarDiasSemana(fechaInicioSemana);
            desplegActis(hoy);
        });

        confirmUsrKitSeleccionado(diaSeleccionado);
        actualizarTextoRangoFecha(fechaInicioSemana);
        btnAgregarActi.setOnClickListener(v -> {
            int num = confirmUsrKitSeleccionado(diaSeleccionado);
            if (num != 0) {
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
            diaView.setPadding(2, 2, 2, 2);
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

            if (diaSeleccionado != null && esMismoDia(diaActual, diaSeleccionado) && esMismoDia(diaActual, hoy)) {
                diaView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dia_hoy));
                diaView.setTextColor(Color.WHITE);
            }

            //pa buscar cada fechita
            diaView.setOnClickListener(v -> {
                try {
                    diaSeleccionado = (Calendar) v.getTag();
                    scrollActividades.setVisibility(View.VISIBLE);
                    layout_no_dia_seleccionado.setVisibility(View.GONE);
                    mostrarDiasSemana(inicioSemanaOriginal);
                    desplegActis(diaSeleccionado);

                    int dia1 = diaSeleccionado.get(Calendar.DAY_OF_MONTH);
                    int mes = diaSeleccionado.get(Calendar.MONTH) + 1;
                    int anio1 = diaSeleccionado.get(Calendar.YEAR);

                    Log.d("fechitaGuardada", "Día seleccionado: " + dia1 + "/" + mes + "/" + anio1);
                } catch (Exception e) {

                }
            });

            layoutDiasSemana.addView(diaView);
            dia.add(Calendar.DAY_OF_MONTH, 1);

        }

    }

    private boolean esMismoDia(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private int confirmUsrKitSeleccionado(Calendar diaSeleccionado) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        if (idKit == 0) {
            scrollActividades.setVisibility(View.GONE);
            layout_no_usr_kit_seleccionado.setVisibility(View.VISIBLE);
        } else {
            layout_no_usr_kit_seleccionado.setVisibility(View.GONE);
            hayDiaSeleccionado(diaSeleccionado);
        }
        return idKit;
    }
    private void hayDiaSeleccionado(Calendar diaSeleccionado) {
        Log.d("vistita", "hay día: " + diaSeleccionado);
        if (diaSeleccionado == null) {
            layout_no_dia_seleccionado.setVisibility(View.VISIBLE);
            scrollActividades.setVisibility(View.GONE);
        } else {
            layout_no_dia_seleccionado.setVisibility(View.GONE);
            scrollActividades.setVisibility(View.VISIBLE);
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
        int idKit = confirmUsrKitSeleccionado(diaSeleccionado);

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
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String fechaSeleccionadaStr = sdf.format(diaSeleccionado.getTime());
                        Log.e("fechaseleccionada", "dia: " + fechaSeleccionadaStr);
                        int conteoActs = 0;
                        Log.e("DEBUG", "Hay actis");
                        for (Actividad actividad : actividades) {
                            if (actividad.getIdKit() == finalIdKit) {
                                String[] fechasArray = actividad.getFechasActividad().split(",");
                                for (String fecha : fechasArray) {
                                    if (fecha.trim().equals(fechaSeleccionadaStr)) {
                                        conteoActs += 1;
                                    }
                                }
                            }
                        }
                        Log.e("conteoActs", "Valor: " + conteoActs);
                        if (conteoActs > 0) {
                            for (Actividad actividad : actividades) {

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
                                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor2 = preferences.edit();
                                                    editor2.putInt("idActividad", actividad.getIdActividad());
                                                    editor2.apply();

                                                    Intent intent = new Intent(requireActivity(), EditarActividad.class);
                                                    startActivity(intent);
                                                });

                                                //Aquí se abre el modal que confirma borrar la acti cuando da click en el boton de borrar acti
                                                btnBorrarActi.setOnClickListener(v1 -> {
                                                    Dialog modalBorrar = new Dialog(requireContext());
                                                    View viewBorrar = getLayoutInflater().inflate(R.layout.modal_cerrar_view_confirm, null);
                                                    modalBorrar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                    modalBorrar.setContentView(viewBorrar);

                                                    TextView txtDialogTitle = viewBorrar.findViewById(R.id.txtDialogTitle);
                                                    TextView txtDialogMessage = viewBorrar.findViewById(R.id.txtDialogMessage);
                                                    Button btnCerrarModal = viewBorrar.findViewById(R.id.btnCerrarModal);
                                                    Button btnConfirm = viewBorrar.findViewById(R.id.btnConfirm);

                                                    btnConfirm.setOnClickListener(v3 -> {
                                                        modalBorrar.dismiss();
                                                        modal.dismiss();
                                                        borrarActividad(actividad.getIdActividad());

                                                    });


                                                    txtDialogTitle.setText("¡Atención!");
                                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'" + actividad.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                                    modalBorrar.show();
                                                });

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
                                                    inputStream3 = requireContext().getAssets().open(assetPath2);
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

                                                SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                                SharedPreferences.Editor editor3 = preferences.edit();
                                                editor3.putInt("idActividad", actividad.getIdActividad());
                                                editor3.apply();
                                            });
                                            //Fin de la lógica de desplegar modal

                                            // Agregar la vista al contenedor
                                            layoutActividades.addView(actividadView);

                                            Log.e("DEBUG", "Cont: " + layoutActividades);
                                        }
                                    }
                                }
                            }
                        } else {
                            textoNoActs = new TextView(requireContext());
                            textoNoActs.setId(View.generateViewId());
                            textoNoActs.setText("No hay actividades");
                            textoNoActs.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textoNoActs.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                            textoNoActs.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gayathri_bold));
                            textoNoActs.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                            layoutActividades.addView(textoNoActs);
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

    private void borrarActividad(int idActividad) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = apiService.deleteActividad(idActividad);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    //Inicio del código para mostrar el toast personalizado
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_personalizado, null);

                    //Inicio de código para cambiar elementos del toast personalizado

                    //Se cambia la imágen
                    ImageView icon = layout.findViewById(R.id.toast_icon);
                    icon.setImageResource(R.drawable.btn_borrar_acti_rojo);

                    //Se cambia el texto
                    TextView text = layout.findViewById(R.id.toast_text);
                    text.setText("Actividad eliminada con éxito");

                    //Se cambia el color de fondo
                    Drawable background = layout.getBackground();
                    background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.rojito_toast), PorterDuff.Mode.SRC_IN);

                    // Cambia color del texto
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

                    //Fin del código que se encarga de cambiar los elementos del toast personalizado

                    //Lo crea y lo pone en la parte de arriba del cel
                    Toast toast = new Toast(requireContext());
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    //Fin del código para mostrar el toast personalizado

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, new ActividadesFragmentTutor()); // Reemplaza con tu fragmento específico
                    transaction.commit();

                } else {
                    Toast.makeText(requireContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    private void desplegarModal(View view) {
        try {
            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Actividad>> call = apiService.getAllActividades();
            call.enqueue(new Callback<List<Actividad>>() {
                @Override
                public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                    List<Actividad> actividades = response.body();

                    if (actividades != null) {
                        Log.e("DEBUG", "Hay actis");
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                        for (Actividad actividad : actividades) {
                            int idActividad = sharedPreferences.getInt("idActividad", 0);
                            Log.e("DEBUG", "Id acti: " + idActividad);
                            if(actividad.getIdActividad() == idActividad){
                                Log.e("PAVER", "SIPAPI 5");
                                desplModalConDatosActi(actividad);
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<List<Actividad>> call, Throwable t) {
                    // Verificar que el fragmento aún está agregado antes de acceder a los recursos
                    if (isAdded()) {
                        try {
                            Context context = requireContext(); // Usar requireContext() ya que es más seguro
                            Log.e("ERROR", "Fallo en la API: " + t.getMessage());
                        } catch (Exception e) {
                            Log.e("ERROR", "Error en el fragmento: " + e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MiFragment", "Error al intentar mostrar el modal: " + e.getMessage());
        }
    }
    private void desplModalConDatosActi(Actividad actividad){
        View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);

        BottomSheetDialog modal = new BottomSheetDialog(requireContext());
        View modalView = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
        modal.setContentView(modalView);


        ImageView imgActividad = view.findViewById(R.id.imgActividad);

        String imgBd = actividad.getRutaImagenHabito();
        Log.d("DEBUG", "valor ruta imagen: " + imgBd);

        String imageName = doesImageExist(requireContext(), imgBd);
        if (imageName != null) {
            InputStream inputStream = null;
            String assetPath = "img/img_actividades/" + imageName;
            Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

            try {
                inputStream = requireContext().getAssets().open(assetPath);
                Log.d("DEBUG", "InputStream abierto correctamente.");

                SVG svg = SVG.getFromInputStream(inputStream);
                if (svg != null) {
                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                    imgActividad.setImageDrawable(drawable);
                    Log.d("DEBUG", "Imagen SVG cargada correctamente.");
                } else {
                    Log.e("DEBUG", "Error al crear el objeto SVG.");
                }
                inputStream.close();
            } catch (IOException | SVGParseException e) {
                Log.e("DEBUG", "Error al cargar el archivo SVG: " + e.getMessage());
            }
        } else {
            Log.e("DEBUG", "Error al encontrar la ruta de la imagen");
        }


        modal.setOnDismissListener(dialog -> {
            if (isAdded() && getActivity() != null) {
                SharedPreferences sharedPreferencesCerrar = getContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                editorCerrar.putBoolean("sesion_activa", false);
                editorCerrar.apply();
            }
        });

        TextView txtTitle = modalView.findViewById(R.id.txtTitle);
        TextView txtTipoHabito = modalView.findViewById(R.id.txtTipoHabito);
        ImageView imgActiModal = modalView.findViewById(R.id.imgActividad);
        TextView numRamitasModal = modalView.findViewById(R.id.numRamitas);
        ImageView btnVerMasInfoActi = modalView.findViewById(R.id.btnVerMasInfoActi);

        btnVerMasInfoActi.setOnClickListener(v1 -> {
            if (isAdded()) {
                Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                startActivity(intent);
            }
        });

        LinearLayout btnEditActi = modalView.findViewById(R.id.layout_btn_edit_acti);
        LinearLayout btnBorrarActi = modalView.findViewById(R.id.layout_btn_borrar_acti);

        btnEditActi.setOnClickListener(v1 -> {
            if (isAdded()) {
                SharedPreferences preferences = getContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = preferences.edit();
                editor2.putInt("idActividad", actividad.getIdActividad());
                editor2.apply();

                Intent intent = new Intent(requireActivity(), EditarActividad.class);
                startActivity(intent);
            }
        });

        btnBorrarActi.setOnClickListener(v1 -> {
            if (isAdded()) {
                Dialog modalBorrar = new Dialog(requireContext());
                View viewBorrar = getLayoutInflater().inflate(R.layout.modal_cerrar_view_confirm, null);
                modalBorrar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                modalBorrar.setContentView(viewBorrar);

                TextView txtDialogTitle = viewBorrar.findViewById(R.id.txtDialogTitle);
                TextView txtDialogMessage = viewBorrar.findViewById(R.id.txtDialogMessage);
                Button btnCerrarModal = viewBorrar.findViewById(R.id.btnCerrarModal);
                Button btnConfirm = viewBorrar.findViewById(R.id.btnConfirm);

                btnConfirm.setOnClickListener(v3 -> {
                    modalBorrar.dismiss();
                    modal.dismiss();
                    borrarActividad(actividad.getIdActividad());
                });

                txtDialogTitle.setText("¡Atención!");
                txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividad.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                modalBorrar.show();
            }
        });

        String nomHabito = String.valueOf(actividad.getNombreHabito());
        String tipHabit = String.valueOf(actividad.getTipoHabito());
        int numeroRamitas = actividad.getNumRamitas();
        String strgNumRamitas = String.valueOf(numeroRamitas);

        txtTitle.setText(nomHabito);
        txtTipoHabito.setText(tipHabit);
        numRamitasModal.setText(strgNumRamitas + " ramitas");

        String imageName2 = doesImageExist(requireContext(), imgBd);
        if (imageName2 != null) {
            try (InputStream inputStream3 = requireContext().getAssets().open("img/img_actividades/" + imageName2)) {
                SVG svg = SVG.getFromInputStream(inputStream3);
                Drawable drawable2 = new PictureDrawable(svg.renderToPicture());
                imgActiModal.setImageDrawable(drawable2);
            } catch (IOException | SVGParseException e) {
                Log.e("DEBUG", "Error al cargar imagen SVG: " + e.getMessage());
            }
        }
        modal.show();
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
    @Override
    public void onResume() {
        super.onResume();
        // Verificar si el fragmento está adjunto y si el contexto está disponible
        if (isAdded() && getContext() != null) {
            Log.e("PAVER", "SIPAPI 1");

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("sesionModalActis", Context.MODE_PRIVATE);
            boolean isModalOpened = sharedPreferences.getBoolean("sesion_activa", false);

            Log.e("PAVER", "estado modal: " + isModalOpened);

            if(isModalOpened){
                // Asegurarse de que la vista del fragmento esté disponible
                if (getView() != null) {
                    Log.e("PAVER", "SIPAPI 3");

                    // Mostrar el modal en el hilo principal usando un Handler
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("PAVER", "SIPAPI 4");
                            desplegarModal(getView());
                        }
                    });
                }
            }
        }else{
            Log.e("PAVER", "NOPAPI");
        }
    }
}