package com.example.castorway;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeTutor extends AppCompatActivity {
    private ImageView lastSelectedIcon, img_prof_usr, btnDesplegar;
    private ImageView iconHome, iconActividades, iconCalendar, iconRecompensas, iconDiario, iconChat;
    TextView txt_hjs_congeladas, txt_ramas;
    FrameLayout circulogrande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tutor);

        actuInfoTopNav();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        iconHome = findViewById(R.id.icon_home);
        iconActividades = findViewById(R.id.icon_actividades);
        iconCalendar = findViewById(R.id.icon_calendario);
        iconRecompensas = findViewById(R.id.icon_recompensa);
        iconDiario = findViewById(R.id.icon_diario);
        iconChat = findViewById(R.id.icon_chat);

        setMinWidthForIcons();

        iconHome.setOnClickListener(iconClickListener);
        iconActividades.setOnClickListener(iconClickListener);
        iconCalendar.setOnClickListener(iconClickListener);
        iconRecompensas.setOnClickListener(iconClickListener);
        iconDiario.setOnClickListener(iconClickListener);
        iconChat.setOnClickListener(iconClickListener);

        selectIcon(iconHome);
        loadFragment(new HomeFragmentTutor());

        circulogrande = findViewById(R.id.circulo_grande);
        circulogrande.setOnClickListener(this::desplListUsrsKit);

        btnDesplegar = findViewById(R.id.btnDesplegar);
        btnDesplegar.setOnClickListener(this::desplListUsrsKit);
    }
    private void actuInfoTopNav(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();

        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castores = response.body();
                AtomicInteger cntCoincidEmail = new AtomicInteger(0);
                int idCastor = 0;
                if (castores != null) {
                    SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                    for (Castor castor : castores) {
                        if(castor.getEmail().equalsIgnoreCase(preferences.getString("email", ""))){
                            cntCoincidEmail.incrementAndGet();
                            idCastor = castor.getIdCastor();
                            Log.d("MainActivity", "Castor: " + castor.getEmail());
                            break;
                        }
                    }
                    if(cntCoincidEmail.get() == 1 && idCastor != 0){
                        ApiService apiServiceKit = RetrofitClient.getApiService();
                        Call<List<Kit>> call2 = apiService.getAllKits();
                        call2.enqueue(new Callback<List<Kit>>() {
                            @Override
                            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                                List<Kit> kits = response.body();
                                AtomicInteger cntCoincidUsrKit = new AtomicInteger(0);
                                if (kits != null){
                                    SharedPreferences preferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
                                    int idKit = preferences.getInt("idKit", 0);
                                    for (Kit kit : kits) {
                                        if(kit.getIdKit() == idKit && idKit != 0){
                                            int numHjsCongleadas = kit.getHojasCongeladas();
                                            String stringHjsCongeldas = String.valueOf(numHjsCongleadas);

                                            int numRamitas = kit.getRamitas();
                                            String stringNumRamitas = String.valueOf(numRamitas);

                                            txt_hjs_congeladas = findViewById(R.id.txt_hjs_congeladas);
                                            txt_hjs_congeladas.setText(stringHjsCongeldas);

                                            txt_ramas = findViewById(R.id.txt_ramas);
                                            txt_ramas.setText(stringNumRamitas);
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Kit>> call, Throwable t) {

                            }
                        });
                    }else{
                        Toast.makeText(HomeTutor.this, "Ocurrió un error con su cuenta", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeTutor.this, IniciarSesionTutor.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(HomeTutor.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void desplListUsrsKit(View view){
        HorizontalScrollView userScrollView = findViewById(R.id.userScrollView);
        LinearLayout userContainer = findViewById(R.id.userContainer);

        if (userScrollView.getVisibility() == View.GONE) {
            userScrollView.setVisibility(View.VISIBLE);
            userScrollView.setBackgroundResource(R.drawable.rounded_background);
            cargarUsuarios(userContainer);
        } else {
            userScrollView.setVisibility(View.GONE);
            userScrollView.setBackground(null);
        }
    }

    private void cargarUsuarios(LinearLayout userContainer) {
        userContainer.removeAllViews();

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();
        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castores = response.body();
                String codPresa = null;
                if (castores != null) {
                    for (Castor castor : castores) {
                        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                        if (castor.getEmail().equals(sharedPreferences.getString("email", ""))) {
                            codPresa = castor.getCodPresa();
                            break;
                        }
                    }
                }
                if (codPresa != null) {
                    ApiService apiService2 = RetrofitClient.getApiService();
                    Call<List<Kit>> call2 = apiService2.getAllKits();
                    String finalCodPresa = codPresa;
                    call2.enqueue(new Callback<List<Kit>>() {
                        @Override
                        public void onResponse(Call<List<Kit>> call2, Response<List<Kit>> response) {
                            List<Kit> kits = response.body();
                            AtomicInteger cntCoincidUsr = new AtomicInteger(0);
                            if (kits != null) {
                                for (Kit kit : kits) {
                                    if (kit.getCodPresa().equals(finalCodPresa)) {
                                        View usuarioView = LayoutInflater.from(HomeTutor.this).inflate(R.layout.item_usuario, userContainer, false);

                                        ImageView imgUsuario = usuarioView.findViewById(R.id.imgCadaUsuario);
                                        TextView txtNombre = usuarioView.findViewById(R.id.usrNameCdaUsr);
                                        imgUsuario.setImageResource(R.drawable.icn_user_gris);
                                        txtNombre.setText(kit.getNombreUsuario());

                                        LinearLayout usuarioLayout = usuarioView.findViewById(R.id.layoutCadUsr);

                                        final Kit finalKit = kit;
                                        int idKit = kit.getIdKit();
                                        usuarioLayout.setOnClickListener(v -> {
                                            SharedPreferences sharedPreferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("idKit", idKit);
                                            editor.apply();
                                            Toast.makeText(HomeTutor.this, "Usuario seleccionado: " + kit.getNombreUsuario(), Toast.LENGTH_SHORT).show();
                                            actuInfoTopNav();
                                        });
                                        userContainer.addView(usuarioView);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Kit>> call2, Throwable t) {
                            Toast.makeText(HomeTutor.this, "Error en la conexión hijo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(HomeTutor.this, "Ocurrió un error con su cuenta", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeTutor.this, IniciarSesionTutor.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(HomeTutor.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setMinWidthForIcons() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidthPx = displayMetrics.widthPixels;

        int minWidthPx = (int) (screenWidthPx * 0.16);
        iconHome.setMinimumWidth(minWidthPx);
        iconActividades.setMinimumWidth(minWidthPx);
        iconCalendar.setMinimumWidth(minWidthPx);
        iconRecompensas.setMinimumWidth(minWidthPx);
        iconDiario.setMinimumWidth(minWidthPx);
        iconChat.setMinimumWidth(minWidthPx);
    }


    private final View.OnClickListener iconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectIcon((ImageView) view);
            switchFragment(view.getId());
        }
    };

    private void selectIcon(ImageView selectedIcon) {
        if (lastSelectedIcon != null) {
            lastSelectedIcon.setColorFilter(null);
            lastSelectedIcon.setSelected(false);
        }
        selectedIcon.setSelected(true);
        animateIcon(selectedIcon);

        lastSelectedIcon = selectedIcon;
    }

    private void animateIcon(ImageView icon) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    private void switchFragment(int iconId) {
        Fragment fragment = null;
        if (iconId == R.id.icon_home) {
            fragment = new HomeFragmentTutor();
        } else if (iconId == R.id.icon_actividades) {
            fragment = new ActividadesFragmentTutor();
        } else if (iconId == R.id.icon_calendario) {
            fragment = new CalendarioFragmentTutor();
        } else if (iconId == R.id.icon_recompensa) {
            fragment = new RecompensasFragmentTutor();
        } else if (iconId == R.id.icon_diario) {
            fragment = new DiarioFragmentTutor();
        } else if (iconId == R.id.icon_chat) {
            fragment = new ChatFragmentTutor();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
