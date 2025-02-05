package com.example.castorway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarKit extends AppCompatActivity {
    TextView txtTitRegistrarKit, txtYaTienesCuenta;
    ImageView imgRegresarAzul2;
    EditText inputNombreUsuario, inputNombre, inputApellidos, inputEdad, inputCodPresa;
    Button btnRegistrarKit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_kit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            txtTitRegistrarKit = findViewById(R.id.txtTitRegistrarKit);
            String fullText = "Registro de Kit";

            String brownWord = "Kit";
            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, brownWord, 0xFFFFDF9B);
            txtTitRegistrarKit.setText(spannableString);


            txtYaTienesCuenta = findViewById(R.id.txtYaTienesCuenta);
            String txtFullLink = "¿Ya tienes una cuenta? -  Inicia sesión ahora";
            String clickableText = "Inicia sesión ahora";

            SpannableString spannableString2 = new SpannableString(txtFullLink);

            applyClickableColor(spannableString2, txtFullLink, clickableText, 0xFFFFDF9B);

            txtYaTienesCuenta.setText(spannableString2);
            txtYaTienesCuenta.setMovementMethod(LinkMovementMethod.getInstance());

            imgRegresarAzul2 = findViewById(R.id.imgRegresarAzul2);
            imgRegresarAzul2.setOnClickListener(this::backElegirUsrRegistro);

            btnRegistrarKit = findViewById(R.id.btnRegistrarKit);
            btnRegistrarKit.setOnClickListener(this::validRegistroTutor);
            return insets;
        });
    }
    private void validRegistroTutor(View view){
        try{
            inputNombreUsuario = findViewById(R.id.inputNombreUsuario);
            inputNombre = findViewById(R.id.inputNombre);
            inputApellidos = findViewById(R.id.inputApellidos);
            inputEdad = findViewById(R.id.inputEdad);
            inputCodPresa = findViewById(R.id.inputCodPresa);

            String nombreUsuario = inputNombreUsuario.getText().toString().trim();
            String nombre = inputNombre.getText().toString().trim();
            String apellidos = inputApellidos.getText().toString().trim();
            String edadStr = inputEdad.getText().toString().trim();
            String codPresa = inputCodPresa.getText().toString().trim();


            if (!nombre.isEmpty() && !apellidos.isEmpty() && !edadStr.isEmpty() && !nombreUsuario.isEmpty() && !codPresa.isEmpty()) {
                String nombrePattern = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
                if(nombre.matches(nombrePattern) && apellidos.matches(nombrePattern)){
                    int edad;
                    try {
                        edad = Integer.parseInt(edadStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Sólo se aceptan números para la edad.", Toast.LENGTH_SHORT).show();
                        inputEdad.requestFocus();
                        return;
                    }
                    if(edad > 0 && edad < 100){
                        ApiService apiService = RetrofitClient.getApiService();
                        Call<List<Kit>> call = apiService.getAllKits();
                        call.enqueue(new Callback<List<Kit>>() {
                            @Override
                            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                                if (response.isSuccessful()) {
                                    List<Kit> kits = response.body();
                                    AtomicInteger cntCoincidNombreUser = new AtomicInteger(0);

                                    if (kits != null) {
                                        for (Kit kit : kits) {
                                            if(kit.getNombreUsuario().equals(nombreUsuario)){
                                                cntCoincidNombreUser.incrementAndGet();
                                                Log.d("MainActivity", "Castor: " + kit.getNombreUsuario());
                                            }
                                        }
                                    }
                                    if(cntCoincidNombreUser.get() == 0){
                                        ApiService apiService2 = RetrofitClient.getApiService();
                                        Call<List<Castor>> call2 = apiService2.getAllCastores();
                                        call2.enqueue(new Callback<List<Castor>>() {
                                            @Override
                                            public void onResponse(Call<List<Castor>> call2, Response<List<Castor>> response2) {
                                                if (response2.isSuccessful()) {
                                                    List<Castor> castors = response2.body();
                                                    AtomicInteger cntCoincidCodPresa = new AtomicInteger(0);

                                                    if (castors != null) {
                                                        for (Castor castor : castors) {
                                                            if (castor.getCodPresa() != null && castor.getCodPresa().equals(codPresa)) {
                                                                cntCoincidCodPresa.incrementAndGet();
                                                                Log.d("CodPresa", "SiCodPresa: " + castor.getCodPresa());
                                                                break;
                                                            }else{
                                                                Log.d("CodPresa", "NoCodPresa: " + castor.getCodPresa());
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        Log.d("NoCastores", "Sin Castor");
                                                    }
                                                    if(cntCoincidCodPresa.get() == 1){

                                                        ApiService apiService3 = RetrofitClient.getApiService();
                                                        Kit nuevoKit = new Kit();
                                                        nuevoKit.setCodPresa(codPresa);
                                                        nuevoKit.setNombreUsuario(nombreUsuario);
                                                        nuevoKit.setNombre(nombre);
                                                        nuevoKit.setApellidos(apellidos);
                                                        nuevoKit.setEdad(edad);

                                                        Call<Kit> call3 = apiService3.createKit(nuevoKit);
                                                        call3.enqueue(new Callback<Kit>() {
                                                            @Override
                                                            public void onResponse(Call<Kit> call3, Response<Kit> response3) {
                                                                if (response3.isSuccessful()) {
                                                                    Toast.makeText(RegistrarKit.this, "¡Bienvenido(a) a CastorWay!", Toast.LENGTH_SHORT).show();

                                                                    SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                                                                    SharedPreferences.Editor editor = preferences.edit();

                                                                    editor.putString("nombreUsuario", nombreUsuario);
                                                                    editor.putString("tipoUsuario", "Kit");
                                                                    editor.putBoolean("sesionActiva", true);
                                                                    editor.apply();

                                                                    Intent intent = new Intent(RegistrarKit.this, HomeKit.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(RegistrarKit.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            @Override
                                                            public void onFailure(Call<Kit> call, Throwable t) {
                                                                Toast.makeText(RegistrarKit.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(RegistrarKit.this, "No existe el código de presa ingresado.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Log.e("API_RESPONSE", "Error HTTP: " + response2.code());
                                                    Log.e("API_RESPONSE", "Mensaje de error: " + response2.message());
                                                    try {
                                                        Log.e("API_RESPONSE", "Cuerpo del error: " + response2.errorBody().string());
                                                    } catch (Exception e) {
                                                        Log.e("API_RESPONSE", "Error al leer el cuerpo de la respuesta", e);
                                                    } finally {
                                                        if (response2.errorBody() != null) {
                                                            response2.errorBody().close();
                                                        }
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<List<Castor>> call2, Throwable t) {
                                                Log.e("ConsultarCastor", "Error de conexión: " + t.getMessage());
                                            }
                                        });
                                    }else{
                                        Toast.makeText(RegistrarKit.this, "El nombre de usuario ingresado ya está ocupado por otra cuenta.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
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
                                Log.e("RegistrarKit", "Error de conexión: " + t.getMessage());
                            }
                        });
                    }else{
                        Toast.makeText(this, "Ingrese una edad válida.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "El campo de nombre/s y apellidos no admite números.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Favor de llenar todos los campos.", Toast.LENGTH_SHORT).show();
                if(nombreUsuario.isEmpty()){
                    inputNombreUsuario.requestFocus();
                    return;
                }
                else if(nombre.isEmpty()){
                    inputNombre.requestFocus();
                    return;
                } else if (apellidos.isEmpty()) {
                    inputApellidos.requestFocus();
                    return;
                }else if (edadStr.isEmpty()) {
                    inputEdad.requestFocus();
                    return;
                }else  if (codPresa.isEmpty()) {
                    inputCodPresa.requestFocus();
                    return;
                }
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applyColorToWord(SpannableString spannableString, String fullText, String word, int color) {
        int startIndex = fullText.indexOf(word);
        while (startIndex != -1) {
            int endIndex = startIndex + word.length();
            spannableString.setSpan(
                    new ForegroundColorSpan(color),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            startIndex = fullText.indexOf(word, endIndex);
        }
    }private void applyClickableColor(SpannableString spannableString, String fullText, String fragment, int color) {
        int startIndex = fullText.indexOf(fragment);
        if (startIndex != -1) {
            int endIndex = startIndex + fragment.length();
            spannableString.setSpan(
                    new ForegroundColorSpan(color),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RegistrarKit.this, IniciarSesionKit.class);
                    startActivity(intent);
                }
                public void updateDrawState(android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(0xFFFFDF9B);
                    ds.bgColor = 0x00000000;
                }
            }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }private void backElegirUsrRegistro(View view){
        Intent backElegirUsrRegistro = new Intent(this, ElegirUsrRegistrarse.class);
        startActivity(backElegirUsrRegistro);
    }
}