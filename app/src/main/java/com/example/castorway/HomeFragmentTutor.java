package com.example.castorway;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.modelsDB.RelPrem;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentTutor extends Fragment {
    private TextView NameCastor;
    private SharedPreferences preferences;
    private LinearLayout useritoContainer;
    private LinearLayout contenedorActividades;
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
        useritoContainer = view.findViewById(R.id.useritoContainer);
        preferences = requireContext().getSharedPreferences("User", MODE_PRIVATE);
        contenedorPremioMasCostoso = view.findViewById(R.id.contenedorPremioMasCostoso);
        contenedorActividades = view.findViewById(R.id.contenedorActividades);


        cargarDatosLocales();
        fetchUserAndKits();
        fetchRecompensaMasCostosa();
        fetchActividades();
    }

    private void cargarDatosLocales() {
        String nombreCastor = preferences.getString("castor_name", "Usuario");
        NameCastor.setText(nombreCastor);

        Set<String> hijosGuardados = preferences.getStringSet("hijos_list", new HashSet<>());

        useritoContainer.removeAllViews();

        if (hijosGuardados != null && !hijosGuardados.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            for (String nombreHijo : hijosGuardados) {
                View hijoView = inflater.inflate(R.layout.item_hijo, useritoContainer, false);
                ((TextView) hijoView.findViewById(R.id.txtHijoNombre)).setText(nombreHijo);
                ((ImageView) hijoView.findViewById(R.id.imgHijo)).setImageResource(R.drawable.icn_user_gris);
                useritoContainer.addView(hijoView);
            }
        } else {
            insertarTextViewNoHayHijos();
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
        if (!isAdded() || useritoContainer == null) return;

        useritoContainer.removeAllViews();

        if (hijos != null && !hijos.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            for (String nombreHijo : hijos) {
                View hijoView = inflater.inflate(R.layout.item_hijo, useritoContainer, false);
                ((TextView) hijoView.findViewById(R.id.txtHijoNombre)).setText(nombreHijo);
                ((ImageView) hijoView.findViewById(R.id.imgHijo)).setImageResource(R.drawable.icn_user_gris);
                useritoContainer.addView(hijoView);
            }
        } else {
            insertarTextViewNoHayHijos();
        }
    }
    private void insertarTextViewNoHayHijos() {
        TextView txtNoHijos = new TextView(requireContext());
        txtNoHijos.setText("No hay hijos disponibles");
        txtNoHijos.setTextSize(35);
        txtNoHijos.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
        txtNoHijos.setTextColor(Color.WHITE);
        txtNoHijos.setPadding(16, 16, 16, 16);
        txtNoHijos.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        txtNoHijos.setLayoutParams(params);

        useritoContainer.addView(txtNoHijos);
    }

    private void fetchRecompensaMasCostosa() {
        // Mostrar desde caché primero
        SharedPreferences cache = requireContext().getSharedPreferences("cachePremioCostoso", MODE_PRIVATE);
        String premioJson = cache.getString("premio_mas_costoso", null);

        if (premioJson != null) {
            Gson gson = new Gson();
            Premios premioCache = gson.fromJson(premioJson, Premios.class);
            mostrarPremio(premioCache);
        } else {
            // Mostrar mensaje mientras carga si no hay nada en caché
            requireActivity().runOnUiThread(this::mostrarMensajeNoPremios);
        }

        // Luego realizar la llamada a la API
        ApiService apiService = RetrofitClient.getApiService();

        CompletableFuture<List<Premios>> premiosFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Premios>> response = apiService.getAllPremios().execute();
                return response.isSuccessful() ? response.body() : Collections.emptyList();
            } catch (IOException e) {
                return Collections.emptyList();
            }
        });

        CompletableFuture<List<RelPrem>> relPremFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<RelPrem>> response = apiService.getAllRelPrem().execute();
                return response.isSuccessful() ? response.body() : Collections.emptyList();
            } catch (IOException e) {
                return Collections.emptyList();
            }
        });

        CompletableFuture.allOf(premiosFuture, relPremFuture).thenRun(() -> {
            List<Premios> premios = premiosFuture.join();
            List<RelPrem> relPremList = relPremFuture.join();

            if (!isAdded()) return;

            SharedPreferences preferences = getActivity().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
            int idKit = preferences.getInt("idKit", 0);
            if (idKit == 0) {
                requireActivity().runOnUiThread(this::mostrarMensajeSeleccionarIdKit);
                return;
            }

            Map<Integer, Premios> premiosMap = premios.stream()
                    .collect(Collectors.toMap(Premios::getIdPremio, premio -> premio));

            Premios premioMayorCosto = relPremList.stream()
                    .filter(relPrem -> relPrem.getIdKit() == idKit)
                    .map(relPrem -> premiosMap.get(relPrem.getIdPremio()))
                    .filter(premio -> premio != null && premio.getEstadoPremio() == 0)
                    .max(Comparator.comparingDouble(Premios::getCostoPremio))
                    .orElse(null);

            requireActivity().runOnUiThread(() -> {
                if (premioMayorCosto != null) {
                    // Guardar en caché
                    Gson gson = new Gson();
                    String jsonPremio = gson.toJson(premioMayorCosto);
                    cache.edit().putString("premio_mas_costoso", jsonPremio).apply();

                    mostrarPremio(premioMayorCosto);
                } else {
                    mostrarMensajeNoPremios();
                }
            });
        });
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

        LinearLayout LinearPrinP = vistaPremio.findViewById(R.id.LinearPrinP);

        LinearPrinP.setOnClickListener(view -> {
            SharedPreferences preferences= requireContext().getSharedPreferences("premioSelected", MODE_PRIVATE);
            SharedPreferences.Editor editorp = preferences.edit();
            editorp.putInt("idPremio", premio.getIdPremio());
            editorp.apply();

            SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
            SharedPreferences.Editor editorcerrarp = sharedPreferencesCerrar.edit();
            editorcerrarp.putBoolean("sesion_activa_prem", true);
            editorcerrarp.apply();

            animarLinear(LinearPrinP);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(requireContext(), HomeTutor.class);
                intent.putExtra("fragmenPremCrear", "RecompensasFragmentTutor1");
                startActivity(intent);
            }, 500);

        });

        txtHijoPremio.setText(premio.getNombrePremio());
        txtPremNivel.setText(premio.getNivelPremio());
        txtPremCat.setText(premio.getCategoriaPremio());
        txtPremRam.setText(premio.getCostoPremio() + " ramitas");

        String imgBd = premio.getRutaImagenHabito();

        String imageName = doesImageExist(requireContext(), imgBd);
        if (imageName != null) {
            InputStream inputStream = null;
            String assetPath = "img/Iconos-recompensas/" + imageName;

            try {
                inputStream = requireContext().getAssets().open(assetPath);

                SVG svg = SVG.getFromInputStream(inputStream);
                if (svg != null) {
                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                    imgPremio.setImageDrawable(drawable);
                } else {
                }
            } catch (IOException | SVGParseException e) {
            }
        } else {
        }



        contenedorPremioMasCostoso.removeAllViews();
        contenedorPremioMasCostoso.addView(vistaPremio);
    }

    private void mostrarMensajeSeleccionarIdKit() {
        if (!isAdded() || contenedorPremioMasCostoso == null) return;

        contenedorPremioMasCostoso.removeAllViews();

        TextView mensajeSeleccionarIdKit = new TextView(requireContext());
        mensajeSeleccionarIdKit.setText("Seleccione primero un Kit");
        mensajeSeleccionarIdKit.setTextSize(35);
        mensajeSeleccionarIdKit.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
        mensajeSeleccionarIdKit.setTextColor(Color.WHITE);
        mensajeSeleccionarIdKit.setPadding(16, 16, 16, 16);
        mensajeSeleccionarIdKit.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mensajeSeleccionarIdKit.setLayoutParams(params);
        contenedorPremioMasCostoso.addView(mensajeSeleccionarIdKit);
    }
    private void mostrarMensajeNoPremios() {
        if (!isAdded() || contenedorPremioMasCostoso == null) return;

        contenedorPremioMasCostoso.removeAllViews();

        TextView mensajeNoPremios = new TextView(requireContext());
        mensajeNoPremios.setText("No hay Premios para este kit");
        mensajeNoPremios.setTextSize(35);
        mensajeNoPremios.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
        mensajeNoPremios.setTextColor(Color.WHITE);
        mensajeNoPremios.setPadding(16, 16, 16, 16);
        mensajeNoPremios.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mensajeNoPremios.setLayoutParams(params);
        contenedorPremioMasCostoso.addView(mensajeNoPremios);
    }

    private void mostrarMensajeSeleccionarIdKitAct() {
        if (contenedorActividades == null) return;

        contenedorActividades.removeAllViews();

        TextView mensajeSeleccionarIdKit = new TextView(requireContext());
        mensajeSeleccionarIdKit.setText("Seleccione primero un Kit");
        mensajeSeleccionarIdKit.setTextSize(35);
        mensajeSeleccionarIdKit.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
        mensajeSeleccionarIdKit.setTextColor(Color.WHITE);
        mensajeSeleccionarIdKit.setPadding(16, 16, 16, 16);
        mensajeSeleccionarIdKit.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mensajeSeleccionarIdKit.setLayoutParams(params);
        contenedorActividades.addView(mensajeSeleccionarIdKit);
    }

    private void animarLinear(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f);

        scaleX.setDuration(150);
        scaleY.setDuration(150);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f).setDuration(150).start();
                ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f).setDuration(150).start();
            }
        });
        animatorSet.start();
    }




    private void fetchActividades() {
        SharedPreferences preferences = getActivity().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);
        if (idKit == 0) {
            requireActivity().runOnUiThread(this::mostrarMensajeSeleccionarIdKitAct);
            return;
        }

        // Mostrar primero actividades guardadas en caché
        SharedPreferences cachePrefs = getActivity().getSharedPreferences("cacheActividades", MODE_PRIVATE);
        String actividadesJson = cachePrefs.getString("actividades", null);
        if (actividadesJson != null) {
            Type listType = new TypeToken<List<Actividad>>() {}.getType();
            List<Actividad> actividadesCache = new Gson().fromJson(actividadesJson, listType);
            if (actividadesCache != null) {
                List<Actividad> filtradas = actividadesCache.stream()
                        .filter(actividad -> actividad.getIdKit() == idKit)
                        .sorted(Comparator.comparing(Actividad::getHoraInicioHabito, Comparator.nullsLast(String::compareTo)))
                        .collect(Collectors.toList());

                mostrarActividadesEnContenedor(filtradas);
            }
        }

        // Luego hacer la petición real a la API y actualizar el caché
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllActividades().enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (!isAdded() || response.body() == null) return;

                List<Actividad> actividades = response.body().stream()
                        .filter(actividad -> actividad.getIdKit() == idKit)
                        .sorted(Comparator.comparing(Actividad::getHoraInicioHabito, Comparator.nullsLast(String::compareTo)))
                        .collect(Collectors.toList());

                // Guardar en caché
                String actividadesJson = new Gson().toJson(response.body());
                SharedPreferences.Editor editor = cachePrefs.edit();
                editor.putString("actividades", actividadesJson);
                editor.apply();

                requireActivity().runOnUiThread(() -> mostrarActividadesEnContenedor(actividades));
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                // Podrías mostrar un Toast si lo deseas
            }
        });
    }

    private void mostrarActividadesEnContenedor(List<Actividad> actividades) {

        contenedorActividades.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        int maxActividades = 2;
        if (actividades == null || actividades.isEmpty()) {

            TextView mensajeNoActividades = new TextView(requireContext());
            mensajeNoActividades.setText("No hay actividades para este kit");
            mensajeNoActividades.setTextSize(35);
            mensajeNoActividades.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
            mensajeNoActividades.setTextColor(Color.WHITE);
            mensajeNoActividades.setPadding(16, 16, 16, 16);
            mensajeNoActividades.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            mensajeNoActividades.setLayoutParams(params);
            contenedorActividades.addView(mensajeNoActividades);
            return;
        }
        for (int i = 0; i < actividades.size(); i++) {
            if (i >= maxActividades) {
                break;
            }

            Actividad actividad = actividades.get(i);

            View actividadView = inflater.inflate(R.layout.item_actividad_home, contenedorActividades, false);

            TextView txtNombreActividad = actividadView.findViewById(R.id.txtHijoActividad);
            TextView txtEstadoActividad = actividadView.findViewById(R.id.txtaActProceso);
            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
            TextView txtHoraActividad = actividadView.findViewById(R.id.txtaActHora);
            LinearLayout LinearPrin = actividadView.findViewById(R.id.LinearPrin);


            LinearPrin.setOnClickListener(view -> {

                SharedPreferences preferences= requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idActividad", actividad.getIdActividad());
                editor.apply();

                SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                SharedPreferences.Editor editorcerrar = sharedPreferencesCerrar.edit();
                editorcerrar.putBoolean("sesion_activa", true);
                editorcerrar.apply();

                animarLinear(LinearPrin);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(requireContext(), HomeTutor.class);
                    intent.putExtra("fragmentActiCrear", "ActividadesFragmentTutor");
                    startActivity(intent);
                }, 500);
            });

            String horaInicio = actividad.getHoraInicioHabito();
            String horaFinaliza = actividad.getHoraFinHabito();
            if (horaInicio != null && horaFinaliza != null) {
                horaInicio = horaInicio.substring(0, 5);
                horaFinaliza = horaFinaliza.substring(0, 5);
            }

            String textoActividad = actividad.getNombreHabito();
            txtNombreActividad.setText(textoActividad);
            if (horaInicio != null && !horaInicio.isEmpty() && horaFinaliza != null && !horaFinaliza.isEmpty()) {
                String horaCompleta = horaInicio + " - " + horaFinaliza;
                txtHoraActividad.setText(horaCompleta);
            }

            String estado = "En proceso";
            String[] estadosArray = actividad.getEstadosActi().split(",");

            boolean todoCompletado = true;
            for (String estadoValor : estadosArray) {
                if (!estadoValor.trim().equals("2")) {
                    todoCompletado = false;
                    estado = "En proceso";
                    break;
                }
            }

            if (todoCompletado) {
                estado = "Completado";
            }


            if (!estado.equals("Completado")) {
                txtEstadoActividad.setText(estado);
            }

            String imgBd = actividad.getRutaImagenHabito();
            String imageName = doesImageExist(requireContext(), imgBd);
            if (imageName != null) {
                InputStream inputStream = null;
                String assetPath = "img/img_actividades/" + imageName;
                try {
                    inputStream = requireContext().getAssets().open(assetPath);
                    SVG svg = SVG.getFromInputStream(inputStream);
                    if (svg != null) {
                        Drawable drawable = new PictureDrawable(svg.renderToPicture());
                        imgActividad.setImageDrawable(drawable);
                    } else {
                    }
                } catch (IOException | SVGParseException e) {
                }
            } else {
            }
            //Fin del código de cargar imágen svg desde asset

            contenedorActividades.addView(actividadView);
        }
    }

    public static String doesImageExist(Context context, String inputPath) {
        String fileName = extractFileName(inputPath);
        return fileName;
    }
    private static String extractFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash != -1) {
            path = path.substring(lastSlash + 1);
        }
        return path;
    }


}