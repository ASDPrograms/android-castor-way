package com.example.castorway;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Rect;
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
import android.view.ViewTreeObserver;
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
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Premios;
import com.example.castorway.modelsDB.RelPrem;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    private final int limCharNombreHabit = 80;
    private final int limCharInfoExtr = 350;

    private String imagenPremSelected = "";


    private View rootView;
    private boolean isKeyboardVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_prem_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            btnSalirAddPrem = findViewById(R.id.btnSalirAddPrem);
            btnSalirAddPrem.setOnClickListener(v1 -> mostrarModalCerrarView("¡Atención!", "Si das click en aceptar saldrás del formio y no se guardará la información ingresada."));
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
                    "Aurices inalámbricos",
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

            nombrePremInput.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    nombrePremInput.showDropDown();
                }
            });

            nombrePremInput.setOnClickListener(view -> nombrePremInput.showDropDown());


            nombrePremInput.setOnItemClickListener((parent, view, position, id) -> {
                String seleccion = (String) parent.getItemAtPosition(position);

                switch (seleccion) {
                    case "Coche de juguete":
                        try {

                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            


                            
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


                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Coche de juguete");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                           

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                            


                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("coche_juguete.svg");

                            spinnerCatPrem.setSelection(1);
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);



                            txtMasInfo.setText("Este coche de juguete es perfecto para desarrollar la imaginación y" +
                                    " fomentar el juego simbólico. Los niños pueden crear sus propias aventuras " +
                                    "y escenarios mientras mejoran sus habilidades motoras.");
                            

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Muñeca":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/muneca.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Muñeca");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("muneca.svg");

                            spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Una muñeca que se convertirá en la mejor amiga de tu hijo. Ideal para fomentar el juego de roles, la creatividad y la empatía mientras los niños crean historias y escenarios divertidos.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Pelota":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/pelota.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Pelota");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("pelota.svg");

                            spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(1);

                            txtMasInfo.setText("Una pelota versátil para disfrutar al aire libre. Jugar con una pelota ayuda a desarrollar habilidades motoras, coordinación y fomenta un estilo de vida activo y saludable.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Bloques de construcción":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/bloques_construccion.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Bloques de construcción");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 300;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bloques_construccion.svg");

                            spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(2);

                            txtMasInfo.setText("Estos bloques de construcción estimulan la creatividad y el pensamiento crítico. Los niños pueden construir lo que imaginen, desarrollando habilidades espaciales y de resolución de problemas.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Set de LEGO":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Lego.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Set de LEGO");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 500;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Lego.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(2);

                            txtMasInfo.setText("Un clásico que nunca pasa de moda. Este set de LEGO permite a los niños crear estructuras complejas mientras desarrollan su imaginación y habilidades de planificación. Ideal para horas de diversión creativa.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Robot de juguete":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Robot.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Robot de juguete");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 800;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Robot.svg");

 spinnerCatPrem.setSelection(2);
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un robot de juguete interactivo que introduce a los niños en la tecnología y la programación. Fomenta el aprendizaje de ciencias, tecnología, ingeniería y matemáticas (STEM) mientras se divierten.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Drone para niños":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Dron.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Drone para niños");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 600;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Dron.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un emocionante drone diseñado para niños que permite explorar el aire. Desarrolla habilidades de control y coordinación mientras los niños se divierten en el exterior.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Camión de bomberos":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Bomberos.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Camión de bomberos");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 350;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Bomberos.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Este camión de bomberos inspirará historias de rescate y aventuras emocionantes. Fomenta el juego imaginativo y la creatividad mientras los niños recrean situaciones heroicas.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Juegos de mesa (ej. Monopoly, Uno)":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Juegos_de_mesa.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Juegos de mesa");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 400;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Juegos_de_mesa.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una forma excelente de pasar tiempo en familia o con amigos. Los juegos de mesa ayudan a desarrollar habilidades sociales, pensamiento estratégico y, sobre todo, ¡muchas risas!");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Puzzles y rompecabezas":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Puzzle.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Puzzles y rompecabezas");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 350;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Puzzle.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(2);

                            txtMasInfo.setText("Un reto divertido que fomenta la concentración y la resolución de problemas. Los puzzles ayudan a desarrollar habilidades cognitivas mientras los niños se divierten armando imágenes.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Kits de ciencia o experimentos":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Ciencia.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                         
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Kits de ciencia o experimentos");

                           
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 450;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Ciencia.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(3);
                          spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Estos kits permiten a los niños explorar el mundo de la ciencia a través de experimentos prácticos y divertidos. Aprenderán conceptos científicos de manera interactiva y entretenida.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Figuras de acción (ej. superhéroes)":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Heroe.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Figuras de acción");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Heroe.svg");

 spinnerCatPrem.setSelection(1); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Figuras de acción que invitan a los niños a crear sus propias historias llenas de aventuras y heroísmo. Perfectas para el juego imaginativo y el desarrollo de narrativas creativas.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "15 minutos adicionales de TV":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/TV.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("15 minutos adicionales de TV");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 50;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("TV.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un pequeño premio para disfrutar de un episodio extra de tu serie o programa favorito. Ideal para relajarse y disfrutar de un momento de entretenimiento.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "30 minutos de videojuego":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/VideoJuego.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("30 minutos de videojuego");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("VideoJuego.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Más tiempo para sumergirte en el mundo de tus videojuegos favoritos. Un premio que proporciona diversión y entretenimiento adicional.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Hora de dormir extendida":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Dormir.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Hora de dormir extendida");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Dormir.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(6);

                            txtMasInfo.setText("Una hora extra para quedarte despierto y disfrutar de actividades tranquilas. Perfecto para un momento especial de conexión familiar antes de dormir.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "30 minutos de parque":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Parque.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("30 minutos de parque");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Parque.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(1);

                            txtMasInfo.setText("Disfruta de un tiempo adicional al aire libre, jugando en el parque. Ideal para mantenerse activo, socializar y disfrutar de la naturaleza.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Una tarde en casa de amigos":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Amigos.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Una tarde en casa de amigos");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Amigos.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(2);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un tiempo especial para jugar y compartir con amigos. Fomenta las relaciones sociales y crea recuerdos divertidos.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día libre de tareas escolares":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Tareas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día libre de tareas escolares");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 75;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Tareas.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un día libre de todas las tareas escolares, perfecto para relajarse y disfrutar de tiempo libre.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Una noche de películas con palomitas":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Pelicula.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Una noche de películas con palomitas");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 30;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Pelicula.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Disfruta de una noche acogedora viendo tus películas favoritas acompañada de palomitas.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Sesión de lectura extra":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Libro.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Sesión de lectura extra");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 20;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Libro.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(1);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Dedica tiempo extra a la lectura de tus libros favoritos para relajarte y aprender.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día sin reglas de casa":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/No_Reglas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día sin reglas de casa");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 50;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("No_Reglas.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(2);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un día especial donde puedes disfrutar de libertad total en casa, sin reglas que seguir.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Tarde de juegos de mesa en familia":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Juegos_Familia.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Tarde de juegos de mesa en familia");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 40;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Juegos_Familia.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Disfruta de una divertida tarde jugando juegos de mesa con la familia.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Tiempo de relajación en la bañera":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/banera.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Tiempo de relajación en la bañera");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("banera.svg");

 spinnerCatPrem.setSelection(2); 
                            spinnerNivelPrem.setSelection(1);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Dedica un tiempo para relajarte en la bañera, con burbujas y música suave.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Helado":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Helado.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Helado");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 5;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Helado.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un delicioso helado de tu sabor favorito, ideal para refrescarte en un día caluroso. Perfecto para compartir con amigos o disfrutar en un momento de relax.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Pizza":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Pizza.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Pizza");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 25;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Pizza.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(2);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una rica pizza con tus ingredientes preferidos, perfecta para disfrutar en una noche de juegos o una reunión familiar. Comparte con quienes más quieres.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Chocolates":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Chocolate.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Chocolates");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 10;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Chocolate.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una selección de deliciosos chocolates para satisfacer tu antojo. Ideal para compartir con amigos o como un regalo especial.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Palomitas de maíz":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Palomitas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Palomitas de maíz");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 5;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Palomitas.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un clásico de las noches de cine, estas palomitas son perfectas para disfrutar mientras miras tu película favorita. Comparte la diversión con tus amigos.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Cena en tu restaurante favorito":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Restaurante.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Cena en tu restaurante favorito");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 50;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Restaurante.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(4);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una cena gourmet en tu restaurante favorito, donde podrás disfrutar de una experiencia culinaria única con un ambiente acogedor y excelente servicio.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Tarta de cumpleaños extra":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Pastel_cumpleaños.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Tarta de cumpleaños extra");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 30;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Pastel_cumpleaños.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(2);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una deliciosa tarta de cumpleaños decorada de forma especial para hacer tu celebración aún más memorable. Ideal para compartir con amigos y familiares.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Galletas caseras":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Galleta.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Galletas caseras");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 15;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Galleta.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Galletas recién horneadas, crujientes por fuera y suaves por dentro. Perfectas para acompañar con un vaso de leche o como un regalo para alguien especial.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Una caja de dulces sorpresa":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Dulces.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Una caja de dulces sorpresa");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 10;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Dulces.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(1);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una caja llena de deliciosos dulces variados que te sorprenderán en cada bocado. Ideal para disfrutar en cualquier ocasión especial.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Batidos de frutas":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Batido_Frutas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Batidos de frutas");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 20;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Batido_Frutas.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(1);

                            txtMasInfo.setText("Batidos refrescantes de frutas frescas, perfectos para nutrir tu cuerpo y deleitar tu paladar. Una opción saludable para cualquier momento del día.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Desayuno en la cama":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Desayuno_Cama.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Desayuno en la cama");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 15;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Desayuno_Cama.svg");

 spinnerCatPrem.setSelection(3); 
                            spinnerNivelPrem.setSelection(1);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un delicioso desayuno servido en la cama, con todo lo que amas para comenzar el día con energía. Perfecto para mimarte en una mañana especial.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;


                    case "Salir al cine":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Cine.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Salir al cine");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 50;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Cine.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(4);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Disfruta de una experiencia cinematográfica en la pantalla grande con entradas para la película que has estado esperando. Perfecto para una noche de diversión.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Paseo a la playa":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Playa.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Paseo a la playa");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 70;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Playa.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(4);
                           spinnerTipoPrem.setSelection(6);

                            txtMasInfo.setText("Un día completo en la playa, disfrutando del sol, el mar y la arena. Ideal para relajarte y desconectar de la rutina diaria.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día en el zoológico":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Zoo.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día en el zoológico");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Zoo.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(4);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un día emocionante observando a tus animales favoritos en el zoológico. Ideal para aprender y divertirte en un entorno familiar.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Visita al parque de diversiones":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Parque_Diversiones.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Visita al parque de diversiones");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 80;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Parque_Diversiones.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(4);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una aventura llena de emoción en un parque de diversiones. Disfruta de atracciones, juegos y deliciosas comidas con amigos o familiares.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de picnic":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Picnic.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de picnic");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 40;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Picnic.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(2);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un día al aire libre con un picnic delicioso en un parque. Disfruta de la naturaleza y la buena compañía mientras saboreas tus comidas favoritas.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de compras":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Compras.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de compras");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Compras.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(4);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un día emocionante de compras donde podrás adquirir lo que desees. Ideal para consentirte con un nuevo atuendo o gadgets.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Concierto de tu banda favorita":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Concierto.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Concierto de tu banda favorita");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Concierto.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(4);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una noche inolvidable disfrutando de un concierto de tu banda favorita en vivo. Comparte la experiencia con amigos y vibra al ritmo de la música.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Excursión al museo":
                        try {
                            
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Museo.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Excursión al museo");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Museo.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(2);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un emocionante día de exploración en un museo, aprendiendo sobre historia, arte o ciencia.");
                            

                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Viaje de fin de semana a una ciudad cercana":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Viaje_Ciudad.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Viaje de fin de semana a una ciudad cercana");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 400;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Viaje_Ciudad.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(3);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un viaje de fin de semana a una ciudad cercana para explorar nuevas culturas y gastronomía.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de spa en casa":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Spa.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de spa en casa");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 80;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Spa.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Dedica un día a consentirte con un spa en casa, incluyendo tratamientos relajantes.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Visita a una granja o reserva natural":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Granja.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Visita a una granja o reserva natural");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Granja.svg");

 spinnerCatPrem.setSelection(4); 
                            spinnerNivelPrem.setSelection(3);
                         spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Disfruta de un día en la naturaleza visitando una granja o reserva natural, aprendiendo sobre la vida rural.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Nueva tablet o e-reader":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Tablet.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Nueva tablet o e-reader");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 300;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Tablet.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un dispositivo portátil que te permitirá disfrutar de tus libros y aplicaciones favoritas en cualquier lugar. Ideal para leer, estudiar o entretenerte.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Aurices inalámbricos":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Aurices.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Aurices inalámbricos");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Aurices.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(1);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Aurices de alta calidad para disfrutar de tu música sin cables. Perfectos para hacer ejercicio o simplemente relajarte con tus canciones favoritas.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Juego de video nuevo":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Video_Nuevo.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Juego de video nuevo");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Video_Nuevo.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(10); 

                            txtMasInfo.setText("Un nuevo juego de video que te ofrecerá horas de diversión y desafíos. Ideal para compartir con amigos o disfrutar en solitario.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Accesorios para la consola de videojuegos":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Accesorios.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Accesorios para la consola de videojuegos");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 50;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Accesorios.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(1);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Accesorios para mejorar tu experiencia de juego, ya sea un control adicional, un soporte o un headset. Perfectos para los amantes de los videojuegos.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Cámara instantánea":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Camara.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Cámara instantánea");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Camara.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Una cámara que imprime fotos al instante, ideal para capturar y compartir momentos especiales. Perfecta para eventos y celebraciones.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Suscripción a un servicio de streaming":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Streaming.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Suscripción a un servicio de streaming");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 12;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Streaming.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(10); 

                            txtMasInfo.setText("Acceso a un mundo de series, películas y documentales al instante. Ideal para disfrutar de noches de cine en casa.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Reloj inteligente":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Reloj.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Reloj inteligente");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 250;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Reloj.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Un reloj que te ayuda a mantenerte conectado y activo, monitoreando tu salud y actividades diarias. Ideal para un estilo de vida moderno.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Laptop nueva":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Laptop.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Laptop nueva");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 700;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Laptop.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(2);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Una nueva laptop para realizar tus tareas diarias, estudiar o jugar. Perfecta para el trabajo y el entretenimiento.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Altavoz Bluetooth":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Altavoz.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Altavoz Bluetooth");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Altavoz.svg");

                            spinnerCatPrem.setSelection(5); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(10); 

                            txtMasInfo.setText("Un altavoz portátil que te permitirá disfrutar de tu música favorita en cualquier lugar. Perfecto para fiestas o días de campo.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Entradas para un espectáculo":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Entradas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Entradas para un espectáculo");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Entradas.svg");

                            spinnerCatPrem.setSelection(6);
                             spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Entradas para disfrutar de un emocionante espectáculo en vivo, ya sea teatro, comedia o danza. Perfecto para una noche de diversión.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Suscripción a una revista":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Revista.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Suscripción a una revista");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 10;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Revista.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(12); 

                            txtMasInfo.setText("Una suscripción a tu revista favorita, donde podrás aprender sobre temas de interés y disfrutar de contenido exclusivo.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Curso en línea de un tema de interés":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Curso_Linea.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Curso en línea de un tema de interés");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 50;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Curso_Linea.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(12); 

                            txtMasInfo.setText("Un curso en línea para aprender sobre un tema que te apasione. Ideal para mejorar tus habilidades y conocimientos desde la comodidad de tu hogar.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Pase anual a un parque temático":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Pase_Anual.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Pase anual a un parque temático");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 500;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Pase_Anual.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(4);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Un pase que te permitirá disfrutar de un año completo de diversión en un parque temático, con acceso a atracciones y eventos especiales.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Tarjeta de regalo para una tienda de entretenimiento":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Tarjeta.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Tarjeta de regalo para una tienda de entretenimiento");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Tarjeta.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una tarjeta de regalo para gastar en tu tienda de entretenimiento favorita. Perfecta para elegir lo que más te gusta.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de karaoke con amigos":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Karaoke.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de karaoke con amigos");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 30;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Karaoke.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Una divertida noche de karaoke con amigos, cantando tus canciones favoritas y disfrutando de risas y buena compañía.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Taller de arte o manualidades":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Manualidades.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Taller de arte o manualidades");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 40;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Manualidades.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(9); 

                            txtMasInfo.setText("Un taller práctico donde podrás aprender nuevas habilidades artísticas y crear tus propias obras de arte. Perfecto para expresar tu creatividad.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Clases de cocina":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Cocina.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Clases de cocina");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 120;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Cocina.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(12); 

                            txtMasInfo.setText("Aprende a preparar deliciosos platillos con clases de cocina que despierten tu creatividad culinaria.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Participación en una escape room":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/EscapeRoom.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Participación en una escape room");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("EscapeRoom.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Vive una emocionante experiencia de resolución de acertijos y trabajo en equipo en una escape room.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Festival de música":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Musica.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Festival de música");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 300;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Musica.svg");

                            spinnerCatPrem.setSelection(6);
                            spinnerNivelPrem.setSelection(4);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Disfruta de un festival de música con tus artistas favoritos y vive una experiencia inolvidable.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Equipo deportivo nuevo (ej. bicicleta, patines)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Equipo_Deportivo.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Equipo deportivo nuevo (ej. bicicleta, patines)");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 400;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Equipo_Deportivo.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(3);
                           spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Obtén un equipo deportivo nuevo, como una bicicleta o patines, para disfrutar de actividades al aire libre y mantenerte activo.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Entradas para un evento deportivo":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Entradas_Estadio.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Entradas para un evento deportivo");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Entradas_Estadio.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(4);

                            txtMasInfo.setText("Disfruta de la emoción de un evento deportivo en vivo con entradas para ver a tu equipo favorito.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Clase de deportes (ej. natación, fútbol)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Clases_Deporte.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Clase de deportes (ej. natación, fútbol)");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Clases_Deporte.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Participa en clases de deportes como natación o fútbol para mejorar tus habilidades y mantenerte activo.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Pase para un gimnasio":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Gym.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Pase para un gimnasio");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 80;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Gym.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Obtén un pase para un gimnasio y accede a instalaciones para entrenar y mantenerte en forma.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Ropa deportiva nueva":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Ropa_Deportiva.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Ropa deportiva nueva");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Ropa_Deportiva.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Consigue ropa deportiva nueva para que te sientas cómodo y con estilo mientras practicas tus deportes favoritos.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Accesorios para practicar un deporte (ej. balón, raqueta)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Accesorios_Deporte.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Accesorios para practicar un deporte (ej. balón, raqueta)");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 70;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Accesorios_Deporte.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Adquiere accesorios necesarios para practicar tu deporte favorito, como un balón o una raqueta.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Un día en un parque de aventuras":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Parque_Aventuras.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Un día en un parque de aventuras");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 300;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Parque_Aventuras.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(4);
                            spinnerTipoPrem.setSelection(4); 

                            txtMasInfo.setText("Disfruta de un día lleno de emoción en un parque de aventuras, donde podrás practicar diferentes deportes y actividades al aire libre.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de senderismo en la naturaleza":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Senderismo.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de senderismo en la naturaleza");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 90;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Senderismo.svg");

                            spinnerCatPrem.setSelection(7); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(4); 

                            txtMasInfo.setText("Participa en un día de senderismo y disfruta de la belleza de la naturaleza mientras te mantienes activo.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Herramientas para el aprendizaje (ej. calculadora, diccionario)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Calculadora.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Herramientas para el aprendizaje (ej. calculadora, diccionario)");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 30;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Calculadora.svg");

                            spinnerCatPrem.setSelection(8); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Consigue herramientas que facilitarán tu aprendizaje, como una calculadora o un diccionario.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Clases de música o arte":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Clases_Musica.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Clases de música o arte");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Clases_Musica.svg");

                            spinnerCatPrem.setSelection(8); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Participa en clases de música o arte para desarrollar tu creatividad y habilidades artísticas.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Visitas a bibliotecas o centros culturales":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Biblioteca_1.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Visitas a bibliotecas o centros culturales");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 20;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Biblioteca_1.svg");

                            spinnerCatPrem.setSelection(8); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(4); 

                            txtMasInfo.setText("Realiza visitas a bibliotecas o centros culturales para ampliar tus horizontes y aprender de nuevas experiencias.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Material de arte (pinturas, lápices, etc.)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Arte.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Material de arte (pinturas, lápices, etc.)");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Arte.svg");

                            spinnerCatPrem.setSelection(8); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(3);

                            txtMasInfo.setText("Consigue material de arte como pinturas y lápices para expresar tu creatividad y realizar tus obras.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de camping":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Camping.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de camping");

                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));
                            updateButtonImage("Camping.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(8); 

                            txtMasInfo.setText("Disfruta de un día al aire libre en la naturaleza, acampando y explorando el entorno.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Escalada en roca":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Escalada.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Escalada en roca");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 250;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Escalada.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(15);

                            txtMasInfo.setText("Experimenta la emoción de escalar en roca en un entorno seguro y desafiante.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Paseo en kayak o canoa":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Kayak.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Paseo en kayak o canoa");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 100;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Kayak.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(15);

                            txtMasInfo.setText("Disfruta de un paseo en kayak o canoa por ríos o lagos, rodeado de naturaleza.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Safari en un parque nacional":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Safari.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Safari en un parque nacional");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 600;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Safari.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(4);
                            spinnerTipoPrem.setSelection(15);

                            txtMasInfo.setText("Embárcate en un safari y observa la vida salvaje en su hábitat natural.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Exploración de cuevas":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Cuevas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Exploración de cuevas");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 180;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Cuevas.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(15);

                            txtMasInfo.setText("Explora cuevas misteriosas y descubre su belleza oculta en un tour guiado.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Día de rafting":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Rafting.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Día de rafting");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 220;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Rafting.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(10);

                            txtMasInfo.setText("Vive la aventura en un día de rafting, desafiando las corrientes de un río emocionante.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Tirolesa en un parque de aventura":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Tirolesa.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Tirolesa en un parque de aventura");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 120;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Tirolesa.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(10);

                            txtMasInfo.setText("Disfruta de la adrenalina mientras te deslizas por una tirolesa en un parque de aventura.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Un viaje en globo aerostático":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Globo_Aerostatico.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Un viaje en globo aerostático");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 800;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Globo_Aerostatico.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(4);
                            spinnerTipoPrem.setSelection(15);

                            txtMasInfo.setText("Vuela alto en un globo aerostático y disfruta de vistas impresionantes desde el aire.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Clases de supervivencia al aire libre":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Supervivencia.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Clases de supervivencia al aire libre");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 200;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Supervivencia.svg");

                            spinnerCatPrem.setSelection(9); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Aprende técnicas de supervivencia esenciales en un entorno natural.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Taller de fotografía":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Fotografia.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Taller de fotografía");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 120;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Fotografia.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Aprende a capturar momentos especiales en un taller de fotografía.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Clases de pintura o escultura":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/escultura.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Clases de pintura o escultura");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("escultura.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Desarrolla tus habilidades artísticas con clases de pintura o escultura.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Material de escritura (ej. cuadernos, plumas)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Escritura.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Material de escritura (ej. cuadernos, plumas)");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 15;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Escritura.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(3); 

                            txtMasInfo.setText("Consigue material de escritura que incluye cuadernos y plumas para tus ideas y pensamientos.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Accesorios para música (ej. guitarra, teclado)":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Guitarra.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Accesorios para música (ej. guitarra, teclado)");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 300;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Guitarra.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(3); 

                            txtMasInfo.setText("Adquiere accesorios para música, como una guitarra o un teclado, y expresa tu creatividad musical.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Curso de diseño gráfico":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Diseño_Grafico.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Curso de diseño gráfico");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 500;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Diseño_Grafico.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(4);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Aprende diseño gráfico y desarrolla tus habilidades creativas en un curso especializado.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Kit de jardinería":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Jardineria.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Kit de jardinería");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 70;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Jardineria.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(3); 

                            txtMasInfo.setText("Cultiva tus propias plantas y flores con un kit de jardinería que incluye todo lo necesario.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Participación en un club de arte":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Club.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Participación en un club de arte");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 30;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Club.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(1);
                            spinnerTipoPrem.setSelection(4); 

                            txtMasInfo.setText("Únete a un club de arte y comparte tus intereses creativos con otros entusiastas.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Materiales para hacer joyas":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Joyas.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Materiales para hacer joyas");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 60;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Joyas.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(2);
                            spinnerTipoPrem.setSelection(3); 

                            txtMasInfo.setText("Crea tus propias joyas con materiales diseñados específicamente para este propósito.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Experiencias de improvisación teatral":
                        try {
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            try (InputStream inputStream = getAssets().open("img/Iconos-recompensas/Teatral.svg")) {
                                SVG svg = SVG.getFromInputStream(inputStream);
                                if (svg != null) {
                                    Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                    icon.setImageDrawable(drawable);
                                }
                            } catch (IOException | SVGParseException e) {
                                e.printStackTrace();
                            }

                            
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Experiencias de improvisación teatral");

                            
                            Drawable background = layout.getBackground();
                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azulito_toast), PorterDuff.Mode.SRC_IN);

                            
                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            
                            int nuevoNumero = 150;
                            numRamitasPrem.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("Teatral.svg");

                            spinnerCatPrem.setSelection(10); 
                            spinnerNivelPrem.setSelection(3);
                            spinnerTipoPrem.setSelection(12);

                            txtMasInfo.setText("Participa en experiencias de improvisación teatral y mejora tus habilidades de actuación.");
                        } catch (Exception ex) {
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;


                    default:
                        break;
                }
            });

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



        
            btnAgregarImgPrem = findViewById(R.id.btnAgregarImgPrem);
            btnAgregarImgPrem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageSelectionDialog();
                }
            });


           
            txtMasInfo = findViewById(R.id.txtMasInfoPrem);
            
            
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


            btnCrearPremMandar = findViewById(R.id.btnCrearPremMandar);
           btnCrearPremMandar.setOnClickListener(this::crearPremio);

            return insets;
        });
    }

    private void showImageSelectionDialog() {
        BottomSheetDialog modal = new BottomSheetDialog(AgregarPremTutor.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_iconos_actis_view, null);
        modal.setContentView(view);

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setDraggable(false);

        GridView gridViewImages = view.findViewById(R.id.gridViewImages);

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

        ImageAdapter imageAdapter = new ImageAdapter(imageFiles, modal);
        gridViewImages.setAdapter(imageAdapter);

        gridViewImages.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedImage = imageFiles.get(position);

            updateButtonImage(selectedImage);
            modal.dismiss();
        });

        modal.show();
    }

    private void updateButtonImage(String imageName) {
        if (!imageName.endsWith(".svg")) {
            imageName += ".svg";
        }

        String imagePath = "img/Iconos-recompensas/" + imageName;
        imagenPremSelected = imagePath;

        try {
            InputStream inputStream = getAssets().open(imagePath);
            SVG svg = SVG.getFromInputStream(inputStream);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            btnAgregarImgPrem.setImageDrawable(drawable);
        } catch (IOException | SVGParseException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir del formio, si das click en aceptar saldrás y no se guardará la información ingresada.");
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

            

            
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

         
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Ingrese un número mayor a 1");

           
            Drawable background = layout.getBackground();
            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.rojito_toast), PorterDuff.Mode.SRC_IN);

            
            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

           

            
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            numRamitasPrem.setText("");
        }
    }


    private void mostrarModalCerrarView(String titulo, String mensaje) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_cerrar_view_confirm);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView txtTitle = dialog.findViewById(R.id.txtDialogTitle);
        TextView txtMessage = dialog.findViewById(R.id.txtDialogMessage);
        Button btnClose = dialog.findViewById(R.id.btnCerrarModal);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

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

        dialog.show();
    }

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
                InputStream is = getAssets().open(imagePath);

                SVG svg = SVG.getFromInputStream(is);
                Drawable drawable = new PictureDrawable(svg.renderToPicture());

                imageView.setImageDrawable(drawable);

                is.close();
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
            }


            imageView.setOnClickListener(v -> {
                String selectedImage = images.get(position);
                Log.d("IMAGE_SELECT", "Imagen seleccionada: " + selectedImage);
                String nuevaRuta = selectedImage.replace(".svg", "");
                Log.d("IMAGE_SELECT", "Ruta de la imagen sin extensión: " + nuevaRuta);
                updateButtonImage(nuevaRuta);
                modal.dismiss();
            });

            return imageView;
        }
    }

    private void crearPremio(View view) {
        Log.d("API_LOG", "🟢 Iniciando método crearPremio...");


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

        if (nombrePremio.isEmpty() || numeroRamitas.isEmpty() || imagenPremSelected.isEmpty() || masInfo.isEmpty()
                || tipo.contains("- Selecciona un tipo de premio -") || categoria.contains("- Selecciona una categoria de premio -") || nivel.contains("- Selecciona un nivel de premio -")) {

            mostrarToastPersonalizado("Favor de completar todos los campos antes de crear", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
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

        verificarPremioExistente(nombrePremio);

    }

    private void verificarPremioExistente(String nombrePremio) {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAllPremios().enqueue(new Callback<List<Premios>>() {
            @Override
            public void onResponse(Call<List<Premios>> call, Response<List<Premios>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean existe = false;
                    for (Premios premio : response.body()) {
                        if (premio.getNombrePremio().equalsIgnoreCase(nombrePremio)) {
                            existe = true;
                            break;
                        }
                    }

                    if (existe) {
                        mostrarToastPersonalizado("Ya existe un premio con ese nombre", R.drawable.img_circ_tache_rojo, R.color.rojito_toast);
                    } else {
                        continuarCreacionPremio();
                    }
                } else {
                    Toast.makeText(AgregarPremTutor.this, "Error al obtener la lista de premios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Premios>> call, Throwable t) {
                Toast.makeText(AgregarPremTutor.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void continuarCreacionPremio() {
        SharedPreferences preferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);
        if (idKit == 0) {
            Toast.makeText(this, "Error: No se pudo obtener el idKit", Toast.LENGTH_SHORT).show();
            return;
        }

        obtenerIdCastorYCrearPremio(idKit, nombrePremInput.getText().toString().trim(), spinnerTipoPrem.getSelectedItem().toString(),
                spinnerCatPrem.getSelectedItem().toString(), spinnerNivelPrem.getSelectedItem().toString(),
                Integer.parseInt(numRamitasPrem.getText().toString().trim()), txtMasInfo.getText().toString().trim(), imagenPremSelected);
    }

    private void obtenerIdCastorYCrearPremio(int idKit, String nombrePremio, String tipo, String categoria, String nivel,
                                             int numRamitasPrem, String masInfo, String imagenRuta) {
        Log.d("API_LOG", "🟢 Iniciando método obtenerIdCastorYCrearPremio...");
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAllCastores().enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AgregarPremTutor.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                String emailGuardado = preferences.getString("email", "");

                final int[] idCastor = {-1};

                for (Castor castor : response.body()) {
                    if (castor.getEmail().equals(emailGuardado)) {
                        idCastor[0] = castor.getIdCastor();
                        break;
                    }
                }

                if (idCastor[0] == -1) {
                    Toast.makeText(AgregarPremTutor.this, "No se encontró el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                Premios premio = new Premios();
                premio.setIdCastor(idCastor[0]);
                premio.setNombrePremio(nombrePremio);
                premio.setTipoPremio(tipo);
                premio.setCategoriaPremio(categoria);
                premio.setNivelPremio(nivel);
                premio.setCostoPremio(numRamitasPrem);
                premio.setInfoExtraPremio(masInfo);
                premio.setEstadoPremio(0);
                premio.setFavorito(0);

                try {
                    int index = imagenRuta.lastIndexOf("/");
                    String nombreImg = imagenRuta.substring(index + 1);
                    String rutaFinal = "../img/Iconos-recompensas/" + nombreImg;
                    Log.d("Imagen","Ruta imagen:"+ rutaFinal);
                    premio.setRutaImagenHabito(rutaFinal);
                } catch (Exception e) {
                    Log.e("CHECK", "❌ Error al establecer ruta de imagen: " + e.getMessage());
                }

                ApiService apiService = RetrofitClient.getApiService();

                apiService.createPremio(premio).enqueue(new Callback<Premios>() {
                    @Override
                    public void onResponse(Call<Premios> call, Response<Premios> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("API_LOG", "Respuesta exitosa, cuerpo: " + response.body().toString());
                            int idPremio = response.body().getIdPremio();
                            Log.d("API_LOG", "Premio creado exitosamente con idPremio: " + idPremio);
                            RelPrem relPrem = new RelPrem();
                            relPrem.setIdKit(idKit);
                            relPrem.setIdCastor(idCastor[0]);
                            relPrem.setIdPremio(idPremio);
                            Log.d("API_LOG", "Enviando relación: idKit=" + idKit + ", idCastor=" + idCastor[0] + ", idPremio=" + idPremio);

                            ApiService apiService = RetrofitClient.getApiService();
                            apiService.createRelPrem(relPrem).enqueue(new Callback<RelPrem>() {
                                @Override
                                public void onResponse(Call<RelPrem> call, Response<RelPrem> response) {
                                    if (response.isSuccessful()) {
                                        mostrarToastExitoYRedirigir();
                                    } else {
                                        Toast.makeText(AgregarPremTutor.this, "Error al crear la relación en relPrem", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<RelPrem> call, Throwable t) {
                                    Log.e("API_LOG", "Error en la conexión con el servidor al crear la relación: " + t.getMessage());
                                    Toast.makeText(AgregarPremTutor.this, "Error en la conexión con el servidor al crear la relación", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.e("API_LOG", "Error al crear el premio, código de estado: " + response.code());
                            Log.e("API_LOG", "Cuerpo de la respuesta de error: " + response.errorBody());
                            Toast.makeText(AgregarPremTutor.this, "Error al crear el premio", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Premios> call, Throwable t) {
                        Toast.makeText(AgregarPremTutor.this, "Error en la conexión con el servidor al crear el premio", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(AgregarPremTutor.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("fragmenPremCrear", "RecompensasFragmentTutor1");
        startActivity(intent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("spinnerTipoPrem", spinnerTipoPrem.getSelectedItemPosition());
        outState.putInt("spinnerCatPrem", spinnerCatPrem.getSelectedItemPosition());
        outState.putInt("spinnerNivelPrem", spinnerNivelPrem.getSelectedItemPosition());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            int tipoPremPosition = savedInstanceState.getInt("spinnerTipoPrem");
            int catPremPosition = savedInstanceState.getInt("spinnerCatPrem");
            int nivelPremPosition = savedInstanceState.getInt("spinnerNivelPrem");

            spinnerTipoPrem.setSelection(tipoPremPosition);
            spinnerCatPrem.setSelection(catPremPosition);
            spinnerNivelPrem.setSelection(nivelPremPosition);
        }
    }



}


