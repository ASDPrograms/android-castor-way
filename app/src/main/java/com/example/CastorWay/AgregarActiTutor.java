package com.example.castorway;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import android.graphics.Color;


import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;


public class AgregarActiTutor extends AppCompatActivity {
    TextView txtFechaInicial, txtFechaFinal;
    AutoCompleteTextView nombreHabitInput;
    CheckBox radioBtnCadaDia, semanaDia1, semanaDia2, semanaDia3, semanaDia4, semanaDia5, semanaDia6, semanaDia7;
    Spinner spinnerTiposHabit;
    List<String> selectedCheckBoxNames = new ArrayList<>();
    List<Integer> selectedWeekdays = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_acti_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

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

        // Cuando se seleccione una fecha, ajustarla correctamente y mostrarla en txtFechaInicial
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Ajustar la fecha seleccionada
            Calendar selectedCalendar = Calendar.getInstance(); // Usando la zona horaria y Locale del dispositivo
            selectedCalendar.setTimeInMillis(selection);

            // Establecer hora, minuto, segundo y milisegundos a cero
            selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
            selectedCalendar.set(Calendar.MINUTE, 0);
            selectedCalendar.set(Calendar.SECOND, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);

            // Asegúrate de que la fecha de hoy también esté sin la hora
            Calendar todayCalendar = Calendar.getInstance(); // Usando la zona horaria y Locale del dispositivo
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);

            // Verificar si la fecha seleccionada es anterior a la actual
            if (selectedCalendar.before(todayCalendar)) {
                Toast.makeText(this, "No se puede seleccionar una fecha anterior a hoy", Toast.LENGTH_SHORT).show();
            } else {
                // Formatear la fecha correctamente (sin influir en la hora)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = sdf.format(selectedCalendar.getTime());

                // Establecer la fecha en el TextView
                txtFechaInicial.setText(formattedDate);

                    updateFinalDates();
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

    //Función que se encarga de generar las fechas que corresponden al intervalo
    //de acuerdo a la fecha incial y los días seleccionados


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
    // Función para convertir los nombres de los CheckBoxes seleccionados en los días de la semana
    private List<Integer> getSelectedWeekdays() {
        List<Integer> selectedWeekdays = new ArrayList<>();

        for (String checkboxName : selectedCheckBoxNames) {
            switch (checkboxName) {
                case "semanaDia1": // Lunes
                    selectedWeekdays.add(Calendar.MONDAY);
                    break;
                case "semanaDia2": // Martes
                    selectedWeekdays.add(Calendar.TUESDAY);
                    break;
                case "semanaDia3": // Miércoles
                    selectedWeekdays.add(Calendar.WEDNESDAY);
                    break;
                case "semanaDia4": // Jueves
                    selectedWeekdays.add(Calendar.THURSDAY);
                    break;
                case "semanaDia5": // Viernes
                    selectedWeekdays.add(Calendar.FRIDAY);
                    break;
                case "semanaDia6": // Sábado
                    selectedWeekdays.add(Calendar.SATURDAY);
                    break;
                case "semanaDia7": // Domingo
                    selectedWeekdays.add(Calendar.SUNDAY);
                    break;
            }
        }

        return selectedWeekdays;
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
        // Retorna el día de la semana correspondiente al CheckBox
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
            return -1;  // En caso de que no coincida con ningún ID
        }
    }



}