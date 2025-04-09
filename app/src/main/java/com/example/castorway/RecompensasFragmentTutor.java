package com.example.castorway;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.modelsDB.RelPrem;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecompensasFragmentTutor extends Fragment {
    private LinearLayout imgBanner;
    private LinearLayout Contenedor_Premios;
    private ImageView flecha_iz;
    private ImageView flecha_der;
    ImageView btnAgregarPrem;
    TextView prem;
    private static final float RADIUS = 80f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recompensas_tutor, container, false);
        imgBanner = view.findViewById(R.id.Banner);
        view.post(() -> cargarImagenSVG());

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flecha_iz = view.findViewById(R.id.flecha_iz);
        flecha_der = view.findViewById(R.id.flecha_de);
        Contenedor_Premios = view.findViewById(R.id.Contenedor_Premios);
        btnAgregarPrem = view.findViewById(R.id.btnAgregarPrem);
        prem=view.findViewById(R.id.Prem);
        prem.setText("Premios Reclamados");

        btnAgregarPrem.setOnClickListener(this::abrirFormRecompensas);

        flecha_iz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarImageView(flecha_iz);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Fragment nuevoFragment = new RecompensasFragmentTutor1();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_container, nuevoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }, 300); // Espera 300ms para que termine la animación
            }
        });

        flecha_der.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarImageView(flecha_der);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Fragment nuevoFragment = new RecompensasFragmentTutor1();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_container, nuevoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }, 300); // Igual, espera 300ms
            }
        });



        fetchRecompensaMasCostosa();
    }

    private int confirmUsrKitSeleccionado(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = sharedPreferences.getInt("idKit", 0);
        return idKit;
    }
    private void abrirFormRecompensas(View view){
        animarImageView(btnAgregarPrem);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            int num = confirmUsrKitSeleccionado();
            if(num != 0){
                Intent intent = new Intent(view.getContext(), AgregarPremTutor.class);
                startActivity(intent);
            }
        }, 300); // Espera 300ms antes de lanzar la nueva Activity
    }


    private void mostrarMensajeSeleccionarIdKit() {
        if (!isAdded() || Contenedor_Premios == null) return;

        Contenedor_Premios.removeAllViews();

        LinearLayout contenedorMensaje = new LinearLayout(requireContext());
        contenedorMensaje.setOrientation(LinearLayout.VERTICAL);
        contenedorMensaje.setGravity(Gravity.CENTER);
        contenedorMensaje.setPadding(16, 16, 16, 16);

        ImageView imgMensaje = new ImageView(requireContext());
        imgMensaje.setImageResource(R.drawable.castor_arreglando);

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                500, 500);
        imgMensaje.setLayoutParams(imgParams);

        TextView mensajeNoPremios = new TextView(requireContext());
        mensajeNoPremios.setText("Seleccione primero un Kit");
        mensajeNoPremios.setTextSize(35);
        mensajeNoPremios.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
        mensajeNoPremios.setTextColor(Color.BLACK);
        mensajeNoPremios.setPadding(16, 16, 16, 16);
        mensajeNoPremios.setGravity(Gravity.CENTER);


        contenedorMensaje.addView(mensajeNoPremios);
        contenedorMensaje.addView(imgMensaje);

        LinearLayout.LayoutParams contenedorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        contenedorMensaje.setLayoutParams(contenedorParams);

        Contenedor_Premios.addView(contenedorMensaje);
    }
    private void mostrarMensajeNoPremios() {
        if (!isAdded() || Contenedor_Premios == null) return;

        Contenedor_Premios.removeAllViews();

        LinearLayout contenedorMensaje = new LinearLayout(requireContext());
        contenedorMensaje.setOrientation(LinearLayout.VERTICAL);
        contenedorMensaje.setGravity(Gravity.CENTER);
        contenedorMensaje.setPadding(16, 16, 16, 16);

        ImageView imgMensaje = new ImageView(requireContext());
        imgMensaje.setImageResource(R.drawable.castor_arreglando);

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                500, 500);
        imgMensaje.setLayoutParams(imgParams);

        TextView mensajeNoPremios = new TextView(requireContext());
        mensajeNoPremios.setText("No hay Premios Reclamados");
        mensajeNoPremios.setTextSize(35);
        mensajeNoPremios.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.dongle_bold));
        mensajeNoPremios.setTextColor(Color.BLACK);
        mensajeNoPremios.setPadding(16, 16, 16, 16);
        mensajeNoPremios.setGravity(Gravity.CENTER);


        contenedorMensaje.addView(mensajeNoPremios);
        contenedorMensaje.addView(imgMensaje);

        LinearLayout.LayoutParams contenedorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        contenedorMensaje.setLayoutParams(contenedorParams);

        Contenedor_Premios.addView(contenedorMensaje);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Verificar si el fragmento está adjunto y si el contexto está disponible
        if (isAdded() && getContext() != null) {
            Log.e("PAVER", "SIPAPI 1");

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("sesionModalPrem", Context.MODE_PRIVATE);
            boolean isModalOpened = sharedPreferences.getBoolean("sesion_activa_prem", false);

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
    private void desplegarModal(View view) {
        try {
            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Premios>> call = apiService.getAllPremios();
            call.enqueue(new Callback<List<Premios>>() {
                @Override
                public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                    List<Premios> listaPremios = response.body();

                    if (listaPremios != null) {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("premioSelected", Context.MODE_PRIVATE);
                        int idPremio = sharedPreferences.getInt("idPremio", 0);

                        for (Premios premio : listaPremios) {
                            if (premio.getIdPremio() == idPremio) {
                                mostrarModalPremio(premio, listaPremios);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Premios>> call, Throwable t) {
                    if (isAdded()) {
                        Log.e("ERROR", "Fallo en la API: " + t.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MiFragment", "Error al intentar mostrar el modal: " + e.getMessage());
        }
    }

    private void mostrarModalPremio(Premios premio, List<Premios> listaPremios) {
        if (!isAdded()) return;

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
        SharedPreferences.Editor editorP = sharedPreferences.edit();
        editorP.putBoolean("sesion_activa_prem", true);
        editorP.apply();

        BottomSheetDialog modal = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_modal_view_premio, null);
        modal.setContentView(view);

        modal.setOnDismissListener(dialog -> {
            SharedPreferences.Editor editorCerrarP = sharedPreferences.edit();
            editorCerrarP.putBoolean("sesion_activa_prem", false);
            editorCerrarP.apply();
        });

        modal.show();

        int idKit = requireActivity().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE).getInt("idKit", 0);
        if (idKit == 0) {
            requireActivity().runOnUiThread(this::mostrarMensajeSeleccionarIdKit);
            return;
        }

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtNivel = view.findViewById(R.id.txtNivel);
        ImageView imgPremModal = view.findViewById(R.id.imgPremio);
        TextView txtCate = view.findViewById(R.id.txtCategoria);
        TextView txtTipo = view.findViewById(R.id.txtTipo);
        TextView TxtNumRam = view.findViewById(R.id.CostoPrem);
        TextView TxtRamitasKit = view.findViewById(R.id.ramitas_Kit);
        TextView TxtRamitascosto = view.findViewById(R.id.ramitas_costo);
        TextView TxtInfo = view.findViewById(R.id.info_extra);
        ImageView imgEstadoModal = view.findViewById(R.id.Img_Estado_M);
        ImageView imgFavoritoModal = view.findViewById(R.id.Img_Favorito_M);
        LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
        LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

        txtTitle.setText(premio.getNombrePremio());
        txtNivel.setText(premio.getNivelPremio());
        txtCate.setText(premio.getCategoriaPremio());
        txtTipo.setText(premio.getTipoPremio());
        TxtNumRam.setText(String.valueOf(premio.getCostoPremio()));
        TxtRamitascosto.setText(String.valueOf(premio.getCostoPremio()));
        TxtInfo.setText(premio.getInfoExtraPremio());

        String imageName = doesImageExist(requireContext(), premio.getRutaImagenHabito());
        if (imageName != null) {
            try (InputStream inputStream = requireContext().getAssets().open("img/Iconos-recompensas/" + imageName)) {
                SVG svg = SVG.getFromInputStream(inputStream);
                if (svg != null) {
                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                    imgPremModal.setImageDrawable(drawable);
                }
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }
        }

        imgEstadoModal.setImageResource(premio.getEstadoPremio() == 0 ? R.drawable.trofeo_vacio : R.drawable.trofeo_relleno);
        imgFavoritoModal.setImageResource(premio.getFavorito() == 0 ? R.drawable.corazon_vacio : R.drawable.corazon_relleno);

        imgFavoritoModal.setOnClickListener(vi -> {
            animarImageView(imgFavoritoModal);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                premio.setFavorito(premio.getFavorito() == 0 ? 1 : 0);
                actualizarLista(listaPremios);
                actualizarPremio(premio);
                imgFavoritoModal.setImageResource(premio.getFavorito() == 0 ? R.drawable.corazon_vacio : R.drawable.corazon_relleno);
            }, 300);
        });

        imgEstadoModal.setOnClickListener(vi -> {
            animarImageView(imgEstadoModal);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                premio.setEstadoPremio(premio.getEstadoPremio() == 0 ? 1 : 0);
                actualizarLista(listaPremios);
                actualizarPremio(premio);
                imgEstadoModal.setImageResource(premio.getEstadoPremio() == 0 ? R.drawable.trofeo_vacio : R.drawable.trofeo_relleno);
            }, 300);
        });
        btnEditActi.setOnClickListener(v1 -> {
            animarLinear(btnEditActi);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                SharedPreferences preferences1 = requireContext().getSharedPreferences("premioSelected", MODE_PRIVATE);
                SharedPreferences.Editor editorp = preferences1.edit();
                editorp.putInt("idPremio", premio.getIdPremio());
                editorp.apply();


                Intent intent = new Intent(requireActivity(), EditarPremio.class);
                startActivity(intent);
            },300);
        });

        btnBorrarActi.setOnClickListener(v1 -> {
            animarLinear(btnBorrarActi);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

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
                    borrarPremio(premio.getIdPremio());

                });


                txtDialogTitle.setText("¡Atención!");
                txtDialogMessage.setText("Estás a punto de borrar el premio: " + "'" + premio.getNombrePremio() + "'" + " si aceptas no se podrá deshacer la acción.");

                btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                modalBorrar.show();
            },300);
        });


        fetchRamitasDelKit(idKit, TxtRamitasKit);
    }

    private void fetchRecompensaMasCostosa() {
        // Mostrar mensaje "No hay premios" primero
        requireActivity().runOnUiThread(this::mostrarMensajeNoPremios);

        // Mostrar primero desde caché
        SharedPreferences cache = requireContext().getSharedPreferences("cachePremiosEstado1", MODE_PRIVATE);
        String premiosJson = cache.getString("premios_estado_1", null);
        if (premiosJson != null) {
            Gson gson = new Gson();
            List<Premios> premiosCache = gson.fromJson(premiosJson, new TypeToken<List<Premios>>(){}.getType());
            if (!premiosCache.isEmpty()) {
                mostrarPremios(premiosCache);
                return; // Salir, ya que los premios ya fueron cargados
            }
        }

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
                    .collect(Collectors.toMap(Premios::getIdPremio, p -> p));

            List<Premios> premiosConEstado1 = relPremList.stream()
                    .filter(r -> r.getIdKit() == idKit)
                    .map(r -> premiosMap.get(r.getIdPremio()))
                    .filter(p -> p != null && p.getEstadoPremio() == 1)
                    .collect(Collectors.toList());

            // Mostrar los premios obtenidos
            requireActivity().runOnUiThread(() -> {
                if (!premiosConEstado1.isEmpty()) {
                    // Guardar en caché
                    Gson gson = new Gson();
                    String jsonPremios = gson.toJson(premiosConEstado1);
                    SharedPreferences.Editor editor = cache.edit();
                    editor.putString("premios_estado_1", jsonPremios);
                    editor.apply();

                    mostrarPremios(premiosConEstado1);
                } else {
                    mostrarMensajeNoPremios();  // Mostrar mensaje si no hay premios
                }
            });
        });
    }

    private void mostrarPremios(List<Premios> premios) {
        if (!isAdded() || Contenedor_Premios == null) return;

        List<Premios> premiosFiltrados = premios.stream()
                .filter(premio -> premio.getEstadoPremio() == 1)
                .collect(Collectors.toList());

        if (premiosFiltrados.isEmpty()) {
            mostrarMensajeNoPremios();
            return;
        }

        premiosFiltrados.sort(Comparator.comparingInt(Premios::getFavorito).reversed());

        Contenedor_Premios.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        LinearLayout filaActual = null;
        int premiosPorFila = 2;

        for (int i = 0; i < premiosFiltrados.size(); i++) {
            if (i % premiosPorFila == 0) {
                filaActual = new LinearLayout(requireContext());
                filaActual.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                filaActual.setOrientation(LinearLayout.HORIZONTAL);
                filaActual.setGravity(Gravity.CENTER_HORIZONTAL);
                filaActual.setPadding(0, 8, 0, 8);
                Contenedor_Premios.addView(filaActual);
            }

            Premios premio = premiosFiltrados.get(i);
            View vistaPremio = inflater.inflate(R.layout.item_premio, filaActual, false);

            TextView txtHijoPremio = vistaPremio.findViewById(R.id.Name_Prem);
            txtHijoPremio.setText(premio.getNombrePremio());

            ImageView imgPremio = vistaPremio.findViewById(R.id.Img_Prem);
            ImageView imgEstado = vistaPremio.findViewById(R.id.Img_Estado);
            ImageView imgFavorito = vistaPremio.findViewById(R.id.Img_Favorito);
            LinearLayout Linear_Img_Prem = vistaPremio.findViewById(R.id.Linear_Img_Prem);

            String imgBd = premio.getRutaImagenHabito();
            String imageName = doesImageExist(requireContext(), imgBd);
            if (imageName != null) {
                try (InputStream inputStream = requireContext().getAssets().open("img/Iconos-recompensas/" + imageName)) {
                    SVG svg = SVG.getFromInputStream(inputStream);
                    if (svg != null) {
                        Drawable drawable = new PictureDrawable(svg.renderToPicture());
                        imgPremio.setImageDrawable(drawable);
                    }
                } catch (IOException | SVGParseException e) {
                }
            }

            imgEstado.setImageResource(premio.getEstadoPremio() == 0 ? R.drawable.trofeo_vacio : R.drawable.trofeo_relleno);
            imgFavorito.setImageResource(premio.getFavorito() == 0 ? R.drawable.corazon_vacio : R.drawable.corazon_relleno);

            imgFavorito.setOnClickListener(v -> {
                animarImageView(imgFavorito);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    premio.setFavorito(premio.getFavorito() == 0 ? 1 : 0);
                    actualizarLista(premios);
                    actualizarPremio(premio);
                }, 300);
            });

            imgEstado.setOnClickListener(v -> {
                animarImageView(imgEstado);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    premio.setEstadoPremio(premio.getEstadoPremio() == 0 ? 1 : 0);
                    actualizarLista(premios);
                    actualizarPremio(premio);
                }, 300);
            });


            imgPremio.setOnClickListener(v -> {
                animarLinear(Linear_Img_Prem);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // Guardar sesión activa del modal
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
                    SharedPreferences.Editor editorP = sharedPreferences.edit();
                    editorP.putBoolean("sesion_activa_prem", true);
                    editorP.apply();

                    BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                    View view = getLayoutInflater().inflate(R.layout.bottom_modal_view_premio, null);
                    modal.setContentView(view);

                    modal.setOnDismissListener(dialog -> {
                        // Cerrar sesión del modal
                        SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
                        SharedPreferences.Editor editorCerrarP = sharedPreferencesCerrar.edit();
                        editorCerrarP.putBoolean("sesion_activa_prem", false);
                        editorCerrarP.apply();
                    });

                    modal.show();

                    SharedPreferences preferences = requireActivity().getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
                    int idKit = preferences.getInt("idKit", 0);

                    if (idKit == 0) {
                        requireActivity().runOnUiThread(() -> mostrarMensajeSeleccionarIdKit());
                        return;
                    }

                    TextView txtTitle = view.findViewById(R.id.txtTitle);
                    TextView txtNivel = view.findViewById(R.id.txtNivel);
                    ImageView imgPremModal = view.findViewById(R.id.imgPremio);
                    TextView txtCate = view.findViewById(R.id.txtCategoria);
                    TextView txtTipo = view.findViewById(R.id.txtTipo);
                    TextView TxtNumRam = view.findViewById(R.id.CostoPrem);
                    TextView TxtRamitasKit = view.findViewById(R.id.ramitas_Kit);
                    TextView TxtRamitascosto = view.findViewById(R.id.ramitas_costo);
                    TextView TxtInfo = view.findViewById(R.id.info_extra);

                    ImageView imgEstadoModal = view.findViewById(R.id.Img_Estado_M);
                    ImageView imgFavoritoModal = view.findViewById(R.id.Img_Favorito_M);

                    LinearLayout btnEditActi = view.findViewById(R.id.layout_btn_edit_acti);
                    LinearLayout btnBorrarActi = view.findViewById(R.id.layout_btn_borrar_acti);

                    int costoPremio = premio.getCostoPremio();

                    txtTitle.setText(premio.getNombrePremio());
                    txtNivel.setText(premio.getNivelPremio());
                    txtCate.setText(premio.getCategoriaPremio());
                    txtTipo.setText(premio.getTipoPremio());
                    TxtNumRam.setText(String.valueOf(costoPremio));
                    TxtRamitascosto.setText(String.valueOf(costoPremio));
                    TxtInfo.setText(premio.getInfoExtraPremio());

                    String imageName2 = doesImageExist(requireContext(), imgBd);
                    if (imageName2 != null) {
                        try (InputStream inputStream = requireContext().getAssets().open("img/Iconos-recompensas/" + imageName2)) {
                            SVG svg = SVG.getFromInputStream(inputStream);
                            if (svg != null) {
                                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                imgPremModal.setImageDrawable(drawable);
                            }
                        } catch (IOException | SVGParseException e) {
                        }
                    }

                    imgEstadoModal.setImageResource(premio.getEstadoPremio() == 0 ? R.drawable.trofeo_vacio : R.drawable.trofeo_relleno);
                    imgFavoritoModal.setImageResource(premio.getFavorito() == 0 ? R.drawable.corazon_vacio : R.drawable.corazon_relleno);

                    imgFavoritoModal.setOnClickListener(vi -> {
                        animarImageView(imgFavoritoModal);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            premio.setFavorito(premio.getFavorito() == 0 ? 1 : 0);
                            actualizarLista(premios);
                            actualizarPremio(premio);
                            imgFavoritoModal.setImageResource(premio.getFavorito() == 0 ? R.drawable.corazon_vacio : R.drawable.corazon_relleno);
                        }, 300);
                    });

                    imgEstadoModal.setOnClickListener(vi -> {
                        animarImageView(imgEstadoModal);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            premio.setEstadoPremio(premio.getEstadoPremio() == 0 ? 1 : 0);
                            actualizarLista(premios);
                            actualizarPremio(premio);
                            imgEstadoModal.setImageResource(premio.getEstadoPremio() == 0 ? R.drawable.trofeo_vacio : R.drawable.trofeo_relleno);
                        }, 300);
                    });
                    btnEditActi.setOnClickListener(v1 -> {
                        animarLinear(btnEditActi);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            SharedPreferences preferences1 = requireContext().getSharedPreferences("premioSelected", MODE_PRIVATE);
                            SharedPreferences.Editor editorp = preferences1.edit();
                            editorp.putInt("idPremio", premio.getIdPremio());
                            editorp.apply();


                            Intent intent = new Intent(requireActivity(), EditarPremio.class);
                            startActivity(intent);
                        }, 300);
                    });

                    btnBorrarActi.setOnClickListener(v1 -> {
                        animarLinear(btnBorrarActi);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

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
                                borrarPremio(premio.getIdPremio());

                            });


                            txtDialogTitle.setText("¡Atención!");
                            txtDialogMessage.setText("Estás a punto de borrar el premio: " + "'" + premio.getNombrePremio() + "'" + " si aceptas no se podrá deshacer la acción.");

                            btnCerrarModal.setOnClickListener(v2 -> modalBorrar.dismiss());
                            modalBorrar.show();
                        }, 300);
                    });



                    fetchRamitasDelKit(idKit, TxtRamitasKit);
                }, 300);
            });

            filaActual.addView(vistaPremio);

            if (i == premiosFiltrados.size() - 1 && i % premiosPorFila != 1) {
                filaActual.setGravity(Gravity.CENTER);
            }
        }
    }

    private void borrarPremio(int idPremio) {
        Log.d("API_LOG", "Iniciando eliminación de relaciones para el premio ID: " + idPremio);

        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> callRel = apiService.deleteRelPrem(idPremio);

        callRel.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_LOG", "Relaciones eliminadas exitosamente. Código: " + response.code());

                    // Ahora sí, eliminamos el premio
                    eliminarPremio(idPremio);

                } else {
                    Log.e("API_ERROR", "Error al eliminar relaciones. Código: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_ERROR", "Cuerpo del error (relaciones): " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("API_ERROR", "Error al leer el cuerpo del error: " + e.getMessage());
                        }
                    }
                    Toast.makeText(requireContext(), "No se pudo eliminar las relaciones del premio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Fallo al eliminar relaciones del premio: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Error de conexión al eliminar relaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarPremio(int idPremio) {
        Log.d("API_LOG", "Eliminando premio con ID: " + idPremio);

        ApiService apiService = RetrofitClient.getApiService();
        Call<Void> callPremio = apiService.deletePremio(idPremio);

        callPremio.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_LOG", "Premio eliminado exitosamente. Código: " + response.code());

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_personalizado, null);

                    ImageView icon = layout.findViewById(R.id.toast_icon);
                    icon.setImageResource(R.drawable.btn_borrar_acti_rojo);

                    TextView text = layout.findViewById(R.id.toast_text);
                    text.setText("Premio eliminado con éxito");

                    Drawable background = layout.getBackground();
                    background.setColorFilter(ContextCompat.getColor(requireContext(), R.color.rojito_toast), PorterDuff.Mode.SRC_IN);
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

                    Toast toast = new Toast(requireContext());
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_container, new RecompensasFragmentTutor1());
                    transaction.commit();
                } else {
                    Log.e("API_ERROR", "Error al eliminar el premio. Código: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_ERROR", "Cuerpo del error (premio): " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("API_ERROR", "Error al leer el cuerpo del error: " + e.getMessage());
                        }
                    }
                    Toast.makeText(requireContext(), "Ocurrió un error al eliminar el premio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Fallo al eliminar el premio: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Error de conexión al eliminar el premio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRamitasDelKit(int idKit, TextView TxtRamitasKit) {
        ApiService apiService = RetrofitClient.getApiService();

        CompletableFuture<List<Kit>> kitFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Kit>> response = apiService.getAllKits().execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                } else {
                    return Collections.emptyList();
                }
            } catch (IOException e) {
                return Collections.emptyList();
            }
        });

        kitFuture.thenAccept(kits -> {
            Kit kitSeleccionado = kits.stream()
                    .filter(kit -> kit.getIdKit() == idKit)
                    .findFirst()
                    .orElse(null);

            if (kitSeleccionado != null) {
                int ramitas = kitSeleccionado.getRamitas();
                requireActivity().runOnUiThread(() -> {
                    if (TxtRamitasKit != null) {
                        TxtRamitasKit.setText(String.valueOf(ramitas));
                    } else {
                    }
                });
            } else {
            }
        });
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
    private void animarImageView(ImageView imageView) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.1f);

        scaleX.setDuration(150);
        scaleY.setDuration(150);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Regresar al tamaño original
                ObjectAnimator.ofFloat(imageView, "scaleX", 1.1f, 1f).setDuration(150).start();
                ObjectAnimator.ofFloat(imageView, "scaleY", 1.1f, 1f).setDuration(150).start();
            }
        });
        animatorSet.start();
    }

    private void actualizarLista(List<Premios> premios) {

        List<Premios> premiosFiltrados = premios.stream()
                .filter(premio -> premio.getEstadoPremio() == 1)
                .collect(Collectors.toList());


        if (premiosFiltrados.isEmpty()) {
            mostrarMensajeNoPremios();
            return;
        }
        premiosFiltrados.sort(Comparator.comparingInt(Premios::getFavorito).reversed());
        mostrarPremios(premiosFiltrados);
    }
    private void actualizarPremio(Premios premio) {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.updatePremios(premio).enqueue(new Callback<Premios>() {
            @Override
            public void onResponse(Call<Premios> call, Response<Premios> response) {
                if (!response.isSuccessful()) {
                    Log.e("API", "Error al actualizar premio: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Premios> call, Throwable t) {
                Log.e("API", "Error en la actualización del premio: " + t.getMessage(), t);
            }
        });
    }

    private void cargarImagenSVG() {
        String imgBd = "Banner_Recom.svg";
        String imageName = doesImageExist(requireContext(), imgBd);

        if (imageName != null) {
            String assetPath = "img/" + imageName;
            try (InputStream inputStream = requireContext().getAssets().open(assetPath)) {
                SVG svg = SVG.getFromInputStream(inputStream);
                if (svg != null) {
                    int width = imgBanner.getWidth();
                    if (width > 0) {
                        Bitmap bitmap = renderSvgToBitmap(svg, width);
                        Bitmap roundedBitmap = getRoundedCornerBitmap(bitmap, RADIUS);
                        imgBanner.setBackground(new BitmapDrawable(getResources(), roundedBitmap));
                    }
                }
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap renderSvgToBitmap(SVG svg, int width) {
        Picture picture = svg.renderToPicture();
        int originalWidth = picture.getWidth();
        int originalHeight = picture.getHeight();
        int height = (originalHeight * width) / originalWidth;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(picture, new android.graphics.Rect(0, 0, width, height));

        return bitmap;
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawRoundRect(rect, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    public static String doesImageExist(Context context, String inputPath) {
        return extractFileName(inputPath);
    }

    private static String extractFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return (lastSlash != -1) ? path.substring(lastSlash + 1) : path;
    }

}
