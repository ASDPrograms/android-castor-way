package com.example.castorway;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeKit extends AppCompatActivity {
    private ImageView lastSelectedIcon;
    TextView txt_hjs_congeladas, txt_ramas;
    private ImageView iconHome, iconActividades, iconCalendar, iconRecompensas, iconDiario, iconChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_kit);

        //Al abrir intentar recibir el valor de modal trás actividad creada
        String fragmentName = getIntent().getStringExtra("fragmentActiCrear");
        String fragmentNamePremio = getIntent().getStringExtra("fragmenPremCrear");
        String fragmentNamePremioEditar = getIntent().getStringExtra("fragmenPremEditar");

        Fragment fragment = null;

        if (fragmentName != null && fragmentName.equals("ActividadesFragmentKit")) {
            fragment = new ActividadesFragmentKit();
        }

        //if (fragmentNamePremio != null && fragmentNamePremio.equals("RecompensasFragmentKit1")) {
        //            fragment = new RecompensasFragmentKit1();
        //        }
        if (fragmentNamePremioEditar != null && fragmentNamePremio.equals("RecompensasFragmentKit")) {
            fragment = new RecompensasFragmentKit();
        }

        // Si se recibe un fragmento válido, lo mostramos
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment); // Asegúrate de tener un contenedor adecuado
            transaction.commit();
        }


        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String nombreUsuario = preferences.getString("nombreUsuario", null);

        if (nombreUsuario == null) {
            Log.e("DEBUG", "Sin nombre de usuario");
            Toast.makeText(HomeKit.this, "Ocurrió un error con su cuenta", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeKit.this, IniciarSesionKit.class);
            startActivity(intent);
            finish();

        }

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
        loadFragment(new HomeFragmentKit());
        actuInfoTopNav();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Obtener el nombre del fragmento desde cualquiera de los extras
        String fragmentName = getIntent().getStringExtra("fragmentActiCrear");
        String fragmentNamePremio = getIntent().getStringExtra("fragmenPremCrear");
        String fragmentNamePremioEditar = getIntent().getStringExtra("fragmenPremEditar");

        Fragment fragment = null;
        ImageView iconToSelect = null;

        // Verificar cuál fragmento se debe cargar
        if (fragmentName != null && fragmentName.equals("ActividadesFragmentKit")) {
            fragment = new ActividadesFragmentKit();
            iconToSelect = iconActividades;
        } //else if (fragmentNamePremio != null && fragmentNamePremio.equals("RecompensasFragmentKit1")) {
        //fragment = new RecompensasFragmentKit1();
        //iconToSelect = iconRecompensas;
        //}
        else if (fragmentNamePremioEditar != null && fragmentNamePremioEditar.equals("RecompensasFragmentKit")) {
            fragment = new RecompensasFragmentKit();
            iconToSelect = iconRecompensas;
        }

        // Cargar el fragmento seleccionado y seleccionar el icono correspondiente
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment);
            transaction.commit();

            if (iconToSelect != null) {
                selectIcon(iconToSelect);
            }
        }
    }
    //para cuando cierra la app (el activity de homekit)
    @Override
    public void onDestroy() {
        super.onDestroy();
        limpiarSesionModalActis();
    }
    private void limpiarSesionModalActis(){
        SharedPreferences preferences = getSharedPreferences("sesionModalActis", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void actuInfoTopNav(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Kit>> call = apiService.getAllKits();
        call.enqueue(new Callback<List<Kit>>() {
            @Override
            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                List<Kit> kits = response.body();
                if (kits != null) {
                    SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                    for (Kit kit : kits) {
                        if(kit.getNombreUsuario().equals(preferences.getString("nombreUsuario", ""))){

                            int ramitas = kit.getRamitas();
                            String stringNumRamitas = String.valueOf(ramitas);

                            int hjsCongeladas = kit.getHojasCongeladas();
                            String stringHjsCongeldas = String.valueOf(hjsCongeladas);

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
                Toast.makeText(HomeKit.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
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
            fragment = new HomeFragmentKit();
        } else if (iconId == R.id.icon_actividades) {
            fragment = new ActividadesFragmentKit();
        } else if (iconId == R.id.icon_calendario) {
            fragment = new CalendarioFragmentKit();
        } else if (iconId == R.id.icon_recompensa) {
            fragment = new RecompensasFragmentKit();
        } else if (iconId == R.id.icon_diario) {
            fragment = new DiarioFragmentKit();
        } else if (iconId == R.id.icon_chat) {
            fragment = new ChatFragmentKit();
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