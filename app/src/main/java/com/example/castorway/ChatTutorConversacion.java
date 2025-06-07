package com.example.castorway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.text.LineBreaker;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Chat;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatTutorConversacion extends AppCompatActivity implements ChatSocketListener{

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

    //Para la conexión:
    private ChatSocketCastor chatManager;
    private WebSocket webSocket;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tutor_conversacion);

        contenedorMensajes = findViewById(R.id.contMensajes);
        scrollView = findViewById(R.id.scrollMensajesChat);

        //se declara el view pa q cambie de color el ícono y el txt de la conexión:
        circuloEstadoConeccion = findViewById(R.id.circuloEstadoConeccion);
        txtEstadoConexion = findViewById(R.id.txtEstadoConexion);

        //se hace inicia la conexión al socket:
        iniciarConexion();

        //Pq por alguna razón le cambiaba el color del rectángulo que contiene info del cel(batería, wifi, etc.)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        btnRegresarActi = findViewById(R.id.btnRegresarActi);
        btnRegresarActi.setOnClickListener(v -> {
            finish();
        });

        nombreUsuarioChat = findViewById(R.id.nombreUsuarioChat);

        desplMsj();


        mostrarNombrUsr();

        //por default va a estar escondido el linearlayout, hasta que esté actividado
        //para poder escribir
        containBtnEnviarMsj = findViewById(R.id.containBtnEnviarMsj);
        containBtnEnviarMsj.setVisibility(View.GONE);

        //listener al input de escribir:
        txtMsjMandar = findViewById(R.id.txtMsjMandar);
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
        btnEnviarMsj = findViewById(R.id.btnEnviarMsj);
        btnEnviarMsj.setOnClickListener(v -> {
            mandarMensaje();
        });


        CoordinatorLayout rootCoordinator = findViewById(R.id.rootCoordinator);
        View panelEmojis = findViewById(R.id.panelEmojis);
        View mainContent = findViewById(R.id.main);

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

        btnEmojis = findViewById(R.id.btnEmojis);

        btnEmojis.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                // Primero oculta el teclado y cuando esté oculto abre el BottomSheet
                hideKeyboardWithCallback(() -> {
                    mostrarModalEmojis(); // Cargar emojis
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                });
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        final View activityRootView = findViewById(R.id.rootCoordinator);

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

        final View rootView = findViewById(R.id.rootCoordinator);
        final LinearLayout contMensajes = findViewById(R.id.contMensajes);
        final ScrollView scrollView = findViewById(R.id.scrollMensajesChat);

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
    private void iniciarConexion(){
        chatManager = ChatSocketCastor.getInstance();
        chatManager.setChatSocketListener(this);

        //se reciben los idsKit desde el ChatFragmentTutor:
        ArrayList<Integer> idsKit = getIntent().getIntegerArrayListExtra("idsKit");
        String idsKitStr = TextUtils.join(",", idsKit);

        //se obtienen los datos de la sesión:
        SharedPreferences sharedPreferences = getSharedPreferences("ChatUsrKitElegido", MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        int idCastor = sharedPreferences.getInt("idCastor", 0);

        //se hace la conexión pasando los parámetros que se ocupan:
        chatManager.iniciarConexion(String.valueOf(idCastor), "Castor", idsKitStr);
    }

    private void mandarMensaje() {
        //se obtienen los datos de la sesión y todos los que se pasarán al socket:
        SharedPreferences sharedPreferences = getSharedPreferences("ChatUsrKitElegido", MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        int idCastor = sharedPreferences.getInt("idCastor", 0);
        String txtMsj = txtMsjMandar.getText().toString().trim();
        String emisor = "Castor";
        String estado = "enviado";

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
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "App cerrada");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (chatManager != null) {
            chatManager.cerrarConexion();
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
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    // 👇 Implementación de los métodos de la interfaz:
    @Override
    public void onNuevoMensaje(String emisor, String mensaje, String hora, int idKit){
        runOnUiThread(() -> {

            Log.d("ChatUI", "Nuevo mensaje de " + emisor + ": " + mensaje + ". Hora: " + hora);

            //se infla la vista que tiene la estructura de la burbuja de mensaje del chat
            View view = LayoutInflater.from(ChatTutorConversacion.this).inflate(R.layout.estructura_mensaje_chat, contenedorMensajes, false);

            TextView textoMensaje = view.findViewById(R.id.textoMensaje);
            LinearLayout burbujaMsjChat = view.findViewById(R.id.burbujaMsjChat);
            TextView horaMsj = view.findViewById(R.id.horaMsj);
            textoMensaje.setText(mensaje);

            if (mensaje.contains("<img src=") && mensaje.contains("emoji-in-text")) {
                burbujaMsjChat.removeView(textoMensaje);

                TextView textView = new TextView(ChatTutorConversacion.this);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setTypeface(ResourcesCompat.getFont(ChatTutorConversacion.this, R.font.gayathri_bold));
                textView.setGravity(Gravity.START);
                if(emisor.equals("Kit")){
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

                        int resId = getResources().getIdentifier(nombre, "drawable", getPackageName());
                        if (resId != 0) {
                            Drawable drawable = ContextCompat.getDrawable(ChatTutorConversacion.this, resId);
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


            if(emisor.equals("Kit")){
                Log.d("ChatUI", "Es de kit");

                //en caso de que el mensaje lo mandó el hijo, se cambie el color del mensaje
                //se voltea la burbuja y se voltea el texto porque se gira completo al girar la burbuja
                LinearLayout linearLayoutContMsj = view.findViewById(R.id.linearLayoutContMsj);

                burbujaMsjChat.setBackgroundTintList(ContextCompat.getColorStateList(ChatTutorConversacion.this, R.color.gricecito_chat));
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
                View separadorFecha = LayoutInflater.from(ChatTutorConversacion.this).inflate(R.layout.separador_fechas_chat, null);
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
        runOnUiThread(() -> {
            Log.d("WebSocket", "Usuario " + userId + (conectado ? " conectado" : " desconectado"));
            actualizarEstadoConexion(userId, conectado);
        });
    }

    @Override
    public void onErrorAlParsearMensaje() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Error al procesar el mensaje", Toast.LENGTH_SHORT).show();
        });
    }

    private void desplMsj(){
        //Se obtienen los datos de idKit y de idCastor de la sesión:
        SharedPreferences sharedPreferences = getSharedPreferences("ChatUsrKitElegido", MODE_PRIVATE);
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
                            View view = LayoutInflater.from(ChatTutorConversacion.this).inflate(R.layout.estructura_mensaje_chat, contenedorMensajes, false);
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

                                TextView textView = new TextView(ChatTutorConversacion.this);
                                textView.setTextColor(getResources().getColor(R.color.black));
                                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                textView.setTypeface(ResourcesCompat.getFont(ChatTutorConversacion.this, R.font.gayathri_bold));
                                textView.setGravity(Gravity.START);
                                if(String.valueOf(chat.getEmisor()).equals("Kit")){
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

                                        int resId = getResources().getIdentifier(nombre, "drawable", getPackageName());
                                        if (resId != 0) {
                                            Drawable drawable = ContextCompat.getDrawable(ChatTutorConversacion.this, resId);
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

                            if(tipoEmisor.equals("Kit")){
                                //en caso de que el mensaje lo mandó el hijo, se cambie el color del mensaje
                                //se voltea la burbuja y se voltea el texto porque se gira completo al girar la burbuja
                                LinearLayout linearLayoutContMsj = view.findViewById(R.id.linearLayoutContMsj);

                                burbujaMsjChat.setBackgroundTintList(ContextCompat.getColorStateList(ChatTutorConversacion.this, R.color.gricecito_chat));
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
                                View separadorFecha = LayoutInflater.from(ChatTutorConversacion.this).inflate(R.layout.separador_fechas_chat, null);
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
        SharedPreferences sharedPreferences = getSharedPreferences("ChatUsrKitElegido", MODE_PRIVATE);
        String nombreUsuario = sharedPreferences.getString("nombreUsuario", "");
        boolean estadoConect = sharedPreferences.getBoolean("estadoConect", false);

        if(estadoConect){
            int color = ContextCompat.getColor(this, R.color.verde_estatus_conexion);
            circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
            txtEstadoConexion.setText("En línea");
        }else{
            int color = ContextCompat.getColor(this, R.color.grisEstatusChatConect);
            circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
            txtEstadoConexion.setText("Desconectado");
        }
        nombreUsuarioChat.setText(nombreUsuario);
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
    public static void cargarImagenesDesdeHtml(Context context, String htmlString, LinearLayout contenedor) {
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(htmlString);

        while (matcher.find()) {
            String srcCompleto = matcher.group(1);
            // Extraer solo el nombre del archivo
            String nombreArchivo = srcCompleto.substring(srcCompleto.lastIndexOf('/') + 1);

            // Crear ImageView
            ImageView imageView = new ImageView(context);

            int sizeInDp = 100;
            float density = context.getResources().getDisplayMetrics().density;
            int sizeInPx = (int) (sizeInDp * density + 0.5f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
            params.gravity = android.view.Gravity.CENTER;
            int marginInPx = (int) (10 * density + 0.5f);
            params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
            imageView.setLayoutParams(params);

            // Añadir ImageView al contenedor
            contenedor.addView(imageView);

            // Cargar la imagen SVG desde assets u otra fuente (debes implementar esta función)
            cargarSvgDesdeAssets(context, nombreArchivo, imageView);
        }
    }

    // Ejemplo básico para cargar SVG, adaptar según tu implementación
    private static void cargarSvgDesdeAssets(Context context, String nombreArchivo, ImageView imageView) {
        try {
            java.io.InputStream inputStream = context.getAssets().open("img/" + nombreArchivo);
            com.caverock.androidsvg.SVG svg = com.caverock.androidsvg.SVG.getFromInputStream(inputStream);
            android.graphics.drawable.PictureDrawable drawable = new android.graphics.drawable.PictureDrawable(svg.renderToPicture());

            imageView.setImageDrawable(drawable);
            imageView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarSvgDesdeAssets(String nombreArchivo, ImageView imageView) {
        try {
            String ruta = "img/estado_animo_img/" + nombreArchivo;
            Log.i("cargarSvgDesdeAssets", "Intentando abrir archivo: " + ruta);
            InputStream inputStream = getAssets().open(ruta);
            Log.i("cargarSvgDesdeAssets", "Archivo abierto correctamente");

            SVG svg = SVG.getFromInputStream(inputStream);
            if (svg == null) {
                Log.e("cargarSvgDesdeAssets", "No se pudo parsear el SVG");
                return;
            }
            Log.i("cargarSvgDesdeAssets", "SVG parseado correctamente");

            PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
            if (drawable == null) {
                Log.e("cargarSvgDesdeAssets", "No se pudo crear PictureDrawable");
                return;
            }
            Log.i("cargarSvgDesdeAssets", "Drawable creado correctamente");

            if (imageView == null) {
                Log.e("cargarSvgDesdeAssets", "El ImageView es null");
                return;
            }
            Log.i("cargarSvgDesdeAssets", "Asignando drawable al ImageView");

            imageView.setImageDrawable(drawable);
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            Log.i("cargarSvgDesdeAssets", "Imagen asignada al ImageView correctamente");

        } catch (IOException | SVGParseException e) {
            Log.e("cargarSvgDesdeAssets", "Error al cargar SVG", e);
            e.printStackTrace();
        }
    }

    private void mostrarModalEmojis() {
        FlexboxLayout emojiContainer = findViewById(R.id.emojiContainer);
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
            ImageView imageView = new ImageView(this);
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
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Espera un poco y luego ejecuta la acción (abrir bottomsheet)
        new Handler(Looper.getMainLooper()).postDelayed(callback, 200);
    }


    private void actualizarEstadoConexion(int conectadoPor, Boolean conectado){
        SharedPreferences sharedPreferences = getSharedPreferences("ChatUsrKitElegido", MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        if(idKit == conectadoPor){
            if(conectado){
                int color = ContextCompat.getColor(this, R.color.verde_estatus_conexion);
                circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
                txtEstadoConexion.setText("En línea");
            }else{
                int color = ContextCompat.getColor(this, R.color.grisEstatusChatConect);
                circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
                txtEstadoConexion.setText("Desconectado");
            }
        }
    }
}