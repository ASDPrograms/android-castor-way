package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.icu.util.Calendar;
import android.media.Image;
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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Color;


import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AgregarActiTutor extends AppCompatActivity {
    TextView txtFechaInicial, txtFechaFinal, txtOptTipFechaSelected, contCaractNameHabit, txtTitNuevoHabit, txtHoraInicial, txtHoraFinal, numRamitas, contCaractInfoExtr, txtMasInfo;
    AutoCompleteTextView nombreHabitInput;
    CheckBox radioBtnCadaDia, semanaDia1, semanaDia2, semanaDia3, semanaDia4, semanaDia5, semanaDia6, semanaDia7,
            diaCalendar1, diaCalendar2, diaCalendar3, diaCalendar4, diaCalendar5, diaCalendar6, diaCalendar7,
            diaCalendar8, diaCalendar9, diaCalendar10, diaCalendar11, diaCalendar12, diaCalendar13, diaCalendar14,
            diaCalendar15, diaCalendar16, diaCalendar17, diaCalendar18, diaCalendar19, diaCalendar20, diaCalendar21,
            diaCalendar22, diaCalendar23, diaCalendar24, diaCalendar25, diaCalendar26, diaCalendar27, diaCalendar28,
            diaCalendar29, diaCalendar30, diaCalendar31;
    Spinner spinnerTiposHabit;
    List<String> selectedCheckBoxNames = new ArrayList<>();
    List<Integer> selectedWeekdays = new ArrayList<>();
    LinearLayout linLayChangeOptFechas, linearLayDiasSemana, linearLayIntervalos, linearLayDiasMes, linLayCeckBxCadaDia, bottomSheetLayout, btnCambiarIconoColor;
    ImageView btnChangeTipoInterFechas, btnSalirAddActi, btnAgregarImgActi, circleColorElegidoIcon;
    RadioButton intervalo2, intervalo3, intervalo4, intervalo5, intervalo6, intervalo7;
    RadioGroup radioGroupIntervalosDias;
    Button btnCrearActiMandar;
    private int estElegirOptFechas = 0;
    private final int limCharNombreHabit = 35;
    private final int limCharInfoExtr = 250;
    private String fechasSeleccionadas = "";
    // Variable para almacenar la hora inicial
    private Calendar selectedStartTime = null;
    private Calendar selectedEndTime = null;

    private ArrayList<String> imageFiles;
    private GridView gridViewImages;
    private String imagenActiSelected = "";
    private int colorSeleccionado = Color.BLACK;
    private int selectedPosition = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_acti_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //Se declara el btn que permite salir del activity de nuevo hábito:
            btnSalirAddActi = findViewById(R.id.btnSalirAddActi);
            btnSalirAddActi.setOnClickListener(v1 -> mostrarModalCerrarView("¡Atención!", "Si das click en aceptar saldrás del formulario y no se guardará la información ingresada."));

            //Se declara el título para poder cambiarle el color
            txtTitNuevoHabit = findViewById(R.id.txtTitNuevoHabit);
            String textoCompleto = "Nuevo Hábito";

            SpannableString spannableString = new SpannableString(textoCompleto);

            int start = textoCompleto.indexOf("Hábito");
            int end = start + "Hábito".length();

            int colorHabit = ContextCompat.getColor(this, R.color.aguita);

            spannableString.setSpan(new ForegroundColorSpan(colorHabit), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtTitNuevoHabit.setText(spannableString);

            spinnerTiposHabit = findViewById(R.id.spinnerTipHabits);

            ArrayAdapter<CharSequence> adapterTip = ArrayAdapter.createFromResource(
                    this,
                    R.array.opciones,
                    R.layout.dropdown_actis_nombre
            );
            spinnerTiposHabit.setPopupBackgroundResource(R.drawable.input_border);
            adapterTip.setDropDownViewResource(R.layout.dropdown_actis_nombre);
            spinnerTiposHabit.setAdapter(adapterTip);

            txtFechaInicial = findViewById(R.id.txtFechaInicial);
            txtFechaFinal = findViewById(R.id.txtFechaFinal);

            //Función para mostrar la fecha en el textview
            txtFechaInicial.setOnClickListener(this::mostrarDatePicker);

            String[] opciones = {"Comer frutas y verduras", "Beber agua",
                    "Hacer ejercicio", "Mantener higiene personal",
                    "Dormir lo necesario", "Elegir snacks saludables",
                    "Seguir una rutina de higiene", "Preparar tu desayuno",
                    "Cuidar tu piel", "Organizar tu Mochila", "Hacer la tarea",
                    "Seguir una rutina de estudio", "Limpiar espacio personal",
                    "Llevar diario personal en CastorWay", "Cuidar tus Pertenencias",
                    "Elegir tu Ropa", "Practicar un Hobby", "Leer un Libro",
                    "Ser Responsable con los Animales", "Invitar Amigos a Jugar",
                    "Compartir Juguetes", "Jugar en Juegos de Grupo", "Dar Gracias",
                    "Ahorrar Dinero", "Hacer Compras Pequeñas", "Hacer un Registro de Gastos",
                    "Identificar Tus Sentimientos", "Usar Técnicas de Relajación",
                    "Escuchar Música Relajante"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.dropdown_actis_nombre,
                    R.id.tvDropdownItem,
                    opciones
            );

            nombreHabitInput = findViewById(R.id.nombreHabitInput);
            nombreHabitInput.setDropDownBackgroundResource(R.drawable.input_border);

            //Se agrega un listener a cambio del texto para el contador de caractéres
            //y se declara el contador de caractéres
            contCaractNameHabit = findViewById(R.id.contCaractNameHabit);
            nombreHabitInput.addTextChangedListener(new TextWatcher() {
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

            nombreHabitInput.setAdapter(adapter);

            //el evento lo que hace es al presionar el input de nombre de hábito se despliega la lista
            nombreHabitInput.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    nombreHabitInput.showDropDown();
                }
            });

            nombreHabitInput.setOnClickListener(view -> nombreHabitInput.showDropDown());


            nombreHabitInput.setOnItemClickListener((parent, view, position, id) -> {
                String seleccion = (String) parent.getItemAtPosition(position);

                // Llenar datos del formulario según la opción seleccionada
                switch (seleccion) {
                    case "Comer frutas y verduras":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Comer frutas y verduras");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-pear-verde.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.verde_actis);

                            txtMasInfo.setText("Las frutas y verduras \uD83C\uDF4E\uD83E\uDD66 te ayudan a crecer fuerte y con mucha energía. Comerlas cada día es un regalo \uD83C\uDF81 para tu cuerpo, ¡sigue así! \uD83D\uDCAA");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Beber agua":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Beber agua");

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
                            int nuevoNumero = 20;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bx-water-azul.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.azul_actis);

                            txtMasInfo.setText("Tomar agua \uD83D\uDEB0 te mantiene fresco(a) ❄\uFE0F y con fuerzas para jugar \uD83C\uDF88, aprender \uD83D\uDCDA y hacer todo lo que te gusta. ¡Un vasito más y sigues adelante! \uD83D\uDC99");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Hacer ejercicio":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Hacer ejercicio");

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
                            int nuevoNumero = 50;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bx-dumbbell-rojo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);

                            txtMasInfo.setText("Moverse es genial para el cuerpo y la mente! \uD83C\uDFC3\u200D♀\uFE0F\uD83C\uDFB6 Saltar, correr o bailar te hará sentir con más energía ⚡ y alegría \uD83D\uDE0A. ¡Disfrútalo! \uD83C\uDF89");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Mantener higiene personal":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Mantener higiene personal");

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
                            int nuevoNumero = 50;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-face-azul.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.azul_actis);

                            txtMasInfo.setText("Cuidar tu higiene \uD83D\uDEC0 es una forma de quererte \uD83D\uDC96. Una ducha \uD83D\uDEBF, las manos limpias ✋\uD83E\uDDFC y dientes cepillados \uD83E\uDDB7 te hacen sentir bien y listo(a) para cualquier aventura! \uD83C\uDF0D");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Dormir lo necesario":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Dormir lo necesario");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-hotel-morado.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.morado_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.morado_actis);

                            txtMasInfo.setText("Dormir bien \uD83D\uDECC te ayuda a despertar \uD83C\uDF05 con energía ⚡ y listo(a) para un gran día. Descansa \uD83D\uDE0C, relájate \uD83C\uDF19 y deja que tus sueños ✨ te lleven a lugares maravillosos.");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Elegir snacks saludables":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Elegir snacks saludables");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-cookie-amarillo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.amarillo_actis);

                            txtMasInfo.setText("Elegir algo saludable \uD83C\uDF4E\uD83E\uDD5C para comer es una decisión muy inteligente \uD83E\uDDE0. Te ayuda a crecer \uD83D\uDCC8, a sentirte bien \uD83D\uDE0A y a seguir disfrutando el día \uD83C\uDF1E.");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Seguir una rutina de higiene":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Seguir una rutina de higiene");

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
                            int nuevoNumero = 50;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-book-heart-rojo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);

                            txtMasInfo.setText("Tener una rutina para cuidar tu higiene \uD83D\uDEC1\uD83E\uDDB7 hace que cada día empiece y termine de la mejor manera \uD83C\uDF1F. Es un pequeño hábito que hace una gran diferencia! \uD83D\uDCAB");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Preparar tu desayuno":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Preparar tu desayuno");

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
                            int nuevoNumero = 35;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-coffee-morado.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.morado_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.morado_actis);

                            txtMasInfo.setText("Comenzar el día con un desayuno hecho por ti \uD83E\uDD63\uD83E\uDD6A es un gran paso hacia la independencia \uD83D\uDCAA. Además, ¡sabe aún más rico cuando lo preparas con cariño! \uD83D\uDE0A✨");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Cuidar tu piel":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Cuidar tu piel");

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
                            int nuevoNumero = 25;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-face-verde.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.verde_actis);

                            txtMasInfo.setText("Tu piel es única y merece ser tratada con amor \uD83D\uDC96. Con solo unos minutos al día, puedes mantenerla fresca y saludable \uD83C\uDF1F. ¡Tu bienestar comienza con un pequeño gesto de cuidado! \uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Organizar tu Mochila":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Organizar tu Mochila");

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
                            int nuevoNumero = 25;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-magic-wand-azul.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.azul_actis);

                            txtMasInfo.setText("Mantener tu mochila ordenada te ayudará a sentirte más tranquilo y preparado para el día \uD83D\uDDC2\uFE0F. Es un pequeño hábito que hace una gran diferencia para que siempre tengas todo a la mano \uD83C\uDF1F. ¡Un espacio ordenado, mente ordenada! \uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Hacer la tarea":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Hacer la tarea");

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
                            int nuevoNumero = 65;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-pencil-amarillo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.amarillo_actis);

                            txtMasInfo.setText("Cada tarea que haces \uD83D\uDCD6✏\uFE0F te ayuda a aprender cosas nuevas \uD83E\uDDE0 y a mejorar cada día. ¡Eres increíble! \uD83C\uDF1F");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Seguir una rutina de estudio":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Seguir una rutina de estudio");

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
                            int nuevoNumero = 45;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-calendar-edit-rojo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);

                            txtMasInfo.setText("Establecer una rutina de estudio te ayudará a avanzar con confianza \uD83C\uDFC6. Cada día un paso más cerca de tus metas \uD83D\uDCC5. ¡Recuerda que los pequeños esfuerzos de hoy se convierten en grandes logros mañana! \uD83C\uDF1F");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Limpiar espacio personal":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Limpiar espacio personal");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-spa-verde.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.verde_actis);

                            txtMasInfo.setText("Un espacio limpio te da claridad y tranquilidad \uD83E\uDDD8\u200D♀\uFE0F. Al ordenar tu entorno, también ordenas tus pensamientos y te preparas para un día productivo ✨. ¡Cada rincón cuenta! \uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Llevar diario personal en CastorWay":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Llevar diario personal en CastorWay");

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
                            int nuevoNumero = 35;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-food-menu-morado.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.morado_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.morado_actis);

                            txtMasInfo.setText("Escribir en tu diario es una forma genial de expresar tus pensamientos y sentimientos ✍\uFE0F. Cada día, un nuevo capítulo de tu historia, ¡y lo más bonito es que tú eres el autor! \uD83C\uDF1F\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Cuidar tus Pertenencias":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Cuidar tus Pertenenciass");

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
                            int nuevoNumero = 45;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-mobile.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.black);

                            txtMasInfo.setText("Tus cosas son valiosas y cuidarlas te ayuda a mantenerlas en buen estado por más tiempo \uD83C\uDF1F. Cada vez que te ocupas de ellas, estás mostrando responsabilidad y amor propio \uD83D\uDC96. ¡Esas pequeñas acciones marcan una gran diferencia! \uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Elegir tu Ropa":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Elegir tu Ropa");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-t-shirt-amarillo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.amarillo_actis);

                            txtMasInfo.setText("Escoger tu ropa con cariño te ayuda a sentirte cómodo y seguro en tu día \uD83C\uDF1F. ¡Tu estilo es una forma de expresarte, así que diviértete con él y siéntete genial! \uD83D\uDC96✨");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Practicar un Hobby":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Practicar un Hobby");

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
                            int nuevoNumero = 25;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-tennis-ball-verde.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.verde_actis);

                            txtMasInfo.setText("Dedicar tiempo a lo que te gusta te hace más feliz y relajado \uD83C\uDF08. ¡Es tu momento para desconectar, explorar nuevas ideas y disfrutar de lo que más te apasiona! \uD83D\uDE0A✨");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Leer un Libro":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Leer un Libro");

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
                            int nuevoNumero = 70;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-book-heart-azul.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.azul_actis);

                            txtMasInfo.setText("Sumergirse en un buen libro es una aventura maravillosa \uD83C\uDF1F. Cada página te lleva a un nuevo mundo, te hace soñar y aprender cosas nuevas. ¡Disfruta de cada historia que encuentres! \uD83D\uDCD6✨");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Ser Responsable con los Animales":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Ser Responsable con los Animales");

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
                            int nuevoNumero = 50;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-cat-amarillo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.amarillo_actis);

                            txtMasInfo.setText("Cuidar de los animales es un acto de amor y respeto \uD83D\uDC36\uD83D\uDC96. Ellos dependen de ti, y al darles lo mejor, también aprendes sobre empatía y responsabilidad. ¡Tu cariño y cuidado hacen su mundo mejor! \uD83C\uDF1F\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Invitar Amigos a Jugar":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Invitar Amigos a Jugar");

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
                            int nuevoNumero = 40;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-joystick-rojo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);

                            txtMasInfo.setText("\uD83C\uDFAE Compartir momentos divertidos con tus amigos fortalece los lazos y crea recuerdos increíbles \uD83C\uDF1F. ¡Invitarlos a jugar es una forma genial de disfrutar juntos y aprender a trabajar en equipo! \uD83D\uDE0A\uD83C\uDF89");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Compartir Juguetes":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Compartir Juguetes");

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
                            int nuevoNumero = 20;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-bot-azul.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.azul_actis);

                            txtMasInfo.setText("\uD83C\uDFAE Compartir momentos divertidos con tus amigos fortalece los lazos y crea recuerdos increíbles \uD83C\uDF1F. ¡Invitarlos a jugar es una forma genial de disfrutar juntos y aprender a trabajar en equipo! \uD83D\uDE0A\uD83C\uDF89");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Jugar en Juegos de Grupo":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Participar en Juegos de Grupo");

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
                            int nuevoNumero = 20;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-dice-3.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.black);

                            txtMasInfo.setText("Jugar en grupo es una excelente manera de divertirse, hacer nuevos amigos y aprender a colaborar \uD83E\uDD1D. ¡Cada partida es una oportunidad para disfrutar, reír y hacer equipo! \uD83C\uDF89\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Dar Gracias":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Dar Gracias");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-user-voice.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.black);

                            txtMasInfo.setText("Ser agradecido es una forma hermosa de mostrar aprecio por todo lo bueno que te rodea \uD83C\uDF38. Un simple 'gracias' puede alegrar el día de alguien y hacer que el mundo sea más amable \uD83D\uDC96. ¡Hazlo siempre, y verás la magia! ✨\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Ahorrar Dinero":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Ahorrar Dinero");

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
                            int nuevoNumero = 60;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-coin-amarillo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.amarillo_actis);

                            txtMasInfo.setText("Ahorrar es una forma de cuidar tu futuro y ser más responsable con lo que tienes \uD83D\uDCA1. Cada pequeño esfuerzo suma, y con el tiempo, ¡verás cómo crece tu ahorro! \uD83C\uDF31✨ ¡Es una gran habilidad para aprender! \uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Hacer Compras Pequeñas":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Hacer Compras Pequeñas");

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
                            int nuevoNumero = 40;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-cart-alt-verde.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.verde_actis);

                            txtMasInfo.setText("Las compras pequeñas te enseñan a ser más consciente de lo que necesitas y lo que es importante \uD83E\uDDE0\uD83D\uDCA1. ¡Con cada compra, estás aprendiendo a manejar tu dinero de manera sabia y responsable! \uD83C\uDF1F\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Hacer un Registro de Gastos":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Hacer un Registro de Gastos");

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
                            int nuevoNumero = 50;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-calculator-rojo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);

                            txtMasInfo.setText("Registrar tus gastos te ayuda a entender mejor cómo administras tu dinero \uD83D\uDCA1. Es una excelente forma de ser más organizado y responsable, ¡y te ayudará a tomar decisiones más inteligentes! \uD83D\uDCB0✨");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Identificar Tus Sentimientos":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Identificar Tus Sentimientos");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-heart-rojo.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);

                            txtMasInfo.setText("Entender cómo te sientes es un paso importante para cuidar de ti mismo \uD83D\uDC96. Al conocer tus emociones, puedes manejarlas mejor y tomar decisiones más saludables para tu bienestar. ¡Escúchate siempre! \uD83C\uDF1F\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Usar Técnicas de Relajación":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Usar Técnicas de Relajación");

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
                            int nuevoNumero = 30;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-yin-yang.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.black);

                            txtMasInfo.setText("Tomarte un momento para relajarte es vital para cuidar de tu cuerpo y mente \uD83E\uDDE0\uD83D\uDC96. Las técnicas de relajación te ayudan a sentirte más tranquilo y equilibrado. ¡Hazlo con calma y verás lo bien que te hace! \uD83C\uDF1F\uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione

                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Escuchar Música Relajante":
                        try {
                            //Inicio del código para mostrar el toast personalizado
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                            //Inicio de código para cambiar elementos del toast personalizado

                            //Se cambia la imágen
                            ImageView icon = layout.findViewById(R.id.toast_icon);
                            icon.setImageResource(R.drawable.img_tachuela);

                            //Se cambia el texto
                            TextView text = layout.findViewById(R.id.toast_text);
                            text.setText("Se seleccionó Escuchar Música Relajante");

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
                            int nuevoNumero = 40;
                            numRamitas.setText(String.valueOf(nuevoNumero));

                            updateButtonImage("bxs-music-morado.svg");
                            circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.morado_actis), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.morado_actis);

                            txtMasInfo.setText("La música tiene el poder de calmar tu mente y ayudarte a desconectar \uD83C\uDF38. Cuando te sientas agobiado, poner una melodía tranquila puede ser la clave para sentirte mejor y recargar energías \uD83D\uDC96✨. ¡Hazlo con amor por ti mismo! \uD83D\uDE0A");
                            //Fin del código que cambia los valores dependiendo de lo que seleccione
                        }catch (Exception ex){
                            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            });
            //Fin lógica de mostrar la fecha en el textview


            //Color del checkbox pa personalizarlo cuando se selecciona:
            radioBtnCadaDia = findViewById(R.id.radioBtnCadaDia);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked}, // Cuando está seleccionado
                            new int[]{-android.R.attr.state_checked} // Cuando no está seleccionado
                    },
                    new int[]{
                            Color.parseColor("#566C9F"),
                            Color.parseColor("#808080")
                    }
            );
            radioBtnCadaDia.setButtonTintList(colorStateList);

            //Se declaran los chec de elegir periodos por semana, y se agregan
            //las funciones de lo que pasa al dar click en cada elemento,
            //se hagan los cálculos para que se cambie el valor de la fecha final de acuerdo
            //a la fecha incial y los periodos seleccionados
            semanaDia1 = findViewById(R.id.semanaDia1);
            semanaDia2 = findViewById(R.id.semanaDia2);
            semanaDia3 = findViewById(R.id.semanaDia3);
            semanaDia4 = findViewById(R.id.semanaDia4);
            semanaDia5 = findViewById(R.id.semanaDia5);
            semanaDia6 = findViewById(R.id.semanaDia6);
            semanaDia7 = findViewById(R.id.semanaDia7);

            //Se agrega que a cada checkbox un onclicklistener para
            //cambiar el valor de la fecha inicial y final al momento de
            //seleccionar un intervalo

            CheckBox[] allCheckBoxes = {semanaDia1, semanaDia2, semanaDia3, semanaDia4, semanaDia5, semanaDia6, semanaDia7};
            radioBtnCadaDia.setOnClickListener(v1 -> {
                // Verificar el estado del radio button
                boolean selected = radioBtnCadaDia.isChecked();
                // Alternar el estado: si ya estaba seleccionado, deseleccionarlo; de lo contrario, seleccionarlo.
                radioBtnCadaDia.setChecked(selected);

                toggleAllCheckBoxes(allCheckBoxes, selected);
            });

            semanaDia1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia1, txtFechaInicial, allCheckBoxes);
            });
            semanaDia2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia2, txtFechaInicial, allCheckBoxes);
            });
            semanaDia3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia3, txtFechaInicial, allCheckBoxes);
            });
            semanaDia4.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia4, txtFechaInicial, allCheckBoxes);
            });
            semanaDia5.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia5, txtFechaInicial, allCheckBoxes);
            });
            semanaDia6.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia6, txtFechaInicial, allCheckBoxes);
            });
            semanaDia7.setOnCheckedChangeListener((buttonView, isChecked) -> {
                handleCheckboxSelection(semanaDia7, txtFechaInicial, allCheckBoxes);
            });

            //Se declaran los LinearLayout que se mostrarán dependiendo del elegido:
            linearLayDiasSemana = findViewById(R.id.linearLayDiasSemana);
            linearLayIntervalos = findViewById(R.id.linearLayIntervalos);
            linearLayDiasMes = findViewById(R.id.linearLayDiasMes);

            //Este es el lay del checkbox que se muestra solo si está en días de semana:
            linLayCeckBxCadaDia = findViewById(R.id.linLayCeckBxCadaDia);

            //Se declara el texto que va a ir cambiando dependiendo del que esté seleccionado:
            txtOptTipFechaSelected = findViewById(R.id.txtOptTipFechaSelected);

            //Se declara el imageview que cambia entre opciones:
            //Semanal, intervalos, Mensual
            btnChangeTipoInterFechas = findViewById(R.id.btnChangeTipoInterFechas);
            mostrarLinLayFechas(estElegirOptFechas);


            //Aquí se declaran las opciones de fechas por intervalos de 2, 3, 4 ...
            intervalo2 = findViewById(R.id.intervalo2);
            intervalo3 = findViewById(R.id.intervalo3);
            intervalo4 = findViewById(R.id.intervalo4);
            intervalo5 = findViewById(R.id.intervalo5);
            intervalo6 = findViewById(R.id.intervalo6);
            intervalo7 = findViewById(R.id.intervalo7);

            RadioButton[] allRadioButtonInterval = {intervalo2, intervalo3, intervalo4, intervalo5, intervalo6, intervalo7};


            //Se declara el radioGroup que contiene a los radioButton:
            radioGroupIntervalosDias = findViewById(R.id.radioGroupIntervalosDias);

            radioGroupIntervalosDias.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    elegirIntervaloXDias(txtFechaInicial, allRadioButtonInterval);
                }
            });


            //para ir cambiando entre las opciones de intervalos disponibles


            //Se declaran los 31 diaCalendar
            diaCalendar1 = findViewById(R.id.diaCalendar1);
            diaCalendar2 = findViewById(R.id.diaCalendar2);
            diaCalendar3 = findViewById(R.id.diaCalendar3);
            diaCalendar4 = findViewById(R.id.diaCalendar4);
            diaCalendar5 = findViewById(R.id.diaCalendar5);
            diaCalendar6 = findViewById(R.id.diaCalendar6);
            diaCalendar7 = findViewById(R.id.diaCalendar7);
            diaCalendar8 = findViewById(R.id.diaCalendar8);
            diaCalendar9 = findViewById(R.id.diaCalendar9);
            diaCalendar10 = findViewById(R.id.diaCalendar10);
            diaCalendar11 = findViewById(R.id.diaCalendar11);
            diaCalendar12 = findViewById(R.id.diaCalendar12);
            diaCalendar13 = findViewById(R.id.diaCalendar13);
            diaCalendar14 = findViewById(R.id.diaCalendar14);
            diaCalendar15 = findViewById(R.id.diaCalendar15);
            diaCalendar16 = findViewById(R.id.diaCalendar16);
            diaCalendar17 = findViewById(R.id.diaCalendar17);
            diaCalendar18 = findViewById(R.id.diaCalendar18);
            diaCalendar19 = findViewById(R.id.diaCalendar19);
            diaCalendar20 = findViewById(R.id.diaCalendar20);
            diaCalendar21 = findViewById(R.id.diaCalendar21);
            diaCalendar22 = findViewById(R.id.diaCalendar22);
            diaCalendar23 = findViewById(R.id.diaCalendar23);
            diaCalendar24 = findViewById(R.id.diaCalendar24);
            diaCalendar25 = findViewById(R.id.diaCalendar25);
            diaCalendar26 = findViewById(R.id.diaCalendar26);
            diaCalendar27 = findViewById(R.id.diaCalendar27);
            diaCalendar28 = findViewById(R.id.diaCalendar28);
            diaCalendar29 = findViewById(R.id.diaCalendar29);
            diaCalendar30 = findViewById(R.id.diaCalendar30);
            diaCalendar31 = findViewById(R.id.diaCalendar31);

            //Lista de los checkboxes
            CheckBox[] allCheckBoxsDiaCalendar = {
                    diaCalendar1, diaCalendar2, diaCalendar3, diaCalendar4, diaCalendar5, diaCalendar6,
                    diaCalendar7, diaCalendar8, diaCalendar9, diaCalendar10, diaCalendar11, diaCalendar12,
                    diaCalendar13, diaCalendar14, diaCalendar15, diaCalendar16, diaCalendar17, diaCalendar18,
                    diaCalendar19, diaCalendar20, diaCalendar21, diaCalendar22, diaCalendar23, diaCalendar24,
                    diaCalendar25, diaCalendar26, diaCalendar27, diaCalendar28, diaCalendar29, diaCalendar30,
                    diaCalendar31
            };

            //Se recorre la lista y se agrega un listener a cada elemento:
            for (CheckBox checkBox : allCheckBoxsDiaCalendar) {
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        elegirDiasCalendar(txtFechaInicial, allCheckBoxsDiaCalendar);
                    }
                });
            }

            btnChangeTipoInterFechas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Se deseleccionan todos los elementos de los grupos de checkboxes y radiobuttons de
                    //las opciones de intervalos de fechas disponibles

                    for (CheckBox cb : allCheckBoxes) {
                        cb.setChecked(false);
                    }
                    radioGroupIntervalosDias.clearCheck();
                    for(CheckBox chbDC : allCheckBoxsDiaCalendar){
                        chbDC.setChecked(false);
                    }

                    radioBtnCadaDia.setChecked(false);

                    // Se incrementa el estado de forma ciclica
                    // llegando hasta 2 y luego regresando a 0
                    estElegirOptFechas = (estElegirOptFechas + 1) % 3;
                    mostrarLinLayFechas(estElegirOptFechas);
                }
            });


            //Se declara el Textview de hora inicial y final:
            txtHoraInicial = findViewById(R.id.txtHoraInicial);
            txtHoraFinal = findViewById(R.id.txtHoraFinal);

            txtHoraInicial.setOnClickListener(this::openTimePickerFechaInicial);
            txtHoraFinal.setOnClickListener(this::openTimePickerFechaFinal);

            //Se declara variable para el número de ramitas:
            numRamitas = findViewById(R.id.numRamitas);
            numRamitas.addTextChangedListener(new TextWatcher() {
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


            //Se declara el linearlayout como botón para cambiar el color:
            btnCambiarIconoColor = findViewById(R.id.btnCambiarIconoColor);
            btnCambiarIconoColor.setOnClickListener(this::cambiarColorIconActi);


            //Botón que se encarga de abrir el modal de las imágenes y el círculo que cambia de color
            btnAgregarImgActi = findViewById(R.id.btnAgregarImgActi);
            circleColorElegidoIcon = findViewById(R.id.circleColorElegidoIcon);
            btnAgregarImgActi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImageSelectionDialog();
                }
            });


            //Declaración del input de info extra del modal:
            txtMasInfo = findViewById(R.id.txtMasInfo);
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


            //Declaración del botón que crea la actividad:
            btnCrearActiMandar = findViewById(R.id.btnCrearActiMandar);
            btnCrearActiMandar.setOnClickListener(this::crearActividad);

            return insets;
        });
    }
    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir del formulario, si das click en aceptar saldrás y no se guardará la información ingresada.");
    }

    private void mostrarDatePicker(View view) {
        // Obtener la fecha actual del dispositivo
        Calendar calendar = Calendar.getInstance(); // Usando la zona horaria y el Locale del dispositivo por defecto
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Establecer la fecha mínima que será la fecha actual (reseteando la hora)
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0); // Asegurarse de que los milisegundos sean 0
        long todayInMillis = calendar.getTimeInMillis();

        // Crear el modal para mostrar las fechas disponibles en el calendario
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccione una fecha")
                .setTheme(R.style.CustomMaterialDatePickerTheme)
                .setSelection(todayInMillis) // Preseleccionar la fecha actual
                .setCalendarConstraints(
                        new CalendarConstraints.Builder()
                                .setStart(todayInMillis) // Configurar el límite inferior para hoy
                                .build()
                )
                .build();

        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Se ajusta la fecha seleccionada
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.setTimeInMillis(selection);

            selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
            selectedCalendar.set(Calendar.MINUTE, 0);
            selectedCalendar.set(Calendar.SECOND, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);

            selectedCalendar.add(Calendar.DAY_OF_MONTH, 1);

            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);

            // Se verifica si la fecha seleccionada es anterior a la actual
            if (selectedCalendar.before(todayCalendar)) {

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado, null);

                //Inicio de código para cambiar elementos del toast personalizado

                //Se cambia la imágen
                ImageView icon = layout.findViewById(R.id.toast_icon);
                icon.setImageResource(R.drawable.img_circ_tache_rojo);

                //Se cambia el texto
                TextView text = layout.findViewById(R.id.toast_text);
                text.setText("No se puede seleccionar una fecha anterior a hoy");

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
            } else {
                // Se formatea la fecha correctamente (sin influir en la hora)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = sdf.format(selectedCalendar.getTime());

                // Se pone la fecha en el TextView
                txtFechaInicial.setText(formattedDate);

                CheckBox[] allCheckBoxsDiaCalendar = {
                        diaCalendar1, diaCalendar2, diaCalendar3, diaCalendar4, diaCalendar5, diaCalendar6,
                        diaCalendar7, diaCalendar8, diaCalendar9, diaCalendar10, diaCalendar11, diaCalendar12,
                        diaCalendar13, diaCalendar14, diaCalendar15, diaCalendar16, diaCalendar17, diaCalendar18,
                        diaCalendar19, diaCalendar20, diaCalendar21, diaCalendar22, diaCalendar23, diaCalendar24,
                        diaCalendar25, diaCalendar26, diaCalendar27, diaCalendar28, diaCalendar29, diaCalendar30,
                        diaCalendar31
                };
                boolean verifiSelectedDiasMes = hayCheckBoxSelectedDiasMes(allCheckBoxsDiaCalendar);

                if(semanaDia1.isChecked() || semanaDia2.isChecked() || semanaDia3.isChecked() || semanaDia4.isChecked() || semanaDia5.isChecked() || semanaDia6.isChecked() || semanaDia7.isChecked()){
                    updateFinalDates();
                }else if(intervalo2.isChecked() || intervalo3.isChecked() || intervalo4.isChecked() || intervalo5.isChecked() || intervalo6.isChecked() || intervalo7.isChecked()){
                    RadioButton[] allRadioButtonInterval = {intervalo2, intervalo3, intervalo4, intervalo5, intervalo6, intervalo7};
                    for (RadioButton rb : allRadioButtonInterval) {
                        if (rb.isChecked()) {
                            elegirIntervaloXDias(txtFechaInicial, allRadioButtonInterval);
                            break;
                        }
                    }
                }else if(verifiSelectedDiasMes){

                    elegirDiasCalendar(txtFechaInicial, allCheckBoxsDiaCalendar);
                }
            }
        });
    }


    // Función para establecer el estado de todos los CheckBox
    private void toggleAllCheckBoxes(CheckBox[] checkBoxes, boolean isChecked) {
        for (CheckBox cb : checkBoxes) {
            cb.setChecked(isChecked);
        }
        if(!isChecked){
            txtFechaFinal.setText(" - ");
        }
    }
    // Función que se encarga de generar las fechas que corresponden al intervalo
// de acuerdo a la fecha inicial y los días seleccionados
    private String generateSelectedDates(String startDateStr, List<Integer> selectedWeekdays) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // Si el string no es "Seleccione una fecha", intenta parsearlo; si falla o es ese valor, se usa la fecha actual.
        if (!startDateStr.equals("Seleccione una fecha")) {
            try {
                Date startDate = sdf.parse(startDateStr);
                cal.setTime(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
                // Se queda con la fecha actual
            }
        }


        // Crear una lista para almacenar las fechas que se generarán
        List<String> dates = new ArrayList<>();

        // Mientras no tengamos al menos 21 fechas, seguir generando
        while (dates.size() < 21) {
            // Revisar si el día de la semana actual está en los seleccionados
            int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // Día de la semana actual

            if (selectedWeekdays.contains(currentDayOfWeek)) {
                // Si el día de la semana es uno de los seleccionados, lo agregamos a la lista
                dates.add(sdf.format(cal.getTime()));
            }

            // Avanzar al siguiente día
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }


        // Unir las fechas en una cadena separada por comas y devolverla
        fechasSeleccionadas = String.join(", ", dates.subList(0, 21));
        return String.join(", ", dates.subList(0, 21)); // Asegurarse de solo devolver 21 fechas
    }


    // Función para actualizar la fecha final en función de la fecha inicial y los días seleccionados.
    // Función para actualizar la fecha final
    private void updateFinalDates() {
        String startDateStr = txtFechaInicial.getText().toString();
        if (startDateStr.isEmpty() || selectedWeekdays.isEmpty()) {
            return; //
        }else{
            String resultDates = generateSelectedDates(startDateStr, selectedWeekdays);
            String[] partes = resultDates.split(",");
            String ultFecha = partes[partes.length - 1];  // Tomamos la última fecha
            Log.e("DEBUG", resultDates);
            txtFechaFinal.setText(ultFecha);  // Actualizamos el TextView con la última fecha
        }
    }
    private void handleCheckboxSelection(CheckBox checkBox, TextView fechaTextView, CheckBox[] allCheckBoxes) {
        updateFinalDates();

        String name = getResources().getResourceEntryName(checkBox.getId());

        if (checkBox.isChecked()) {
            // Agregar el día de la semana a selectedWeekdays
            int dayOfWeek = getDayOfWeekFromCheckbox(checkBox);
            if (!selectedWeekdays.contains(dayOfWeek)) {
                selectedWeekdays.add(dayOfWeek);
            }

            if (fechaTextView.getText().toString().equals("Seleccione una fecha")) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = sdf.format(calendar.getTime());
                fechaTextView.setText(formattedDate);
            }
        } else {
            // Eliminar el día de la semana de selectedWeekdays
            int dayOfWeek = getDayOfWeekFromCheckbox(checkBox);
            selectedWeekdays.remove(Integer.valueOf(dayOfWeek));

            boolean allUnchecked = true;
            for (CheckBox cb : allCheckBoxes) {
                if (cb.isChecked()) {
                    allUnchecked = false;
                    break;
                }
            }
            if (allUnchecked) {
                fechaTextView.setText("Seleccione una fecha");
                txtFechaFinal.setText("-");
            }
        }

        updateFinalDates();
    }

    private int getDayOfWeekFromCheckbox(CheckBox checkBox) {
        // Retorna el día de la semana correspondiente al CheckBox para
        //en una función abarcar todos los elementos
        if (checkBox.getId() == R.id.semanaDia1) {
            return Calendar.MONDAY;
        } else if (checkBox.getId() == R.id.semanaDia2) {
            return Calendar.TUESDAY;
        } else if (checkBox.getId() == R.id.semanaDia3) {
            return Calendar.WEDNESDAY;
        } else if (checkBox.getId() == R.id.semanaDia4) {
            return Calendar.THURSDAY;
        } else if (checkBox.getId() == R.id.semanaDia5) {
            return Calendar.FRIDAY;
        } else if (checkBox.getId() == R.id.semanaDia6) {
            return Calendar.SATURDAY;
        } else if (checkBox.getId() == R.id.semanaDia7) {
            return Calendar.SUNDAY;
        } else {
            return -1;
        }
    }
    private void mostrarLinLayFechas(int estado) {
        switch (estado) {
            case 0:
                // Estado 0: muestra linearLayout1, oculta 2 y 3
                linearLayDiasSemana.setVisibility(View.VISIBLE);
                linearLayIntervalos.setVisibility(View.GONE);
                linearLayDiasMes.setVisibility(View.GONE);
                txtOptTipFechaSelected.setText("Semanal");
                linLayCeckBxCadaDia.setVisibility(View.VISIBLE);
                break;
            case 1:
                // Estado 1: muestra linearLayout2, oculta 1 y 3
                linearLayDiasSemana.setVisibility(View.GONE);
                linearLayIntervalos.setVisibility(View.VISIBLE);
                linearLayDiasMes.setVisibility(View.GONE);
                txtOptTipFechaSelected.setText("Intervalos cada ... días");
                linLayCeckBxCadaDia.setVisibility(View.GONE);
                break;
            case 2:
                // Estado 2: muestra linearLayout3, oculta 1 y 2
                linearLayDiasSemana.setVisibility(View.GONE);
                linearLayIntervalos.setVisibility(View.GONE);
                linearLayDiasMes.setVisibility(View.VISIBLE);
                txtOptTipFechaSelected.setText("Mensual");
                linLayCeckBxCadaDia.setVisibility(View.GONE);
                break;
        }
    }
    private void elegirIntervaloXDias(TextView fechaTextView, RadioButton[] allRadioButtonInterval){
        for (RadioButton rb : allRadioButtonInterval) {
            if (rb.isChecked()) {
                int idSeleccionado = rb.getId();
                String nameRadio = getResources().getResourceEntryName(idSeleccionado);
                String formattedDate = "";

                if (fechaTextView.getText().toString().equals("Seleccione una fecha")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    formattedDate = sdf.format(calendar.getTime());
                    fechaTextView.setText(formattedDate);
                }else{
                    formattedDate = fechaTextView.getText().toString();
                }

                if(nameRadio.equals("intervalo2")){
                    recorrerFechasXIntervalDias(formattedDate, 2);
                }
                else if(nameRadio.equals("intervalo3")){
                    recorrerFechasXIntervalDias(formattedDate, 3);
                }
                else if(nameRadio.equals("intervalo4")){
                    recorrerFechasXIntervalDias(formattedDate, 4);
                }
                else if(nameRadio.equals("intervalo5")){
                    recorrerFechasXIntervalDias(formattedDate, 5);
                }
                else if(nameRadio.equals("intervalo6")){
                    recorrerFechasXIntervalDias(formattedDate, 6);
                }
                else if(nameRadio.equals("intervalo7")){
                    recorrerFechasXIntervalDias(formattedDate, 7);
                }
                break;
            }else{
                fechaTextView.setText("Seleccione una fecha");
                txtFechaFinal.setText("-");
            }
        }
    }

    private String recorrerFechasXIntervalDias(String fechaInicial, int intervalo){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        List<String> listaFechas = new ArrayList<>();

        try {
            // Aquí se parsea a calendar la fecha inicial que le pasamos en la función
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(fechaInicial));

            // Se generan las 21 fechas de acuerdo al intervalo que le pasamos
            for (int i = 0; i < 21; i++) {
                listaFechas.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, intervalo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error en el formato de la fecha inicial";
        }

        fechasSeleccionadas = String.join(", ", listaFechas);
        Log.e("DEBUG", "fechasSeleccionadasInter" + fechasSeleccionadas);

        String[] partes = fechasSeleccionadas.split(",");
        String ultFecha = partes[partes.length - 1];  // Tomamos la última fecha
        //Se establece el valor de la fecha final en el input de fecha final
        txtFechaFinal.setText(ultFecha);
        return String.join(", ", listaFechas);
    }

    //Función para el cambio de chekboxes seleccionados para los días del calendario
    private void elegirDiasCalendar(TextView fechaTextView, CheckBox[] allCheckBox){
        //Se van a ir guardando los números de los checkboxes seleccionados
        List<Integer> listaSeleccionados = new ArrayList<>();
        String formattedDate = "";

        boolean verifiSelected = false;
        for(CheckBox chb : allCheckBox){
            if(chb.isChecked()){
                verifiSelected = true;
                String textoCheckBox = chb.getText().toString();
                try {
                    int numero = Integer.parseInt(textoCheckBox);
                    listaSeleccionados.add(numero);
                } catch (NumberFormatException e) {
                    Log.e("Error", "El texto no es un número válido: " + textoCheckBox);
                }
                if (fechaTextView.getText().toString().equals("Seleccione una fecha")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    formattedDate = sdf.format(calendar.getTime());
                    fechaTextView.setText(formattedDate);
                }else{
                    formattedDate = fechaTextView.getText().toString();
                }
            }
        }

        if(verifiSelected){
            recorrerFechasDiasSeleccionados(formattedDate, listaSeleccionados);
        }else{
            fechaTextView.setText("Seleccione una fecha");
            txtFechaFinal.setText("-");
        }
    }


    private String recorrerFechasDiasSeleccionados(String formattedDate, List<Integer> listaSeleccionados) {
        List<String> fechas = new ArrayList<>();

        try {
            // Convertir la fecha inicial (formattedDate) a un objeto Calendar
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(formattedDate));

            // Lista para almacenar las fechas que coincidan con los días seleccionados

            // Contador de fechas recorridas
            int contador = 0;

            while (contador < 21) {
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                // Verificar si el día del mes está en la lista de días seleccionados
                if (listaSeleccionados.contains(dia)) {
                    // Si es uno de los días seleccionados, agregar la fecha a la lista
                    fechas.add(sdf.format(calendar.getTime()));
                    contador++;
                }

                // Avanzar al siguiente día
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        String fechasRecorridas = String.join(", ", fechas);

        fechasSeleccionadas = fechasRecorridas;
        Log.e("DEBUG", "fechasSeleccionadasInter" + fechasSeleccionadas);

        String[] partes = fechasSeleccionadas.split(",");
        String ultFecha = partes[partes.length - 1];  // Tomamos la última fecha
        //Se establece el valor de la fecha final en el input de fecha final
        txtFechaFinal.setText(ultFecha);

        return fechasRecorridas;
    }
    private boolean hayCheckBoxSelectedDiasMes(CheckBox[] checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void openTimePickerFechaInicial(View view){
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // Formato de 12 horas con AM/PM
                .setHour(7)
                .setMinute(0)
                .setTitleText("Seleccione una hora")
                .setTheme(R.style.CustomTimePickerTheme) // Aplica el tema personalizado
                .build();


        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = (hour >= 12) ? "PM" : "AM";
            int formattedHour = (hour == 0 || hour == 12) ? 12 : hour % 12;

            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm);

            selectedStartTime = Calendar.getInstance();
            selectedStartTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedStartTime.set(Calendar.MINUTE, minute);

            // Si ya existe una fecha final seleccionada, validamos si la fecha inicial es al menos 15 minutos antes
            if (selectedEndTime != null) {
                long differenceInMillis = selectedEndTime.getTimeInMillis() - selectedStartTime.getTimeInMillis();
                if (differenceInMillis < 15 * 60 * 1000) {
                    // Si la diferencia es menor a 15 minutos, mostramos un mensaje de error
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_personalizado, null);

                    //Inicio de código para cambiar elementos del toast personalizado

                    //Se cambia la imágen
                    ImageView icon = layout.findViewById(R.id.toast_icon);
                    icon.setImageResource(R.drawable.img_circ_tache_rojo);

                    //Se cambia el texto
                    TextView text = layout.findViewById(R.id.toast_text);
                    text.setText("La hora final debe ser 15 minutos después de la inicial");

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
                    toast.show();                    return; // Detenemos el proceso
                }
            }

            // Asegúrate de que txtHoraInicial está inicializado
            if (txtHoraInicial != null) {
                txtHoraInicial.setText(selectedTime);
            }
        });

        // Muestra el diálogo con el fragment manager correcto
        timePicker.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), "timePickerFechaInicial");
    }


    private void openTimePickerFechaFinal(View view) {
        if (selectedStartTime == null) {
            // Si no se ha seleccionado la hora inicial, muestra un mensaje y no permite seleccionar la final
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            //Inicio de código para cambiar elementos del toast personalizado

            //Se cambia la imágen
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

            //Se cambia el texto
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Por favor, selecciona primero la hora inicial");

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

            return;
        }

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // Formato de 12 horas con AM/PM
                .setHour(7)
                .setMinute(0)
                .setTitleText("Seleccione una hora")
                .setTheme(R.style.CustomTimePickerTheme) // Aplica el tema personalizado
                .build();


        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = (hour >= 12) ? "PM" : "AM";
            int formattedHour = (hour == 0 || hour == 12) ? 12 : hour % 12;

            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm);

            selectedEndTime = Calendar.getInstance();
            selectedEndTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedEndTime.set(Calendar.MINUTE, minute);

            // Compara la hora final con la hora inicial usando Calendar
            Calendar selectedEndTime = Calendar.getInstance();
            selectedEndTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedEndTime.set(Calendar.MINUTE, minute);

            // Verifica si la hora final es al menos 15 minutos después de la hora inicial
            long differenceInMillis = selectedEndTime.getTimeInMillis() - selectedStartTime.getTimeInMillis();
            if (differenceInMillis < 15 * 60 * 1000) {

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado, null);

                //Inicio de código para cambiar elementos del toast personalizado

                //Se cambia la imágen
                ImageView icon = layout.findViewById(R.id.toast_icon);
                icon.setImageResource(R.drawable.img_circ_tache_rojo);

                //Se cambia el texto
                TextView text = layout.findViewById(R.id.toast_text);
                text.setText("La hora final debe ser al menos 15 minutos después de la hora inicial");

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
                return;
            }

            // Asegúrate de que txtHoraFinal está inicializado
            if (txtHoraFinal != null) {
                txtHoraFinal.setText(selectedTime);
            }
        });

        // Muestra el diálogo con el fragment manager correcto
        timePicker.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), "timePickerFechaFinal");
    }
    private void validNumRamitas(){
        int valueNumRamitas = -1;
        try {
            valueNumRamitas = Integer.parseInt(numRamitas.getText().toString());
        }catch (Exception ex){
        }
        if (!(valueNumRamitas >= 1 && valueNumRamitas <= 100) && valueNumRamitas != -1){

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            //Inicio de código para cambiar elementos del toast personalizado

            //Se cambia la imágen
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

            //Se cambia el texto
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Ingrese un número entre 1 y 100");

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
            numRamitas.setText("");
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
    private void showImageSelectionDialog() {
        // Crear el BottomSheetDialog
        BottomSheetDialog modal = new BottomSheetDialog(AgregarActiTutor.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_iconos_actis_view, null);
        modal.setContentView(view);
        //Se infla el elemento pa poder poner las imagenes
        GridView gridViewImages = view.findViewById(R.id.gridViewImages);

        // Configurar las imágenes en el GridView
        ArrayList<String> imageFiles = new ArrayList<>();
        try {
            String[] files = getAssets().list("img/img_actividades");
            if (files != null) {
                for (String file : files) {
                    if (!file.contains("rojo") && !file.contains("azul") && !file.contains("morado")
                            && !file.contains("verde") && !file.contains("amarillo") &&!file.contains("icono_selector_iconos")) {
                        imageFiles.add(file);
                    }
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

    private void updateButtonImage(String imageName) {
        String imagePath = "img/img_actividades/" + imageName;
        imagenActiSelected = imagePath;
        Log.d("IMAGEN", "Imagen seleccionada: " + imagenActiSelected);

        try {
            InputStream inputStream = getAssets().open(imagePath);
            SVG svg = SVG.getFromInputStream(inputStream);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());

            // Asigna la imagen al botón que abre el modal
            btnAgregarImgActi.setImageDrawable(drawable);
        } catch (IOException | SVGParseException e) {
            e.printStackTrace();
        }
    }

    public void cambiarColorIconActi(View view){
        Log.d("IMAGEN", "PA EL COLOR: " + imagenActiSelected);
        if(!imagenActiSelected.equals("")){

            final Dialog dialog = new Dialog(this);

            // Infla el layout del modal
            View modalView = LayoutInflater.from(this).inflate(R.layout.modal_colores_acti, null);
            dialog.setContentView(modalView);

            // Se jalan los ImageViews del modal
            ImageView colorRojo = modalView.findViewById(R.id.colorRojo);
            ImageView colorAmarillo = modalView.findViewById(R.id.coloAmarillo);
            ImageView colorAzul = modalView.findViewById(R.id.colorAzul);
            ImageView colorVerde = modalView.findViewById(R.id.colorVerde);
            ImageView colorMorado = modalView.findViewById(R.id.colorMorado);
            ImageView colorNegro = modalView.findViewById(R.id.colorNegro);

            // Establece los OnClickListener para cada ImageView
            colorRojo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.rojo_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "rojo");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorAmarillo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);
                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.amarillo_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "amarillo");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorAzul.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.rojo_actis);
                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.azul_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "azul");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorVerde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.verde_actis);

                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "verde");

                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorNegro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.black);
                    String nuevaImg = imagenActiSelected.replaceAll("-(rojo|amarillo|azul|verde|morado)", "");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorMorado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.morado_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(AgregarActiTutor.this, R.color.morado_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "morado");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });
            dialog.show();

        }else{
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            //Inicio de código para cambiar elementos del toast personalizado

            //Se cambia la imágen
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

            //Se cambia el texto
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Primero se debe elegir una imagen.");

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
        }
    }

    // Método para reemplazar el sufijo de color si existe
    private String replaceColorSuffix(String imageName, String color) {
        // Se elimina cualquier sufijo de color existente
        String baseName = imageName.replace(".svg", "");
        baseName = baseName.replaceAll("-(rojo|amarillo|azul|verde|morado)", "");

        // Añadir el nuevo sufijo
        return baseName + "-" + color + ".svg";
    }

    private void loadImageFromAssets(String imageName) {
        imagenActiSelected = imageName;
        Log.d("IMAGEN", "SE RECIBE: " + imageName);
        try {
            // Carga el archivo SVG desde los assets
            SVG svg = SVG.getFromAsset(getAssets(), imageName);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            // Asigna el Drawable al ImageView
            btnAgregarImgActi.setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crearActividad(View view) {
        // Validaciones iniciales
        String nombreHabito = nombreHabitInput.getText().toString().trim();
        String selectedText = spinnerTiposHabit.getSelectedItem().toString();
        String fechaInicial = txtFechaInicial.getText().toString().trim();
        String fechaFinal = txtFechaFinal.getText().toString().trim();
        String horaInicial = txtHoraInicial.getText().toString().trim();
        String horaFinal = txtHoraFinal.getText().toString().trim();
        String numeroRamitas = numRamitas.getText().toString().trim();
        String masInfo = txtMasInfo.getText().toString().trim();

        if (nombreHabito.isEmpty() || fechaInicial.isEmpty() || fechaInicial.contains("Seleccione")
                || fechaFinal.isEmpty() || fechaFinal.equals("-") || horaInicial.isEmpty()
                || horaInicial.contains("Seleccione") || horaFinal.isEmpty()
                || horaFinal.contains("Seleccione") || numeroRamitas.isEmpty()
                || imagenActiSelected.isEmpty() || masInfo.isEmpty() || selectedText.equalsIgnoreCase("- Selecciona un tipo de hábito -")) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            //Inicio de código para cambiar elementos del toast personalizado

            //Se cambia la imágen
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

            //Se cambia el texto
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Favor de completar todos los campos antes de crear");

            //Se cambia el color de fondo
            Drawable background = layout.getBackground();
            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.rojito_toast), PorterDuff.Mode.SRC_IN);

            // Cambia color del texto
            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            //Fin del código que se encarga de cambiar los elementos del toast personalizado
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return;
        }

        int numRam;
        try {
            numRam = Integer.parseInt(numeroRamitas);
            if (numRam < 1 || numRam > 100) {

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado, null);

                //Inicio de código para cambiar elementos del toast personalizado

                //Se cambia la imágen
                ImageView icon = layout.findViewById(R.id.toast_icon);
                icon.setImageResource(R.drawable.img_circ_tache_rojo);

                //Se cambia el texto
                TextView text = layout.findViewById(R.id.toast_text);
                text.setText("Ingrese un número de ramitas entre 1 y 100");

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
                return;
            }
        } catch (NumberFormatException ex) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            //Inicio de código para cambiar elementos del toast personalizado

            //Se cambia la imágen
            ImageView icon = layout.findViewById(R.id.toast_icon);
            icon.setImageResource(R.drawable.img_circ_tache_rojo);

            //Se cambia el texto
            TextView text = layout.findViewById(R.id.toast_text);
            text.setText("Número de ramitas inválido");

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

            return;
        }

        // Obtener idKit de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
        int idKit = preferences.getInt("idKit", 0);
        if (idKit == 0) {
            Toast.makeText(this, "Error: No se pudo obtener el idKit", Toast.LENGTH_SHORT).show();
            return;
        }

        // Primera llamada: Obtener actividades existentes
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllActividades().enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AgregarActiTutor.this, "Error al obtener actividades", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean hayTraslape = false;
                SimpleDateFormat formato12H = new SimpleDateFormat("hh:mm a", Locale.US);
                SimpleDateFormat formato24H = new SimpleDateFormat("HH:mm", Locale.US);
                Date inicioNueva = null, finNueva = null;
                String horaInicioText = "", horaFinText = "";
                try {
                    // Primero convierte las horas a formato de 24 horas
                    inicioNueva = formato24H.parse(formato24H.format(formato12H.parse(horaInicial)));
                    finNueva = formato24H.parse(formato24H.format(formato12H.parse(horaFinal)));

                    horaInicioText = formato24H.format(inicioNueva);
                    horaFinText = formato24H.format(finNueva);

                    Log.d("HORAS", "Hora inicio Nueva: " + inicioNueva);
                    Log.d("HORAS", "Hora fin Nueva: " + finNueva);


                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "Error al convertir las horas", e);
                }


                for (Actividad actividad : response.body()) {
                    if (actividad.getIdKit() == idKit) {
                        for (String fecha : actividad.getFechasActividad().split(",")) {
                            if (Arrays.asList(fechasSeleccionadas.split(",")).contains(fecha.trim())) {
                                try {
                                    Date inicioBD = formato24H.parse(actividad.getHoraInicioHabito());
                                    Date finBD = formato24H.parse(actividad.getHoraFinHabito());

                                    if ((inicioNueva.before(finBD) && inicioNueva.after(inicioBD)) ||
                                            (finNueva.after(inicioBD) && finNueva.before(finBD)) ||
                                            (inicioNueva.equals(inicioBD) || finNueva.equals(finBD)) ||
                                            (inicioNueva.before(inicioBD) && finNueva.after(finBD))) {
                                        hayTraslape = true;
                                        break;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if (hayTraslape) {
                    runOnUiThread(() -> {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_personalizado, null);

                        //Inicio de código para cambiar elementos del toast personalizado

                        //Se cambia la imágen
                        ImageView icon = layout.findViewById(R.id.toast_icon);
                        icon.setImageResource(R.drawable.img_circ_tache_rojo);

                        //Se cambia el texto
                        TextView text = layout.findViewById(R.id.toast_text);
                        text.setText("Las horas se traslapan con otra actividad");

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
                    });
                } else {
                    obtenerIdCastorYCrearActividad(idKit, nombreHabito, fechaInicial, fechaFinal, horaInicioText, horaFinText, numRam, masInfo);
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.e("API_ERROR", "Error al obtener actividades: " + t.getMessage());
                Toast.makeText(AgregarActiTutor.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método separado para obtener el ID del Castor y luego crear la actividad
    private void obtenerIdCastorYCrearActividad(int idKit, String nombreHabito, String fechaInicial, String fechaFinal, String horaInicial, String horaFinal, int numRamitas, String masInfo) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllCastores().enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AgregarActiTutor.this, "Error al obtener el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                String emailGuardado = preferences.getString("email", "");
                int idCastor = -1;

                for (Castor castor : response.body()) {
                    if (castor.getEmail().equals(emailGuardado)) {
                        idCastor = castor.getIdCastor();
                        break;
                    }
                }

                if (idCastor == -1) {
                    Toast.makeText(AgregarActiTutor.this, "No se encontró el ID del Castor", Toast.LENGTH_SHORT).show();
                    return;
                }

                String jalarIntervalos = jalarIntervalos();

                Log.e("Actisss", "idKit: " + idKit);
                Log.e("Actisss", "idCastor: " + idCastor);
                Log.e("Actisss", "nombreHabito: " + nombreHabito);
                Log.e("Actisss", "fechaInicial: " + fechaInicial);
                Log.e("Actisss", "fechaFinal: " + fechaFinal);
                Log.e("Actisss", "horaInicial: " + horaInicial);
                Log.e("Actisss", "horaFinal: " + horaFinal);
                Log.e("Actisss", "numRamitas: " + numRamitas);
                Log.e("Actisss", "masInfo: " + masInfo);
                Log.e("Actisss", "fechasSeleccionadas: " + fechasSeleccionadas);
                Log.e("Actisss", "jalarIntervalos: " + jalarIntervalos);

                Actividad actividad = new Actividad();
                actividad.setIdKit(idKit);
                actividad.setIdCastor(idCastor);
                actividad.setNombreHabito(nombreHabito);
                actividad.setDiaInicioHabito(fechaInicial);
                actividad.setDiaMetaHabito(fechaFinal);
                actividad.setHoraInicioHabito(horaInicial);
                actividad.setHoraFinHabito(horaFinal);
                actividad.setNumRamitas(numRamitas);
                actividad.setInfoExtraHabito(masInfo);
                actividad.setFechasActividad(fechasSeleccionadas);
                actividad.setRepeticiones(jalarIntervalos);
                actividad.setTipoHabito(spinnerTiposHabit.getSelectedItem().toString());

                String colorH = String.format("#%06X", (0xFFFFFF & colorSeleccionado));
                Log.e("Actisss", "colorH: " + colorH);

                String codColorMandar = "";
                if(colorH.equalsIgnoreCase("#ff595e")){
                    codColorMandar = "rojo";
                }else if(colorH.equalsIgnoreCase("#ffca3a")){
                    codColorMandar = "amarillo";
                }else if(colorH.equalsIgnoreCase("#8ac926")){
                    codColorMandar = "verde";
                }else if(colorH.equalsIgnoreCase("#1982c4")){
                    codColorMandar = "azul";
                }else if(colorH.equalsIgnoreCase("#6a4c93")){
                    codColorMandar = "morado";
                }else if(colorH.equalsIgnoreCase("#FF0000")){
                    codColorMandar = "negro";
                }
                Log.e("Actisss", "codColorMandar: " + codColorMandar);


                actividad.setColor(codColorMandar);

                int index = imagenActiSelected.lastIndexOf("/");
                String nombreImg = imagenActiSelected.substring(index + 1);

                actividad.setRutaImagenHabito("../img/iconos_formularios/" + nombreImg);

                apiService.createActividad(actividad).enqueue(new Callback<Actividad>() {
                    @Override
                    public void onResponse(Call<Actividad> call, Response<Actividad> response) {

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_personalizado, null);

                        ImageView icon = layout.findViewById(R.id.toast_icon);
                        icon.setImageResource(R.drawable.img_circ_palomita_verde);

                        TextView text = layout.findViewById(R.id.toast_text);
                        text.setText("Actividad creada con éxito");

                        Drawable background = layout.getBackground();
                        background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.verdecito_toast), PorterDuff.Mode.SRC_IN);

                        text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                        Intent intent = new Intent(AgregarActiTutor.this, HomeTutor.class);
                        intent.putExtra("fragmentActiCrear", "ActividadesFragmentTutor");
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Actividad> call, Throwable t) {
                        Log.e("API_ERROR", "Error en la creación de actividad: " + t.getMessage());
                        Toast.makeText(AgregarActiTutor.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Log.e("API_ERROR", "Error al obtener el ID del Castor: " + t.getMessage());
            }
        });
    }

    private String jalarIntervalos(){
        List<String> intervalosSeleccionados = new ArrayList<>();

        boolean verifiCheckBoxesDiasSemana = false;
        CheckBox[] allCheckBoxes = {semanaDia1, semanaDia2, semanaDia3, semanaDia4, semanaDia5, semanaDia6, semanaDia7};
        for (CheckBox checkBox : allCheckBoxes) {
            if (checkBox.isChecked()) {
                verifiCheckBoxesDiasSemana = true;
                if(checkBox.getText().toString().equals("L")){
                    intervalosSeleccionados.add("Lunes");
                }else if(checkBox.getText().toString().equals("M")){
                    intervalosSeleccionados.add("Martes");
                }else if(checkBox.getText().toString().equals("W")){
                    intervalosSeleccionados.add("Miércoles");
                }else if(checkBox.getText().toString().equals("J")){
                    intervalosSeleccionados.add("Jueves");
                }else if(checkBox.getText().toString().equals("V")){
                    intervalosSeleccionados.add("Viernes");
                }else if(checkBox.getText().toString().equals("S")){
                    intervalosSeleccionados.add("Sábado");
                }else if(checkBox.getText().toString().equals("D")){
                    intervalosSeleccionados.add("Domingo");
                }
            }
        }

        boolean verifiRadioButtonInterval = false;
        RadioButton[] allRadioButtonInterval = {intervalo2, intervalo3, intervalo4, intervalo5, intervalo6, intervalo7};
        String selectedIntervalo = "";
        for (RadioButton radioButton : allRadioButtonInterval) {
            if (radioButton.isChecked()) {
                verifiRadioButtonInterval = true;
                if(radioButton.getText().toString().equals("2")){
                    intervalosSeleccionados.add("Repetir cada 2");
                }
                else if(radioButton.getText().toString().equals("3")){
                    intervalosSeleccionados.add("Repetir cada 3");
                }
                else if(radioButton.getText().toString().equals("4")){
                    intervalosSeleccionados.add("Repetir cada 4");
                }
                else if(radioButton.getText().toString().equals("5")){
                    intervalosSeleccionados.add("Repetir cada 5");
                }
                else if(radioButton.getText().toString().equals("6")){
                    intervalosSeleccionados.add("Repetir cada 6");
                }
                else if(radioButton.getText().toString().equals("7")){
                    intervalosSeleccionados.add("Repetir cada 7");
                }
                break;
            }
        }


        boolean verififiDiasCalendar = false;
        CheckBox[] allCheckBoxsDiaCalendar = {
                diaCalendar1, diaCalendar2, diaCalendar3, diaCalendar4, diaCalendar5, diaCalendar6,
                diaCalendar7, diaCalendar8, diaCalendar9, diaCalendar10, diaCalendar11, diaCalendar12,
                diaCalendar13, diaCalendar14, diaCalendar15, diaCalendar16, diaCalendar17, diaCalendar18,
                diaCalendar19, diaCalendar20, diaCalendar21, diaCalendar22, diaCalendar23, diaCalendar24,
                diaCalendar25, diaCalendar26, diaCalendar27, diaCalendar28, diaCalendar29, diaCalendar30,
                diaCalendar31
        };
        List<String> selectedDiasCalendar = new ArrayList<>();
        for (CheckBox checkBox : allCheckBoxsDiaCalendar) {
            if (checkBox.isChecked()) {
                verififiDiasCalendar = true;
                intervalosSeleccionados.add("Cada mes el dia " + checkBox.getText().toString());
            }
        }

        String intervalosString = String.join(", ", intervalosSeleccionados);

        return intervalosString;
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
                imageView = new ImageView(AgregarActiTutor.this);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            String imagePath = "img/img_actividades/" + images.get(position);
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

                // Usar el color seleccionado previamente
                int colorActual = colorSeleccionado;
                String colorSuffix = "";

                // Determinar el sufijo de color basado en el valor de colorActual
                if (colorActual == getResources().getColor(R.color.rojo_actis)) {
                    colorSuffix = "-rojo";
                } else if (colorActual == getResources().getColor(R.color.amarillo_actis)) {
                    colorSuffix = "-amarillo";
                } else if (colorActual == getResources().getColor(R.color.azul_actis)) {
                    colorSuffix = "-azul";
                } else if (colorActual == getResources().getColor(R.color.verde_actis)) {
                    colorSuffix = "-verde";
                } else if (colorActual == getResources().getColor(R.color.morado_actis)) {
                    colorSuffix = "-morado";
                } else if (colorActual == getResources().getColor(R.color.black)) {
                    colorSuffix = "";
                }

                String nuevaRuta = selectedImage.replace(".svg", "") + colorSuffix + ".svg";
                updateButtonImage(nuevaRuta);
                modal.dismiss();
            });


            return imageView;
        }
    }


}
