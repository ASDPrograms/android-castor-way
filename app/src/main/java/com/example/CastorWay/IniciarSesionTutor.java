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
import android.widget.ImageButton;
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
import com.example.castorway.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.text.InputType;


public class IniciarSesionTutor extends AppCompatActivity {
    Button btnIniciarSesionTutor;
    TextView txtTitInicioSesionTutor, txtNoTienesCuenta;
    ImageView imgInicioSesionRegresarAzul;
    EditText inputEmail, inputContrasena;
    private boolean isPasswordVisible = false;
    ImageButton btnPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            imgInicioSesionRegresarAzul = findViewById(R.id.imgInicioSesionRegresarAzul);
            imgInicioSesionRegresarAzul.setOnClickListener(this::regresarElegirIniciarSesion);

            txtTitInicioSesionTutor = findViewById(R.id.txtTitInicioSesionTutor );
            String fullText = "Inicio de sesión de Castor";

            String brownWord = "Castor";
            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, brownWord, 0xFF885551);
            txtTitInicioSesionTutor.setText(spannableString);

            txtNoTienesCuenta = findViewById(R.id.txtNoTienesCuenta);
            String txtFullLink = "¿No tienes una cuenta? - Regístrate ahora";
            String clickableText = "Regístrate ahora";

            SpannableString spannableString2 = new SpannableString(txtFullLink);

            applyClickableColor(spannableString2, txtFullLink, clickableText, 0xFF885551);

            txtNoTienesCuenta.setText(spannableString2);
            txtNoTienesCuenta.setMovementMethod(LinkMovementMethod.getInstance());

            btnIniciarSesionTutor = findViewById(R.id.btnIniciarSesionTutor);
            btnIniciarSesionTutor.setOnClickListener(this::ValidIniciarSesionTutor);


            btnPassword = findViewById(R.id.btnPassword);
            inputContrasena = findViewById(R.id.inputInicioSesionContrasena);

            btnPassword.setOnClickListener(this::togglePasswordVisibility);

            return insets;
        });
    }
    private void ValidIniciarSesionTutor(View view){
        try{
            inputEmail = findViewById(R.id.inputInicioSesionEmail);
            inputContrasena = findViewById(R.id.inputInicioSesionContrasena);

            String email = inputEmail.getText().toString().trim();
            String contrasena = inputContrasena.getText().toString().trim();

            if(!email.isEmpty() && !contrasena.isEmpty()){

                ApiService apiService = RetrofitClient.getApiService();
                Call<List<Castor>> call = apiService.getAllCastores();
                call.enqueue(new Callback<List<Castor>>() {
                    @Override
                    public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                        if (response.isSuccessful()) {
                            List<Castor> castores = response.body();
                            AtomicInteger cntCoincidUser = new AtomicInteger(0);

                            if (castores != null) {
                                for (Castor castor : castores) {
                                    if(castor.getEmail().equalsIgnoreCase(email) && castor.getContraseña().equals(contrasena)){
                                        cntCoincidUser.incrementAndGet();
                                        Log.d("MainActivity", "Castor: " + castor.getNombre());
                                        break;
                                    }
                                }
                            }

                            if (cntCoincidUser.get() == 1) {
                                SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("email", email);
                                editor.putString("tipoUsuario", "Castor");
                                editor.putBoolean("sesionActiva", true);
                                editor.apply();


                                Toast.makeText(IniciarSesionTutor.this, "¡Estás de regreso!, bienvenido", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(IniciarSesionTutor.this, HomeTutor.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(IniciarSesionTutor.this, "No se encontró una cuenta con la información proporcionada, pruebe con otros datos.", Toast.LENGTH_SHORT).show();
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
                    public void onFailure(Call<List<Castor>> call, Throwable t) {
                        Log.e("MainActivity", "Error de conexión: " + t.getMessage());
                    }
                });

            }else{
                Toast.makeText(this, "Favor de completar todos los campos.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void regresarElegirIniciarSesion(View view){
        Intent regresarHome = new Intent(this, ElegirUsrIniciarSesion.class);
        startActivity(regresarHome);
    }private void applyColorToWord(SpannableString spannableString, String fullText, String word, int color) {
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
                    Intent intent = new Intent(IniciarSesionTutor.this, RegistrarTutor.class);
                    startActivity(intent);
                }
                public void updateDrawState(android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(0xFF885551);
                    ds.bgColor = 0x00000000;
                }
            }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    private void togglePasswordVisibility(View view) {
        if (isPasswordVisible) {
            inputContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnPassword.setImageResource(R.drawable.no_ver_contrasena);
        } else {
            inputContrasena.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnPassword.setImageResource(R.drawable.ver_contrasena);
        }

        isPasswordVisible = !isPasswordVisible;

        inputContrasena.setTypeface(inputContrasena.getTypeface());

        inputContrasena.setSelection(inputContrasena.getText().length());
    }

}