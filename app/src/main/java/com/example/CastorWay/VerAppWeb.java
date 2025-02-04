package com.example.castorway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerAppWeb extends AppCompatActivity {
    Button btnSalirWeb, btnVerInfo;
    WebView webView;
    TextView pruebaNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_app_web);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


            btnSalirWeb = findViewById(R.id.btnSalirWeb);
            btnSalirWeb.setOnClickListener(this::cerrarSesion);

            pruebaNombre = findViewById(R.id.pruebaNombre);
            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Castor>> call = apiService.getAllCastores();
            pruebaNombre.setText("...");
            call.enqueue(new Callback<List<Castor>>() {
                @Override
                public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                    if (response.isSuccessful()) {
                        List<Castor> castores = response.body();
                        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                        String email = preferences.getString("email", null);
                        if (castores != null && email != null) {

                            for (Castor castor : castores) {
                                if(castor.getEmail().equalsIgnoreCase(email)){
                                    pruebaNombre.setText(castor.getNombre());
                                    SharedPreferences.Editor editor = preferences.edit();

                                    editor.putInt("idCastor", castor.getIdCastor());
                                    editor.apply();

                                    pruebaNombre.setText(castor.getNombre() + ", y el id: " + castor.getIdCastor());
                                    Log.d("MainActivity", "Castor: " + castor.getNombre());
                                }
                            }
                        }
                    } else {
                        pruebaNombre.setText("No");
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
                public void onFailure(Call<List<Castor>> call, Throwable t) {
                    Log.e("MainActivity", "Error de conexión: " + t.getMessage());
                }
            });


            return insets;
        });
    }
    public void irVerInfo(View vista){
        Intent irVerInfo = new Intent(this, InformacionTutor.class);
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