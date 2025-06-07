package com.example.castorway;

import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.retrofit.RetrofitClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.drawable.Drawable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActividadesFragmentTutor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActividadesFragmentTutor extends Fragment {
    BottomSheetDialog modalFiltros;
    BottomSheetDialog modalActiGeneral;
    RadioButton radioButtonRojo, radioButtonAmarillo ,radioButtonVerde, radioButtonAzul, radioButtonMorado, radioButtonNegro;
    SwipeRefreshLayout refrescarFragment;
    LinearLayout layout_esta_semana, layout_siguiente_semana, layout_mas_tarde, contenedor_despues, contenedor_esta_semana, contenedor_siguiente_semana, layout_no_usr_kit_seleccionado, linLayContainAllActis;
    TextView numActisEstaSemana, numActisSigSemana, numActisMasTarde, edit_busqueda, numRamitas,txtFechaInicial, txtFechaFinal;
    ImageView imgFlechEstaSemana, imgFlechSigSemana, imgFlechMasTarde, btnAgregarActi, btn_filtros;
    Button btnLimpiarFiltros;
    RadioGroup radioGroupCatActiFiltros, radioGroupRepFiltros, radioGroupEstadoActiFiltr;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActividadesFragmentTutor() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActividadesFragmentTutor.
     */
    // TODO: Rename and change types and number of parameters
    public static ActividadesFragmentTutor newInstance(String param1, String param2) {
        ActividadesFragmentTutor fragment = new ActividadesFragmentTutor();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_actividades_tutor, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linLayContainAllActis = view.findViewById(R.id.linLayContainAllActis);
        refrescarFragment = view.findViewById(R.id.refrescarFragment);
        refrescarFragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction transaction = requireFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, new ActividadesFragmentTutor());
                transaction.addToBackStack(null);
                transaction.commit();

                refrescarFragment.setRefreshing(false);
            }
        });

        layout_esta_semana = view.findViewById(R.id.layout_esta_semana);
        layout_siguiente_semana = view.findViewById(R.id.layout_siguiente_semana);
        layout_mas_tarde = view.findViewById(R.id.layout_mas_tarde);
        layout_no_usr_kit_seleccionado = view.findViewById(R.id.layout_no_usr_kit_seleccionado);

        numActisEstaSemana = view.findViewById(R.id.numActisEstaSemana);
        numActisSigSemana = view.findViewById(R.id.numActisSigSemana);
        numActisMasTarde = view.findViewById(R.id.numActisMasTarde);

        contenedor_despues = view.findViewById(R.id.contenedor_despues);
        contenedor_esta_semana = view.findViewById(R.id.contenedor_esta_semana);
        contenedor_siguiente_semana = view.findViewById(R.id.contenedor_siguiente_semana);

        btnAgregarActi = view.findViewById(R.id.btnAgregarActi);

        imgFlechEstaSemana = view.findViewById(R.id.imgFlechEstaSemana);
        imgFlechSigSemana = view.findViewById(R.id.imgFlechSigSemana);
        imgFlechMasTarde = view.findViewById(R.id.imgFlechSigMasTarde);

        // se configuran listeners
        layout_esta_semana.setOnClickListener(v -> alternarVisibActis(contenedor_esta_semana));
        layout_siguiente_semana.setOnClickListener(v -> alternarVisibActis(contenedor_siguiente_semana));
        layout_mas_tarde.setOnClickListener(v -> alternarVisibActis(contenedor_despues));


        confirmUsrKitSeleccionado();
        //Btn que se encarga de abrir modal para agregar nueva acti, al dar click manda a otra acti sin terminar este fragment para que al regresar mande al fragment de nuevo
        btnAgregarActi.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
            int idKit = sharedPreferences.getInt("idKit", 0);
            if(idKit != 0){
                Intent intent = new Intent(requireActivity(), AgregarActiTutor.class);
                startActivity(intent);
            }
        });

        actuNumActis();

        //para el filtrado de actividades por la barra de búsqueda
        edit_busqueda = view.findViewById(R.id.edit_busqueda);
        edit_busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                despleActisPorBusqueda(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_filtros = view.findViewById(R.id.btn_filtros);
        btn_filtros.setOnClickListener(view1 -> {
            desplFiltros();
        });

        if (isAdded() && getContext() != null) {
            Log.e("PAVER", "SIPAPI 1");

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("sesionModalActis", Context.MODE_PRIVATE);
            boolean isModalOpened = sharedPreferences.getBoolean("sesion_activa", false);

            Log.e("PAVER", "estado modal: " + isModalOpened);

            if(isModalOpened){
                if(modalActiGeneral == null){
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
            }
        }else{
            Log.e("PAVER", "NOPAPI");
        }
    }

    private void desplegarModal(View view) {
        try {
            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);

            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Actividad>> call = apiService.getAllActividades();
            call.enqueue(new Callback<List<Actividad>>() {
                @Override
                public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                    List<Actividad> actividades = response.body();

                    if (actividades != null) {
                        Log.e("DEBUG", "Hay actis");
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

    private void alternarVisibActis(LinearLayout contenedor) {
        // Aquí se va alternando la visibilidad de los contenedores de las actividades
        Log.d("DEBUG", "Botón CLICKEADO");

        if (contenedor.getVisibility() == View.GONE) {
            Log.d("DEBUG", "Desplegando");
            contenedor.setVisibility(View.VISIBLE);
            if(contenedor.getResources().getResourceEntryName(contenedor.getId()).equals("contenedor_esta_semana")){
                imgFlechEstaSemana.setImageResource(R.drawable.icon_flechita_arriba);
            }else if(contenedor.getResources().getResourceEntryName(contenedor.getId()).equals("contenedor_siguiente_semana")){
                imgFlechSigSemana.setImageResource(R.drawable.icon_flechita_arriba);
            }else if(contenedor.getResources().getResourceEntryName(contenedor.getId()).equals("contenedor_despues")){
                imgFlechMasTarde.setImageResource(R.drawable.icon_flechita_arriba);
            }
            desplegActis(contenedor);
        } else {
            Log.d("DEBUG", "Ocultando");
            if(contenedor.getResources().getResourceEntryName(contenedor.getId()).equals("contenedor_esta_semana")){
                imgFlechEstaSemana.setImageResource(R.drawable.icon_flechita_abajo);
            }else if(contenedor.getResources().getResourceEntryName(contenedor.getId()).equals("contenedor_siguiente_semana")){
                imgFlechSigSemana.setImageResource(R.drawable.icon_flechita_abajo);
            }else if(contenedor.getResources().getResourceEntryName(contenedor.getId()).equals("contenedor_despues")){
                imgFlechMasTarde.setImageResource(R.drawable.icon_flechita_abajo);
            }
            contenedor.setVisibility(View.GONE);
        }
    }
    //Esta función verifica que haya almenos un usuario de hijo seleccionado
    private int confirmUsrKitSeleccionado(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        if(idKit == 0){
            layout_esta_semana.setVisibility(View.GONE);
            layout_siguiente_semana.setVisibility(View.GONE);
            layout_mas_tarde.setVisibility(View.GONE);

            layout_no_usr_kit_seleccionado.setVisibility(View.VISIBLE);
        }else{
            layout_no_usr_kit_seleccionado.setVisibility(View.GONE);

            layout_esta_semana.setVisibility(View.VISIBLE);
            layout_siguiente_semana.setVisibility(View.VISIBLE);
            layout_mas_tarde.setVisibility(View.VISIBLE);
        }
        return idKit;
    }
    private void desplegActis(LinearLayout contenedor) {
        //Se jala el nombre del id del layout para determinar si se despliega en este o no, dependiendo de las fechas
        String idName = contenedor.getResources().getResourceEntryName(contenedor.getId());
        contenedor.removeAllViews();

        SharedPreferences preferencesKit = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
        int idKitVer = preferencesKit.getInt("idKit", 0);
        if (idKitVer != 0) {
            Log.d("DEBUG", "Si hay idKit");
            ApiService apiService2 = RetrofitClient.getApiService();
            Call<List<Actividad>> call2 = apiService2.getAllActividades();
            call2.enqueue(new Callback<List<Actividad>>() {
                @Override
                public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                    List<Actividad> actividades = response.body();

                    if (actividades != null) {
                        //ordena por la hora de inicio de la acti
                        Collections.sort(actividades, new Comparator<Actividad>() {
                            @Override
                            public int compare(Actividad a1, Actividad a2) {
                                // Primero compara por horaInicio
                                int comparacionInicio = a1.getHoraInicioHabito().compareTo(a2.getHoraInicioHabito());

                                // Si son iguales, compara por horaFin
                                return (comparacionInicio != 0) ? comparacionInicio : a1.getHoraFinHabito().compareTo(a2.getHoraFinHabito());
                            }
                        });

                        Log.e("DEBUG", "Hay actis");
                        SharedPreferences preferencesKit = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                        int idKit = preferencesKit.getInt("idKit", 0);
                        for (Actividad actividad : actividades) {
                            if (actividad.getIdKit() == idKit) {
                                String fechasActis = String.valueOf(actividad.getFechasActividad());
                                if(idName.equals(obtenerPeriodo(fechasActis)) ){
                                    Log.d("DEBUG", "Entró al if de acti");

                                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                                    View actividadView = inflater.inflate(R.layout.item_list_actividades, contenedor, false);

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
                                            inputStream = requireContext().getAssets().open(assetPath);
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
                                        //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("sesion_activa", true);
                                        editor.apply();

                                        modalActiGeneral = new BottomSheetDialog(requireContext());

                                        View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                        modalActiGeneral.setContentView(view);

                                        modalActiGeneral.setOnDismissListener(dialog -> {
                                            // Aquí se cambia el valor de la sesión cuando se cierra
                                            SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                            SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                            editorCerrar.putBoolean("sesion_activa", false);
                                            editorCerrar.apply();
                                        });

                                        // Obtener referencias de los elementos
                                        TextView txtTitle = view.findViewById(R.id.txtTitle);
                                        TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                        ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                        TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                        ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                        //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                        btnVerMasInfoActi.setOnClickListener(v1 -> {
                                            Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                            startActivity(intent);
                                        });

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
                                                modalActiGeneral.dismiss();
                                                borrarActividad(actividad.getIdActividad());

                                            });


                                            txtDialogTitle.setText("¡Atención!");
                                            txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividad.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

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
                                        modalActiGeneral.show();

                                        SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                        SharedPreferences.Editor editor3 = preferences.edit();
                                        editor3.putInt("idActividad", actividad.getIdActividad());
                                        editor3.apply();
                                    });
                                    //Fin de la lógica de desplegar modal

                                    // Agregar la vista al contenedor
                                    contenedor.addView(actividadView);

                                    Log.e("DEBUG", "Cont: " + contenedor);
                                }
                            }
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

    // Función para extraer solo el nombre del archivo de la ruta, ya que en la bd se tiene una ruta antes del name y el .svg
    private static String extractFileName(String path) {
        // Eliminar la parte de la ruta antes del nombre del archivo
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash != -1) {
            path = path.substring(lastSlash + 1);
        }
        return path;
    }

    private void actuNumActis(){
        //Se consultan todas las actividades del hijo seleccionado

        SharedPreferences preferencesKit = requireContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
        int idKit = preferencesKit.getInt("idKit", 0);

        if(idKit != 0){
            Log.d("DEBUG", "Si hay idKit");
            ApiService apiService2 = RetrofitClient.getApiService();
            Call<List<Actividad>> call2 = apiService2.getAllActividades();
            call2.enqueue(new Callback<List<Actividad>>() {
                @Override
                public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                    List<Actividad> actividades = response.body();

                    if (actividades != null) {
                        int numActisEstaSemanaInt = 0;
                        int numActisSigSemanaInt = 0;
                        int numActisMasTardeInt = 0;
                        Log.e("DEBUG", "Hay actis");
                        for (Actividad actividad : actividades) {
                            Log.e("DEBUG", String.valueOf(actividad.getIdKit() == idKit));
                            if (actividad.getIdKit() == idKit) {
                                Log.d("DEBUG", "Entró al if de acti: " + idKit);

                                String verifi = obtenerPeriodo(String.valueOf(actividad.getFechasActividad()));

                                if(verifi.equals("No hay fechas disponibles en el futuro")){
                                    continue;
                                }else if(verifi.equals("contenedor_esta_semana")){
                                    numActisEstaSemanaInt++;
                                }else if(verifi.equals("contenedor_siguiente_semana")){
                                    numActisSigSemanaInt++;
                                }else if(verifi.equals("contenedor_despues")){
                                    numActisMasTardeInt++;
                                }
                            }
                        }
                        numActisEstaSemana.setText(String.valueOf(numActisEstaSemanaInt));
                        numActisSigSemana.setText(String.valueOf(numActisSigSemanaInt));
                        numActisMasTarde.setText(String.valueOf(numActisMasTardeInt));
                    }else{
                        numActisEstaSemana.setText("0");
                        numActisSigSemana.setText("0");
                        numActisMasTarde.setText("0");
                    }
                }
                @Override
                public void onFailure(Call<List<Actividad>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Hubo un error al conectar al servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static String obtenerPeriodo(String fechasStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaActual = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);

        // Obtener las fechas a partir de lasfechas que se pasan
        List<Date> fechas = parsearFechas(fechasStr);

        Date fechaMasCercana = null;

        // Recorrer las fechas y encontrar la más cercana que sea igual o posterior a la fecha actual
        for (Date fecha : fechas) {
            if (!fecha.before(fechaActual)) {
                if (fechaMasCercana == null || fecha.before(fechaMasCercana)) {
                    fechaMasCercana = fecha;
                }
            }
        }

        // Si no hay fecha igual o posterior a la actual, retornar nada o mensaje adecuado
        if (fechaMasCercana == null) {
            return "No hay fechas disponibles en el futuro";
        }

        // Determinar en qué periodo cae la fecha más cercana
        calendar.setTime(fechaMasCercana);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Llamamos al método para determinar si es esta semana, siguiente semana, o más tarde
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(fechaActual);
        int currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);

        currentCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date inicioSemana = currentCalendar.getTime();
        currentCalendar.add(Calendar.DATE, 6);
        Date finSemana = currentCalendar.getTime();

        currentCalendar.add(Calendar.DATE, 7);
        Date inicioSiguienteSemana = currentCalendar.getTime();
        currentCalendar.add(Calendar.DATE, 6);
        Date finSiguienteSemana = currentCalendar.getTime();

        // Determinar en qué periodo cae la fecha más cercana
        if (!fechaMasCercana.before(inicioSemana) && !fechaMasCercana.after(finSemana)) {
            return "contenedor_esta_semana";
        } else if (!fechaMasCercana.before(inicioSiguienteSemana) && !fechaMasCercana.after(finSiguienteSemana)) {
            return "contenedor_siguiente_semana";
        } else {
            return "contenedor_despues";
        }
    }

    // Método para convertir el String de fechas a una lista de Date
    private static List<Date> parsearFechas(String fechasStr) {
        List<Date> fechas = new ArrayList<>();
        String[] fechasArray = fechasStr.split(", ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (String fechaStr : fechasArray) {
            try {
                fechas.add(dateFormat.parse(fechaStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return fechas;
    }

    //Método para borrar la actividad seleccionada en el botón:
    private void borrarActividad(int idActividad){
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

    //método que se encarga de desplegar las actividades que concuerden
    //con lo ingresado en la barra de búsqueda
    private void despleActisPorBusqueda(String textoIngresado){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                        for (Actividad actividad : actividades) {
                            Log.e("DEBUG", "idKit: " + idKit);
                            if (actividad.getIdKit() == idKit) {
                                if (String.valueOf(actividad.getNombreHabito()).toLowerCase().contains(textoIngresado.toLowerCase())) {
                                    //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                    actisCoincid.add(actividad);
                                    verifiActiCoincid = true;
                                }
                            }
                        }
                    if(textoIngresado.isEmpty()){
                        verifiActiCoincid = false;
                    }


                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }

    private void desplFiltros(){
        if(modalFiltros == null){
            modalFiltros = new BottomSheetDialog(requireContext());
            View modalView = getLayoutInflater().inflate(R.layout.filtros_actis, null);
            modalFiltros.setContentView(modalView);

            View bottomSheet = modalFiltros.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                // Evita el colapso intermedio
                behavior.setSkipCollapsed(true);
                behavior.setDraggable(false);
            }

            ScrollView scrollView = modalView.findViewById(R.id.scrollFiltros);
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                    // Si el scroll está arriba, se permite cerrar arrastrando, sino , ps no
                    if (scrollY == 0) {
                        behavior.setDraggable(true);
                    } else {
                        behavior.setDraggable(false);
                    }
                }
            });

            //se cargan los elementos del layout:

            //para los radiobutton que cambien de color:
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    },
                    new int[]{
                            // Color cuando está seleccionado
                            Color.parseColor("#879FD4"),
                            // Color cuando no está seleccionado
                            Color.parseColor("#BDBDBD")
                    }
            );

            //se declaran los radiobutton

            //RadioButton de "Categoría de la actividad"
            RadioButton radioButtonSalud = modalView.findViewById(R.id.radioButtonSalud);
            RadioButton radioButtonProductividad = modalView.findViewById(R.id.radioButtonProductividad);
            RadioButton radioButtonPersonales = modalView.findViewById(R.id.radioButtonPersonales);
            RadioButton radioButtonSociales = modalView.findViewById(R.id.radioButtonSociales);
            RadioButton radioButtonFinancieros = modalView.findViewById(R.id.radioButtonFinancieros);
            RadioButton radioButtonEmocionales = modalView.findViewById(R.id.radioButtonEmocionales);

            //RadioButton de "Frecuencia de repetición"
            RadioButton radioButtonDiasSemana = modalView.findViewById(R.id.radioButtonDiasSemana);
            RadioButton radioButtonDiasMes = modalView.findViewById(R.id.radioButtonDiasMes);
            RadioButton radioButtonIntervalos = modalView.findViewById(R.id.radioButtonIntervalos);

            //RadioButton de "Estado de la actividad"
            RadioButton radioButtonCompletada = modalView.findViewById(R.id.radioButtonCompletada);
            RadioButton radioButtonProgreso = modalView.findViewById(R.id.radioButtonProgreso);
            RadioButton radioButtonSinTerminar = modalView.findViewById(R.id.radioButtonSinTerminar);

            //RadioButton de "Ícono o color asociado"
            radioButtonRojo = modalView.findViewById(R.id.radioButtonRojo);
            radioButtonAmarillo = modalView.findViewById(R.id.radioButtonAmarillo);
            radioButtonVerde = modalView.findViewById(R.id.radioButtonVerde);
            radioButtonAzul = modalView.findViewById(R.id.radioButtonAzul);
            radioButtonMorado = modalView.findViewById(R.id.radioButtonMorado);
            radioButtonNegro = modalView.findViewById(R.id.radioButtonNegro);

            //se les asigna a los radiobutton el color de cuando está desactivado y el color de cuando está desactivado
            radioButtonSalud.setButtonTintList(colorStateList);
            radioButtonProductividad.setButtonTintList(colorStateList);
            radioButtonPersonales.setButtonTintList(colorStateList);
            radioButtonSociales.setButtonTintList(colorStateList);
            radioButtonFinancieros.setButtonTintList(colorStateList);
            radioButtonEmocionales.setButtonTintList(colorStateList);

            radioButtonDiasSemana.setButtonTintList(colorStateList);
            radioButtonDiasMes.setButtonTintList(colorStateList);
            radioButtonIntervalos.setButtonTintList(colorStateList);

            radioButtonCompletada.setButtonTintList(colorStateList);
            radioButtonProgreso.setButtonTintList(colorStateList);
            radioButtonSinTerminar.setButtonTintList(colorStateList);

            radioButtonRojo.setButtonTintList(colorStateList);
            radioButtonAmarillo.setButtonTintList(colorStateList);
            radioButtonVerde.setButtonTintList(colorStateList);
            radioButtonAzul.setButtonTintList(colorStateList);
            radioButtonMorado.setButtonTintList(colorStateList);
            radioButtonNegro.setButtonTintList(colorStateList);

            //para el btn de Limpiar Filtros:
            btnLimpiarFiltros = modalView.findViewById(R.id.btnLimpiarFiltros);
            btnLimpiarFiltros.setOnClickListener(v -> {

                LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                int padding = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                linearLayoutCont.setPadding(padding, padding, padding, padding);
                linearLayoutCont.setTag("actisDesplegadas");
                linearLayoutCont.removeAllViews();
                for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                    View child = linLayContainAllActis.getChildAt(i);
                    if ("actisDesplegadas".equals(child.getTag())) {
                        linLayContainAllActis.removeViewAt(i);
                    }
                }
                if (layout_esta_semana.getVisibility() == View.GONE) {
                    layout_esta_semana.setVisibility(View.VISIBLE);
                }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                    layout_siguiente_semana.setVisibility(View.VISIBLE);
                }if (layout_mas_tarde.getVisibility() == View.GONE) {
                    layout_mas_tarde.setVisibility(View.VISIBLE);
                }
                modalFiltros.dismiss();
                modalFiltros = null;
            });

            //para manejar algo similar al efecto de un radiogroup
            radioButtonRojo.setOnClickListener(v -> {
                contrRadioButtonSelected(radioButtonRojo);
            });
            radioButtonAmarillo.setOnClickListener(v -> {
                contrRadioButtonSelected(radioButtonAmarillo);
            });
            radioButtonVerde.setOnClickListener(v -> {
                contrRadioButtonSelected(radioButtonVerde);
            });
            radioButtonAzul.setOnClickListener(v -> {
                contrRadioButtonSelected(radioButtonAzul);
            });
            radioButtonMorado.setOnClickListener(v -> {
                contrRadioButtonSelected(radioButtonMorado);
            });
            radioButtonNegro.setOnClickListener(v -> {
                contrRadioButtonSelected(radioButtonNegro);
            });

            radioGroupCatActiFiltros = modalView.findViewById(R.id.radioGroupCatActiFiltros);
            radioGroupRepFiltros = modalView.findViewById(R.id.radioGroupRepFiltros);
            radioGroupEstadoActiFiltr = modalView.findViewById(R.id.radioGroupEstadoActiFiltr);

            numRamitas = modalView.findViewById(R.id.numRamitas);

            txtFechaInicial = modalView.findViewById(R.id.txtFechaInicial);
            txtFechaFinal = modalView.findViewById(R.id.txtFechaFinal);

            //abrir modales de reloj al dar click sobre los textview de fecha incial y final:
            txtFechaInicial.setOnClickListener(this::mostrarDatePickerInicial);
            txtFechaFinal.setOnClickListener(this::mostrarDatePickerFinal);

            radioGroupCatActiFiltros.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == radioButtonSalud.getId()){
                        filtrPorCategoriaActi("Salud");
                        quitarRestoFiltrosCatActi();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("salud");
                    }
                    else if (checkedId == radioButtonProductividad.getId()) {
                        filtrPorCategoriaActi("Productividad");
                        quitarRestoFiltrosCatActi();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("productividad");

                    }
                    else if (checkedId == radioButtonPersonales.getId()) {
                        filtrPorCategoriaActi("Personales");
                        quitarRestoFiltrosCatActi();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("personales");
                    }
                    else if (checkedId == radioButtonSociales.getId()) {
                        filtrPorCategoriaActi("Sociales");
                        quitarRestoFiltrosCatActi();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("sociales");
                    }
                    else if (checkedId == radioButtonFinancieros.getId()) {
                        filtrPorCategoriaActi("Financieros");
                        quitarRestoFiltrosCatActi();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("financieros");

                    }
                    else if (checkedId == radioButtonEmocionales.getId()) {
                        filtrPorCategoriaActi("Emocionales");
                        quitarRestoFiltrosCatActi();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("emocionales");
                    }
                }
            });

            radioGroupRepFiltros.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == radioButtonDiasSemana.getId()){
                        filtrPorRepeticionDiasSemana();
                        quitarRestoFiltrosRepFiltr();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("días de la semana");
                    }else if(checkedId == radioButtonDiasMes.getId()){
                        filtrPorRepeticionDiasMes();
                        quitarRestoFiltrosRepFiltr();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("días del mes");
                    }else if(checkedId == radioButtonIntervalos.getId()){
                        filtrPorRepeticionDiasIntervalos();
                        quitarRestoFiltrosRepFiltr();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("intervalos de días");
                    }
                }
            });

            numRamitas.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    // Si el usuario presiona "Done" o "Enter"
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                        // Realizar la acción que desees, como validar y perder el foco
                        String texto = numRamitas.getText().toString().trim();

                        if (!texto.isEmpty()) {
                            try {
                                int valor = Integer.parseInt(texto);
                                filtrPorNumRamitas(valor); // Función de filtro
                                numRamitas.clearFocus(); // Pierde el foco
                                modalFiltros.hide();
                                quitarRestoFiltrRamitas();
                                desplToastFiltrSelecte(numRamitas.getText() + " ramitas");
                            } catch (NumberFormatException e) {
                            }
                        } else {
                        }

                        // Ocultar el teclado (esto es desde el Fragmento)
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null && getView() != null) {
                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        }

                        return true;
                    }
                    return false;
                }
            });

            radioGroupEstadoActiFiltr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == radioButtonCompletada.getId()){
                        filtrPorEstadoActi("Completada");
                        quitarRestoFiltrosEstados();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("días de la semana");
                    }else if(checkedId == radioButtonProgreso.getId()){
                        filtrPorEstadoActi("En proceso");
                        quitarRestoFiltrosEstados();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("días del mes");
                    }else if(checkedId == radioButtonSinTerminar.getId()){
                        filtrPorEstadoActi("Sin terminar");
                        quitarRestoFiltrosEstados();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("sin terminar");
                    }
                }
            });

            radioButtonRojo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filtrPorColor("rojo");
                        quitarRestoFiltrosColor();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("color rojo");
                    }
                }
            });
            radioButtonVerde.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filtrPorColor("verde");
                        quitarRestoFiltrosColor();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("color verde");
                    }
                }
            });
            radioButtonNegro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filtrPorColor("negro");
                        quitarRestoFiltrosColor();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("color negro");
                    }
                }
            });
            radioButtonMorado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filtrPorColor("morado");
                        quitarRestoFiltrosColor();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("color morado");
                    }
                }
            });
            radioButtonAzul.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filtrPorColor("azul");
                        quitarRestoFiltrosColor();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("color azul");
                    }
                }
            });
            radioButtonAmarillo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        filtrPorColor("amarillo");
                        quitarRestoFiltrosColor();
                        modalFiltros.hide();
                        desplToastFiltrSelecte("color amarillo");
                    }
                }
            });

            // Fechas
            String fechaInicial = txtFechaInicial.getText().toString();
            String fechaFinal = txtFechaFinal.getText().toString();
            if (!fechaInicial.equals("Seleccione una fecha")) {
            }
            if (!fechaFinal.equals("Seleccione una fecha")) {
            }
        }
        modalFiltros.show();
    }

    public void quitarRestoFiltrosCatActi(){
        radioGroupEstadoActiFiltr.clearCheck();
        radioGroupRepFiltros.clearCheck();
        numRamitas.setText("");
        txtFechaInicial.setText("Seleccione una fecha");
        txtFechaFinal.setText("Seleccione una fecha");
        radioButtonAmarillo.setChecked(false);
        radioButtonAzul.setChecked(false);
        radioButtonMorado.setChecked(false);
        radioButtonNegro.setChecked(false);
        radioButtonVerde.setChecked(false);
        radioButtonRojo.setChecked(false);
    }
    public void quitarRestoFiltrosRepFiltr(){
        radioGroupEstadoActiFiltr.clearCheck();
        radioGroupCatActiFiltros.clearCheck();
        numRamitas.setText("");
        txtFechaInicial.setText("Seleccione una fecha");
        txtFechaFinal.setText("Seleccione una fecha");
        radioButtonAmarillo.setChecked(false);
        radioButtonAzul.setChecked(false);
        radioButtonMorado.setChecked(false);
        radioButtonNegro.setChecked(false);
        radioButtonVerde.setChecked(false);
        radioButtonRojo.setChecked(false);
    }
    public void quitarRestoFiltrosEstados(){
        radioGroupCatActiFiltros.clearCheck();
        radioGroupRepFiltros.clearCheck();
        numRamitas.setText("");
        txtFechaInicial.setText("Seleccione una fecha");
        txtFechaFinal.setText("Seleccione una fecha");
        radioButtonAmarillo.setChecked(false);
        radioButtonAzul.setChecked(false);
        radioButtonMorado.setChecked(false);
        radioButtonNegro.setChecked(false);
        radioButtonVerde.setChecked(false);
        radioButtonRojo.setChecked(false);
    }
    public void quitarRestoFiltrRamitas(){
        radioGroupRepFiltros.clearCheck();
        radioGroupCatActiFiltros.clearCheck();
        radioGroupEstadoActiFiltr.clearCheck();
        radioButtonAmarillo.setChecked(false);
        radioButtonAzul.setChecked(false);
        radioButtonMorado.setChecked(false);
        radioButtonNegro.setChecked(false);
        radioButtonVerde.setChecked(false);
        radioButtonRojo.setChecked(false);
    }
    public void quitarRestoFiltrosFechas(){
        radioGroupRepFiltros.clearCheck();
        radioGroupCatActiFiltros.clearCheck();
        radioGroupEstadoActiFiltr.clearCheck();
        numRamitas.setText("");
        radioButtonAmarillo.setChecked(false);
        radioButtonAzul.setChecked(false);
        radioButtonMorado.setChecked(false);
        radioButtonNegro.setChecked(false);
        radioButtonVerde.setChecked(false);
        radioButtonRojo.setChecked(false);
    }
    public void quitarRestoFiltrosColor(){
        radioGroupRepFiltros.clearCheck();
        radioGroupCatActiFiltros.clearCheck();
        radioGroupEstadoActiFiltr.clearCheck();
        numRamitas.setText("");
        txtFechaInicial.setText("Seleccione una fecha");
        txtFechaFinal.setText("Seleccione una fecha");
    }


    private void filtrPorCategoriaActi(String catActi){

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {
                            if (String.valueOf(actividad.getTipoHabito()).toLowerCase().contains(catActi.toLowerCase())) {
                                //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }
                        }
                    }
                    if(catActi.isEmpty()){
                        verifiActiCoincid = false;
                    }


                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    modal.dismiss();
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorNumRamitas(int numRamitas){

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {
                            if (actividad.getNumRamitas() == numRamitas) {
                                //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }
                        }
                    }
                    if(!(numRamitas > 0 && numRamitas <= 100)){
                        verifiActiCoincid = false;
                    }


                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorRepeticionDiasSemana(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {
                            if ((String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Lunes".toLowerCase())) || (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Martes".toLowerCase())) || (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Miércoles".toLowerCase())) || (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Jueves".toLowerCase())) || (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Viernes".toLowerCase())) || (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Sábado".toLowerCase())) || (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("Domingo".toLowerCase()))) {
                                //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }
                        }
                    }

                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorRepeticionDiasMes(){

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {
                            if (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("cada mes")) {
                                //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }
                        }
                    }

                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorRepeticionDiasIntervalos(){

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {
                            if (String.valueOf(actividad.getRepeticiones()).toLowerCase().contains("repetir")) {
                                //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }
                        }
                    }

                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorIntervaloFechas(){
        LocalDate fechaInicioFiltro = LocalDate.parse(txtFechaInicial.getText().toString());
        LocalDate fechaFinFiltro = LocalDate.parse(txtFechaFinal.getText().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {

                            LocalDate fechaInicioAct = LocalDate.parse(actividad.getDiaInicioHabito(), formatter);
                            LocalDate fechaFinAct = LocalDate.parse(actividad.getDiaMetaHabito(), formatter);

                            if (!fechaInicioAct.isAfter(fechaFinFiltro) && !fechaFinAct.isBefore(fechaInicioFiltro)) {
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }

                        }
                    }

                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorEstadoActi(String estado){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {

                            //Se recopila la info
                            String listEstadosActi = String.valueOf(actividad.getEstadosActi());
                            String fechaFin = String.valueOf(actividad.getDiaMetaHabito());

                            //se formate la fecha fin

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate fechaFinAct = LocalDate.parse(fechaFin, formatter);
                            LocalDate hoy = LocalDate.now();

                            String[] valores = listEstadosActi.split(",");

                            boolean todosSonDos = true;
                            boolean hayAlMenosUnCero = false;

                            for (String valor : valores) {
                                if (valor.equals("0")) {
                                    hayAlMenosUnCero = true;
                                    todosSonDos = false;
                                } else if (!valor.equals("2")) {
                                    todosSonDos = false;
                                }
                            }

                            //condicionales para ver en cual de los siguientes entra:

                            if (todosSonDos && estado.equals("Completado")) {
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            } else{
                                if(estado.equals("Sin terminar") && fechaFinAct.isBefore(hoy)) {
                                    actisCoincid.add(actividad);
                                    verifiActiCoincid = true;
                                }else if(estado.equals("En proceso")){
                                    actisCoincid.add(actividad);
                                    verifiActiCoincid = true;
                                }
                            }
                        }
                    }

                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }
    private void filtrPorColor(String color){

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("usrKitCuentaTutor", Context.MODE_PRIVATE);
                    int idKit = sharedPreferences.getInt("idKit", 0);
                    List<Actividad> actisCoincid = new ArrayList<>();
                    Boolean verifiActiCoincid = false;

                    for (Actividad actividad : actividades) {
                        Log.e("DEBUG", "idKit: " + idKit);
                        if (actividad.getIdKit() == idKit) {
                            if (String.valueOf(actividad.getColor()).toLowerCase().equals(color)) {
                                //se agregan las actividades que coincidan y se establece un boolean pa decidir si o si no
                                actisCoincid.add(actividad);
                                verifiActiCoincid = true;
                            }
                        }
                    }

                    LinearLayout linearLayoutCont = new LinearLayout(getActivity());
                    linearLayoutCont.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayoutCont.setOrientation(LinearLayout.VERTICAL);
                    int padding = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    linearLayoutCont.setPadding(padding, padding, padding, padding);
                    linearLayoutCont.setTag("actisDesplegadas");
                    linearLayoutCont.removeAllViews();
                    for (int i = linLayContainAllActis.getChildCount() - 1; i >= 0; i--) {
                        View child = linLayContainAllActis.getChildAt(i);
                        if ("actisDesplegadas".equals(child.getTag())) {
                            linLayContainAllActis.removeViewAt(i);
                        }
                    }

                    if(verifiActiCoincid){
                        //se esconden los linearlayout
                        layout_esta_semana.setVisibility(View.GONE);
                        layout_siguiente_semana.setVisibility(View.GONE);
                        layout_mas_tarde.setVisibility(View.GONE);
                        for(Actividad actividadVer : actisCoincid){
                            Log.d("DEBUG", "Entró al if de acti");
                            LayoutInflater inflater = LayoutInflater.from(requireContext());
                            View actividadView = inflater.inflate(R.layout.item_list_actividades, linearLayoutCont, false);

                            //Inicio código de cargar actividades desde asset, solo copien lo de la imágen, lo demás no es necesario
                            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
                            TextView txtNombre = actividadView.findViewById(R.id.txtTitActi);
                            TextView txtRamitasActi = actividadView.findViewById(R.id.txtRamitasActi);
                            TextView txtIntervaloHrs = actividadView.findViewById(R.id.txtIntervaloHrs);

                            String hraInicial = String.valueOf(actividadVer.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividadVer.getHoraFinHabito());

                            txtIntervaloHrs.setText(hraInicial + " - " + horaFinal);

                            Button btn_ir = actividadView.findViewById(R.id.btn_ir);

                            String imgBd = actividadVer.getRutaImagenHabito();
                            Log.d("DEBUG", "valor ruta imágen: " + imgBd);

                            String imageName = doesImageExist(requireContext(), imgBd);
                            if (imageName != null) {
                                InputStream inputStream = null;
                                String assetPath = "img/img_actividades/" + imageName; // Asegúrate de que la ruta esté correcta
                                Log.d("DEBUG", "Intentando abrir archivo: " + assetPath);

                                try {
                                    inputStream = requireContext().getAssets().open(assetPath);
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


                            String nombreHabito = String.valueOf(actividadVer.getNombreHabito());
                            int numRamitas = actividadVer.getNumRamitas();
                            String stringNumRamitas = String.valueOf(numRamitas);

                            txtNombre.setText(nombreHabito);
                            txtRamitasActi.setText(stringNumRamitas + " ramitas");

                            //Esta es la lógica del botón de "Ir" de cada acti que despliega la información de cada acti en la parte inferior
                            //de la pantalla, se tiene que inflar el xml del diseño y sustituir valores
                            btn_ir.setOnClickListener(v -> {
                                //Sesión para que se despliegue la acti seleccionada el editar, recargar, abrir, etc.
                                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences2.edit();
                                editor.putBoolean("sesion_activa", true);
                                editor.apply();

                                BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                                View view = getLayoutInflater().inflate(R.layout.bottom_modal_view, null);
                                modal.setContentView(view);

                                modal.setOnDismissListener(dialog -> {
                                    // Aquí se cambia el valor de la sesión cuando se cierra
                                    SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                                    SharedPreferences.Editor editorCerrar = sharedPreferencesCerrar.edit();
                                    editorCerrar.putBoolean("sesion_activa", false);
                                    editorCerrar.apply();
                                });

                                // Obtener referencias de los elementos
                                TextView txtTitle = view.findViewById(R.id.txtTitle);
                                TextView txtTipoHabito = view.findViewById(R.id.txtTipoHabito);
                                ImageView imgActiModal = view.findViewById(R.id.imgActividad);
                                TextView numRamitasModal = view.findViewById(R.id.numRamitas);
                                ImageView btnVerMasInfoActi = view.findViewById(R.id.btnVerMasInfoActi);

                                //Aquí va lo que pasa cuando quiere ver más información de la acti:
                                btnVerMasInfoActi.setOnClickListener(v1 -> {
                                    Intent intent = new Intent(requireActivity(), VerMasInfoActi.class);
                                    startActivity(intent);
                                });

                                LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                                LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                                btnEditActi.setOnClickListener(v1 -> {
                                    //Aquí va lo que pasa cuando quiere editar el modal
                                    SharedPreferences preferences = requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.putInt("idActividad", actividadVer.getIdActividad());
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
                                        borrarActividad(actividadVer.getIdActividad());

                                    });
                                    txtDialogTitle.setText("¡Atención!");
                                    txtDialogMessage.setText("Estás a punto de borrar el hábito: " + "'"+actividadVer.getNombreHabito() + "'" + " si aceptas no se podrá deshacer la acción.");

                                    btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                                    modalBorrar.show();
                                });

                                String nomHabito = String.valueOf(actividadVer.getNombreHabito());
                                String tipHabit = String.valueOf(actividadVer.getTipoHabito());
                                int numeroRamitas = actividadVer.getNumRamitas();
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
                                editor3.putInt("idActividad", actividadVer.getIdActividad());
                                editor3.apply();
                            });
                            //Fin de la lógica de desplegar modal

                            // Agregar la vista al contenedor
                            if (linearLayoutCont.getParent() != null) {
                                ((ViewGroup) linearLayoutCont.getParent()).removeView(linearLayoutCont);
                            }
                            linearLayoutCont.addView(actividadView);
                            linLayContainAllActis.addView(linearLayoutCont);

                            Log.e("DEBUG", "Cont: " + linearLayoutCont);
                        }
                    }else{
                        //se comprueba si están desplegadas y luego se despliegan en caso de que no
                        if (layout_esta_semana.getVisibility() == View.GONE) {
                            layout_esta_semana.setVisibility(View.VISIBLE);
                        }if (layout_siguiente_semana.getVisibility() == View.GONE) {
                            layout_siguiente_semana.setVisibility(View.VISIBLE);
                        }if (layout_mas_tarde.getVisibility() == View.GONE) {
                            layout_mas_tarde.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
    }


    private void desplToastFiltrSelecte(String txtMostrarToast){
        //Inicio del código para mostrar el toast personalizado
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_personalizado, null);

        //Inicio de código para cambiar elementos del toast personalizado

        //Se cambia la imágen
        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(R.drawable.img_tachuela);

        //Se cambia el texto
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText("Actividad filtrada por hábitos de " + txtMostrarToast);

        //Se cambia el color de fondo
        Drawable background = layout.getBackground();
        background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

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
    }

    private void contrRadioButtonSelected(RadioButton selectedRadioButton) {
        radioButtonRojo.setChecked(false);
        radioButtonAmarillo.setChecked(false);
        radioButtonVerde.setChecked(false);
        radioButtonAzul.setChecked(false);
        radioButtonMorado.setChecked(false);
        radioButtonNegro.setChecked(false);

        selectedRadioButton.setChecked(true);
    }
    private void mostrarDatePickerInicial(View view) {
        // Obtener la fecha actual del dispositivo
        android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance(); // Usando la zona horaria y el Locale del dispositivo por defecto
        int year = calendar.get(android.icu.util.Calendar.YEAR);
        int month = calendar.get(android.icu.util.Calendar.MONTH);
        int day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH);

        // Establecer la fecha mínima que será la fecha actual (reseteando la hora)
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(android.icu.util.Calendar.MILLISECOND, 0); // Asegurarse de que los milisegundos sean 0
        long todayInMillis = calendar.getTimeInMillis();

        // Crear el modal para mostrar las fechas disponibles en el calendario
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione una fecha")
                .setTheme(R.style.CustomMaterialDatePickerTheme)
                .setSelection(todayInMillis) // Preseleccionar la fecha actual
                .setCalendarConstraints(
                        new CalendarConstraints.Builder()
                                .build()
                )
                .build();

        datePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Se ajusta la fecha seleccionada
            android.icu.util.Calendar selectedCalendar = android.icu.util.Calendar.getInstance();
            selectedCalendar.setTimeInMillis(selection);

            selectedCalendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 0);
            selectedCalendar.set(android.icu.util.Calendar.MINUTE, 0);
            selectedCalendar.set(android.icu.util.Calendar.SECOND, 0);
            selectedCalendar.set(android.icu.util.Calendar.MILLISECOND, 0);

            selectedCalendar.add(android.icu.util.Calendar.DAY_OF_MONTH, 1);

            android.icu.util.Calendar todayCalendar = android.icu.util.Calendar.getInstance();
            todayCalendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(android.icu.util.Calendar.MINUTE, 0);
            todayCalendar.set(android.icu.util.Calendar.SECOND, 0);
            todayCalendar.set(android.icu.util.Calendar.MILLISECOND, 0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selectedCalendar.getTime());

            // Se pone la fecha en el TextView
            txtFechaInicial.setText(formattedDate);

            if(!(txtFechaFinal.getText().toString().equals("Seleccione una fecha"))){
                filtrPorIntervaloFechas();
                quitarRestoFiltrosFechas();
                modalFiltros.hide();
                desplToastFiltrSelecte("intervalo de fechas");
            }
        });
    }

    private void mostrarDatePickerFinal(View view) {
        // Obtener la fecha actual del dispositivo
        android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance(); // Usando la zona horaria y el Locale del dispositivo por defecto
        int year = calendar.get(android.icu.util.Calendar.YEAR);
        int month = calendar.get(android.icu.util.Calendar.MONTH);
        int day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH);

        // Establecer la fecha mínima que será la fecha actual (reseteando la hora)
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(android.icu.util.Calendar.MILLISECOND, 0); // Asegurarse de que los milisegundos sean 0
        long todayInMillis = calendar.getTimeInMillis();

        // Crear el modal para mostrar las fechas disponibles en el calendario
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione una fecha")
                .setTheme(R.style.CustomMaterialDatePickerTheme)
                .setSelection(todayInMillis) // Preseleccionar la fecha actual
                .setCalendarConstraints(
                        new CalendarConstraints.Builder()
                                .build()
                )
                .build();

        datePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Se ajusta la fecha seleccionada
            android.icu.util.Calendar selectedCalendar = android.icu.util.Calendar.getInstance();
            selectedCalendar.setTimeInMillis(selection);

            selectedCalendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 0);
            selectedCalendar.set(android.icu.util.Calendar.MINUTE, 0);
            selectedCalendar.set(android.icu.util.Calendar.SECOND, 0);
            selectedCalendar.set(android.icu.util.Calendar.MILLISECOND, 0);

            selectedCalendar.add(android.icu.util.Calendar.DAY_OF_MONTH, 1);

            android.icu.util.Calendar todayCalendar = android.icu.util.Calendar.getInstance();
            todayCalendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(android.icu.util.Calendar.MINUTE, 0);
            todayCalendar.set(android.icu.util.Calendar.SECOND, 0);
            todayCalendar.set(android.icu.util.Calendar.MILLISECOND, 0);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selectedCalendar.getTime());

            // Se pone la fecha en el TextView
            txtFechaFinal.setText(formattedDate);

            if(!(txtFechaInicial.getText().toString().equals("Seleccione una fecha"))){
                filtrPorIntervaloFechas();
                quitarRestoFiltrosFechas();
                modalFiltros.hide();
                desplToastFiltrSelecte("intervalo de fechas");
            }
        });
    }

}