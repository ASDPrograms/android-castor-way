package com.example.castorway;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;


import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.modelsDB.RelPrem;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AgregarPremTutor extends AppCompatActivity {
    TextView contCaractNameHabit, numRamitasPrem, contCaractInfoExtr, txtMasInfo;
    AutoCompleteTextView nombrePremInput;
    Spinner spinnerTipoPrem, spinnerCatPrem, spinnerNivelPrem;
    ImageView  btnAgregarImgPrem, btnSalirAddPrem;

    Button btnCrearPremMandar;
    private final int limCharNombreHabit = 35;
    private final int limCharInfoExtr = 350;

    private String imagenPremSelected = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_prem_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //Se declara el btn que permite salir del activity de nuevo hábito:
            btnSalirAddPrem = findViewById(R.id.btnSalirAddPrem);
            btnSalirAddPrem.setOnClickListener(v1 -> mostrarModalCerrarView("¡Atención!", "Si das click en aceptar saldrás del formulario y no se guardará la información ingresada."));


            spinnerCatPrem = findViewById(R.id.spinnerCatPremio);
            spinnerNivelPrem = findViewById(R.id.spinnerNivPremio);
            spinnerTipoPrem = findViewById(R.id.spinnerTipPremio);
            if (savedInstanceState != null) {
                spinnerNivelPrem.setSelection(savedInstanceState.getInt("niv", 0));
                spinnerCatPrem.setSelection(savedInstanceState.getInt("cat", 0));
                spinnerTipoPrem.setSelection(savedInstanceState.getInt("tip", 0));
            }

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


            String[] opciones = {
                    "Coche de juguete",
                    "Muñeca",
                    "Pelota",
                    "Bloques de construcción",
                    "Set de LEGO",
                    "Robot de juguete",
                    "Drone para niños",
                    "Camión de bomberos",
                    "Juegos de mesa (ej. Monopoly, Uno)",
                    "Puzzles y rompecabezas",
                    "Kits de ciencia o experimentos",
                    "Figuras de acción (ej. superhéroes)",
                    "15 minutos adicionales de TV",
                    "30 minutos de videojuego",
                    "Hora de dormir extendida",
                    "30 minutos de parque",
                    "Una tarde en casa de amigos",
                    "Día libre de tareas escolares",
                    "Una noche de películas con palomitas",
                    "Sesión de lectura extra",
                    "Día sin reglas de casa",
                    "Tarde de juegos de mesa en familia",
                    "Tiempo de relajación en la bañera",
                    "Helado",
                    "Pizza",
                    "Chocolates",
                    "Palomitas de maíz",
                    "Cena en tu restaurante favorito",
                    "Tarta de cumpleaños extra",
                    "Galletas caseras",
                    "Una caja de dulces sorpresa",
                    "Batidos de frutas",
                    "Desayuno en la cama",
                    "Salir al cine",
                    "Paseo a la playa",
                    "Día en el zoológico",
                    "Visita al parque de diversiones",
                    "Día de picnic",
                    "Día de compras",
                    "Concierto de tu banda favorita",
                    "Excursión al museo",
                    "Viaje de fin de semana a una ciudad cercana",
                    "Día de spa en casa",
                    "Visita a una granja o reserva natural",
                    "Nueva tablet o e-reader",
                    "Auriculares inalámbricos",
                    "Juego de video nuevo",
                    "Accesorios para la consola de videojuegos",
                    "Cámara instantánea",
                    "Suscripción a un servicio de streaming",
                    "Reloj inteligente",
                    "Laptop nueva",
                    "Altavoz Bluetooth",
                    "Entradas para un espectáculo",
                    "Suscripción a una revista",
                    "Curso en línea de un tema de interés",
                    "Pase anual a un parque temático",
                    "Tarjeta de regalo para una tienda de entretenimiento",
                    "Día de karaoke con amigos",
                    "Taller de arte o manualidades",
                    "Clases de cocina",
                    "Participación en una escape room",
                    "Festival de música",
                    "Equipo deportivo nuevo (ej. bicicleta, patines)",
                    "Entradas para un evento deportivo",
                    "Clase de deportes (ej. natación, fútbol)",
                    "Pase para un gimnasio",
                    "Ropa deportiva nueva",
                    "Accesorios para practicar un deporte (ej. balón, raqueta)",
                    "Un día en un parque de aventuras",
                    "Día de senderismo en la naturaleza",
                    "Herramientas para el aprendizaje (ej. calculadora, diccionario)",
                    "Clases de música o arte",
                    "Visitas a bibliotecas o centros culturales",
                    "Material de arte (pinturas, lápices, etc.)",
                    "Día de camping",
                    "Escalada en roca",
                    "Paseo en kayak o canoa",
                    "Safari en un parque nacional",
                    "Exploración de cuevas",
                    "Día de rafting",
                    "Tirolesa en un parque de aventura",
                    "Un viaje en globo aerostático",
                    "Clases de supervivencia al aire libre",
                    "Taller de fotografía",
                    "Clases de pintura o escultura",
                    "Material de escritura (ej. cuadernos, plumas)",
                    "Accesorios para música (ej. guitarra, teclado)",
                    "Curso de diseño gráfico",
                    "Kit de jardinería",
                    "Participación en un club de arte",
                    "Materiales para hacer joyas",
                    "Experiencias de improvisación teatral"
            };


            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.dropdown_actis_nombre,
                    R.id.tvDropdownItem,
                    opciones
            );

            nombrePremInput = findViewById(R.id.nombrePremInput);
            nombrePremInput.setDropDownBackgroundResource(R.drawable.input_border);

            //Se agrega un listener a cambio del texto para el contador de caractéres
            //y se declara el contador de caractéres
            contCaractNameHabit = findViewById(R.id.contCaractNameHabit);
            nombrePremInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int caracteresActuales = s.length();
                    contCaractNameHabit.setText(caracteresActuales + "/" + limCharNombreHabit);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            nombrePremInput.setAdapter(adapter);

            //el evento lo que hace es al presionar el input de nombre de hábito se despliega la lista
            nombrePremInput.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    nombrePremInput.showDropDown();
                }
            });

            nombrePremInput.setOnClickListener(view -> nombrePremInput.showDropDown());


            nombrePremInput.setOnItemClickListener((parent, view, position, id) -> {
                String seleccion = (String) parent.getItemAtPosition(position);

                // Llenar datos del formulario según la opción seleccionada
                switch (seleccion) {
                    case "Coche de juguete":
                        try {

                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado


                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);

                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/coche_juguete.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }


                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Coche de juguete");

                            //Se cambia el color de fondo
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            // Cambia color del texto
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            //Fin del código que se encarga de cambiar los elementos del toast personalizado

                            //Lo crea y lo pone en la parte de arriba del cel
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                            //Fin del código para mostrar el toast personalizado


                            //Inicio del código que cambia los valores dependiendo de lo que seleccione
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("coche_juguete.svg");

                            spinnerCatPrem.setSelection(1);
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(1);



                            txtMasInfo.setText("Este coche de juguete es perfecto para desarrollar la imaginación y" +
                                            " fomentar el juego simbólico. Los niños pueden crear sus propias aventuras " +
                                            "y escenarios mientras mejoran sus habilidades motoras.");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            });
            //Se declara variable para el número de ramitas:
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
                    validNumRamitas();
                }
            });



            //Botón que se encarga de abrir el modal de las imágenes y el círculo que cambia de color
            btnAgregarImgPrem = findViewById(R.id.btnAgregarImgPrem);
            btnAgregarImgPrem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageSelectionDialog();
                }
            });


            //Declaración del input de info extra del modal:
            txtMasInfo = findViewById(R.id.txtMasInfoPrem);
            //Se agrega un listener a cambio del texto para el contador de caractéres
            //y se declara el contador de caractéres
            contCaractInfoExtr = findViewById(R.id.contCaractInfoExtr);
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


            //Declaración del botón que crea el premio
            btnCrearPremMandar = findViewById(R.id.btnCrearPremMandar);
           btnCrearPremMandar.setOnClickListener(this::crearPremio);

            return insets;
        });
    }
    private void showImageSelectionDialog() {
        // Crear el BottomSheetDialog
        BottomSheetDialog modal = new BottomSheetDialog(AgregarPremTutor.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_iconos_actis_view, null);
        modal.setContentView(view);
        //Se infla el elemento pa poder poner las imagenes
        GridView gridViewImages = view.findViewById(R.id.gridViewImages);

        // Configurar las imágenes en el GridView
        ArrayList<String> imageFiles = new ArrayList<>();
        try {
            String[] files = getAssets().list("img/Iconos-recompensas");
            if (files != null) {
                for (String file : files) {
                        imageFiles.add(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("IMAGENES", "IMG: " + imageFiles);

        // Configurar el adaptador para el GridView
        ImageAdapter imageAdapter = new ImageAdapter(imageFiles, modal);
        gridViewImages.setAdapter(imageAdapter);

        modal.show();
    }


    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir del formulario, si das click en aceptar saldrás y no se guardará la información ingresada.");
    }
    private void updateButtonImage(String imageName) {
        String imagePath = "img/Iconos-recompensas/" + imageName;
        imagenPremSelected = imagePath;
        Log.d("IMAGEN", "Imagen seleccionada: " + imagenPremSelected);

        try {
            InputStream inputStream = getAssets().open(imagePath);
            SVG svg = SVG.getFromInputStream(inputStream);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());

            // Asigna la imagen al botón que abre el modal
            btnAgregarImgPrem.setImageDrawable(drawable);
        } catch (IOException | SVGParseException e) {
            e.printStackTrace();
        }
    }




    private void validNumRamitas(){
        int valueNumRamitas = -1;
        try {
            valueNumRamitas = Integer.parseInt(numRamitasPrem.getText().toString());
        }catch (Exception ex){
        }
        if (!(valueNumRamitas >= 1) && valueNumRamitas != -1){

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
            finish();
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

    //Clase para poder procesar las imagenes como svg y de assets
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
                imageView = new ImageView(AgregarPremTutor.this);
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
                String selectedImage = images.get(position);

                String nuevaRuta = selectedImage.replace(".svg", "");
                updateButtonImage(nuevaRuta);
                modal.dismiss();
            });


            return imageView;
        }
    }

    private void crearPremio(View view) {
        Log.d("API_LOG", "Iniciando creación de premio..."); // 🔍 LOG

        // Validaciones iniciales
        String nombrePremio = nombrePremInput.getText().toString().trim();
        String selectedText = spinnerTipoPrem.getSelectedItem().toString();
        String selectedText1 = spinnerCatPrem.getSelectedItem().toString();
        String selectedText2 = spinnerNivelPrem.getSelectedItem().toString();
        String numeroRamitas = numRamitasPrem.getText().toString().trim();
        String masInfo = txtMasInfo.getText().toString().trim();

        Log.d("API_LOG", "Datos ingresados -> Nombre: " + nombrePremio + ", Tipo: " + selectedText +
                ", Categoría: " + selectedText1 + ", Nivel: " + selectedText2 + ", Ramitas: " + numeroRamitas +
                ", Info extra: " + masInfo + ", Imagen: " + imagenPremSelected); // 🔍 LOG

        if (nombrePremio.isEmpty() || numeroRamitas.isEmpty()
                || imagenPremSelected.isEmpty() || masInfo.isEmpty()
                || selectedText.equalsIgnoreCase("- Selecciona un tipo de premio -")
                || selectedText1.equalsIgnoreCase("- Selecciona una categoria de premio -")
                || selectedText2.equalsIgnoreCase("- Selecciona un nivel de premio -")) {

            Log.w("API_LOG", "Faltan campos obligatorios"); // 🔍 LOG
            mostrarToastPersonalizado("Favor de completar todos los campos antes de crear", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
            return;
        }

        int numRam;
        try {
            numRam = Integer.parseInt(numeroRamitas);
            if (numRam < 1) {
                Log.w("API_LOG", "Número de ramitas menor a 1"); // 🔍 LOG
                mostrarToastPersonalizado("Ingrese un número de ramitas mayor a 1", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
                return;
            }
        } catch (NumberFormatException ex) {
            Log.e("API_LOG", "Número de ramitas inválido: " + ex.getMessage()); // 🔍 LOG
            mostrarToastPersonalizado("Número de ramitas inválido", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
            return;
        }

        // Obtener idKit de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);
        Log.d("API_LOG", "idKit obtenido: " + idKit); // 🔍 LOG
        if (idKit == 0) {
            Log.e("API_LOG", "No se pudo obtener el idKit desde SharedPreferences"); // 🔍 LOG
            Toast.makeText(this, "Error: No se pudo obtener el idKit", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllPremios().enqueue(new Callback<List<Premios>>() {
            @Override
            public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                Log.d("API_LOG", "Respuesta de getAllPremios: " + response.code()); // 🔍 LOG
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API_ERROR", "Error al obtener premios: " + response.message()); // 🔍 LOG
                    Toast.makeText(AgregarPremTutor.this, "Error al obtener premios", Toast.LENGTH_SHORT).show();
                    return;
                }

                obtenerIdCastorYCrearPremio(idKit, nombrePremio, numRam, masInfo);
            }

            @Override
            public void onFailure(Call<List<Premios>> call, Throwable t) {
                Log.e("API_ERROR", "Fallo al obtener premios: " + t.getMessage(), t); // 🔍 LOG completo
                Toast.makeText(AgregarPremTutor.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void obtenerIdCastorYCrearPremio(int idKit, String nombrePremio, int numRamitasPrem, String masInfo) {
        Log.d("API_LOG", "Iniciando creación de premio...");
        Log.d("API_LOG", "Datos ingresados -> Nombre: " + nombrePremio +
                ", Tipo: " + spinnerTipoPrem.getSelectedItem() +
                ", Categoría: " + spinnerCatPrem.getSelectedItem() +
                ", Nivel: " + spinnerNivelPrem.getSelectedItem() +
                ", Ramitas: " + numRamitasPrem +
                ", Info extra: " + masInfo +
                ", Imagen: " + imagenPremSelected);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllCastores().enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                Log.d("API_LOG", "Respuesta de getAllCastores: " + response.code());

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API_ERROR", "Error al obtener los castores: " + response.message());
                    Toast.makeText(AgregarPremTutor.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                String emailGuardado = preferences.getString("email", "");
                final int[] idCastor = new int[1];
                boolean encontrado = false;

                Log.d("API_LOG", "Recibiendo lista de castores: " + response.body().size());

                for (Castor castor : response.body()) {
                    Log.d("API_LOG", "Castor encontrado: " + castor.getEmail());
                    if (castor.getEmail().equals(emailGuardado)) {
                        idCastor[0] = castor.getIdCastor();
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    Log.e("API_ERROR", "No se encontró el ID del Castor con el email: " + emailGuardado);
                    Toast.makeText(AgregarPremTutor.this, "No se encontró el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("API_LOG", "ID del Castor encontrado: " + idCastor[0]);

                // Comprobaciones de datos antes de enviar
                Log.d("CHECK", "Validando datos antes de crear el premio...");

                if (nombrePremio == null || nombrePremio.trim().isEmpty())
                    Log.e("CHECK_ERROR", "Nombre del premio es nulo o vacío");
                if (imagenPremSelected == null || imagenPremSelected.trim().isEmpty())
                    Log.e("CHECK_ERROR", "Ruta de imagen es nula o vacía");

                String tipo = spinnerTipoPrem.getSelectedItem() != null ? spinnerTipoPrem.getSelectedItem().toString() : "null";
                String categoria = spinnerCatPrem.getSelectedItem() != null ? spinnerCatPrem.getSelectedItem().toString() : "null";
                String nivel = spinnerNivelPrem.getSelectedItem() != null ? spinnerNivelPrem.getSelectedItem().toString() : "null";

                if (tipo.equals("null") || categoria.equals("null") || nivel.equals("null"))
                    Log.e("CHECK_ERROR", "Spinner(s) devuelven valor nulo");

                Premios premio = new Premios();
                premio.setIdCastor(idCastor[0]);
                premio.setNombrePremio(nombrePremio);
                premio.setTipoPremio(tipo);
                premio.setCategoriaPremio(categoria);
                premio.setNivelPremio(nivel);
                premio.setCostoPremio(numRamitasPrem);
                premio.setInfoExtraPremio(masInfo);

                try {
                    int index = imagenPremSelected.lastIndexOf("/");
                    String nombreImg = imagenPremSelected.substring(index + 1);
                    String ruta = "../img/Iconos_recompensas/" + nombreImg;
                    premio.setRutaImagenHabito(ruta);
                    Log.d("CHECK", "Ruta de imagen generada correctamente: " + ruta);
                } catch (Exception e) {
                    Log.e("CHECK_ERROR", "Error al generar ruta de imagen: " + e.getMessage());
                }

                Log.d("DEBUG_PREMIO", "Datos del premio a enviar: \n" +
                        "idCastor: " + premio.getIdCastor() + "\n" +
                        "nombrePremio: " + premio.getNombrePremio() + "\n" +
                        "tipoPremio: " + premio.getTipoPremio() + "\n" +
                        "categoriaPremio: " + premio.getCategoriaPremio() + "\n" +
                        "nivelPremio: " + premio.getNivelPremio() + "\n" +
                        "costoPremio: " + premio.getCostoPremio() + "\n" +
                        "infoExtraPremio: " + premio.getInfoExtraPremio() + "\n" +
                        "rutaImagenHabito: " + premio.getRutaImagenHabito());

                apiService.createPremio(premio).enqueue(new Callback<Premios>() {
                    @Override
                    public void onResponse(Call<Premios> call, Response<Premios> response) {
                        Log.d("API_LOG", "Respuesta de crearPremio: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("API_LOG", "Premio creado exitosamente con ID: " + response.body().getIdPremio());
                            mostrarToastExitoYRedirigir();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin cuerpo de error";
                                Log.e("API_ERROR", "Error al crear el premio. Código: " + response.code()
                                        + ", Mensaje: " + response.message()
                                        + ", Body: " + errorBody);
                            } catch (IOException e) {
                                Log.e("API_ERROR", "Error al leer el cuerpo del error: " + e.getMessage());
                            }
                            Toast.makeText(AgregarPremTutor.this, "Error al crear el premio", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Premios> call, Throwable t) {
                        Log.e("API_ERROR", "Error al crear el premio: " + t.getMessage());
                        Toast.makeText(AgregarPremTutor.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Log.e("API_ERROR", "Error al obtener el ID del Castor: " + t.getMessage());
                Toast.makeText(AgregarPremTutor.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mostrarToastExitoYRedirigir() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_personalizado, null);

        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(R.drawable.img_circ_palomita_verde);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText("Premio creado con éxito");

        Drawable background = layout.getBackground();
        background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.verdecito_toast), PorterDuff.Mode.SRC_IN);
        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        Intent intent = new Intent(AgregarPremTutor.this, HomeTutor.class);
        intent.putExtra("fragmenPremCrear", "RecompensasFragmentTutor");
        startActivity(intent);
    }
}