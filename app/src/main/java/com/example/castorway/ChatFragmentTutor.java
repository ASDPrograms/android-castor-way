package com.example.castorway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragmentTutor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragmentTutor extends Fragment implements ChatSocketListener{

    SwipeRefreshLayout refrescarFragment;
    LinearLayout linLayContainAllActis;
    private List<Integer> idsKit = new ArrayList<>();
    //Para la conexión:
    private ChatSocketCastor chatManager;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragmentTutor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragmentTutor.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragmentTutor newInstance(String param1, String param2) {
        ChatFragmentTutor fragment = new ChatFragmentTutor();
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
    public void onPause() {
        super.onPause();
        if (chatManager != null) {
            ChatSocketCastor.getInstance().cerrarConexion();
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
            ChatSocketCastor.getInstance().cerrarConexion();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_tutor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Para refrescar el chat
        refrescarFragment = view.findViewById(R.id.refrescarFragment);
        ScrollView scrollViewChat = view.findViewById(R.id.scrollViewChat);

        linLayContainAllActis = view.findViewById(R.id.linLayContainAllActis);

        refrescarFragment.setOnRefreshListener(() -> {
            FragmentTransaction transaction = requireFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, new ChatFragmentTutor());
            transaction.addToBackStack(null);
            transaction.commit();

            refrescarFragment.setRefreshing(false);
        });

// Escucha el scroll en el ScrollView
        scrollViewChat.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Solo habilitar swipe-to-refresh si está totalmente arriba
            refrescarFragment.setEnabled(scrollViewChat.getScrollY() == 0);
        });

        //Fin del código para refescar el chat

        //Función para desplegar los usuarios correspondientes al chat:
        desplgUsrChat(view);
    }
    public void desplgUsrChat(View view){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        ApiService apiService = RetrofitClient.getApiService();

        Call<List<Castor>> call = apiService.getAllCastores();
        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castores = response.body();
                String codPresa = null;
                int idCastor = 0 ;
                String email = sharedPreferences.getString("email", "");

                if (castores != null) {
                    for (Castor castor : castores) {
                        if (castor.getEmail().equals(email)) {
                            codPresa = castor.getCodPresa();
                            idCastor = castor.getIdCastor();
                            break;
                        }
                    }
                }

                if (codPresa != null) {
                    ApiService apiService2 = RetrofitClient.getApiService();
                    Call<List<Kit>> call2 = apiService2.getAllKits();
                    String finalCodPresa = codPresa;
                    int finalIdCastor = idCastor;
                    call2.enqueue(new Callback<List<Kit>>() {
                        @Override
                        public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                            List<Kit> kits = response.body();
                            if (kits != null) {
                                linLayContainAllActis.removeAllViews(); // limpiar vista antes

                                LayoutInflater inflater = LayoutInflater.from(getContext());

                                for (Kit kit : kits) {
                                    if (kit.getCodPresa().equals(finalCodPresa)) {
                                        idsKit.add(kit.getIdKit());
                                        // Inflar layout
                                        View chatView = inflater.inflate(R.layout.estructura_contacto_chat, linLayContainAllActis, false);

                                        //se le asigna el identificador para que pueda ser editado en caso de que se reciba un mensaje:
                                        chatView.setTag(kit.getIdKit());

                                        // Referencias a vistas de ese chat
                                        TextView txtNombre = chatView.findViewById(R.id.nombreUsuarioChat);
                                        TextView txtUltMsj = chatView.findViewById(R.id.ultMsjChat);
                                        TextView txtHora = chatView.findViewById(R.id.horaUltMsjChat);
                                        LinearLayout layoutContactoUsr = chatView.findViewById(R.id.layout_contacto_usr);

                                        txtNombre.setText(kit.getNombreUsuario());
                                        txtUltMsj.setText("Cargando...");
                                        txtHora.setText("");

                                        // Listener para abrir chat
                                        layoutContactoUsr.setOnClickListener(v -> {
                                            irConversacionChat(kit.getIdKit(), finalIdCastor, kit.getNombreUsuario(), kit.getEstadoConect());
                                        });

                                        linLayContainAllActis.addView(chatView);

                                        // Ahora, se llama asíncronamente para obtener el último mensaje
                                        obtInfoUltMsj(finalIdCastor, kit.getIdKit(), new UltimoMsjCallback() {
                                            @Override
                                            public void onResultado(String contenido, String fechaEnvio) {
                                                // Actualizar UI del último mensaje
                                                if (contenido != null && !contenido.isEmpty()) {
                                                    Spanned spanned = null;
                                                    if (contenido.contains("<img src=") && contenido.contains("emoji-in-text")) {
                                                        TextView textView = new TextView(requireContext());
                                                        textView.setTextColor(getResources().getColor(R.color.black));
                                                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                        textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gayathri_bold));
                                                        textView.setGravity(Gravity.START);

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
                                                                        int sizePx = (int) (40 * density);

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

                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            spanned = Html.fromHtml(contenido, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
                                                        } else {
                                                            spanned = Html.fromHtml(contenido, imageGetter, null);
                                                        }

                                                        textView.setText(spanned);
                                                    }
                                                    txtUltMsj.setText(spanned);
                                                } else {
                                                    txtUltMsj.setText("Sin mensajes");
                                                }
                                                txtHora.setText(formatearFechaChat(fechaEnvio));
                                            }
                                        });
                                    }
                                }

                                iniciarConexion();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Kit>> call, Throwable t) {
                            Toast.makeText(getActivity(), "Error al cargar contactos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Ocurrió un error con su cuenta", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), IniciarSesionTutor.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtInfoUltMsj(int idCastor, int idKit, UltimoMsjCallback callback) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Chat>> call = apiService.getLastChat(idKit, idCastor);
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Chat chat = response.body().get(0);
                    callback.onResultado(chat.getContenido(), chat.getFechaEnvio());
                } else {
                    callback.onResultado("", "");
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                Log.e("RetrofitError", "Error en la llamada: " + t.getMessage(), t);
                callback.onResultado("Error", "Error");
            }
        });
    }

    @Override
    public void onNuevoMensaje(String emisor, String mensaje, String hora, int idKit) {
        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                Log.d("WebSocket", "Nuevo mensaje de " + emisor + ": " + mensaje + ". Hora: " + hora + ". idKit: " + idKit);


                View vista = encontrarVistaPorIdKit(linLayContainAllActis, idKit);
                Spanned spanned = null;
                if (vista != null) {

                    Log.d("WebSocket", "Entró al if de vista");

                    //se cambia el contenido del mensaje:
                    TextView txtUltMsj = vista.findViewById(R.id.ultMsjChat);

                    if (mensaje.contains("<img src=") && mensaje.contains("emoji-in-text")) {
                        TextView textView = new TextView(requireContext());
                        textView.setTextColor(getResources().getColor(R.color.black));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                        textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gayathri_bold));
                        textView.setGravity(Gravity.START);
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
                                        int sizePx = (int) (40 * density);

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

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            spanned = Html.fromHtml(mensaje, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
                        } else {
                            spanned = Html.fromHtml(mensaje, imageGetter, null);
                        }

                        textView.setText(spanned);
                    }


                    txtUltMsj.setText(spanned);

                    //se cambia la hora a la que se mandó:
                    TextView horaUltMsjChat = vista.findViewById(R.id.horaUltMsjChat);
                    String soloHora = hora.split(" ")[1];
                    String horaFormateada = formatearHoraADoce(soloHora);
                    horaUltMsjChat.setText(horaFormateada);
                }

            });
        }
    }

    @Override
    public void onEstadoConexionActualizado(int userId, boolean conectado) {
        Log.d("WebSocket", "Usuario " + userId + (conectado ? " conectado" : " desconectado"));
        actualizarEstadoConexion(userId, conectado);
    }

    @Override
    public void onErrorAlParsearMensaje() {

    }

    public interface UltimoMsjCallback {
        void onResultado(String contenido, String fechaEnvio);
    }
    public static String formatearFechaChat(String fechaOriginal) {
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatoHora12 = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // Formato 12 hrs
        SimpleDateFormat formatoSoloFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Solo fecha

        try {
            Date fecha = formatoEntrada.parse(fechaOriginal);

            // Obtener la fecha de hoy
            Calendar hoy = Calendar.getInstance();
            // Obtener fecha de ayer
            Calendar ayer = Calendar.getInstance();
            ayer.add(Calendar.DAY_OF_YEAR, -1);
            // Obtener fecha de antier
            Calendar antier = Calendar.getInstance();
            antier.add(Calendar.DAY_OF_YEAR, -2);

            // Formatear solo las fechas para comparar (sin horas)
            String fechaChat = formatoSoloFecha.format(fecha);
            String fechaHoy = formatoSoloFecha.format(hoy.getTime());
            String fechaAyer = formatoSoloFecha.format(ayer.getTime());
            String fechaAntier = formatoSoloFecha.format(antier.getTime());

            if (fechaChat.equals(fechaHoy)) {
                // Si es hoy, mostrar solo hora en 12 horas con am/pm en minúsculas
                return formatoHora12.format(fecha).toLowerCase();
            } else if (fechaChat.equals(fechaAyer)) {
                return "ayer";
            } else if (fechaChat.equals(fechaAntier)) {
                return "antier";
            } else {
                // Si es diferente, solo retorna la fecha (sin hora)
                return fechaChat;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return fechaOriginal;
        }
    }
    public void irConversacionChat(int idKit, int idCastor, String nombreUsr, Boolean estadoConect){
        //Se crea la sesión para mostrar los mensajes de ese chat entre esos dos usuarios:
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ChatUsrKitElegido", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("idKit", idKit);
        editor.putInt("idCastor", idCastor);
        editor.putString("nombreUsuario", nombreUsr);
        editor.putBoolean("estadoConect", estadoConect);
        editor.apply();

        //se cambia a la activity donde se tiene la conversación:
        Intent intent = new Intent(getActivity(), ChatTutorConversacion.class);
        intent.putIntegerArrayListExtra("idsKit", (ArrayList<Integer>) idsKit);
        startActivity(intent);
    }

    private void iniciarConexion() {
        chatManager = ChatSocketCastor.getInstance();
        chatManager.setChatSocketListener(ChatFragmentTutor.this);

        String idsKitStr = TextUtils.join(",", idsKit);

        // Se obtienen los datos de la sesión desde SharedPreferences del contexto del fragment
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ChatUsrKitElegido", Context.MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        int idCastor = sharedPreferences.getInt("idCastor", 0);

        // Se hace la conexión pasando los parámetros que se ocupan
        chatManager.iniciarConexion(String.valueOf(idCastor), "Castor", idsKitStr);
    }

    private void cerrarConexion(){
        ChatSocketCastor.getInstance().cerrarConexion();
    }

    public View encontrarVistaPorIdKit(LinearLayout contenedor, int idKitBuscado) {
        for (int i = 0; i < contenedor.getChildCount(); i++) {
            View hijo = contenedor.getChildAt(i);
            Object tag = hijo.getTag();
            if (tag instanceof Integer && ((Integer) tag) == idKitBuscado) {
                return hijo;
            }
        }
        return null;
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

    private void actualizarEstadoConexion(int conectadoPor, Boolean conectado){
        View vista = encontrarVistaPorIdKit(linLayContainAllActis, conectadoPor);
        View circuloEstadoConeccion = vista.findViewById(R.id.circuloEstadoConeccion);
        if (vista != null) {
            if(conectado){
                int color = ContextCompat.getColor(requireContext(), R.color.verde_estatus_conexion);
                circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
            }else{
                int color = ContextCompat.getColor(requireContext(), R.color.grisEstatusChatConect);
                circuloEstadoConeccion.setBackgroundTintList(ColorStateList.valueOf(color));
            }
        }
    }


}

