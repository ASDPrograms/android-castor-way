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
import android.widget.EditText;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.retrofit.RetrofitClient;
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

public class EditarActividad extends AppCompatActivity {
    TextView txtFechaInicial, txtFechaFinal, txtOptTipFechaSelected, contCaractNameHabit, txtTitNuevoHabit, txtHoraInicial, txtHoraFinal, numRamitas, contCaractInfoExtr, txtMasInfo, nombreHabitInput;
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
        setContentView(R.layout.activity_editar_actividad);

        btnChangeTipoInterFechas = findViewById(R.id.btnChangeTipoInterFechas);

        txtTitNuevoHabit = findViewById(R.id.txtTitNuevoHabit);
        String textoCompleto = "Editar Hábito";

        SpannableString spannableString = new SpannableString(textoCompleto);

        int start = textoCompleto.indexOf("Hábito");
        int end = start + "Hábito".length();

        int colorHabit = ContextCompat.getColor(this, R.color.cafe_subtono);

        spannableString.setSpan(new ForegroundColorSpan(colorHabit), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtTitNuevoHabit.setText(spannableString);

        //btn para cerrar la activity;
        btnSalirAddActi = findViewById(R.id.btnSalirAddActi);
        btnSalirAddActi.setOnClickListener(v1 -> mostrarModalCerrarView("¡Atención!", "Si das click en aceptar saldrás del formulario y no se guardará la información ingresada."));

        nombreHabitInput = findViewById(R.id.nombreHabitInput);
        contCaractNameHabit = findViewById(R.id.contCaractNameHabit);
        nombreHabitInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int caracteresActuales = s.length();
                contCaractNameHabit.setText(caracteresActuales + "/" + 35);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinnerTiposHabit = findViewById(R.id.spinnerTipHabits);

        ArrayAdapter<CharSequence> adapterTip = ArrayAdapter.createFromResource(
                this,
                R.array.opciones,
                R.layout.dropdown_actis_nombre
        );
        spinnerTiposHabit.setPopupBackgroundResource(R.drawable.input_border);
        adapterTip.setDropDownViewResource(R.layout.dropdown_actis_nombre);
        spinnerTiposHabit.setAdapter(adapterTip);

        //Se declaran linearLayout de intervalos:
        linearLayDiasSemana = findViewById(R.id.linearLayDiasSemana);
        linearLayIntervalos = findViewById(R.id.linearLayIntervalos);
        linearLayDiasMes = findViewById(R.id.linearLayDiasMes);
        txtOptTipFechaSelected = findViewById(R.id.txtOptTipFechaSelected);
        linLayCeckBxCadaDia = findViewById(R.id.linLayCeckBxCadaDia);

        semanaDia1 = findViewById(R.id.semanaDia1);
        semanaDia2 = findViewById(R.id.semanaDia2);
        semanaDia3 = findViewById(R.id.semanaDia3);
        semanaDia4 = findViewById(R.id.semanaDia4);
        semanaDia5 = findViewById(R.id.semanaDia5);
        semanaDia6 = findViewById(R.id.semanaDia6);
        semanaDia7 = findViewById(R.id.semanaDia7);

        CheckBox[] allCheckBoxes = {semanaDia1, semanaDia2, semanaDia3, semanaDia4, semanaDia5, semanaDia6, semanaDia7};

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
        radioBtnCadaDia.setOnClickListener(v1 -> {
            // Verificar el estado del radio button
            boolean selected = radioBtnCadaDia.isChecked();
            // Alternar el estado: si ya estaba seleccionado, deseleccionarlo; de lo contrario, seleccionarlo.
            radioBtnCadaDia.setChecked(selected);

            toggleAllCheckBoxes(allCheckBoxes, selected);
        });

        txtFechaInicial = findViewById(R.id.txtFechaInicial);
        txtFechaInicial.setOnClickListener(this::mostrarDatePicker);
        txtFechaFinal = findViewById(R.id.txtFechaFinal);


        intervalo2 = findViewById(R.id.intervalo2);
        intervalo3 = findViewById(R.id.intervalo3);
        intervalo4 = findViewById(R.id.intervalo4);
        intervalo5 = findViewById(R.id.intervalo5);
        intervalo6 = findViewById(R.id.intervalo6);
        intervalo7 = findViewById(R.id.intervalo7);

        radioGroupIntervalosDias = findViewById(R.id.radioGroupIntervalosDias);
        RadioButton[] allRadioButtonInterval = {intervalo2, intervalo3, intervalo4, intervalo5, intervalo6, intervalo7};

        radioGroupIntervalosDias.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                elegirIntervaloXDias(txtFechaInicial, allRadioButtonInterval);
            }
        });

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


        //declaración de las horas y se les agrega el listener para que se abra el modal:
        txtHoraInicial = findViewById(R.id.txtHoraInicial);
        txtHoraFinal = findViewById(R.id.txtHoraFinal);

        txtHoraInicial.setOnClickListener(this::openTimePickerFechaInicial);
        txtHoraFinal.setOnClickListener(this::openTimePickerFechaFinal);

        //número de ramitas:
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

        //Para que se cambie el ícono de la imagen y su color
        btnAgregarImgActi = findViewById(R.id.btnAgregarImgActi);
        circleColorElegidoIcon = findViewById(R.id.circleColorElegidoIcon);

        btnCambiarIconoColor = findViewById(R.id.btnCambiarIconoColor);
        btnCambiarIconoColor.setOnClickListener(this::cambiarColorIconActi);

        btnAgregarImgActi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });


        //para la info extra:
        contCaractInfoExtr = findViewById(R.id.contCaractInfoExtr);
        txtMasInfo = findViewById(R.id.txtMasInfo);
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
        establecerDatosActi();


        //btn para actualizar acti
        btnCrearActiMandar = findViewById(R.id.btnCrearActiMandar);
        btnCrearActiMandar.setOnClickListener(this::actualizarActi);
    }
    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir, si das click en aceptar saldrás y no se actualizará la actividad con los nuevos datos.");
    }
    public void establecerDatosActi(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Actividad>> call = apiService.getAllActividades();
        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                List<Actividad> actividades = response.body();
                if (actividades != null) {
                    Log.e("DEBUG", "Hay actis");
                    for (Actividad actividad : actividades) {
                        SharedPreferences sharedPreferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                        int idActividad = sharedPreferences.getInt("idActividad", 0);
                        Log.e("DEBUG", "Id acti: " + idActividad);
                        if(actividad.getIdActividad() == idActividad){
                            Log.e("DEBUG", "Si entró");

                            //Se cambian los valores de los campos por los del formulario
                            String nombreHabit = String.valueOf(actividad.getNombreHabito());

                            nombreHabitInput.setText(nombreHabit);

                            String tipHabit =String.valueOf(actividad.getTipoHabito());
                            if(tipHabit.equals("Salud")){
                                spinnerTiposHabit.setSelection(1);
                            }else if(tipHabit.equals("Productividad")){
                                spinnerTiposHabit.setSelection(2);
                            }else if(tipHabit.equals("Personales")){
                                spinnerTiposHabit.setSelection(3);
                            }else if(tipHabit.equals("Sociales")){
                                spinnerTiposHabit.setSelection(4);
                            }else if(tipHabit.equals("Financieros")){
                                spinnerTiposHabit.setSelection(5);
                            }else if(tipHabit.equals("Emocionales")){
                                spinnerTiposHabit.setSelection(6);
                            }

                            //Cambiar fechas y seleccionados en el de fechas de intervalos
                            String fechaInicial = String.valueOf(actividad.getDiaInicioHabito());
                            String fechaFinal = String.valueOf(actividad.getDiaMetaHabito());

                            txtFechaInicial.setText(fechaInicial);
                            txtFechaFinal.setText(fechaFinal);

                            String[] intervalosArray = fechaFinal.split(",");

                            String intervalos = String.valueOf(actividad.getRepeticiones());
                            if(intervalos.contains("Lunes") || intervalos.contains("Martes") || intervalos.contains("Miércoles") ||
                                    intervalos.contains("Jueves") || intervalos.contains("Viernes") || intervalos.contains("Sábado") ||
                                    intervalos.contains("Domingo")){

                                //El linearlayout que se enseña dependiendo de las repeticiones que se tengan
                                linearLayDiasSemana.setVisibility(View.VISIBLE);
                                linearLayIntervalos.setVisibility(View.GONE);
                                linearLayDiasMes.setVisibility(View.GONE);
                                linLayCeckBxCadaDia.setVisibility(View.VISIBLE);
                                txtOptTipFechaSelected.setText("Semanal");

                                if(intervalos.contains("Lunes") && intervalos.contains("Martes") && intervalos.contains("Miércoles") &&
                                        intervalos.contains("Jueves") && intervalos.contains("Viernes") && intervalos.contains("Sábado") &&
                                        intervalos.contains("Domingo")) {
                                    //Si están todos en la lista, que se marque el check de seleccionar todos los días
                                    radioBtnCadaDia.setChecked(true);
                                }
                                if(intervalos.contains("Lunes")){
                                    semanaDia1.setChecked(true);
                                }
                                if(intervalos.contains("Martes")){
                                    semanaDia2.setChecked(true);
                                }
                                if(intervalos.contains("Miércoles")){
                                    semanaDia3.setChecked(true);
                                }
                                if(intervalos.contains("Jueves")){
                                    semanaDia4.setChecked(true);
                                }
                                if(intervalos.contains("Viernes")){
                                    semanaDia5.setChecked(true);
                                }
                                if(intervalos.contains("Sábado")){
                                    semanaDia6.setChecked(true);
                                }
                                if(intervalos.contains("Domingo")){
                                    semanaDia7.setChecked(true);
                                }
                            }

                            //Cambiar horas
                            String horaInicial = String.valueOf(actividad.getHoraInicioHabito());
                            String horaFinal = String.valueOf(actividad.getHoraFinHabito());

                            selectedStartTime = convertirStringACalendar(horaInicial);
                            selectedEndTime = convertirStringACalendar(horaFinal);

                            String horaInicialAMPM = convertirFormatoHora(horaInicial);
                            String horaFinalAMPM = convertirFormatoHora(horaFinal);

                            txtHoraInicial.setText(horaInicialAMPM);
                            txtHoraFinal.setText(horaFinalAMPM);

                            //Cambiar número de ramitas
                            String ramitasBD = String.valueOf(actividad.getNumRamitas());
                            numRamitas.setText(ramitasBD);

                            //Cambiar la imágen del hábito
                            String urlImgHabitBD = String.valueOf(actividad.getRutaImagenHabito());
                            String urlImgHabit = extraerContenidoImg(urlImgHabitBD);
                            updateButtonImage(urlImgHabit);

                            //Para cambiar el color del hábito
                            String color = String.valueOf(actividad.getColor());
                            String codColor = getColorCod(color);

                            circleColorElegidoIcon.setColorFilter(Color.parseColor(codColor), PorterDuff.Mode.SRC_IN);
                            colorSeleccionado = Color.parseColor(codColor);

                            //pa la info extra:
                            byte[] byteArray = actividad.getInfoExtraHabito().getBytes();
                            String infoExtra = new String(byteArray, StandardCharsets.UTF_8);
                            Log.e("IinfoExtra", infoExtra);  // Verifica si contiene los emojis correctamente
                            txtMasInfo.setText(infoExtra);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
        });
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
    private boolean hayCheckBoxSelectedDiasMes(CheckBox[] checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }
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
    } private void elegirDiasCalendar(TextView fechaTextView, CheckBox[] allCheckBox){
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
    private void toggleAllCheckBoxes(CheckBox[] checkBoxes, boolean isChecked) {
        for (CheckBox cb : checkBoxes) {
            cb.setChecked(isChecked);
        }
        if(!isChecked){
            txtFechaFinal.setText(" - ");
        }
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

            // Si ya existe una fecha inicial seleccionada, validamos si la fecha final es al menos 15 minutos después
            if (selectedStartTime != null) {
                long differenceInMillis = selectedEndTime.getTimeInMillis() - selectedStartTime.getTimeInMillis();
                if (differenceInMillis < 15 * 60 * 1000){
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
                    toast.show();

                    return; // Detenemos el proceso
                }
            }

            // Asegúrate de que txtHoraFinal está inicializado
            if (txtHoraFinal != null) {
                txtHoraFinal.setText(selectedTime);
            }
        });

        // Muestra el diálogo con el fragment manager correcto
        timePicker.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), "timePickerFechaFinal");
    }

    public static String convertirFormatoHora(String hora24) {
        try {
            // Formato de entrada (24 horas)
            SimpleDateFormat formato24 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            // Formato de salida (12 horas con AM/PM sin puntos)
            SimpleDateFormat formato12 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

            Date date = formato24.parse(hora24);

            return formato12.format(date).replace(".", "").toUpperCase();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Calendar convertirStringACalendar(String hora) {
        try {
            String[] horaSplit = hora.split(":");
            int hour = Integer.parseInt(horaSplit[0]);
            int minute = Integer.parseInt(horaSplit[1]);

            // Crear una instancia de Calendar con la fecha actual
            Calendar calendar = Calendar.getInstance();

            // Establecer los valores de hora y minutos
            calendar.set(Calendar.HOUR_OF_DAY, hour);  // Establecer hora
            calendar.set(Calendar.MINUTE, minute);     // Establecer minutos
            calendar.set(Calendar.SECOND, 0);          // Establecer segundos a 0
            calendar.set(Calendar.MILLISECOND, 0);     // Establecer milisegundos a 0

            // Asegurarse de que la fecha también está establecida (por defecto será la fecha actual)
            Log.e("VERIFI", "Calendar time: " + calendar.getTime().toString());

            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    private void showImageSelectionDialog() {
        // Crear el BottomSheetDialog
        BottomSheetDialog modal = new BottomSheetDialog(EditarActividad.this);
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
        EditarActividad.ImageAdapter imageAdapter = new EditarActividad.ImageAdapter(imageFiles, modal);
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

    private String getColorCod(String nombreColor){
        String codColor = "";

        if(nombreColor.equalsIgnoreCase("rojo")){
            codColor = "#ff595e";
        }else if(nombreColor.equalsIgnoreCase("amarillo")){
            codColor = "#ffca3a";
        }else if(nombreColor.equalsIgnoreCase("verde")){
            codColor = "#8ac926";
        }else if(nombreColor.equalsIgnoreCase("azul")){
            codColor = "#1982c4";
        }else if(nombreColor.equalsIgnoreCase("morado")){
            codColor = "#6a4c93";
        }else{
            codColor = "#FF000000";
        }

        return codColor;
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
                    colorSeleccionado = ContextCompat.getColor(EditarActividad.this, R.color.rojo_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "rojo");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorAmarillo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.amarillo_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(EditarActividad.this, R.color.amarillo_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "amarillo");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorAzul.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.azul_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(EditarActividad.this, R.color.azul_actis);
                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "azul");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorVerde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.verde_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(EditarActividad.this, R.color.verde_actis);

                    String nuevaImg = replaceColorSuffix(imagenActiSelected, "verde");

                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorNegro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(EditarActividad.this, R.color.black);
                    String nuevaImg = imagenActiSelected.replaceAll("-(rojo|amarillo|azul|verde|morado)", "");
                    loadImageFromAssets(nuevaImg);
                    dialog.dismiss();
                }
            });

            colorMorado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleColorElegidoIcon.setColorFilter(getResources().getColor(R.color.morado_actis), PorterDuff.Mode.SRC_IN);
                    colorSeleccionado = ContextCompat.getColor(EditarActividad.this, R.color.morado_actis);
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


    //para actualizar la acti:
    private void actualizarActi(View view){
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
                    Toast.makeText(EditarActividad.this, "Error al obtener actividades", Toast.LENGTH_SHORT).show();
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

                                    SharedPreferences preferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
                                    int idActi = preferences.getInt("idActividad", 0);

                                    if ((actividad.getIdActividad() != idActi) && ((inicioNueva.before(finBD) && inicioNueva.after(inicioBD)) ||
                                            (finNueva.after(inicioBD) && finNueva.before(finBD)) ||
                                            (inicioNueva.equals(inicioBD) || finNueva.equals(finBD)) ||
                                            (inicioNueva.before(inicioBD) && finNueva.after(finBD)))) {
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
                    obtenerIdCastorYActualizarActividad(idKit, nombreHabito, fechaInicial, fechaFinal, horaInicioText, horaFinText, numRam, masInfo);
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.e("API_ERROR", "Error al obtener actividades: " + t.getMessage());
                Toast.makeText(EditarActividad.this, "Error en la conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void obtenerIdCastorYActualizarActividad(int idKit, String nombreHabito, String fechaInicial, String fechaFinal, String horaInicial, String horaFinal, int numRamitas, String masInfo){
        Actividad actividad = new Actividad();

        SharedPreferences preferences = getSharedPreferences("actividadSelected", Context.MODE_PRIVATE);
        int idActi = preferences.getInt("idActividad", 0);

        actividad.setIdActividad(idActi);
        actividad.setNombreHabito(nombreHabito);
        actividad.setDiaInicioHabito(fechaInicial);
        actividad.setDiaMetaHabito(fechaFinal);
        actividad.setHoraInicioHabito(horaInicial);
        actividad.setHoraFinHabito(horaFinal);
        actividad.setNumRamitas(numRamitas);
        actividad.setInfoExtraHabito(masInfo);
        actividad.setFechasActividad(fechasSeleccionadas);

        String jalarIntervalos = jalarIntervalos();

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

        ApiService apiService = RetrofitClient.getApiService();
        Call<Actividad> call = apiService.updateActividad(actividad);
        call.enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(Call<Actividad> call, Response<Actividad> response) {
                Log.d("APIResponse", "Error en la actualización: " + response.code());
                if (response.isSuccessful()) {
                    Actividad updatedActividad = response.body();
                    if (updatedActividad != null) {
                        Log.d("APIResponse", "Actividad actualizada: " + updatedActividad.toString());
                    }else {
                        // Si la respuesta no fue exitosa, loguear el código de error
                        Log.d("APIResponse", "Error en la actualización: " + response.code());
                    }
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_personalizado, null);

                    ImageView icon = layout.findViewById(R.id.toast_icon);
                    icon.setImageResource(R.drawable.img_circ_palomita_verde);

                    TextView text = layout.findViewById(R.id.toast_text);
                    text.setText("Actividad actualizada con éxito");

                    Drawable background = layout.getBackground();
                    background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.verdecito_toast), PorterDuff.Mode.SRC_IN);

                    text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                    Intent intent = new Intent(EditarActividad.this, HomeTutor.class);
                    intent.putExtra("fragmentActiCrear", "ActividadesFragmentTutor");
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Actividad> call, Throwable t) {

            }
        });
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
                imageView = new ImageView(EditarActividad.this);
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
