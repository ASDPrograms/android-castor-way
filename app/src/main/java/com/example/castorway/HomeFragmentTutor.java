package com.example.castorway;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.castorway.api.ApiService;
import com.example.castorway.retrofit.RetrofitClient;
import com.example.castorway.modelsDB.Castor;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentTutor extends Fragment {
    TextView NameCastor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_tutor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NameCastor = view.findViewById(R.id.NameCastor);

        actuInfoTopNav();
    }

    private void actuInfoTopNav() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();

        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                if (response.body() != null) {
                    List<Castor> castores = response.body();
                    String emailStored = getEmailFromSharedPreferences();

                    // Buscar el castor correspondiente en la lista de castores
                    for (Castor castor : castores) {
                        if (castor.getEmail().equalsIgnoreCase(emailStored)) {
                            // Actualizar el nombre del castor en la vista
                            updateNameCastor(castor.getNombre());
                            return;  // Salir después de encontrar el primer castor coincidente
                        }
                    }
                    // Si no se encuentra el castor
                    Toast.makeText(requireContext(), "No se encontró un castor con este correo", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("HomeFragmentTutor", "La respuesta de castores está vacía o nula.");
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                Log.d("HomeFragmentTutor", "Error en la conexión: " + t.getMessage());
            }
        });
    }

    private String getEmailFromSharedPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        return preferences.getString("email", "");
    }

    private void updateNameCastor(String nombreCastor) {
        // Asegurarse de que la vista esté disponible antes de actualizarla
        if (getView() != null && NameCastor != null) {
            NameCastor.setText(nombreCastor);
        }
    }
}
