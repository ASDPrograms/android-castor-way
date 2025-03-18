package com.example.castorway;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.retrofit.RetrofitClient;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        //Se establecen los valores de la actividad
        establecerDatosActi();
    }
    @Override
    public void onBackPressed() {
        mostrarModalCerrarView("¡Atención!", "Estás por salir, si das click en aceptar saldrás y no se actualizará la actividad con los nuevos datos.");
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
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {

            }
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
}