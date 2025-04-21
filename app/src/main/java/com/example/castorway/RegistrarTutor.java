package com.example.castorway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarTutor extends AppCompatActivity {
    TextView txtTitRegistroTutor, txtYaTienesCuenta;
    Button btnRegistrarTutor;
    EditText inputNombre, inputApellidos, inputEdad, inputEmail, inputContrasena;
    ImageView imgRegresarAzul2;
    ImageButton btnPassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            btnPassword = findViewById(R.id.btnPassword);

            txtTitRegistroTutor = findViewById(R.id.txtTitRegistroTutor);
            String fullText = "Registro de Castor";

            String brownWord = "Castor";
            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, brownWord, 0xFF885551);
            txtTitRegistroTutor.setText(spannableString);


            txtYaTienesCuenta = findViewById(R.id.txtYaTienesCuenta);
            String txtFullLink = "¿Ya tienes una cuenta? -  Inicia sesión ahora";
            String clickableText = "Inicia sesión ahora";

            SpannableString spannableString2 = new SpannableString(txtFullLink);

            applyClickableColor(spannableString2, txtFullLink, clickableText, 0xFF885551);

            txtYaTienesCuenta.setText(spannableString2);
            txtYaTienesCuenta.setMovementMethod(LinkMovementMethod.getInstance());

            btnRegistrarTutor = findViewById(R.id.btnRegistrarTutor);
            btnRegistrarTutor.setOnClickListener(this::validRegistroTutor);

            imgRegresarAzul2 = findViewById(R.id.imgRegresarAzul2);
            imgRegresarAzul2.setOnClickListener(this::backElegirUsrRegistro);

            inputContrasena = findViewById(R.id.inputContrasena);
            btnPassword.setOnClickListener(this::togglePasswordVisibility);

            return insets;
        });
    }
    private void validRegistroTutor(View view){
        try{
            inputNombre = findViewById(R.id.inputNombre);
            inputApellidos = findViewById(R.id.inputApellidos);
            inputEdad = findViewById(R.id.inputEdad);
            inputEmail = findViewById(R.id.inputEmail);
            inputContrasena = findViewById(R.id.inputContrasena);

            String nombre = inputNombre.getText().toString().trim();
            String apellidos = inputApellidos.getText().toString().trim();
            String edadStr = inputEdad.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String contrasena = inputContrasena.getText().toString().trim();


            if (!nombre.isEmpty() && !apellidos.isEmpty() && !edadStr.isEmpty() && !email.isEmpty() && !contrasena.isEmpty()) {
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
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            //Inicio código verificar email
                            ApiService apiService = RetrofitClient.getApiService();
                            Call<List<Castor>> call = apiService.getAllCastores();

                            call.enqueue(new Callback<List<Castor>>() {
                                @Override
                                public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                                    if (response.isSuccessful()) {
                                        List<Castor> castores = response.body();
                                        AtomicInteger cntCoincidEmail = new AtomicInteger(0);

                                        if (castores != null) {
                                            for (Castor castor : castores) {
                                                if(castor.getEmail().equalsIgnoreCase(email)){
                                                    cntCoincidEmail.incrementAndGet();
                                                    Log.d("MainActivity", "Castor: " + castor.getEmail());
                                                    break;
                                                }
                                            }
                                        }

                                        if (cntCoincidEmail.get() == 0) {
                                            if (contrasena.length() >= 6) {
                                                String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                                                SecureRandom random = new SecureRandom();
                                                StringBuilder codigo = new StringBuilder();

                                                for (int i = 0; i < 12; i++) {
                                                    int indice = random.nextInt(caracteres.length());
                                                    codigo.append(caracteres.charAt(indice));
                                                }
                                                String codPresa = codigo.toString();
                                                Log.d("MainActivity", "codPresa: " + codPresa);

                                                //Inicio de código para insertar nuevo Usuario

                                                ApiService apiService2 = RetrofitClient.getApiService();
                                                Castor nuevoCastor = new Castor();
                                                nuevoCastor.setCodPresa(codPresa);
                                                nuevoCastor.setNombre(nombre);
                                                nuevoCastor.setApellidos(apellidos);
                                                nuevoCastor.setEdad(edad);
                                                nuevoCastor.setEmail(email);
                                                nuevoCastor.setContraseña(contrasena);

                                                Call<Castor> call2 = apiService2.createCastor(nuevoCastor);

                                                call2.enqueue(new Callback<Castor>() {
                                                    @Override
                                                    public void onResponse(Call<Castor> call, Response<Castor> response) {
                                                        if (response.isSuccessful()) {
                                                            Toast.makeText(RegistrarTutor.this, "¡Bienvenido(a) a CastorWay!", Toast.LENGTH_SHORT).show();

                                                            SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = preferences.edit();

                                                            editor.putString("email", email);
                                                            editor.putString("tipoUsuario", "Castor");
                                                            editor.putBoolean("sesionActiva", true);
                                                            editor.apply();

                                                            Intent intent = new Intent(RegistrarTutor.this, HomeTutor.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegistrarTutor.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Castor> call, Throwable t) {
                                                        Toast.makeText(RegistrarTutor.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(RegistrarTutor.this, "La contraseña debe de tener al menos de 6 caractéres.", Toast.LENGTH_SHORT).show();
                                                inputContrasena.requestFocus();
                                            }
                                        }else{
                                            Toast.makeText(RegistrarTutor.this, "El correo ingresado ya está asociado a otra cuenta.", Toast.LENGTH_SHORT).show();
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
                                    Log.e("RegistrarTutor", "Error de conexión: " + t.getMessage());
                                }
                            });

                            //Fin de la consulta a la BD


                        }else{
                            Toast.makeText(this, "Ingresa un correo electrónico válido.", Toast.LENGTH_SHORT).show();
                            inputEmail.requestFocus();
                        }
                    }else{
                        Toast.makeText(this, "Ingrese una edad válida.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "El campo de nombre/s y apellidos no admite números.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Favor de llenar todos los campos.", Toast.LENGTH_SHORT).show();
                if(nombre.isEmpty()){
                    inputNombre.requestFocus();
                } else if (apellidos.isEmpty()) {
                    inputApellidos.requestFocus();
                }else if (edadStr.isEmpty()) {
                    inputEdad.requestFocus();
                }else  if (email.isEmpty()) {
                    inputEmail.requestFocus();
                }else if (contrasena.isEmpty()) {
                    inputContrasena.requestFocus();
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
                    Intent intent = new Intent(RegistrarTutor.this, IniciarSesionTutor.class);
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
    private void backElegirUsrRegistro(View view){
        Intent backElegirUsrRegistro = new Intent(this, ElegirUsrRegistrarse.class);
        startActivity(backElegirUsrRegistro);
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