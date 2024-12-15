package com.example.castorway_surface;

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

public class InformacionKit extends AppCompatActivity {
    TextView txtTitEdicionKit;
    ImageView imgCopiarCodPresaKit, imgCopiarNameUsr, imgRegresarEditNegro;
    EditText inputNombreUsuario2, inputNombre2, inputApellidos2, inputEdad2, inputCodPresa2;
    Button btnEditarKit;
    Boolean verifiFuncionEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informacion_kit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            inputNombreUsuario2 = findViewById(R.id.inputNombreUsuario2);
            inputNombre2 = findViewById(R.id.inputNombre2);
            inputApellidos2 = findViewById(R.id.inputApellidos2);
            inputEdad2 = findViewById(R.id.inputEdad2);
            inputCodPresa2 = findViewById(R.id.inputCodPresa2);

            txtTitEdicionKit = findViewById(R.id.txtTitEdicionKit);
            String fullText = "Edición de datos de Kit";

            String brownWord = "Kit";
            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, brownWord, 0xFFFFDF9B);
            txtTitEdicionKit.setText(spannableString);

            btnEditarKit = findViewById(R.id.btnEditarKit);
            if (!verifiFuncionEdit) {
                editInfoKit();
                verifiFuncionEdit = true;
            }

            btnEditarKit.setOnClickListener(view -> updateInfoKit());

            imgCopiarCodPresaKit = findViewById(R.id.imgCopiarCodPresaKit);
            imgCopiarCodPresaKit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyToClipboardCodPresa(inputCodPresa2.getText().toString());
                }
            });

            imgCopiarNameUsr = findViewById(R.id.imgCopiarNombreUsuarioKit);
            imgCopiarNameUsr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyToClipboardUsrName(inputNombreUsuario2.getText().toString());
                }
            });

            imgRegresarEditNegro = findViewById(R.id.imgRegresarEditNegro);
            imgRegresarEditNegro.setOnClickListener(this::backHomeApp);
            return insets;
        });
    }
    private void editInfoKit() {
        try {
            inputNombreUsuario2.setFocusable(false);
            inputNombreUsuario2.setClickable(false);

            inputCodPresa2.setFocusable(false);
            inputCodPresa2.setClickable(false);

            SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
            boolean sesionActiva = preferences.getBoolean("sesionActiva", false);

            if (sesionActiva) {
                String nombreUsuario = preferences.getString("nombreUsuario", "");
                administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Kit WHERE nombreUsuario = ?", new String[]{nombreUsuario});
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        int indexCodPresa = cursor.getColumnIndex("codPresa");
                        int indexNombre = cursor.getColumnIndex("nombre");
                        int indexApellidos = cursor.getColumnIndex("apellidos");
                        int indexEdad = cursor.getColumnIndex("edad");

                        String codPresa = cursor.getString(indexCodPresa);
                        String nombre = cursor.getString(indexNombre);
                        String apellidos = cursor.getString(indexApellidos);
                        int edad = cursor.getInt(indexEdad);

                        inputNombreUsuario2.setText("Nombre de usuario: " + nombreUsuario);
                        inputNombre2.setText(nombre);
                        inputApellidos2.setText(apellidos);
                        inputEdad2.setText(String.valueOf(edad));
                        inputCodPresa2.setText("Código presa: " + codPresa);
                    } catch (Exception ex) {
                        Toast.makeText(this, "Hubo un error", Toast.LENGTH_SHORT).show();
                    } finally {
                        cursor.close();
                    }
                }

            } else {
                Intent intent = new Intent(this, HomeUsuarioSesion.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void updateInfoKit() {
        try {
            String nuevoNombre = inputNombre2.getText().toString();
            String nuevosApellidos = inputApellidos2.getText().toString();
            String nuevaEdad = inputEdad2.getText().toString();

            if (!nuevoNombre.isEmpty() && !nuevosApellidos.isEmpty() && !nuevaEdad.isEmpty()) {
                String nombrePattern = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";
                if (nuevoNombre.matches(nombrePattern) && nuevosApellidos.matches(nombrePattern)) {
                    int edad;
                    try {
                        edad = Integer.parseInt(nuevaEdad);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Sólo se aceptan números para la edad.", Toast.LENGTH_SHORT).show();
                        inputEdad2.requestFocus();
                        return;
                    }
                    if (edad > 0 && edad < 100) {

                        SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
                        boolean sesionActiva = preferences.getBoolean("sesionActiva", false);

                        if (sesionActiva) {
                            String nombreUsuario = preferences.getString("nombreUsuario", "");
                            administrador_BD admin = new administrador_BD(this, "Base", null, 1);
                            SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

                            Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM Kit WHERE nombreUsuario = ?", new String[]{nombreUsuario});
                            if (cursor != null && cursor.moveToFirst()) {
                                int indexNombre = cursor.getColumnIndex("nombre");
                                int indexApellidos = cursor.getColumnIndex("apellidos");
                                int indexEdad = cursor.getColumnIndex("edad");

                                String dbNombre = cursor.getString(indexNombre);
                                String dbApellidos = cursor.getString(indexApellidos);
                                int dbEdad = cursor.getInt(indexEdad);

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
                                if (isUpdated) {
                                    BaseDeDatos.update("Kit", contentValues, "nombreUsuario = ?", new String[]{nombreUsuario});
                                    Toast.makeText(this, "Los datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
                                    editInfoKit();
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
    }private void copyToClipboardCodPresa(String codPresa) {
        String cdPresa = codPresa.replace("Código presa: ", "").trim();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Código de Presa", cdPresa);

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Código de presa copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
    private void copyToClipboardUsrName(String correo) {
        String email = correo.replace("Nombre de usuario: ", "").trim();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("Correo Electrónico", email);

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Correo electrónico copiado al portapapeles", Toast.LENGTH_SHORT).show();
    } private void backHomeApp(View view){
        Intent backHomeApp = new Intent(this, VerAppWebKit.class);
        startActivity(backHomeApp);
    }
}