package com.example.castorway;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentTutor extends Fragment {
    private TextView NameCastor;
    private SharedPreferences preferences;
    private LinearLayout userContainer;
    private LinearLayout contenedorPremioMasCostoso;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_tutor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NameCastor = view.findViewById(R.id.NameCastor);
        userContainer = view.findViewById(R.id.userContainer);
        preferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        contenedorPremioMasCostoso = view.findViewById(R.id.contenedorPremioMasCostoso);

        cargarDatosLocales();
        fetchUserAndKits();
        fetchRecompensaMasCostosa();
    }

    private void cargarDatosLocales() {
        String nombreCastor = preferences.getString("castor_name", "Cargando...");
        NameCastor.setText(nombreCastor);

        Set<String> hijosGuardados = preferences.getStringSet("hijos_list", new HashSet<>());
        if (hijosGuardados != null) {
            userContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            for (String nombreHijo : hijosGuardados) {
                View hijoView = inflater.inflate(R.layout.item_hijo, userContainer, false);
                ((TextView) hijoView.findViewById(R.id.txtHijoNombre)).setText(nombreHijo);
                ((ImageView) hijoView.findViewById(R.id.imgHijo)).setImageResource(R.drawable.icn_user_gris);
                userContainer.addView(hijoView);
            }
        }
    }

    private void fetchUserAndKits() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> callCastores = apiService.getAllCastores();

        callCastores.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                if (!isAdded() || response.body() == null) return;

                List<Castor> castores = response.body();
                String email = preferences.getString("email", "");
                String codPresa = null;
                String nombreCastor = null;

                for (Castor castor : castores) {
                    if (castor.getEmail().equalsIgnoreCase(email)) {
                        codPresa = castor.getCodPresa();
                        nombreCastor = castor.getNombre();
                        break;
                    }
                }

                if (nombreCastor != null) {
                    preferences.edit().putString("castor_name", nombreCastor).apply();
                    updateNameCastor(nombreCastor);
                }

                if (codPresa != null) {
                    obtenerHijosRapido(codPresa);
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
            }
        });
    }

    private void updateNameCastor(String nombreCastor) {
        if (isAdded() && NameCastor != null) {
            requireActivity().runOnUiThread(() -> NameCastor.setText(nombreCastor));
        }
    }

    private void actualizarDatosRapido() {
        executorService.execute(() -> {
            ApiService apiService = RetrofitClient.getApiService();
            String email = preferences.getString("email", "");

            Call<List<Castor>> callCastores = apiService.getAllCastores();
            callCastores.enqueue(new Callback<List<Castor>>() {
                @Override
                public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                    if (!isAdded() || response.body() == null) return;

                    List<Castor> castores = response.body();
                    String codPresa = null;

                    for (Castor castor : castores) {
                        if (castor.getEmail().equalsIgnoreCase(email)) {
                            codPresa = castor.getCodPresa();
                            break;
                        }
                    }

                    if (codPresa != null) {
                        obtenerHijosRapido(codPresa);
                    }
                }

                @Override
                public void onFailure(Call<List<Castor>> call, Throwable t) {
                }
            });
        });
    }

    private void obtenerHijosRapido(String codPresa) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Kit>> callKits = apiService.getAllKits();

        callKits.enqueue(new Callback<List<Kit>>() {
            @Override
            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                if (!isAdded() || response.body() == null) return;

                List<Kit> kits = response.body();
                Set<String> nuevosHijos = new HashSet<>();

                for (Kit kit : kits) {
                    if (kit.getCodPresa().equals(codPresa)) {
                        nuevosHijos.add(kit.getNombreUsuario());
                    }
                }

                preferences.edit().putStringSet("hijos_list", nuevosHijos).apply();
                handler.post(() -> actualizarVistaHijos(nuevosHijos));
            }

            @Override
            public void onFailure(Call<List<Kit>> call, Throwable t) {
            }
        });
    }

    private void actualizarVistaHijos(Set<String> hijos) {
        if (!isAdded() || userContainer == null) return;

        userContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (String nombreHijo : hijos) {
            View hijoView = inflater.inflate(R.layout.item_hijo, userContainer, false);
            ((TextView) hijoView.findViewById(R.id.txtHijoNombre)).setText(nombreHijo);
            ((ImageView) hijoView.findViewById(R.id.imgHijo)).setImageResource(R.drawable.icn_user_gris);
            userContainer.addView(hijoView);
        }
    }


    private void fetchRecompensaMasCostosa() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Premios>> callPremios = apiService.getAllPremios();

        callPremios.enqueue(new Callback<List<Premios>>() {
            @Override
            public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                if (!isAdded() || response.body() == null) return;

                List<Premios> premios = response.body();
                Premios premioMax = obtenerPremioMasCostoso(premios);

                if (premioMax != null) {
                    mostrarPremio(premioMax);
                }
            }

            @Override
            public void onFailure(Call<List<Premios>> call, Throwable t) {
                // Manejo de error
            }
        });
    }

    private Premios obtenerPremioMasCostoso(List<Premios> premios) {
        Premios premioMax = null;
        for (Premios premio : premios) {
            if (premioMax == null || premio.getCostoPremio() > premioMax.getCostoPremio()) {
                premioMax = premio;
            }
        }
        return premioMax;
    }

    private void mostrarPremio(Premios premio) {
        if (!isAdded() || contenedorPremioMasCostoso == null) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View vistaPremio = inflater.inflate(R.layout.item_premio_home, contenedorPremioMasCostoso, false);

        TextView txtHijoPremio = vistaPremio.findViewById(R.id.txtHijoPremio);
        TextView txtPremNivel = vistaPremio.findViewById(R.id.txtPremNivel);
        TextView txtPremCat = vistaPremio.findViewById(R.id.txtPremCat);
        TextView txtPremRam = vistaPremio.findViewById(R.id.txtPremRam);
        ImageView imgPremio = vistaPremio.findViewById(R.id.imgPremioMasCostoso);

        txtHijoPremio.setText(premio.getNombrePremio());
        txtPremNivel.setText(premio.getNivelPremio());
        txtPremCat.setText(premio.getCategoriaPremio());
        txtPremRam.setText(premio.getCostoPremio() + " ramitas");

        // Obtener la imagen de la ruta almacenada en la base de datos
        String rutaImagen = premio.getRutaImagenHabito();  // Usamos el campo rutaImagenHabito

        // Convertir la ruta de la imagen a un ID de recurso
        int resId = requireContext().getResources().getIdentifier(rutaImagen, "drawable", requireContext().getPackageName());

        if (resId != 0) {
            imgPremio.setImageResource(resId);
        } else {
            imgPremio.setImageResource(R.drawable.icon_reloj_azul); // Imagen por defecto si no se encuentra
        }

        contenedorPremioMasCostoso.removeAllViews();
        contenedorPremioMasCostoso.addView(vistaPremio);
    }
}
