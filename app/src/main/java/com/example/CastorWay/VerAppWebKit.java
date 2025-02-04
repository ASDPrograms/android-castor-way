package com.example.castorway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerAppWebKit extends AppCompatActivity {
    Button btnSalirWeb;
    TextView pruebaNombreKit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_app_web_kit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            btnSalirWeb = findViewById(R.id.btnSalirWeb);
            btnSalirWeb.setOnClickListener(this::cerrarSesion);

            pruebaNombreKit = findViewById(R.id.pruebaNombreKit);

            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Kit>> call = apiService.getAllKits();
            call.enqueue(new Callback<List<Kit>>() {
                @Override
                public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                    if (response.isSuccessful()) {
                        List<Kit> kits = response.body();
                        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                        String nombreUsuario = preferences.getString("nombreUsuario", null);
                        if (kits != null && nombreUsuario != null) {

                            for (Kit kit : kits) {
                                if(kit.getNombreUsuario().equals(nombreUsuario)){
                                    SharedPreferences.Editor editor = preferences.edit();

                                    editor.putInt("idKit", kit.getIdKit());
                                    editor.apply();

                                    pruebaNombreKit.setText(kit.getNombre() + ", y el id: " + kit.getIdKit());
                                    Log.d("MainActivity", "Kit: " + kit.getNombre());
                                }
                            }
                        }
                    } else {
                        pruebaNombreKit.setText("No");
                        Log.e("API_RESPONSE", "Error HTTP: " + response.code());
                        Log.e("API_RESPONSE", "Mensaje de error: " + response.message());
                        try {
                            Log.e("API_RESPONSE", "Cuerpo del error: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("API_RESPONSE", "Error al leer el cuerpo de la respuesta", e);
                        } finally {
                            if (response.errorBody() != null) {
                                response.errorBody().close();
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<List<Kit>> call, Throwable t) {
                    Log.e("MainActivity", "Error de conexión: " + t.getMessage());
                }
            });
            return insets;
        });
    }public void irVerInfo(View vista){
        Intent irVerInfo = new Intent(this, InformacionKit.class);
        startActivity(irVerInfo);
    }
    public void cerrarSesion(View vista){
        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.apply();

        Intent cerrarSesion = new Intent(this, MainActivity.class);
        startActivity(cerrarSesion);
        finish();
    }
}