package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPremio extends AppCompatActivity {
    TextView contCaractNameHabit, numRamitasPrem, contCaractInfoExtr, txtMasInfo;
    AutoCompleteTextView nombrePremInput;
    Spinner spinnerTipoPrem, spinnerCatPrem, spinnerNivelPrem;
    ImageView  btnAgregarImgPrem, btnSalirAddPrem;

    Button btnCrearPremMandar;
    private final int limCharInfoExtr = 350;

    private String imagenPremSelected = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_prem_tutor);


        //btn para cerrar la activity;
        btnSalirAddPrem = findViewById(R.id.btnSalirAddPrem);
        btnSalirAddPrem.setOnClickListener(v1 -> mostrarModalCerrarView("¡Atención!", "Si das click en aceptar saldrás del formulario y no se guardará la información ingresada."));

        nombrePremInput = findViewById(R.id.nombrePremInput);
        contCaractNameHabit = findViewById(R.id.contCaractNameHabit);
        nombrePremInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int caracteresActuales = s.length();
                contCaractNameHabit.setText(caracteresActuales + "/" + 80);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinnerTipoPrem = findViewById(R.id.spinnerTipPremio);
        spinnerCatPrem = findViewById(R.id.spinnerCatPremio);
        spinnerNivelPrem = findViewById(R.id.spinnerNivPremio);


        ArrayAdapter<CharSequence> adapterTip = ArrayAdapter.createFromResource(
                this,
                R.array.tipo,
                R.layout.dropdown_actis_nombre
        );

        ArrayAdapter<CharSequence> adapterNiv = ArrayAdapter.createFromResource(
                this,
                R.array.Nivel,
                R.layout.dropdown_actis_nombre
        );

        ArrayAdapter<CharSequence> adapterCat = ArrayAdapter.createFromResource(
                this,
                R.array.Categoria,
                R.layout.dropdown_actis_nombre
        );
        spinnerCatPrem.setPopupBackgroundResource(R.drawable.input_border);
        adapterTip.setDropDownViewResource(R.layout.dropdown_actis_nombre);
        spinnerCatPrem.setAdapter(adapterCat);

        spinnerNivelPrem.setPopupBackgroundResource(R.drawable.input_border);
        adapterTip.setDropDownViewResource(R.layout.dropdown_actis_nombre);
        spinnerNivelPrem.setAdapter(adapterNiv);


        spinnerTipoPrem.setPopupBackgroundResource(R.drawable.input_border);
        adapterTip.setDropDownViewResource(R.layout.dropdown_actis_nombre);
        spinnerTipoPrem.setAdapter(adapterTip);



        //número de ramitas:
        numRamitasPrem = findViewById(R.id.numRamitasPrem);
        numRamitasPrem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validnumRamitasPrem();
            }
        });

        //Para que se cambie el ícono de la imagen y su color
        btnAgregarImgPrem = findViewById(R.id.btnAgregarImgPrem);

        btnAgregarImgPrem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });


        //para la info extra:
        contCaractInfoExtr = findViewById(R.id.contCaractInfoExtr);
        txtMasInfo = findViewById(R.id.txtMasInfoPrem);
        txtMasInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int caracteresActuales = s.length();
                contCaractInfoExtr.setText(caracteresActuales + "/" + limCharInfoExtr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Se establecen los valores de la actividad
        establecerDatosPrem();


        //btn para actualizar acti
        btnCrearPremMandar = findViewById(R.id.btnCrearPremMandar);
        btnCrearPremMandar.setOnClickListener(this::editarPremio);
    }
    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir, si das click en aceptar saldrás y no se actualizará la actividad con los nuevos datos.");
    }
    public void establecerDatosPrem() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Premios>> call = apiService.getAllPremios(); // Asegúrate que el método se llame así en tu ApiService

        call.enqueue(new Callback<List<Premios>>() {
            @Override
            public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                List<Premios> premios = response.body();
                if (premios != null) {
                    Log.e("DEBUG", "Hay premios");
                    for (Premios premio : premios) {
                        SharedPreferences sharedPreferences = getSharedPreferences("premioSelected", Context.MODE_PRIVATE);
                        int idPremioGuardado = sharedPreferences.getInt("idPremio", 0);
                        Log.e("DEBUG", "Id Prem guardado: " + idPremioGuardado);

                        if (premio.getIdPremio() == idPremioGuardado) {
                            Log.e("DEBUG", "Sí entró");

                            // Cambiar nombre del premio
                            String nombrePrem = premio.getNombrePremio();
                            nombrePremInput.setText(nombrePrem);

                            // Cambiar tipo
                            String tipoPrem = premio.getTipoPremio();
                            if (tipoPrem.equals("Salud")) {
                                spinnerTipoPrem.setSelection(1);
                            } else if (tipoPrem.equals("Productividad")) {
                                spinnerTipoPrem.setSelection(2);
                            } else if (tipoPrem.equals("Personales")) {
                                spinnerTipoPrem.setSelection(3);
                            } else if (tipoPrem.equals("Sociales")) {
                                spinnerTipoPrem.setSelection(4);
                            } else if (tipoPrem.equals("Financieros")) {
                                spinnerTipoPrem.setSelection(5);
                            } else if (tipoPrem.equals("Emocionales")) {
                                spinnerTipoPrem.setSelection(6);
                            } else if (tipoPrem.equals("Mentales")) {
                                spinnerTipoPrem.setSelection(7);
                            } else if (tipoPrem.equals("Aventuras")) {
                                spinnerTipoPrem.setSelection(8);
                            } else if (tipoPrem.equals("Habilidades")) {
                                spinnerTipoPrem.setSelection(9);
                            } else if (tipoPrem.equals("Recreación")) {
                                spinnerTipoPrem.setSelection(10);
                            } else if (tipoPrem.equals("Tecnológicos")) {
                                spinnerTipoPrem.setSelection(11);
                            } else if (tipoPrem.equals("Educativos")) {
                                spinnerTipoPrem.setSelection(12);
                            } else if (tipoPrem.equals("Familiares")) {
                                spinnerTipoPrem.setSelection(13);
                            } else if (tipoPrem.equals("Amistosos")) {
                                spinnerTipoPrem.setSelection(14);
                            } else if (tipoPrem.equals("Experiencias")) {
                                spinnerTipoPrem.setSelection(15);
                            }

                            //cambiar categoria
                            String categoriaPrem = premio.getCategoriaPremio();
                            if (categoriaPrem.equals("Juguetes")) {
                                spinnerCatPrem.setSelection(1);
                            } else if (categoriaPrem.equals("Tiempo")) {
                                spinnerCatPrem.setSelection(2);
                            } else if (categoriaPrem.equals("Comida")) {
                                spinnerCatPrem.setSelection(3);
                            } else if (categoriaPrem.equals("Especiales")) {
                                spinnerCatPrem.setSelection(4);
                            } else if (categoriaPrem.equals("Tecnología")) {
                                spinnerCatPrem.setSelection(5);
                            } else if (categoriaPrem.equals("Entretenimiento")) {
                                spinnerCatPrem.setSelection(6);
                            } else if (categoriaPrem.equals("Deporte")) {
                                spinnerCatPrem.setSelection(7);
                            } else if (categoriaPrem.equals("Educación")) {
                                spinnerCatPrem.setSelection(8);
                            } else if (categoriaPrem.equals("Aventura")) {
                                spinnerCatPrem.setSelection(9);
                            } else if (categoriaPrem.equals("Creatividad")) {
                                spinnerCatPrem.setSelection(10);
                            } else if (categoriaPrem.equals("Viajes")) {
                                spinnerCatPrem.setSelection(11);
                            }

                            //Cambiar nivel
                            String nivelPrem = premio.getNivelPremio();
                            if (nivelPrem.equals("Común")) {
                                spinnerNivelPrem.setSelection(1);
                            } else if (nivelPrem.equals("Raro")) {
                                spinnerNivelPrem.setSelection(2);
                            } else if (nivelPrem.equals("Épico")) {
                                spinnerNivelPrem.setSelection(3);
                            } else if (nivelPrem.equals("Legendario")) {
                                spinnerNivelPrem.setSelection(4);
                            }


                            // Cambiar número de ramitas
                            String ramitasBD = String.valueOf(premio.getCostoPremio());
                            numRamitasPrem.setText(ramitasBD);

                            // Cambiar imagen del premio
                            String urlImgPremioBD = premio.getRutaImagenHabito(); // ruta completa
                            String nombreIcono = extraerContenidoImg(urlImgPremioBD); // nombre del archivo
                            updateButtonImage(nombreIcono);


                            // Info extra
                            byte[] byteArray = premio.getInfoExtraPremio().getBytes();
                            String infoExtra = new String(byteArray, StandardCharsets.UTF_8);
                            Log.e("InfoExtra", infoExtra);
                            txtMasInfo.setText(infoExtra);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Premios>> call, Throwable t) {
                Log.e("ERROR", "Falló la conexión: " + t.getMessage());
            }
        });
    }


    private void mostrarModalCerrarView(String titulo, String mensaje) {
        //Se crea el modal al momento de hacer click en el botón
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_cerrar_view_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        // Se obtienen los elementos del modal
        TextView txtTitle = dialog.findViewById(R.id.txtDialogTitle);
        TextView txtMessage = dialog.findViewById(R.id.txtDialogMessage);
        Button btnClose = dialog.findViewById(R.id.btnCerrarModal);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        // Asignar valores personalizados
        txtTitle.setText(titulo);
        txtMessage.setText(mensaje);

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(EditarPremio.this, HomeTutor.class);
            intent.putExtra("fragmenPremEditar", "RecompensasFragmentTutor");
            startActivity(intent);
        });

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .commit();

        });

        // Mostrar el modal
        dialog.show();
    }


    private void validnumRamitasPrem(){
        int valuenumRamitasPrem = -1;
        try {
            valuenumRamitasPrem = Integer.parseInt(numRamitasPrem.getText().toString());
        }catch (Exception ex){
        }
        if (!(valuenumRamitasPrem >= 1) && valuenumRamitasPrem != -1){

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            //Inicio de código para cambiar elementos del toast personalizado

            //Se cambia la imágen
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

            //Se cambia el texto
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Ingrese un número mayor a 1");

            //Se cambia el color de fondo
            Drawable background = layout.getBackground();
            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.rojito_toast), PorterDuff.Mode.SRC_IN);

            // Cambia color del texto
            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            //Fin del código que se encarga de cambiar los elementos del toast personalizado

            //Lo crea y lo pone en la parte de arriba del cel
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            numRamitasPrem.setText("");
        }
    }
    private void showImageSelectionDialog() {
        // Crear el BottomSheetDialog
        BottomSheetDialog modal = new BottomSheetDialog(EditarPremio.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_iconos_actis_view, null);
        modal.setContentView(view);

        // Deshabilitar el desplazamiento del modal
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setDraggable(false);  // Deshabilitar la capacidad de arrastrar el modal

        // Se infla el elemento para poner las imágenes
        GridView gridViewImages = view.findViewById(R.id.gridViewImages);

        // Configurar las imágenes en el GridView
        ArrayList<String> imageFiles = new ArrayList<>();
        try {
            String[] files = getAssets().list("img/Iconos-recompensas");
            if (files != null) {
                for (String file : files) {
                    imageFiles.add(file);  // Agregar archivo a la lista
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Configurar el adaptador para el GridView
        EditarPremio.ImageAdapter imageAdapter = new EditarPremio.ImageAdapter(imageFiles, modal);
        gridViewImages.setAdapter(imageAdapter);

        // Configurar el evento de selección de imagen
        gridViewImages.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedImage = imageFiles.get(position);  // Obtener la imagen seleccionada

            updateButtonImage(selectedImage);  // Actualizar la imagen del botón
            modal.dismiss();  // Cerrar el modal
        });

        modal.show();  // Mostrar el modal
    }

    private void updateButtonImage(String imageName) {
        // Asegurarse de que la imagen tiene la extensión .svg
        if (!imageName.endsWith(".svg")) {
            imageName += ".svg";  // Agregar la extensión .svg si no está presente
        }

        String imagePath = "img/Iconos-recompensas/" + imageName;  // Construir la ruta de la imagen
        imagenPremSelected = imagePath;  // Guardar la ruta seleccionada

        try {
            InputStream inputStream = getAssets().open(imagePath);  // Intentar abrir el archivo
            SVG svg = SVG.getFromInputStream(inputStream);  // Cargar el SVG
            Drawable drawable = new PictureDrawable(svg.renderToPicture());  // Convertir el SVG en Drawable
            btnAgregarImgPrem.setImageDrawable(drawable);  // Asignar el Drawable al botón
        } catch (IOException | SVGParseException e) {
            e.printStackTrace();
        }
    }




    private String extraerContenidoImg(String cadena) {
        // Encontrar la última aparición de "/"
        int index = cadena.lastIndexOf("/");

        // Si se encuentra el carácter "/"
        if (index != -1) {
            // Extraer la subcadena después del "/"
            return cadena.substring(index + 1);
        } else {
            // Si no se encuentra el "/", devolver la cadena completa
            return cadena;
        }
    }

    private void mostrarToastPersonalizado(String mensaje, int iconoRes, int colorFondoRes) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_personalizado, null);

        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(iconoRes);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(mensaje);

        Drawable background = layout.getBackground();
        background.setColorFilter(ContextCompat.getColor(getApplicationContext(), colorFondoRes), PorterDuff.Mode.SRC_IN);
        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    //para actualizar la acti:
    private void editarPremio(View view) {
        Log.d("API_LOG", "🟢 Iniciando método editarPremio...");

        // Obtener valores del formulario
        String nombrePremio = nombrePremInput.getText().toString().trim();
        String tipo = spinnerTipoPrem.getSelectedItem().toString();
        String categoria = spinnerCatPrem.getSelectedItem().toString();
        String nivel = spinnerNivelPrem.getSelectedItem().toString();
        String numeroRamitas = numRamitasPrem.getText().toString().trim();
        String masInfo = txtMasInfo.getText().toString().trim();

        Log.d("API_LOG", "📋 Datos ingresados: \nNombre: " + nombrePremio +
                "\nTipo: " + tipo + "\nCategoría: " + categoria +
                "\nNivel: " + nivel + "\nRamitas: " + numeroRamitas +
                "\nInfo extra: " + masInfo + "\nImagen: " + imagenPremSelected);

        // Validar campos
        if (nombrePremio.isEmpty() || numeroRamitas.isEmpty() || imagenPremSelected.isEmpty() || masInfo.isEmpty()
                || tipo.contains("- Selecciona un tipo de premio -") || categoria.contains("- Selecciona una categoria de premio -") || nivel.contains("- Selecciona un nivel de premio -")) {

            mostrarToastPersonalizado("Favor de completar todos los campos antes de editar", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
            return;
        }

        int numRam;
        try {
            numRam = Integer.parseInt(numeroRamitas);
            if (numRam < 1) {
                mostrarToastPersonalizado("Ingrese un número de ramitas mayor a 1", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
                return;
            }
        } catch (NumberFormatException ex) {
            mostrarToastPersonalizado("Número de ramitas inválido", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
            return;
        }

        // Verificar si el nombre ya existe (para evitar duplicados)
        verificarPremioExistenteParaEditar(nombrePremio);
    }

    private void verificarPremioExistenteParaEditar(String nombrePremio) {
        ApiService apiService = RetrofitClient.getApiService();

        SharedPreferences preferences = getSharedPreferences("premioSelected", MODE_PRIVATE);
        int idPremioActual = preferences.getInt("idPremio", 0);
        Log.d("API_LOG","idPremios:" + idPremioActual);


        apiService.getAllPremios().enqueue(new Callback<List<Premios>>() {
            @Override
            public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean existe = false;
                    Premios premioOriginal = null;

                    for (Premios premio : response.body()) {
                        if (premio.getNombrePremio().equalsIgnoreCase(nombrePremio)
                                && premio.getIdPremio() != idPremioActual) {
                            existe = true;
                            break;
                        }
                        if (premio.getIdPremio() == idPremioActual) {
                            premioOriginal = premio;
                        }
                    }

                    if (existe) {
                        mostrarToastPersonalizado("Ya existe un premio con ese nombre", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
                        return;
                    }

                    if (premioOriginal != null) {
                        // Comparar los campos
                        String tipoNuevo = spinnerTipoPrem.getSelectedItem().toString();
                        String categoriaNueva = spinnerCatPrem.getSelectedItem().toString();
                        String nivelNuevo = spinnerNivelPrem.getSelectedItem().toString();
                        String ramitasNuevo = numRamitasPrem.getText().toString().trim();
                        String infoNueva = txtMasInfo.getText().toString().trim();
                        String imagenNueva = imagenPremSelected;

                        boolean huboCambio = false;

                        if (!premioOriginal.getNombrePremio().equals(nombrePremio)) huboCambio = true;
                        if (!premioOriginal.getTipoPremio().equals(tipoNuevo)) huboCambio = true;
                        if (!premioOriginal.getCategoriaPremio().equals(categoriaNueva)) huboCambio = true;
                        if (!premioOriginal.getNivelPremio().equals(nivelNuevo)) huboCambio = true;
                        if (premioOriginal.getCostoPremio() != Integer.parseInt(ramitasNuevo)) huboCambio = true;
                        if (!premioOriginal.getInfoExtraPremio().equals(infoNueva)) huboCambio = true;
                        if (!premioOriginal.getRutaImagenHabito().equals(imagenNueva)) huboCambio = true;

                        if (huboCambio) {
                            continuarEdicionPremio();
                        } else {
                            mostrarToastPersonalizado("No se realizaron cambios en el premio", R.drawable.img_circ_palomita_verde, R.color.azulito_toast);
                        }
                    } else {
                        Toast.makeText(EditarPremio.this, "Error: Premio no encontrado para comparar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarPremio.this, "Error al obtener la lista de premios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Premios>> call, Throwable t) {
                Toast.makeText(EditarPremio.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void continuarEdicionPremio() {
        Log.d("API_LOG", "🟢 Iniciando continuarEdicionPremio...");

        SharedPreferences preferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);
        if (idKit == 0) {
            Log.e("API_LOG", "❌ No se pudo obtener el idKit.");
            Toast.makeText(this, "Error: No se pudo obtener el idKit", Toast.LENGTH_SHORT).show();
            return;
        }

        obtenerIdCastorYEditarPremio(idKit,
                nombrePremInput.getText().toString().trim(),
                spinnerTipoPrem.getSelectedItem().toString(),
                spinnerCatPrem.getSelectedItem().toString(),
                spinnerNivelPrem.getSelectedItem().toString(),
                Integer.parseInt(numRamitasPrem.getText().toString().trim()),
                txtMasInfo.getText().toString().trim(),
                imagenPremSelected);
    }

    private void obtenerIdCastorYEditarPremio(int idKit, String nombrePremio, String tipo, String categoria, String nivel,
                                              int numRamitasPrem, String masInfo, String imagenRuta) {
        Log.d("API_LOG", "🟢 Buscando ID del Castor...");
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAllCastores().enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API_LOG", "❌ Error al obtener los castores.");
                    Toast.makeText(EditarPremio.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences preferencesUser = getSharedPreferences("User", MODE_PRIVATE);
                String emailGuardado = preferencesUser.getString("email", "");

                final int[] idCastor = {-1};
                for (Castor castor : response.body()) {
                    if (castor.getEmail().equals(emailGuardado)) {
                        idCastor[0] = castor.getIdCastor();
                        break;
                    }
                }

                if (idCastor[0] == -1) {
                    Log.e("API_LOG", "❌ No se encontró el ID del Castor.");
                    Toast.makeText(EditarPremio.this, "No se encontró el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences preferencesPremio = getSharedPreferences("premioSelected", Context.MODE_PRIVATE);
                int idPremio = preferencesPremio.getInt("idPremio", 0);
                Log.d("API_LOG","idPremios:" + idPremio);

                if (idPremio == 0) {
                    Log.e("API_LOG", "❌ No se encontró el ID del premio.");
                    Toast.makeText(EditarPremio.this, "Error: No se encontró el ID del premio para editar", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("API_LOG", "📦 Obteniendo premio original con ID: " + idPremio);
                ApiService apiService = RetrofitClient.getApiService();

                apiService.getAllPremios().enqueue(new Callback<List<Premios>>() {
                    @Override
                    public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("API_LOG", "❌ Error al obtener los premios.");
                            Toast.makeText(EditarPremio.this, "Error al obtener los premios", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Premios premioEncontrado = null;
                        for (Premios premio : response.body()) {
                            if (premio.getIdPremio() == idPremio) {
                                premioEncontrado = premio;
                                break;
                            }
                        }

                        if (premioEncontrado == null) {
                            Log.e("API_LOG", "❌ No se encontró el premio con ID: " + idPremio);
                            Toast.makeText(EditarPremio.this, "No se encontró el premio con ID: " + idPremio, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Premios premioEditado = new Premios();
                        premioEditado.setIdPremio(idPremio);
                        premioEditado.setIdCastor(idCastor[0]);
                        premioEditado.setNombrePremio(nombrePremio);
                        premioEditado.setTipoPremio(tipo);
                        premioEditado.setCategoriaPremio(categoria);
                        premioEditado.setNivelPremio(nivel);
                        premioEditado.setCostoPremio(numRamitasPrem);
                        premioEditado.setInfoExtraPremio(masInfo);
                        premioEditado.setEstadoPremio(premioEncontrado.getEstadoPremio());
                        premioEditado.setFavorito(premioEncontrado.getFavorito());

                        try {
                            int index = imagenRuta.lastIndexOf("/");
                            String nombreImg = imagenRuta.substring(index + 1);
                            String rutaFinal = "../img/Iconos-recompensas/" + nombreImg;
                            Log.d("Imagen", "Ruta imagen: " + rutaFinal);
                            premioEditado.setRutaImagenHabito(rutaFinal);
                        } catch (Exception e) {
                            Log.e("CHECK", "❌ Error al establecer ruta de imagen: " + e.getMessage());
                        }

                        Log.d("API_LOG", "📤 Enviando premio actualizado al servidor...");
                        apiService.updatePremios(premioEditado).enqueue(new Callback<Premios>() {
                            @Override
                            public void onResponse(Call<Premios> call, Response<Premios> response) {
                                if (response.isSuccessful()) {
                                    Log.d("API_LOG", "✅ Premio editado correctamente.");
                                    mostrarToastExitoYRedirigir();
                                    finish();
                                } else {
                                    Log.e("API_LOG", "❌ Error al editar el premio, código: " + response.code());
                                    Toast.makeText(EditarPremio.this, "Error al editar el premio", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Premios> call, Throwable t) {
                                Log.e("API_LOG", "❌ Error en conexión al editar el premio: " + t.getMessage());
                                Toast.makeText(EditarPremio.this, "Error en la conexión al editar el premio", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Premios>> call, Throwable t) {
                        Log.e("API_LOG", "❌ Error al obtener los datos actuales del premio: " + t.getMessage());
                        Toast.makeText(EditarPremio.this, "Error al obtener los datos actuales del premio", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Log.e("API_LOG", "❌ Error al obtener el ID del Castor: " + t.getMessage());
                Toast.makeText(EditarPremio.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarToastExitoYRedirigir() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_personalizado, null);

        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(R.drawable.img_circ_palomita_verde);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText("Premio editado con éxito");

        Drawable background = layout.getBackground();
        background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.verdecito_toast), PorterDuff.Mode.SRC_IN);
        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        SharedPreferences sharedPreferencesCerrar = getSharedPreferences("sesionModalPrem", MODE_PRIVATE);
        SharedPreferences.Editor editorcerrarp = sharedPreferencesCerrar.edit();
        editorcerrarp.putBoolean("sesion_activa_prem", false);

        editorcerrarp.apply();
        Intent intent = new Intent(EditarPremio.this, HomeTutor.class);
        intent.putExtra("fragmenPremEditar", "RecompensasFragmentTutor");
        Log.d("Cambiecito","Se envia editar");
        startActivity(intent);
    }
    //la clasesita pa la imágen
    private class ImageAdapter extends BaseAdapter {
        private ArrayList<String> images;
        private BottomSheetDialog modal;

        public ImageAdapter(ArrayList<String> images, BottomSheetDialog modal) {
            this.images = images;
            this.modal = modal;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(EditarPremio.this);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            String imagePath = "img/Iconos-recompensas/" + images.get(position);
            try {
                // Abre el archivo SVG
                InputStream is = getAssets().open(imagePath);

                // Usa AndroidSVG para convertir el archivo SVG a Drawable
                SVG svg = SVG.getFromInputStream(is);
                Drawable drawable = new PictureDrawable(svg.renderToPicture());

                // Establece el Drawable en el ImageView
                imageView.setImageDrawable(drawable);

                // Cierra el InputStream
                is.close();
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }


            //se le agrega el onlistener a las imágenes para que se de click y se guarde la imágen
            imageView.setOnClickListener(v -> {
                String selectedImage = images.get(position);  // Obtener la imagen seleccionada
                Log.d("IMAGE_SELECT", "Imagen seleccionada: " + selectedImage);  // Log para ver la imagen seleccionada
                String nuevaRuta = selectedImage.replace(".svg", "");  // Eliminar la extensión .svg si está presente
                Log.d("IMAGE_SELECT", "Ruta de la imagen sin extensión: " + nuevaRuta);  // Log para ver la ruta modificada
                updateButtonImage(nuevaRuta);  // Actualizar la imagen del botón
                modal.dismiss();  // Cerrar el modal
            });

            return imageView;
        }
    }
    }

