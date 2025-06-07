package com.example.castorway;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Chat;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragmentKit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragmentKit extends Fragment implements ChatSocketListenerKit{

    private ChatSocketKit chatManager;
    private ImageView btnRegresarActi, btnEnviarMsj, btnEmojis;
    private TextView nombreUsuarioChat, txtMsjMandar, txtEstadoConexion;
    private LinearLayout containBtnEnviarMsj;
    private LinearLayout contenedorMensajes;
    private ScrollView scrollView;
    private String ultFecha;
    private View circuloEstadoConeccion;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private final Map<Integer, String> emojiHtmlMap = new HashMap<Integer, String>() {{
        put(R.drawable.aburrido, "aburrido");
        put(R.drawable.aceptado, "aceptado");
        put(R.drawable.agresivo, "agresivo");
        put(R.drawable.alegre, "alegre");
        put(R.drawable.angelical, "angelical");
        put(R.drawable.ansioso, "ansioso");
        put(R.drawable.apatico, "apatico");
        put(R.drawable.apenado, "apenado");
        put(R.drawable.asqueado, "asqueado");
        put(R.drawable.asustado, "asustado");
        put(R.drawable.bromista, "bromista");
        put(R.drawable.cansado, "cansado");
        put(R.drawable.confundido, "confundido");
        put(R.drawable.cool, "cool");
        put(R.drawable.divertido, "divertido");
        put(R.drawable.enamorado, "enamorado");
        put(R.drawable.enfermo, "enfermo");
        put(R.drawable.entusiasmado, "entusiasmado");
        put(R.drawable.herido, "herido");
        put(R.drawable.inseguro, "inseguro");
        put(R.drawable.intelectual, "intelectual");
        put(R.drawable.lleno_de_odio, "lleno_de_odio");
        put(R.drawable.llorando, "llorando");
        put(R.drawable.mareado, "mareado");
        put(R.drawable.normal, "normal");
        put(R.drawable.paniqueado, "paniqueado");
        put(R.drawable.pensativo, "pensativo");
        put(R.drawable.poderoso, "poderoso");
        put(R.drawable.rechazado, "rechazado");
        put(R.drawable.solo, "solo");
    }};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragmentKit() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        if (chatManager != null) {
            ChatSocketKit.getInstance().cerrarConexion();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatManager != null) {
            iniciarConexion();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chatManager != null) {
            ChatSocketKit.getInstance().cerrarConexion();
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragmentKit.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragmentKit newInstance(String param1, String param2) {
        ChatFragmentKit fragment = new ChatFragmentKit();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_kit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferencesKit = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String nombreUsuarioKitVer = preferencesKit.getString("nombreUsuario", "");

        if (nombreUsuarioKitVer != null && !nombreUsuarioKitVer.isEmpty()) {
            SharedPreferences preferences = getContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);

            // Verificamos si ya se ha guardado el ID del Kit en SharedPreferences para evitar la consulta
            final int[] idKit = {preferences.getInt("idKit", -1)};  // Si no existe, idKit será -1
            // Si no se ha guardado, hacemos la consulta a la API
            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Kit>> call = apiService.getAllKits();
            call.enqueue(new Callback<List<Kit>>() {
                @Override
                public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                    List<Kit> kits = response.body();
                    if (kits != null) {
                        for (Kit kit : kits) {
                            if (kit.getNombreUsuario().equals(nombreUsuarioKitVer)) {
                                idKit[0] = kit.getIdKit();
                                String codPresa = String.valueOf(kit.getCodPresa());

                                Log.d("WebSocket", "codPresa: " + codPresa);

                                // Guardamos el idKit en SharedPreferences
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("idKit", idKit[0]);
                                editor.apply();
                                consultarCastor(codPresa, view);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Kit>> call, Throwable t) {
                    // Manejo de errores
                }
            });

        }
    }
    private void iniciarApp(View view){
        contenedorMensajes = view.findViewById(R.id.contMensajes);
        scrollView = view.findViewById(R.id.scrollMensajesChat);

        //se declara el view pa q cambie de color el ícono y el txt de la conexión:
        circuloEstadoConeccion = view.findViewById(R.id.circuloEstadoConeccion);
        txtEstadoConexion = view.findViewById(R.id.txtEstadoConexion);

        //se hace inicia la conexión al socket:
        iniciarConexion();

        nombreUsuarioChat = view.findViewById(R.id.nombreUsuarioChat);

        desplMsj();

        mostrarNombrUsr();

        //por default va a estar escondido el linearlayout, hasta que esté actividado
        //para poder escribir
        containBtnEnviarMsj = view.findViewById(R.id.containBtnEnviarMsj);
        containBtnEnviarMsj.setVisibility(View.GONE);

        //listener al input de escribir:
        txtMsjMandar = view.findViewById(R.id.txtMsjMandar);
        txtMsjMandar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = (Editable) txtMsjMandar.getText();

                boolean hasText = editable.toString().trim().length() > 0;
                boolean hasImageSpans = editable.getSpans(0, editable.length(), ImageSpan.class).length > 0;

                if (hasText || hasImageSpans) {
                    containBtnEnviarMsj.setVisibility(View.VISIBLE);
                } else {
                    containBtnEnviarMsj.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //listener al botón de envíar mensaje
        btnEnviarMsj = view.findViewById(R.id.btnEnviarMsj);
        btnEnviarMsj.setOnClickListener(v -> {
            mandarMensaje();
        });

        View panelEmojis = view.findViewById(R.id.panelEmojis);
        View mainContent = view.findViewById(R.id.main);

        bottomSheetBehavior = BottomSheetBehavior.from(panelEmojis);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // Cuando se oculta el modal, regresa el contenido a su posición original
                    mainContent.setTranslationY(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Mueve el contenido principal hacia arriba a medida que se abre el BottomSheet
                mainContent.setTranslationY(-bottomSheet.getHeight() * slideOffset);
            }
        });

        btnEmojis = view.findViewById(R.id.btnEmojis);

        btnEmojis.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                // Primero oculta el teclado y cuando esté oculto abre el BottomSheet
                hideKeyboardWithCallback(() -> {
                    mostrarModalEmojis(view); // Cargar emojis
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                });
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        final View activityRootView = view.findViewById(R.id.rootCoordinator);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            activityRootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = activityRootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            boolean isKeyboardVisible = keypadHeight > screenHeight * 0.15;

            if (isKeyboardVisible) {
                // Si el teclado se abre y el bottomsheet está visible, se oculta
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        final View rootView = view.findViewById(R.id.rootCoordinator);
        final LinearLayout contMensajes = view.findViewById(R.id.contMensajes);
        final ScrollView scrollView = view.findViewById(R.id.scrollMensajesChat);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean isKeyboardShown = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean keyboardNowVisible = keypadHeight > screenHeight * 0.15;

                if (keyboardNowVisible != isKeyboardShown) {
                    isKeyboardShown = keyboardNowVisible;

                    if (isKeyboardShown) {
                        // Teclado abierto - quita el mínimo para que no quede espacio
                        contMensajes.setMinimumHeight(0);
                    } else {
                        // Teclado cerrado - restablece el mínimo para llenar el espacio
                        contMensajes.setMinimumHeight(scrollView.getHeight());
                    }
                }
            }
        });
    }

    private void mandarMensaje() {
        //se obtienen los datos de la sesión y todos los que se pasarán al socket:
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        int idCastor = sharedPreferences.getInt("idCastor", 0);
        String txtMsj = txtMsjMandar.getText().toString().trim();
        String emisor = "Kit";
        String estado = "enviado";

        Log.d("WebSocket", "idKit: " + idKit + " idCastor:" + idCastor + " txtMsj: " + txtMsj);

        try {
            JSONObject mensajeObj = new JSONObject();
            mensajeObj.put("idCastor", idCastor);
            mensajeObj.put("idKit", idKit);
            mensajeObj.put("txtMsj", txtMsj);
            mensajeObj.put("emisor", emisor);
            mensajeObj.put("estado", estado);

            String mensajeJson = mensajeObj.toString();
            chatManager.enviarMensaje(mensajeJson);

            // (opcional) Limpia el campo de texto
            txtMsjMandar.setText("");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onNuevoMensaje(String emisor, String mensaje, String hora, int idKit){
        requireActivity().runOnUiThread(() -> {

            Log.d("ChatUI", "Nuevo mensaje de " + emisor + ": " + mensaje + ". Hora: " + hora);

            //se infla la vista que tiene la estructura de la burbuja de mensaje del chat
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.estructura_mensaje_chat, contenedorMensajes, false);

            TextView textoMensaje = view.findViewById(R.id.textoMensaje);
            LinearLayout burbujaMsjChat = view.findViewById(R.id.burbujaMsjChat);
            TextView horaMsj = view.findViewById(R.id.horaMsj);
            textoMensaje.setText(mensaje);

            if (mensaje.contains("<img src=") && mensaje.contains("emoji-in-text")) {
                burbujaMsjChat.removeView(textoMensaje);

                TextView textView = new TextView(requireContext());
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gayathri_bold));
                textView.setGravity(Gravity.START);
                if(emisor.equals("Castor")){
                    textView.setRotationY(180f);  // gira texto 180 grados ejeY
                }
                textView.setLineSpacing(0, 1.2f);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE);
                }

                Html.ImageGetter imageGetter = new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        String nombre = source.substring(source.lastIndexOf('/') + 1)
                                .replace(".svg", "")
                                .toLowerCase()
                                .trim();

                        int resId = getResources().getIdentifier(nombre, "drawable", requireContext().getPackageName());
                        if (resId != 0) {
                            Drawable drawable = ContextCompat.getDrawable(requireContext(), resId);
                            if (drawable != null) {
                                float density = getResources().getDisplayMetrics().density;
                                int sizePx = (int) (50 * density);

                                int marginPx = (int) (4 * density);  // Margen deseado

                                // Ajusta el drawable para que su tamaño sea menor que el total (dejando espacio para margen)
                                drawable.setBounds(0, 0, sizePx - 2 * marginPx, sizePx - 2 * marginPx);

                                // Usa InsetDrawable para simular el margen (espacio alrededor)
                                Drawable marginDrawable = new InsetDrawable(drawable, marginPx, 10, marginPx, 10);

                                // El bounds del drawable completo con margen (ancho con margen incluido, alto sin margen vertical)
                                marginDrawable.setBounds(0, 0, sizePx, sizePx - 2 * marginPx);

                                return marginDrawable;
                            }
                        }
                        return null;
                    }
                };

                Spanned spanned;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    spanned = Html.fromHtml(mensaje, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
                } else {
                    spanned = Html.fromHtml(mensaje, imageGetter, null);
                }

                textView.setText(spanned);
                burbujaMsjChat.addView(textView);
            }


            if(emisor.equals("Castor")){
                Log.d("ChatUI", "Es de Castor");

                //en caso de que el mensaje lo mandó el hijo, se cambie el color del mensaje
                //se voltea la burbuja y se voltea el texto porque se gira completo al girar la burbuja
                LinearLayout linearLayoutContMsj = view.findViewById(R.id.linearLayoutContMsj);

                burbujaMsjChat.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gricecito_chat));
                burbujaMsjChat.setScaleX(-1);
                textoMensaje.setScaleX(-1);

                //se pone al inicio pq es del hijo
                linearLayoutContMsj.setGravity(Gravity.START);
            }

            // Se recupera la fecha para poder comparar
            String soloFecha = hora.split(" ")[0];
            String soloHora = hora.split(" ")[1];

            String horaFormateada = formatearHoraADoce(soloHora);

            horaMsj.setText(horaFormateada);

            SimpleDateFormat formatoComparacion = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date fechaDate = null;
            try {
                fechaDate = formatoComparacion.parse(soloFecha);
            } catch (ParseException e) {
                e.printStackTrace();
                return; // Si falla, no procesar
            }

            Calendar calHoy = Calendar.getInstance();
            // Se crean calendarios para hoy, ayer y antier
            Calendar calMsj = Calendar.getInstance();
            calMsj.setTime(fechaDate);

            Calendar calAyer = Calendar.getInstance();
            calAyer.add(Calendar.DAY_OF_YEAR, -1);

            Calendar calAntier = Calendar.getInstance();
            calAntier.add(Calendar.DAY_OF_YEAR, -2);

            // Formatear para comparar solo año-mes-día
            String fechaComparacionActual = formatoComparacion.format(fechaDate);
            String fechaHoy = formatoComparacion.format(calHoy.getTime());
            String fechaAyer = formatoComparacion.format(calAyer.getTime());
            String fechaAntier = formatoComparacion.format(calAntier.getTime());

            // Se revisa el cambio de fecha, si hay cambio, se pone la barra
            if (ultFecha == null || !fechaComparacionActual.equals(ultFecha)) {
                ultFecha = fechaComparacionActual;
                View separadorFecha = LayoutInflater.from(requireContext()).inflate(R.layout.separador_fechas_chat, null);
                TextView textoFecha = separadorFecha.findViewById(R.id.textoFechaSeparador);

                // Asignar "Hoy", "Ayer", "Antier" o fecha
                String textoFechaMostrar;
                if (fechaComparacionActual.equals(fechaHoy)) {
                    textoFechaMostrar = "Hoy";
                } else if (fechaComparacionActual.equals(fechaAyer)) {
                    textoFechaMostrar = "Ayer";
                } else if (fechaComparacionActual.equals(fechaAntier)) {
                    textoFechaMostrar = "Antier";
                } else {
                    textoFechaMostrar = fechaComparacionActual;
                }

                textoFecha.setText(textoFechaMostrar);
                contenedorMensajes.addView(separadorFecha);
            }
            Log.d("ChatUI", "Agregado mensaje");
            contenedorMensajes.addView(view);
            contenedorMensajes.post(() -> {
                contenedorMensajes.getParent().requestChildFocus(view, view);
            });
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        });
    }

    @Override
    public void onEstadoConexionActualizado(int userId, boolean conectado) {
        requireActivity().runOnUiThread(() -> {
            Log.d("WebSocket", "Usuario " + userId + (conectado ? " conectado" : " desconectado"));
            actualizarEstadoConexion(userId, conectado);
        });
    }

    @Override
    public void onErrorAlParsearMensaje() {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Error al procesar el mensaje", Toast.LENGTH_SHORT).show();
        });
    }

    private void consultarCastor(String codPresa, View view){
        Log.d("WebSocket", "codPresa al ser recibido: " + codPresa);
        SharedPreferences preferences = getContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();
        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castors = response.body();
                if (castors != null) {
                    for (Castor castor : castors) {
                        if (castor.getCodPresa().equals(codPresa)) {
                            int idCastor = castor.getIdCastor();
                            String nombre = String.valueOf(castor.getNombre());
                            Boolean estadoConect = castor.getEstadoConect();

                            // Guardamos el idKit en SharedPreferences
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("idCastor", idCastor);
                            editor.putString("nombre", nombre);
                            editor.putBoolean("estadoConect", estadoConect);
                            editor.apply();
                        }
                    }
                }
                // se guarda el idKit y el idCastor y luego se inicia la app
                iniciarApp(view); // Llamada a tu método de inicialización de la app
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {

            }
        });

    }

    private void iniciarConexion() {
        chatManager = ChatSocketKit.getInstance();
        chatManager.setChatSocketListenerKit(ChatFragmentKit.this);

        SharedPreferences preferencesKit = getContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);
        int idKit = preferencesKit.getInt("idKit", 0);
        int idCastor = preferencesKit.getInt("idCastor", 0);


        // Se hace la conexión pasando los parámetros que se ocupan
        chatManager.iniciarConexion(String.valueOf(idKit), "Kit", String.valueOf(idCastor));
    }

    private void desplMsj(){
        //Se obtienen los datos de idKit y de idCastor de la sesión:
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        int idCastor = sharedPreferences.getInt("idCastor", 0);

        Log.d("WebSocket", "Desplegando mensajes para idKit con id: " + idKit + " e idCastor con id: " + idCastor);
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Chat>> call = apiService.getAllChats();
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                List<Chat> chats = response.body();
                if (chats != null) {
                    String fechaAnterior = null;
                    for (Chat chat : chats) {
                        if(chat.getIdKit() == idKit && chat.getIdCastor() == idCastor){
                            //se infla la vista que tiene la estructura de la burbuja de mensaje del chat
                            View view = LayoutInflater.from(requireContext()).inflate(R.layout.estructura_mensaje_chat, contenedorMensajes, false);
                            LinearLayout burbujaMsjChat = view.findViewById(R.id.burbujaMsjChat);

                            //se reemplaza el valor del mensaje por el de la bd

                            //pero antes se verifica el tipo de emisor pa ver la posición del mensaje y el color:
                            String tipoEmisor = String.valueOf(chat.getEmisor());
                            TextView textoMensaje = view.findViewById(R.id.textoMensaje);
                            TextView horaMsj = view.findViewById(R.id.horaMsj);

                            String msjTxt = String.valueOf(chat.getContenido());
                            textoMensaje.setText(msjTxt);
                            Log.d("CHAT", tipoEmisor);

                            //para verificar que sea un emoji o no:
                            if (msjTxt.contains("<img src=") && msjTxt.contains("emoji-in-text")) {
                                burbujaMsjChat.removeView(textoMensaje);

                                TextView textView = new TextView(requireContext());
                                textView.setTextColor(getResources().getColor(R.color.black));
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gayathri_bold));
                                textView.setGravity(Gravity.START);
                                if(String.valueOf(chat.getEmisor()).equals("Castor")){
                                    textView.setRotationY(180f);  // gira texto 180 grados ejeY
                                }
                                textView.setLineSpacing(0, 1.2f);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE);
                                }

                                Html.ImageGetter imageGetter = new Html.ImageGetter() {
                                    @Override
                                    public Drawable getDrawable(String source) {
                                        String nombre = source.substring(source.lastIndexOf('/') + 1)
                                                .replace(".svg", "")
                                                .toLowerCase()
                                                .trim();

                                        int resId = getResources().getIdentifier(nombre, "drawable", requireContext().getPackageName());
                                        if (resId != 0) {
                                            Drawable drawable = ContextCompat.getDrawable(requireContext(), resId);
                                            if (drawable != null) {
                                                float density = getResources().getDisplayMetrics().density;
                                                int sizePx = (int) (50 * density);

                                                int marginPx = (int) (4 * density);  // Margen deseado

                                                // Ajusta el drawable para que su tamaño sea menor que el total (dejando espacio para margen)
                                                drawable.setBounds(0, 0, sizePx - 2 * marginPx, sizePx - 2 * marginPx);

                                                // Usa InsetDrawable para simular el margen (espacio alrededor)
                                                Drawable marginDrawable = new InsetDrawable(drawable, marginPx, 10, marginPx, 10);

                                                // El bounds del drawable completo con margen (ancho con margen incluido, alto sin margen vertical)
                                                marginDrawable.setBounds(0, 0, sizePx, sizePx - 2 * marginPx);

                                                return marginDrawable;
                                            }
                                        }
                                        return null;
                                    }
                                };

                                Spanned spanned;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    spanned = Html.fromHtml(msjTxt, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
                                } else {
                                    spanned = Html.fromHtml(msjTxt, imageGetter, null);
                                }

                                textView.setText(spanned);
                                burbujaMsjChat.addView(textView);
                            }

                            if(tipoEmisor.equals("Castor")){
                                //en caso de que el mensaje lo mandó el hijo, se cambie el color del mensaje
                                //se voltea la burbuja y se voltea el texto porque se gira completo al girar la burbuja
                                LinearLayout linearLayoutContMsj = view.findViewById(R.id.linearLayoutContMsj);

                                burbujaMsjChat.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gricecito_chat));
                                burbujaMsjChat.setScaleX(-1);
                                textoMensaje.setScaleX(-1);

                                //se pone al inicio pq es del hijo
                                linearLayoutContMsj.setGravity(Gravity.START);
                            }

                            // Se recupera la fecha para poder comparar
                            String fechaMsj = String.valueOf(chat.getFechaEnvio());
                            String soloFecha = fechaMsj.split(" ")[0];
                            String soloHora = fechaMsj.split(" ")[1];

                            String horaFormateada = formatearHoraADoce(soloHora);
                            horaMsj.setText(horaFormateada);

                            SimpleDateFormat formatoComparacion = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date fechaDate = null;
                            try {
                                fechaDate = formatoComparacion.parse(soloFecha);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return; // Si falla, no procesar
                            }

                            Calendar calHoy = Calendar.getInstance();
                            // Se crean calendarios para hoy, ayer y antier
                            Calendar calMsj = Calendar.getInstance();
                            calMsj.setTime(fechaDate);

                            Calendar calAyer = Calendar.getInstance();
                            calAyer.add(Calendar.DAY_OF_YEAR, -1);

                            Calendar calAntier = Calendar.getInstance();
                            calAntier.add(Calendar.DAY_OF_YEAR, -2);

                            // Formatear para comparar solo año-mes-día
                            String fechaComparacionActual = formatoComparacion.format(fechaDate);
                            String fechaHoy = formatoComparacion.format(calHoy.getTime());
                            String fechaAyer = formatoComparacion.format(calAyer.getTime());
                            String fechaAntier = formatoComparacion.format(calAntier.getTime());

                            ultFecha = fechaComparacionActual;

                            // Se revisa el cambio de fecha, si hay cambio, se pone la barra
                            if (fechaAnterior == null || !fechaComparacionActual.equals(fechaAnterior)) {
                                fechaAnterior = fechaComparacionActual;
                                View separadorFecha = LayoutInflater.from(requireContext()).inflate(R.layout.separador_fechas_chat, null);
                                TextView textoFecha = separadorFecha.findViewById(R.id.textoFechaSeparador);

                                // Asignar "Hoy", "Ayer", "Antier" o fecha
                                String textoFechaMostrar;
                                if (fechaComparacionActual.equals(fechaHoy)) {
                                    textoFechaMostrar = "Hoy";
                                } else if (fechaComparacionActual.equals(fechaAyer)) {
                                    textoFechaMostrar = "Ayer";
                                } else if (fechaComparacionActual.equals(fechaAntier)) {
                                    textoFechaMostrar = "Antier";
                                } else {
                                    textoFechaMostrar = fechaComparacionActual; // Si no, muestra la fecha
                                }

                                textoFecha.setText(textoFechaMostrar);
                                contenedorMensajes.addView(separadorFecha);
                            }

                            contenedorMensajes.addView(view);
                        }
                    }
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    new Handler().postDelayed(() -> scrollView.fullScroll(View.FOCUS_DOWN), 100);
                    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    });
                    contenedorMensajes.getLayoutParams().height = scrollView.getHeight();
                    contenedorMensajes.requestLayout();

                    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                        int scrollHeight = scrollView.getHeight();
                        int contenidoAltura = contenedorMensajes.getHeight();

                        if (contenidoAltura < scrollHeight) {
                            contenedorMensajes.setMinimumHeight(scrollHeight);
                        }
                    });

                    scrollView.post(() -> {
                        contenedorMensajes.setMinimumHeight(scrollView.getHeight()); // ocupar altura aunque haya pocos
                        scrollView.fullScroll(View.FOCUS_DOWN); // baja al final del contenido
                    });


                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                Log.e("WebSocket", "Fallo al cargar los mensajes: " + t.getMessage());
            }

        });
    }

    private void mostrarNombrUsr(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre", "");
        boolean estadoConect = sharedPreferences.getBoolean("estadoConect", false);

        if(estadoConect){
            int color = ContextCompat.getColor(requireContext(), R.color.verde_estatus_conexion);
            circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
            txtEstadoConexion.setText("En línea");
        }else{
            int color = ContextCompat.getColor(requireContext(), R.color.grisEstatusChatConect);
            circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
            txtEstadoConexion.setText("Desconectado");
        }
        nombreUsuarioChat.setText(nombre);
    }

    private String formatearHoraADoce(String soloHora) {
        SimpleDateFormat formato24 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formato12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            // Parsear la hora en formato 24 horas
            Date hora = formato24.parse(soloHora);

            // Convertir a 12 horas con a.m./p.m.
            String horaFormateada = formato12.format(hora);

            return horaFormateada.toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    private void mostrarModalEmojis(View view) {
        FlexboxLayout emojiContainer = view.findViewById(R.id.emojiContainer);
        if (emojiContainer == null) {
            Log.e("Emojis", "emojiContainer no encontrado. ¿Está mal el ID o no se infló?");
            return;
        }

        emojiContainer.removeAllViews();

        int[] emojis = {
                R.drawable.aburrido, R.drawable.aceptado, R.drawable.agresivo, R.drawable.alegre,
                R.drawable.angelical, R.drawable.ansioso, R.drawable.apatico, R.drawable.apenado,
                R.drawable.asqueado, R.drawable.asustado, R.drawable.bromista, R.drawable.cansado,
                R.drawable.confundido, R.drawable.cool, R.drawable.divertido, R.drawable.enamorado,
                R.drawable.enfermo, R.drawable.entusiasmado, R.drawable.herido, R.drawable.inseguro,
                R.drawable.intelectual, R.drawable.lleno_de_odio, R.drawable.llorando, R.drawable.mareado,
                R.drawable.normal, R.drawable.paniqueado, R.drawable.pensativo, R.drawable.poderoso,
                R.drawable.rechazado, R.drawable.solo
        };

        float scale = getResources().getDisplayMetrics().density;
        int sizeInPx = (int) (48 * scale + 0.5f);
        int marginInPx = (int) (8 * scale + 0.5f);

        for (int emojiRes : emojis) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setImageResource(emojiRes);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(sizeInPx, sizeInPx);
            params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
            imageView.setLayoutParams(params);

            imageView.setOnClickListener(v -> {
                Drawable originalDrawable = imageView.getDrawable();
                if (originalDrawable != null && originalDrawable.getConstantState() != null) {
                    Drawable drawable = originalDrawable.getConstantState().newDrawable().mutate();

                    int size = txtMsjMandar.getLineHeight();
                    drawable.setBounds(0, 0, size, size);

                    String nombre = emojiHtmlMap.get(emojiRes);
                    if (nombre == null) nombre = "desconocido";

                    // Esta es la estructura HTML real que se enviará
                    String htmlTag = "<img src=\"../img/estado_animo_img/" + nombre + ".svg\" alt=\"emoji\" class=\"emoji-in-text\">";

                    SpannableString spannableString = new SpannableString(htmlTag);
                    ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                    spannableString.setSpan(imageSpan, 0, htmlTag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    int cursorPos = txtMsjMandar.getSelectionStart();
                    Editable editable = (Editable) txtMsjMandar.getText();
                    editable.insert(cursorPos, spannableString);
                }
            });



            emojiContainer.addView(imageView);
        }
    }

    private void showEmojis() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideEmojis() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void hideKeyboardWithCallback(Runnable callback) {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Espera un poco y luego ejecuta la acción (abrir bottomsheet)
        new Handler(Looper.getMainLooper()).postDelayed(callback, 200);
    }

    private void actualizarEstadoConexion(int conectadoPor, Boolean conectado){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Kit", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idCastor", 0);
        if(idKit == conectadoPor){
            if(conectado){
                int color = ContextCompat.getColor(requireContext(), R.color.verde_estatus_conexion);
                circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
                txtEstadoConexion.setText("En línea");
            }else{
                int color = ContextCompat.getColor(requireContext(), R.color.grisEstatusChatConect);
                circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
                txtEstadoConexion.setText("Desconectado");
            }
        }
    }

}