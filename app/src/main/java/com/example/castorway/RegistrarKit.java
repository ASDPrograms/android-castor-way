package com.example.CastorWay;

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
                        administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                        ContentValues registro = new ContentValues();

                        Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Kit WHERE nombreUsuario = ?", new String[]{nombreUsuario});
                        if (cursor.getCount() == 0) {
                            Cursor cursor2 = BaseDeDatos.rawQuery("SELECT * FROM Castor WHERE codPresa = ?", new String[]{codPresa});
                            if (cursor2.getCount() == 1) {
                                registro.put("codPresa", codPresa);
                                registro.put("nombreUsuario", nombreUsuario);
                                registro.put("nombre", nombre);
                                registro.put("apellidos", apellidos);
                                registro.put("edad", edad);

                                SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("nombreUsuario", nombreUsuario);
                                editor.putString("tipoUsuario", "Kit");
                                editor.putBoolean("sesionActiva", true);
                                editor.apply();

                                BaseDeDatos.insert("Kit", null, registro);
                                BaseDeDatos.close();

                                Toast.makeText(this, "¡Bienvenido(a) a CastorWay!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, VerAppWebKit.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(this, "No existe el código de presa ingresado.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(this, "El nombre de usuario ingresado ya está ocupado por otra cuenta.", Toast.LENGTH_SHORT).show();
                            cursor.close();
                        }
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