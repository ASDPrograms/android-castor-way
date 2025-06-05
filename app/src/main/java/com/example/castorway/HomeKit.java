package com.example.castorway;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

        Fragment fragment = null;

        if (fragmentName != null && fragmentName.equals("ActividadesFragmentKit")) {
            fragment = new ActividadesFragmentKit();
        }

        if (fragmentNamePremio != null && fragmentNamePremio.equals("RecompensasFragmenKit1")) {
            fragment = new RecompensasFragmentKit1();
        }

// Si se recibe un fragmento válido, lo mostramos
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_container, fragment); // Asegúrate de tener un contenedor adecuado
            transaction.commit();
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
    private void actuInfoTopNav(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Kit>> call = apiService.getAllKits();
        call.enqueue(new Callback<List<Kit>>() {
            @Override
            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                if (response.isSuccessful() && response.body() != null) { // ✅ Validar que la respuesta fue exitosa y no es nula
                    List<Kit> kits = response.body();

                    SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                    String nombreUsuario = preferences.getString("nombreUsuario", "");

                    for (Kit kit : kits) {
                        if (kit.getNombreUsuario().equals(nombreUsuario)) { // ✅ Evita llamar dos veces a getString()

                            // ✅ Obtener datos del Kit
                            int ramitas = kit.getRamitas();
                            int hjsCongeladas = kit.getHojasCongeladas();
                            int idKit = kit.getIdKit(); // ✅ Asegurar que se obtiene el idKit del objeto actual

                            // ✅ Guardar idKit en SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("usrKitCuentaKit", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("idKit", idKit);
                            editor.apply();

                            // ✅ Actualizar UI con findViewById y setText
                            TextView txtHojas = findViewById(R.id.txt_hjs_congeladas);
                            txtHojas.setText(String.valueOf(hjsCongeladas));

                            TextView txtRamas = findViewById(R.id.txt_ramas);
                            txtRamas.setText(String.valueOf(ramitas));

                            break; // ✅ Sale del bucle tras encontrar el kit correspondiente
                        }
                    }
                } else {
                    // ⚠️ Puedes agregar un log o Toast para indicar error en la respuesta
                    Log.e("API_RESPONSE", "Error en la respuesta o lista nula");
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
    @Override
    protected void onResume() {
        super.onResume();

        // Obtener el nombre del fragmento desde cualquiera de los extras
        String fragmentName = getIntent().getStringExtra("fragmentActiCrear");
        String fragmentNamePremio = getIntent().getStringExtra("fragmenPremCrear");

        Fragment fragment = null;
        ImageView iconToSelect = null;

        // Verificar cuál fragmento se debe cargar
        if (fragmentName != null && fragmentName.equals("ActividadesFragmentKit")) {
            fragment = new ActividadesFragmentKit();
            iconToSelect = iconActividades;
        } else if (fragmentNamePremio != null && fragmentNamePremio.equals("RecompensasFragmentKit1")) {
            fragment = new RecompensasFragmentKit1();
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