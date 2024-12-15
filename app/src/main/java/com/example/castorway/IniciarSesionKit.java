package com.example.castorway;

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

public class IniciarSesionKit extends AppCompatActivity {
    Button btnIniciarSesionKit;
    TextView txtTitInicioSesionKit, txtNoTienesCuenta;
    ImageView imgInicioSesionRegresarAzul;
    EditText inputInicioSesionNombreUsuario, inputInicioSesionCodPresa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion_kit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            imgInicioSesionRegresarAzul = findViewById(R.id.imgInicioSesionRegresarAzul);
            imgInicioSesionRegresarAzul.setOnClickListener(this::regresarElegirIniciarSesion);

            txtTitInicioSesionKit = findViewById(R.id.txtTitInicioSesionKit );
            String fullText = "Inicio de sesión de Kit";

            String brownWord = "Kit";
            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, brownWord, 0xFF879FD4);
            txtTitInicioSesionKit.setText(spannableString);

            txtNoTienesCuenta = findViewById(R.id.txtNoTienesCuenta);
            String txtFullLink = "¿No tienes una cuenta? - Regístrate ahora";
            String clickableText = "Regístrate ahora";

            SpannableString spannableString2 = new SpannableString(txtFullLink);

            applyClickableColor(spannableString2, txtFullLink, clickableText, 0xFF885551);

            txtNoTienesCuenta.setText(spannableString2);
            txtNoTienesCuenta.setMovementMethod(LinkMovementMethod.getInstance());

            btnIniciarSesionKit = findViewById(R.id.btnIniciarSesionKit);
            btnIniciarSesionKit.setOnClickListener(this::ValidIniciarSesionTutor);


            return insets;
        });
    }private void ValidIniciarSesionTutor(View view){
        try{
            inputInicioSesionNombreUsuario = findViewById(R.id.inputInicioSesionNombreUsuario);
            inputInicioSesionCodPresa = findViewById(R.id.inputInicioSesionCodPresa);

            String nombreUsuario = inputInicioSesionNombreUsuario.getText().toString().trim();
            String codPresa = inputInicioSesionCodPresa.getText().toString().trim();

            if(!nombreUsuario.isEmpty() && !codPresa.isEmpty()){

                administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                ContentValues registro = new ContentValues();
                Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Kit WHERE codPresa = ? AND nombreUsuario = ?", new String[]{codPresa, nombreUsuario});
                if (cursor.getCount() == 1) {

                    SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("nombreUsuario", nombreUsuario);
                    editor.putString("tipoUsuario", "Kit");
                    editor.putBoolean("sesionActiva", true);
                    editor.apply();


                    Toast.makeText(this, "¡Estás de regreso!, bienvenido", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, VerAppWebKit.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "No se encontró una cuenta con la información proporcionada, pruebe con otros datos.", Toast.LENGTH_SHORT).show();
                }

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
                    Intent intent = new Intent(IniciarSesionKit.this, RegistrarTutor.class);
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
}