package com.example.castorway;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
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

public class InformacionTutor extends AppCompatActivity {
    ImageView imgRegresarEditAzul2, imgCopiarCodPresa, imgCopiarCorreo;
    TextView txtTitEdicionTutor;
    EditText inputEditNombre, inputEditApellidos, inputEditEdad, inputEditEmail, inputEditContrasena, inputEditCodigoPresa;
    Button btnEditarInfoTutor;
    Boolean verifiFuncionEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informacion_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            imgRegresarEditAzul2 = findViewById(R.id.imgRegresarEditAzul2);
            imgRegresarEditAzul2.setOnClickListener(this::backHomeApp);

            txtTitEdicionTutor = findViewById(R.id.txtTitEdicionTutor);
            String fullText = "Edición de datos de Castor";

            String brownWord = "Castor";
            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, brownWord, 0xFF885551);
            txtTitEdicionTutor.setText(spannableString);

            btnEditarInfoTutor = findViewById(R.id.btnEditarInfoTutor);

            if (!verifiFuncionEdit) {
                editInfoTutor();
                verifiFuncionEdit = true;
            }

            btnEditarInfoTutor.setOnClickListener(view -> updateInfoTutor());

            imgCopiarCodPresa = findViewById(R.id.imgCopiarCodPresa);
            imgCopiarCodPresa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyToClipboardCodPresa(inputEditCodigoPresa.getText().toString());
                }
            });

            imgCopiarCorreo = findViewById(R.id.imgCopiarCorreo);
            imgCopiarCorreo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyToClipboardCorreo(inputEditEmail.getText().toString());
                }
            });
            return insets;
        });
    }
    private void editInfoTutor(){
        try {
            inputEditNombre = findViewById(R.id.inputEditNombre);
            inputEditApellidos = findViewById(R.id.inputEditApellidos);
            inputEditEdad = findViewById(R.id.inputEditEdad);
            inputEditEmail = findViewById(R.id.inputEditEmail);
            inputEditContrasena = findViewById(R.id.inputEditContrasena);
            inputEditCodigoPresa = findViewById(R.id.inputEditCodigoPresa);

            inputEditEmail.setFocusable(false);
            inputEditEmail.setClickable(false);

            inputEditCodigoPresa.setFocusable(false);
            inputEditCodigoPresa.setClickable(false);

            SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
            boolean sesionActiva = preferences.getBoolean("sesionActiva", false);

            if (sesionActiva) {
                String email = preferences.getString("email", "");
                administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                ContentValues registro = new ContentValues();
                Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Castor WHERE email = ?", new String[]{email});
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        int indexCodPresa = cursor.getColumnIndex("codPresa");
                        int indexNombre = cursor.getColumnIndex("nombre");
                        int indexApellidos = cursor.getColumnIndex("apellidos");
                        int indexEdad = cursor.getColumnIndex("edad");
                        int indexContrasena = cursor.getColumnIndex("contrasena");

                        String codPresa = cursor.getString(indexCodPresa);
                        String nombre = cursor.getString(indexNombre);
                        String apellidos = cursor.getString(indexApellidos);
                        int edad = cursor.getInt(indexEdad);
                        String contrasena = cursor.getString(indexContrasena);

                        inputEditNombre.setText(nombre);
                        inputEditApellidos.setText(apellidos);
                        inputEditEdad.setText(String.valueOf(edad));
                        inputEditEmail.setText("Correo: " + email);
                        inputEditContrasena.setText(contrasena);
                        inputEditCodigoPresa.setText("Código presa: " + codPresa);
                    }catch (Exception ex){
                        Toast.makeText(this, "Hubo un error", Toast.LENGTH_SHORT).show();
                    }finally {
                        cursor.close();
                    }
                }

            } else {
                Intent intent = new Intent(this, HomeUsuarioSesion.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }private void updateInfoTutor() {
        try {
            String nuevoNombre = inputEditNombre.getText().toString();
            String nuevosApellidos = inputEditApellidos.getText().toString();
            String nuevaEdad = inputEditEdad.getText().toString();
            String nuevaContrasena = inputEditContrasena.getText().toString();

            if (!nuevoNombre.isEmpty() && !nuevosApellidos.isEmpty() && !nuevaEdad.isEmpty() && !nuevaContrasena.isEmpty()) {
                String nombrePattern = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
                if (nuevoNombre.matches(nombrePattern) && nuevosApellidos.matches(nombrePattern)) {
                    int edad;
                    try {
                        edad = Integer.parseInt(nuevaEdad);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Sólo se aceptan números para la edad.", Toast.LENGTH_SHORT).show();
                        inputEditEdad.requestFocus();
                        return;
                    }
                    if (edad > 0 && edad < 100) {
                        if(nuevaContrasena.length() >= 6) {

                            SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
                            boolean sesionActiva = preferences.getBoolean("sesionActiva", false);

                            if (sesionActiva) {
                                String email = preferences.getString("email", "");
                                administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                                Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Castor WHERE email = ?", new String[]{email});
                                if (cursor != null && cursor.moveToFirst()) {
                                    int indexNombre = cursor.getColumnIndex("nombre");
                                    int indexApellidos = cursor.getColumnIndex("apellidos");
                                    int indexEdad = cursor.getColumnIndex("edad");
                                    int indexContrasena = cursor.getColumnIndex("contrasena");

                                    String dbNombre = cursor.getString(indexNombre);
                                    String dbApellidos = cursor.getString(indexApellidos);
                                    int dbEdad = cursor.getInt(indexEdad);
                                    String dbContrasena = cursor.getString(indexContrasena);

                                    boolean isUpdated = false;
                                    ContentValues contentValues = new ContentValues();
                                    if (!nuevoNombre.equals(dbNombre)) {
                                        contentValues.put("nombre", nuevoNombre);
                                        isUpdated = true;
                                    }
                                    if (!nuevosApellidos.equals(dbApellidos)) {
                                        contentValues.put("apellidos", nuevosApellidos);
                                        isUpdated = true;
                                    }
                                    if (!nuevaEdad.equals(dbEdad)) {
                                        contentValues.put("edad", nuevaEdad);
                                        isUpdated = true;
                                    }
                                    if (!nuevaContrasena.equals(dbContrasena)) {
                                        contentValues.put("contrasena", nuevaContrasena);
                                        isUpdated = true;
                                    }
                                    if (isUpdated) {
                                        BaseDeDatos.update("Castor", contentValues, "email = ?", new String[]{email});
                                        Toast.makeText(this, "Los datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        editInfoTutor();
                                    } else {
                                        Toast.makeText(this, "No hubo cambios en los datos", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                cursor.close();
                            } else {
                                Intent intent = new Intent(this, HomeUsuarioSesion.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }else{
                        Toast.makeText(this, "Ingrese una edad válida.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "No se aceptan nombre/s y apellidos con números.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Favor de completar todos los campos.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error al actualizar los datos: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void backHomeApp(View view){
        Intent backHomeApp = new Intent(this, VerAppWeb.class);
        startActivity(backHomeApp);
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
    }private void copyToClipboardCodPresa(String codPresa) {
        String cdPresa = codPresa.replace("Código presa: ", "").trim();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Código de Presa", cdPresa);

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Código de presa copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
    private void copyToClipboardCorreo(String correo) {
        String email = correo.replace("Correo: ", "").trim();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Correo Electrónico", email);

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Correo electrónico copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
}