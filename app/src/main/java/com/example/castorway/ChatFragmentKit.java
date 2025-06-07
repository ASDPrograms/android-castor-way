package com.example.castorway;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            if (idKit[0] == -1) {
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

                                    // Guardamos el idKit en SharedPreferences
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("idKit", idKit[0]);
                                    editor.apply();

                                    consultarCastor(codPresa, view);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Kit>> call, Throwable t) {
                        // Manejo de errores
                    }
                });
            } else {
                // Si el idKit ya estaba guardado, no necesitamos hacer la consulta
                iniciarApp(view);  // Solo inicializamos la app con el idKit guardado
            }
        }
    }
    private void iniciarApp(View view){
        iniciarConexion();


    }

    @Override
    public void onNuevoMensaje(String emisor, String mensaje, String hora, int idKit) {
        requireActivity().runOnUiThread(() -> {
            Log.d("ChatUI", "Nuevo mensaje de " + emisor + ": " + mensaje + ". Hora: " + hora);

        });
    }

    @Override
    public void onEstadoConexionActualizado(int userId, boolean conectado) {

    }

    @Override
    public void onErrorAlParsearMensaje() {

    }

    private void consultarCastor(String codPresa, View view){
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
                            int idCastor = 0;
                            // Guardamos el idKit en SharedPreferences
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("idCastor", idCastor);
                            editor.apply();

                            // se guarda el idKit y el idCastor y luego se inicia la app
                            iniciarApp(view); // Llamada a tu método de inicialización de la app
                        }
                    }
                }
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
}