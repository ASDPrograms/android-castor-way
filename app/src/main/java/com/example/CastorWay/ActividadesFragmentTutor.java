package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import android.util.Log;
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

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.drawable.Drawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActividadesFragmentTutor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActividadesFragmentTutor extends Fragment {
    LinearLayout layout_esta_semana, layout_siguiente_semana, layout_mas_tarde, contenedor_despues, contenedor_esta_semana, contenedor_siguiente_semana, layout_no_usr_kit_seleccionado;
    TextView numActisEstaSemanaTxt, numActisSigSemanaTxt, numActisMasTardeTxt;
    ImageView imgFlechEstaSemana, imgFlechSigSemana, imgFlechMasTarde, btnAgregarActi;

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

        // Ahora las vistas están inicializadas correctamente
        layout_esta_semana = view.findViewById(R.id.layout_esta_semana);
        layout_siguiente_semana = view.findViewById(R.id.layout_siguiente_semana);
        layout_mas_tarde = view.findViewById(R.id.layout_mas_tarde);
        layout_no_usr_kit_seleccionado = view.findViewById(R.id.layout_no_usr_kit_seleccionado);

        numActisEstaSemanaTxt = view.findViewById(R.id.numActisEstaSemana);
        numActisSigSemanaTxt = view.findViewById(R.id.numActisSigSemana);
        numActisMasTardeTxt = view.findViewById(R.id.numActisMasTarde);

        contenedor_despues = view.findViewById(R.id.contenedor_despues);
        contenedor_esta_semana = view.findViewById(R.id.contenedor_esta_semana);
        contenedor_siguiente_semana = view.findViewById(R.id.contenedor_siguiente_semana);

        btnAgregarActi = view.findViewById(R.id.btnAgregarActi);

        imgFlechEstaSemana = view.findViewById(R.id.imgFlechEstaSemana);
        imgFlechSigSemana = view.findViewById(R.id.imgFlechSigSemana);
        imgFlechMasTarde = view.findViewById(R.id.imgFlechSigMasTarde);

        // Ahora puedes configurar listeners
        layout_esta_semana.setOnClickListener(v -> alternarVisibActis(contenedor_esta_semana));
        layout_siguiente_semana.setOnClickListener(v -> alternarVisibActis(contenedor_siguiente_semana));
        layout_mas_tarde.setOnClickListener(v -> alternarVisibActis(contenedor_despues));

        confirmUsrKitSeleccionado();
        //Btn que se encarga de abrir modal para agregar nueva acti, al dar click manda a otra acti sin terminar este fragment para que al regresar mande al fragment de nuevo
        btnAgregarActi.setOnClickListener(v -> {
            int num = confirmUsrKitSeleccionado();
            if(num != 0){
                Intent intent = new Intent(requireActivity(), AgregarActiTutor.class);
                startActivity(intent);
            }
        });
        actuNumActis();
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

        // Se consultan todas las actividades del hijo seleccionado
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
                            Log.e("DEBUG", String.valueOf(actividad.getIdKit() == finalIdKit));
                            if (actividad.getIdKit() == finalIdKit) {
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

        int idKit = confirmUsrKitSeleccionado();

        if(idKit != 0){
            Log.d("DEBUG", "Si hay idKit");
            ApiService apiService2 = RetrofitClient.getApiService();
            Call<List<Actividad>> call2 = apiService2.getAllActividades();
            int finalIdKit = idKit;
            call2.enqueue(new Callback<List<Actividad>>() {
                @Override
                public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                    List<Actividad> actividades = response.body();
                    AtomicInteger numActisEstaSemana = new AtomicInteger(0);
                    AtomicInteger numActisSigSemana = new AtomicInteger(0);
                    AtomicInteger numActisMasTarde = new AtomicInteger(0);

                    if (actividades != null) {
                        Log.e("DEBUG", "Hay actis");
                        for (Actividad actividad : actividades) {
                            Log.e("DEBUG", String.valueOf(actividad.getIdKit() == finalIdKit));
                            if (actividad.getIdKit() == finalIdKit) {
                                Log.d("DEBUG", "Entró al if de acti");

                                String verifi = obtenerPeriodo(actividad.getFechasActividad());

                                if(verifi.equals("No hay fechas disponibles en el futuro")){
                                    continue;
                                }else if(verifi.equals("contenedor_esta_semana")){
                                    numActisEstaSemana.incrementAndGet();
                                }else if(verifi.equals("contenedor_siguiente_semana")){
                                    numActisSigSemana.incrementAndGet();
                                }else if(verifi.equals("contenedor_despues")){
                                    numActisMasTarde.incrementAndGet();
                                }
                            }
                        }
                    }
                    numActisEstaSemanaTxt.setText(numActisEstaSemana.toString());
                    numActisSigSemanaTxt.setText(numActisSigSemana.toString());
                    numActisMasTardeTxt.setText(numActisMasTarde.toString());
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
}