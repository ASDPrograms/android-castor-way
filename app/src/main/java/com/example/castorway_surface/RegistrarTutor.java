package com.example.castorway_surface;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

import java.security.SecureRandom;

public class RegistrarTutor extends AppCompatActivity {
    TextView txtTitRegistroTutor, txtYaTienesCuenta;
    Button btnRegistrarTutor;
    EditText inputNombre, inputApellidos, inputEdad, inputEmail, inputContrasena;
    ImageView imgRegresarAzul2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

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
                            administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                            SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                            ContentValues registro = new ContentValues();
                            Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Castor WHERE email = ?", new String[]{email});
                            if (cursor.getCount() == 0) {
                                if (contrasena.length() >= 6) {
                                    String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                                    SecureRandom random = new SecureRandom();
                                    StringBuilder codigo = new StringBuilder();

                                    for (int i = 0; i < 12; i++) {
                                        int indice = random.nextInt(caracteres.length());
                                        codigo.append(caracteres.charAt(indice));
                                    }

                                    registro.put("codPresa", codigo.toString());
                                    registro.put("nombre", nombre);
                                    registro.put("apellidos", apellidos);
                                    registro.put("edad", edad);
                                    registro.put("email", email);
                                    registro.put("contrasena", contrasena);

                                    SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();

                                    editor.putString("email", email);
                                    editor.putString("tipoUsuario", "Castor");
                                    editor.putBoolean("sesionActiva", true);
                                    editor.apply();

                                    BaseDeDatos.insert("Castor", null, registro);
                                    BaseDeDatos.close();

                                    Toast.makeText(this, "¡Bienvenido(a) a CastorWay!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, VerAppWeb.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(this, "La contraseña debe de tener al menos de 6 caractéres.", Toast.LENGTH_SHORT).show();
                                    inputContrasena.requestFocus();
                                    return;
                                }
                            }else{
                                Toast.makeText(this, "El correo ingresado ya está asociado a otra cuenta.", Toast.LENGTH_SHORT).show();
                                cursor.close();
                            }
                        }else{
                            Toast.makeText(this, "Ingresa un correo electrónico válido.", Toast.LENGTH_SHORT).show();
                            inputEmail.requestFocus();
                            return;
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
                    return;
                } else if (apellidos.isEmpty()) {
                    inputApellidos.requestFocus();
                    return;
                }else if (edadStr.isEmpty()) {
                    inputEdad.requestFocus();
                    return;
                }else  if (email.isEmpty()) {
                    inputEmail.requestFocus();
                    return;
                }else if (contrasena.isEmpty()) {
                    inputContrasena.requestFocus();
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
}