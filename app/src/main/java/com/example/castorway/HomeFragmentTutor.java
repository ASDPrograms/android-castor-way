package com.example.castorway;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.modelsDB.RelPrem;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        String nombreCastor = preferences.getString("castor_name", "Cargando...");
        NameCastor.setText(nombreCastor);

        Set<String> hijosGuardados = preferences.getStringSet("hijos_list", new HashSet<>());
        if (hijosGuardados != null) {
            useritoContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(requireContext());

            for (String nombreHijo : hijosGuardados) {
                View hijoView = inflater.inflate(R.layout.item_hijo, useritoContainer, false);
                ((TextView) hijoView.findViewById(R.id.txtHijoNombre)).setText(nombreHijo);
                ((ImageView) hijoView.findViewById(R.id.imgHijo)).setImageResource(R.drawable.icn_user_gris);
                useritoContainer.addView(hijoView);
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
        if (!isAdded() || useritoContainer == null) return;

        useritoContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (String nombreHijo : hijos) {
            View hijoView = inflater.inflate(R.layout.item_hijo, useritoContainer, false);
            ((TextView) hijoView.findViewById(R.id.txtHijoNombre)).setText(nombreHijo);
            ((ImageView) hijoView.findViewById(R.id.imgHijo)).setImageResource(R.drawable.icn_user_gris);
            useritoContainer.addView(hijoView);
        }
    }

    private void fetchRecompensaMasCostosa() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Premios>> callPremios = apiService.getAllPremios();

        callPremios.enqueue(new Callback<List<Premios>>() {
            @Override
            public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                if (!isAdded() || response.body() == null) {
                    Log.d("MainActivity", "La respuesta de premios es nula o no se añadió el fragmento");
                    return;
                }

                List<Premios> premios = response.body();
                Log.d("MainActivity", "Premios obtenidos: " + premios.size());

                // Pasamos la lista de premios a la función obtenerPremioMayorCosto
                Premios premioMax = obtenerPremioMayorCosto(premios);

                if (premioMax != null) {
                    mostrarPremio(premioMax);
                } else {
                    Log.d("MainActivity", "No se encontró un premio con estado 0");
                }
            }

            @Override
            public void onFailure(Call<List<Premios>> call, Throwable t) {
                Log.d("MainActivity", "Error al obtener premios: " + t.getMessage());
            }
        });
    }

    private Premios obtenerPremioMayorCosto(List<Premios> premios) {

        SharedPreferences preferences = getActivity().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);

        // Verificamos si idKit no es 0 (es decir, si se encuentra en la sesión)
        if (idKit == 0) {
            Log.d("MainActivity", "No se encontró un idKit válido en las preferencias");
            return null;
        }


        ApiService apiServiceRelPrem = RetrofitClient.getApiService();
        Call<List<RelPrem>> callRelPrem = apiServiceRelPrem.getAllRelPrem();

        final List<Premios> premiosDisponibles = new ArrayList<>();
        callRelPrem.enqueue(new Callback<List<RelPrem>>() {
            @Override
            public void onResponse(Call<List<RelPrem>> call, Response<List<RelPrem>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<RelPrem> relPremList = response.body();
                        Log.d("MainActivity", "Número de relaciones obtenidas: " + relPremList.size());

                        // Filtramos las relaciones que correspondan al idKit
                        for (RelPrem relPrem : relPremList) {
                            if (relPrem.getIdKit() == idKit) {
                                premiosDisponibles.add(obtenerPremioPorId(relPrem.getIdPremio(), premios));
                            }
                        }

                        // Si no se han agregado premios disponibles
                        if (premiosDisponibles.isEmpty()) {
                            Log.d("MainActivity", "No se encontraron premios para el idKit: " + idKit);
                        } else {
                            Log.d("MainActivity", "Premios disponibles para el idKit: " + premiosDisponibles.size());
                        }

                        // Filtramos los premios con estado 0
                        List<Premios> premiosConEstado0 = new ArrayList<>();
                        for (Premios premio : premiosDisponibles) {
                            if (premio.getEstadoPremio() == 0) {
                                premiosConEstado0.add(premio);
                            }
                        }

                        // Si no hay premios con estado 0
                        if (premiosConEstado0.isEmpty()) {
                            Log.d("MainActivity", "No se encontraron premios con estado 0");
                        } else {
                            Log.d("MainActivity", "Premios con estado 0: " + premiosConEstado0.size());
                        }

                        // Ahora encontramos el premio de mayor costo
                        Premios premioMayorCosto = null;
                        double costoMaximo = -1;
                        for (Premios premio : premiosConEstado0) {
                            if (premio.getCostoPremio() > costoMaximo) {
                                costoMaximo = premio.getCostoPremio();
                                premioMayorCosto = premio;
                            }
                        }

                        // Si encontramos un premio válido, lo mostramos
                        if (premioMayorCosto != null) {
                            Log.d("MainActivity", "Premio de mayor costo: " + premioMayorCosto.getNombrePremio());
                            mostrarPremio(premioMayorCosto);
                        } else {
                            Log.d("MainActivity", "No se encontró un premio con estado 0");
                        }
                    } else {
                        Log.d("MainActivity", "La respuesta de relaciones es nula");
                    }
                } else {
                    Log.d("MainActivity", "La respuesta de relaciones no fue exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RelPrem>> call, Throwable t) {
                Log.d("MainActivity", "Error al obtener relaciones entre kit y premios: " + t.getMessage());
            }
        });

        return null;
    }

    private Premios obtenerPremioPorId(int idPremio, List<Premios> premios) {
        // Buscar el premio por su id en la lista de premios
        for (Premios premio : premios) {
            if (premio.getIdPremio() == idPremio) {
                return premio;
            }
        }
        return null;
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


    private void fetchActividades() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> callActividades = apiService.getAllActividades();

        callActividades.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (!isAdded() || response.body() == null) return;

                List<Actividad> actividades = response.body();
                Log.d("MainActivity", "Actividades obtenidas: " + actividades.size());

                // Ordenar actividades por hora de inicio
                actividades = ordenarActividadesPorHora(actividades);

                filtrarActividadesPorKit(actividades);
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.d("MainActivity", "Error al obtener actividades: " + t.getMessage());
            }
        });
    }

    private List<Actividad> ordenarActividadesPorHora(List<Actividad> actividades) {
        // Ordenar las actividades de más pronto a más tarde según la hora de inicio
        Collections.sort(actividades, new Comparator<Actividad>() {
            @Override
            public int compare(Actividad a1, Actividad a2) {
                String horaInicio1 = a1.getHoraInicioHabito();
                String horaInicio2 = a2.getHoraInicioHabito();

                Log.d("MainActivity", "Comparando horas: " + horaInicio1 + " y " + horaInicio2);

                if (horaInicio1 == null || horaInicio2 == null) return 0;

                // Suponiendo que las horas están en formato "HH:mm"
                return horaInicio1.compareTo(horaInicio2);
            }
        });
        return actividades;
    }

    private void mostrarActividadesEnContenedor(List<Actividad> actividades) {
        Log.d("MainActivity", "Mostrando actividades en el contenedor...");

        if (!isAdded() || contenedorActividades == null) {
            Log.d("MainActivity", "El contenedor de actividades no está disponible o el fragmento no está agregado.");
            return;
        }

        // Limpiar las vistas existentes
        contenedorActividades.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        int maxActividades = 2;
        Log.d("MainActivity", "Máximo número de actividades a mostrar: " + maxActividades);
        if (actividades == null || actividades.isEmpty()) {

            Log.d("MainActivity", "No hay actividades disponibles.");
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
                Log.d("MainActivity", "Se ha alcanzado el límite de actividades a mostrar.");
                break;
            }

            Actividad actividad = actividades.get(i);
            Log.d("MainActivity", "Procesando actividad: " + actividad.getNombreHabito());

            View actividadView = inflater.inflate(R.layout.item_actividad_home, contenedorActividades, false);

            // Asignar los elementos de la vista
            TextView txtNombreActividad = actividadView.findViewById(R.id.txtHijoActividad);
            TextView txtEstadoActividad = actividadView.findViewById(R.id.txtaActProceso);
            ImageView imgActividad = actividadView.findViewById(R.id.imgActividad);
            TextView txtHoraActividad = actividadView.findViewById(R.id.txtaActHora);
            LinearLayout LinearPrin = actividadView.findViewById(R.id.LinearPrin);

            // Configuración del listener de clic
            LinearPrin.setOnClickListener(view -> {
                Log.d("MainActivity", "Clic en actividad con id: " + actividad.getIdActividad());

                SharedPreferences preferences= requireContext().getSharedPreferences("actividadSelected", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("idActividad", actividad.getIdActividad());
                editor.apply();
                Log.d("MainActivity", "idActividad guardado en SharedPreferences: " + actividad.getIdActividad());

                SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalActis", MODE_PRIVATE);
                SharedPreferences.Editor editorcerrar = sharedPreferencesCerrar.edit();
                editorcerrar.putBoolean("sesion_activa", true);
                editorcerrar.apply();
                Log.d("MainActivity", "Estado de sesión activado en SharedPreferences.");

                // Cambiar de fragmento
                Fragment nuevoFragment = new ActividadesFragmentTutor();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame_container, nuevoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Log.d("MainActivity", "Fragment de ActividadesFragmentTutor reemplazado.");
            });

            // Configuración de la hora de la actividad
            String horaInicio = actividad.getHoraInicioHabito();
            String horaFinaliza = actividad.getHoraFinHabito();
            if (horaInicio != null && horaFinaliza != null) {
                horaInicio = horaInicio.substring(0, 5);
                horaFinaliza = horaFinaliza.substring(0, 5);

                Log.d("MainActivity", "Hora de inicio de la actividad: " + horaInicio);
                Log.d("MainActivity", "Hora de finalización de la actividad: " + horaFinaliza);
            }

            // Establecer hora en el TextView
            String textoActividad = actividad.getNombreHabito();
            txtNombreActividad.setText(textoActividad);
            if (horaInicio != null && !horaInicio.isEmpty() && horaFinaliza != null && !horaFinaliza.isEmpty()) {
                String horaCompleta = horaInicio + " - " + horaFinaliza;
                txtHoraActividad.setText(horaCompleta); // Establecer la hora en el TextView
                Log.d("MainActivity", "Hora mostrada en el TextView: " + horaCompleta);
            }

            // Determinar el estado de la actividad
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

            Log.d("MainActivity", "Estado de la actividad: " + estado);

            if (!estado.equals("Completado")) {
                txtEstadoActividad.setText(estado);
            }

            // Configurar la imagen de la actividad
            String rutaImagen = actividad.getRutaImagenHabito();
            Log.d("MainActivity", "Ruta de imagen: " + rutaImagen);
            int resId = requireContext().getResources().getIdentifier(rutaImagen, "drawable", requireContext().getPackageName());

            if (resId != 0) {
                Log.d("MainActivity", "Imagen encontrada, cargando recurso: " + resId);
                imgActividad.setImageResource(resId);
            } else {
                Log.d("MainActivity", "No se encontró la imagen en los recursos.");
            }

            // Agregar la vista al contenedor
            contenedorActividades.addView(actividadView);
        }

        Log.d("MainActivity", "Se han mostrado hasta " + maxActividades + " actividades.");
    }


    private void filtrarActividadesPorKit(List<Actividad> actividades) {
        SharedPreferences preferences = getActivity().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);

        if (idKit == 0) {
            Log.d("MainActivity", "No se encontró un idKit válido en las preferencias");
            return;
        }

        List<Actividad> actividadesFiltradas = new ArrayList<>();
        for (Actividad actividad : actividades) {
            if (actividad.getIdKit() == idKit) {
                actividadesFiltradas.add(actividad);
            }
        }
        mostrarActividadesEnContenedor(actividadesFiltradas);
    }


}