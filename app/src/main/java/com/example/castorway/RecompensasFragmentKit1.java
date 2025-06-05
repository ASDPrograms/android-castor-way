package com.example.castorway;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

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
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.modelsDB.RelPrem;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

public class RecompensasFragmentKit1 extends Fragment {
    private LinearLayout imgBanner;
    private LinearLayout Contenedor_Premios;
    private ImageView flecha_iz;
    private ImageView flecha_der;
    TextView prem;
    private static final float RADIUS = 80f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recompensas_kit, container, false);
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
        prem=view.findViewById(R.id.Prem);
        prem.setText("Premios sin reclamar");


        flecha_iz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarImageView(flecha_iz);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Fragment nuevoFragment = new RecompensasFragmentKit();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_container, nuevoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }, 300);
            }
        });

        flecha_der.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animarImageView(flecha_der);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Fragment nuevoFragment = new RecompensasFragmentKit();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_container, nuevoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }, 300);
            }
        });



        fetchRecompensaMasCostosa();
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
        mensajeNoPremios.setText("No hay premios sin reclamar");
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
        if (isAdded() && getContext() != null) {
            Log.e("PAVER", "SIPAPI 1");

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("sesionModalPrem", Context.MODE_PRIVATE);
            boolean isModalOpened = sharedPreferences.getBoolean("sesion_activa_prem", false);

            Log.e("PAVER", "estado modal: " + isModalOpened);

            if(isModalOpened){
                if (isModalOpened && getView() != null) {
                    Log.e("PAVER", "SIPAPI 3");

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("PAVER", "SIPAPI 4");
                            desplegarModal(getView());
                        }
                    }, 500);
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
        View view = getLayoutInflater().inflate(R.layout.bottom_modal_view_premio_kit, null);
        modal.setContentView(view);

        modal.setOnDismissListener(dialog -> {
            SharedPreferences.Editor editorCerrarP = sharedPreferences.edit();
            editorCerrarP.putBoolean("sesion_activa_prem", false);
            editorCerrarP.apply();
        });

        modal.show();


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

        SharedPreferences preferences = getActivity().getSharedPreferences("usrKitCuentaKit", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);
        fetchRamitasDelKit(idKit, TxtRamitasKit);
    }
    private void fetchRecompensaMasCostosa() {
        requireActivity().runOnUiThread(this::mostrarMensajeNoPremios);

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

            SharedPreferences preferences = getActivity().getSharedPreferences("usrKitCuentaKit", MODE_PRIVATE);
            int idKit = preferences.getInt("idKit", 0);

            Map<Integer, Premios> premiosMap = premios.stream()
                    .collect(Collectors.toMap(Premios::getIdPremio, p -> p));

            List<Premios> premiosConEstado0 = relPremList.stream()
                    .filter(r -> r.getIdKit() == idKit)
                    .map(r -> premiosMap.get(r.getIdPremio()))
                    .filter(p -> p != null && p.getEstadoPremio() == 0)
                    .collect(Collectors.toList());

            requireActivity().runOnUiThread(() -> {
                if (!premiosConEstado0.isEmpty()) {
                    mostrarPremios(premiosConEstado0);
                } else {
                    mostrarMensajeNoPremios();
                }
            });
        });
    }


    private void mostrarPremios(List<Premios> premios) {
        if (!isAdded() || Contenedor_Premios == null) return;

        List<Premios> premiosFiltrados = premios.stream()
                .filter(premio -> premio.getEstadoPremio() == 0)
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


            imgPremio.setOnClickListener(v -> {
                animarLinear(Linear_Img_Prem);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
                    SharedPreferences.Editor editorP = sharedPreferences.edit();
                    editorP.putBoolean("sesion_activa_prem", true);
                    editorP.apply();

                    BottomSheetDialog modal = new BottomSheetDialog(requireContext());
                    View view = getLayoutInflater().inflate(R.layout.bottom_modal_view_premio_kit, null);
                    modal.setContentView(view);

                    modal.setOnDismissListener(dialog -> {
                        SharedPreferences sharedPreferencesCerrar = requireContext().getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
                        SharedPreferences.Editor editorCerrarP = sharedPreferencesCerrar.edit();
                        editorCerrarP.putBoolean("sesion_activa_prem", false);
                        editorCerrarP.apply();
                    });

                    modal.show();

                    SharedPreferences preferences = requireActivity().getSharedPreferences("usrKitCuentaKit", MODE_PRIVATE);
                    int idKit = preferences.getInt("idKit", 0);


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

                    LinearLayout BtnReclamar=view.findViewById(R.id.ReclamarPremio);
                    BtnReclamar.setOnClickListener(vi -> {


                        mostrarModalReclamar(
                                "¿Seguro que quieres reclamar el premio?",
                                "Nota: ¡No se puede regresar esta acción!",
                                premio,
                                idKit,
                                premios,
                                modal
                        );

                    });


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

                    fetchRamitasDelKit(idKit, TxtRamitasKit);
                }, 300);
            });

            filaActual.addView(vistaPremio);

            if (i == premiosFiltrados.size() - 1 && i % premiosPorFila != 1) {
                filaActual.setGravity(Gravity.CENTER);
            }
        }
    }
    private void mostrarModalReclamar(String titulo, String mensaje, Premios premio, int idKit, List<Premios> premios, BottomSheetDialog modal) {
        Context context = getContext();
        if (context == null) return;

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.modal_reclamar_kit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView txtTitle = dialog.findViewById(R.id.txtDialogTitle);
        TextView txtMessage = dialog.findViewById(R.id.txtDialogMessage);
        Button btnClose = dialog.findViewById(R.id.btnCerrarModal);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        txtTitle.setText(titulo);
        txtMessage.setText(mensaje);

        btnConfirm.setOnClickListener(v -> {
            reclamarPremio(premio, idKit, premios,modal);
            dialog.dismiss();

        });

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
    private void reclamarPremio(Premios premio, int idKit, List<Premios> premios,BottomSheetDialog modal) {
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
                int ramitasActuales = kitSeleccionado.getRamitas();
                int costoPremio = premio.getCostoPremio();

                requireActivity().runOnUiThread(() -> {
                    if (ramitasActuales >= costoPremio) {
                        int ramitasRestantes = ramitasActuales - costoPremio;
                        kitSeleccionado.setRamitas(ramitasRestantes);

                        apiService.updateKit(kitSeleccionado).enqueue(new Callback<Kit>() {
                            @Override
                            public void onResponse(Call<Kit> call, Response<Kit> response) {
                                if (response.isSuccessful()) {
                                    SharedPreferences sharedPreferencesCerrar = requireContext()
                                            .getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
                                    sharedPreferencesCerrar.edit()
                                            .putBoolean("sesion_activa_prem", false)
                                            .apply();

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        Fragment nuevoFragment = new RecompensasFragmentKit();
                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.replace(R.id.frame_container, nuevoFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }, 300);

                                    premio.setEstadoPremio(1);
                                    actualizarPremio(premio);
                                    mostrarToastPersonalizado(
                                            "Este premio ha sido reclamado correctamente",
                                            R.drawable.img_circ_palomita_verde,
                                            R.color.verdecito_toast
                                    );

                                    actualizarLista(premios);

                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        if (modal != null && modal.isShowing()) {
                                            modal.dismiss();
                                        }
                                    }, 500);
                                } else {
                                    Toast.makeText(requireContext(), "Error al actualizar ramitas del kit", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Kit> call, Throwable t) {
                                Toast.makeText(requireContext(), "Error de red al actualizar ramitas", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        mostrarToastPersonalizado(
                                "No tienes ramitas suficientes para reclamar este premio",
                                R.drawable.img_circ_tache_rojo,
                                R.color.rojito_toast
                        );
                    }
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error al obtener las ramitas del kit", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void mostrarToastPersonalizado(String mensaje, int iconoRes, int colorFondoRes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout = inflater.inflate(R.layout.toast_personalizado, null);

        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(iconoRes);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(mensaje);

        if (layout.getBackground() != null) {
            Drawable background = layout.getBackground();
            background.setColorFilter(
                    ContextCompat.getColor(requireContext(), colorFondoRes),
                    PorterDuff.Mode.SRC_IN
            );
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(requireContext(), colorFondoRes));
        }

        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        Toast toast = new Toast(requireContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
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
                ObjectAnimator.ofFloat(imageView, "scaleX", 1.1f, 1f).setDuration(150).start();
                ObjectAnimator.ofFloat(imageView, "scaleY", 1.1f, 1f).setDuration(150).start();
            }
        });
        animatorSet.start();
    }

    private void actualizarLista(List<Premios> premios) {

        List<Premios> premiosFiltrados = premios.stream()
                .filter(premio -> premio.getEstadoPremio() == 0)
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
