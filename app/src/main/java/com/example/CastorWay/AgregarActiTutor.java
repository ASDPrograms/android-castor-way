package com.example.castorway;

import android.content.res.ColorStateList;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.graphics.Color;


import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


public class AgregarActiTutor extends AppCompatActivity {
    TextView txtFechaInicial, txtFechaFinal, txtOptTipFechaSelected, contCaractNameHabit, txtTitNuevoHabit, txtHoraInicial, txtHoraFinal, numRamitas;
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
    LinearLayout linLayChangeOptFechas, linearLayDiasSemana, linearLayIntervalos, linearLayDiasMes, linLayCeckBxCadaDia;
    ImageView btnChangeTipoInterFechas, btnSalirAddActi;
    RadioButton intervalo2, intervalo3, intervalo4, intervalo5, intervalo6, intervalo7;
    RadioGroup radioGroupIntervalosDias;
    private int estElegirOptFechas = 0;
    private final int limCharNombreHabit = 35;
    private String fechasSeleccionadas = "";
    private Calendar selectedStartTime = null; // Variable para almacenar la hora inicial


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
            //btnSalirAddActi.setOnClickListener();

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

            String[] opciones = {"Comer frutas y verduras", "Beber agua", "Hacer ejercicio", "Mantener higiene personal", "Dormir lo necesario", "Elegir snacks saludables", "Seguir una rutina de higiene", "Preparar tu desayuno", "Cuidar tu piel"};

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
                    case "Opción 1":
                        //campoDato1.setText("Dato personalizado para Opción 1");
                        //campoDato2.setText("Otro dato para Opción 1");
                        break;
                    case "Opción 2":
                        //campoDato1.setText("Dato específico para Opción 2");
                        //campoDato2.setText("Otro valor para Opción 2");
                        break;
                    case "Opción 3":
                        //campoDato1.setText("Dato asignado para Opción 3");
                        //campoDato2.setText("Otro valor para Opción 3");
                        break;
                    default:
                        // En caso de que se escriba algo manualmente o no coincida con las opciones
                        //campoDato1.setText("");
                        //campoDato2.setText("");
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
            return insets;
        });
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
                .setTitleText("Selecciona una fecha")
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
                Toast.makeText(this, "No se puede seleccionar una fecha anterior a hoy", Toast.LENGTH_SHORT).show();
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
                txtFechaFinal.setText(" - ");
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
                txtFechaFinal.setText(" - ");
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
            txtFechaFinal.setText(" - ");
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
                .setTitleText("Selecciona la hora")
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
            Toast.makeText(view.getContext(), "Por favor, selecciona primero la hora inicial", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // Formato de 12 horas con AM/PM
                .setHour(7)
                .setMinute(0)
                .setTitleText("Selecciona la hora")
                .setTheme(R.style.CustomTimePickerTheme) // Aplica el tema personalizado
                .build();


        timePicker.addOnPositiveButtonClickListener(dialog -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String amPm = (hour >= 12) ? "PM" : "AM";
            int formattedHour = (hour == 0 || hour == 12) ? 12 : hour % 12;

            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm);

            // Compara la hora final con la hora inicial usando Calendar
            Calendar selectedEndTime = Calendar.getInstance();
            selectedEndTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedEndTime.set(Calendar.MINUTE, minute);

            // Verifica si la hora final es al menos 15 minutos después de la hora inicial
            long differenceInMillis = selectedEndTime.getTimeInMillis() - selectedStartTime.getTimeInMillis();
            if (differenceInMillis < 15 * 60 * 1000) {
                Toast.makeText(view.getContext(), "La hora final debe ser al menos 15 minutos después de la hora inicial", Toast.LENGTH_SHORT).show();
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
        if (!(valueNumRamitas >= 2 && valueNumRamitas <= 100) && valueNumRamitas != -1){
            Toast.makeText(this, "Ingrese un número entre 2 y 100", Toast.LENGTH_SHORT).show();
            numRamitas.setText("");
        }
    }
}