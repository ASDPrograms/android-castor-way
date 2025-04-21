package com.example.castorway;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.castorway.api.ApiService;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.retrofit.RetrofitClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeTutor extends AppCompatActivity {
    private ImageView lastSelectedIcon, img_prof_usr, btnDesplegar;
    private ImageView iconHome, iconActividades, iconCalendar, iconRecompensas, iconDiario, iconChat;
    TextView txt_hjs_congeladas, txt_ramas;
    FrameLayout circulogrande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tutor);

        //Al abrir intentar recibir el valor de modal trás actividad creada
        String fragmentName = getIntent().getStringExtra("fragmentActiCrear");

        if (fragmentName != null) {
            // Cargar el fragmento correspondiente
            Fragment fragment = null;

            switch (fragmentName) {
                case "ActividadesFragmentTutor":
                    fragment = new ActividadesFragmentTutor();
                    break;
            }

            // Si se recibe un fragmento válido, lo mostramos
            if (fragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment); // Asegúrate de tener un contenedor adecuado
                transaction.commit();

            }
        }

        HorizontalScrollView userScrollView = findViewById(R.id.userScrollView);
        userScrollView.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        String email = preferences.getString("email", null);

        if (email == null) {
            Log.e("DEBUG", "Email en SharedPreferences es NULL. Esperando...");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    confirmExistUsrKit();
                }
            }, 1000);
        } else {
            confirmExistUsrKit();
        }
        //Aquí se actualizan los datos del navegador de arriba (numRamitas, hojasCongeladas)
        actuInfoTopNav();

        //Pq por alguna razón le cambiaba el color del rectángulo que contiene info del cel(batería, wifi, etc.)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        //Se declaran variables del xml
        iconHome = findViewById(R.id.icon_home);
        iconActividades = findViewById(R.id.icon_actividades);
        iconCalendar = findViewById(R.id.icon_calendario);
        iconRecompensas = findViewById(R.id.icon_recompensa);
        iconDiario = findViewById(R.id.icon_diario);
        iconChat = findViewById(R.id.icon_chat);

        //se actualiza el tamaño de los íconos de acuerdo a las medidas del dispositivo
        setMinWidthForIcons();



        //se asignan interacciones a los elementos del nav para q dependiendo de lo q seleccione te lleve a un fragment u otro
        iconHome.setOnClickListener(iconClickListener);
        iconActividades.setOnClickListener(iconClickListener);
        iconCalendar.setOnClickListener(iconClickListener);
        iconRecompensas.setOnClickListener(iconClickListener);
        iconDiario.setOnClickListener(iconClickListener);
        iconChat.setOnClickListener(iconClickListener);

        //por default el seleccionado es el de home
        selectIcon(iconHome);
        loadFragment(new HomeFragmentTutor());

        //declaración de elementos del xml y funcionalidad para la burbuja de elegir usr kit
        circulogrande = findViewById(R.id.circulo_grande);
        circulogrande.setOnClickListener(this::desplListUsrsKit);

        //declaración de xml y funcionalidad del botón que despliega los hijos disponibles
        btnDesplegar = findViewById(R.id.btnDesplegar);
        btnDesplegar.setOnClickListener(this::desplListUsrsKit);

    }
    @Override
    protected void onResume() {
        super.onResume();

        String fragmentName = getIntent().getStringExtra("fragmentActiCrear");

        if (fragmentName != null) {
            // Cargar el fragmento correspondiente
            Fragment fragment = null;

            ImageView iconToSelect = null; // Variable para saber qué icono seleccionar

            switch (fragmentName) {
                case "ActividadesFragmentTutor":
                    fragment = new ActividadesFragmentTutor();
                    iconToSelect = iconActividades; // Ícono de actividades
                    break;
                case "HomeFragmentTutor":
                    fragment = new HomeFragmentTutor();
                    iconToSelect = iconHome; // Ícono de inicio
                    break;
            }

            if (fragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment);
                transaction.commit();

                // Seleccionar el ícono correspondiente visualmente
                if (iconToSelect != null) {
                    selectIcon(iconToSelect);
                }
            }

        }

        confirmExistUsrKit();
    }
    //para cuando cierra la app (el activity de hometutor)
    @Override
    public void onDestroy() {
        super.onDestroy();
        limpiarSesionModalActis();
    }

    //@Override
    //    protected void onStop() {
    //        super.onStop();
    //        limpiarSesionModalActis();
    //    }

    private void limpiarSesionModalActis(){
        SharedPreferences preferences = getSharedPreferences("sesionModalActis", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void actuInfoTopNav(){
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();

        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castores = response.body();
                AtomicInteger cntCoincidEmail = new AtomicInteger(0);
                int idCastor = 0;
                if (castores != null) {
                    SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                    for (Castor castor : castores) {
                        if(castor.getEmail().equalsIgnoreCase(preferences.getString("email", ""))){
                            cntCoincidEmail.incrementAndGet();
                            idCastor = castor.getIdCastor();
                            Log.d("MainActivity", "Castor: " + castor.getEmail());
                            break;
                        }
                    }
                    if(cntCoincidEmail.get() == 1 && idCastor != 0){
                        ApiService apiServiceKit = RetrofitClient.getApiService();
                        Call<List<Kit>> call2 = apiService.getAllKits();
                        call2.enqueue(new Callback<List<Kit>>() {
                            @Override
                            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                                List<Kit> kits = response.body();
                                AtomicInteger cntCoincidUsrKit = new AtomicInteger(0);
                                if (kits != null){
                                    SharedPreferences preferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
                                    int idKit = preferences.getInt("idKit", 0);
                                    for (Kit kit : kits) {
                                        if(kit.getIdKit() == idKit && idKit != 0){
                                            int numHjsCongleadas = kit.getHojasCongeladas();
                                            String stringHjsCongeldas = String.valueOf(numHjsCongleadas);

                                            int numRamitas = kit.getRamitas();
                                            String stringNumRamitas = String.valueOf(numRamitas);

                                            txt_hjs_congeladas = findViewById(R.id.txt_hjs_congeladas);
                                            txt_hjs_congeladas.setText(stringHjsCongeldas);

                                            txt_ramas = findViewById(R.id.txt_ramas);
                                            txt_ramas.setText(stringNumRamitas);
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Kit>> call, Throwable t) {

                            }
                        });
                    }else{
                        Toast.makeText(HomeTutor.this, "Ocurrió un error con su cuenta", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeTutor.this, IniciarSesionTutor.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(HomeTutor.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmExistUsrKit() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();

        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castores = response.body();
                String codPresa = null;
                int cont = 0;
                if (castores != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                    String userEmail = sharedPreferences.getString("email", "");

                    for (Castor castor : castores) {
                        if (castor.getEmail().equals(userEmail)) {
                            codPresa = castor.getCodPresa();
                            cont ++;
                            break;
                        }
                    }
                }

                if (cont > 0) {
                    verificarRelacionKit(codPresa);
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(HomeTutor.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarRelacionKit(String codPresa) {
        if (codPresa == null || codPresa.isEmpty()) {
            Log.e("DEBUG", "Error: codPresa es NULL o vacío antes de llamar a la API.");
            return;
        }
        ApiService apiService2 = RetrofitClient.getApiService();
        Call<List<Kit>> call2 = apiService2.getAllKits();

        call2.enqueue(new Callback<List<Kit>>() {
            @Override
            public void onResponse(Call<List<Kit>> call, Response<List<Kit>> response) {
                List<Kit> kits = response.body();

                if (kits != null) {
                    boolean existeKitRelacionado = false;
                    for (Kit kit : kits) {
                        if (codPresa.equals(kit.getCodPresa())) {
                            existeKitRelacionado = true;
                            break;
                        }
                    }
                    if (!existeKitRelacionado) {
                        mostrarModal();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Kit>> call, Throwable t) {
                Toast.makeText(HomeTutor.this, "Error al obtener los kits", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarModal() {
        // Crear el AlertDialog que es como un modal jsjs
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View modalView = getLayoutInflater().inflate(R.layout.modal_basico, null);

        // Aquí se asignan los componentes de la vista
        builder.setView(modalView);
        // Esto es para evitar que se cierre tocando fuera
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //Se declaran título, subtítulo y botón del modal con los id's del modal
        TextView txtDialogTitle = modalView.findViewById(R.id.txtDialogTitle);
        TextView txtDialogMessage = modalView.findViewById(R.id.txtDialogMessage);

        //Se cambian los valores para que sea personalizado y se pueda reciclar código
        txtDialogTitle.setText("Registro de Hijo (a)");
        txtDialogMessage.setText("No tienes ningún hijo (a) registrado (a). Por favor, registra a tu hijo (a) para continuar.");

        Button btnPositive = modalView.findViewById(R.id.btnPositive);
        btnPositive.setText("Registrar hijo(a)");

        // Aquí se personaliza lo que queremos que haga el botón
        btnPositive.setOnClickListener(v -> {

            //Se saca el codPresa que se va a mandar al form de registrar hijo
            ApiService apiService = RetrofitClient.getApiService();
            Call<List<Castor>> call = apiService.getAllCastores();
            call.enqueue(new Callback<List<Castor>>() {
                @Override
                public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                    List<Castor> castores = response.body();
                    String codPresa = null;
                    if (castores != null) {
                        for (Castor castor : castores) {
                            SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                            if (castor.getEmail().equals(sharedPreferences.getString("email", ""))) {
                                codPresa = castor.getCodPresa();
                                break;
                            }
                        }
                    }
                    if (codPresa != null) {
                        Intent intent = new Intent(HomeTutor.this, RegistrarKit.class);
                        intent.putExtra("codPresa", codPresa);
                        startActivity(intent);
                    }
                }
                @Override
                public void onFailure(Call<List<Castor>> call, Throwable t) {
                    Toast.makeText(HomeTutor.this, "Error tratando de conectar", Toast.LENGTH_SHORT).show();
                }
            });
        });


        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }



    private void desplListUsrsKit(View view){
        HorizontalScrollView userScrollView = findViewById(R.id.userScrollView);
        LinearLayout userContainer = findViewById(R.id.userContainer);

        if (userScrollView.getVisibility() == View.GONE) {
            userScrollView.setVisibility(View.VISIBLE);
            userScrollView.setBackgroundResource(R.drawable.rounded_background);
            cargarUsuarios(userContainer);
        } else {
            userScrollView.setVisibility(View.GONE);
            userScrollView.setBackground(null);
        }
    }

    private void cargarUsuarios(LinearLayout userContainer) {
        userContainer.removeAllViews();

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Castor>> call = apiService.getAllCastores();
        call.enqueue(new Callback<List<Castor>>() {
            @Override
            public void onResponse(Call<List<Castor>> call, Response<List<Castor>> response) {
                List<Castor> castores = response.body();
                String codPresa = null;
                if (castores != null) {
                    for (Castor castor : castores) {
                        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                        if (castor.getEmail().equals(sharedPreferences.getString("email", ""))) {
                            codPresa = castor.getCodPresa();
                            break;
                        }
                    }
                }
                if (codPresa != null) {
                    ApiService apiService2 = RetrofitClient.getApiService();
                    Call<List<Kit>> call2 = apiService2.getAllKits();
                    String finalCodPresa = codPresa;
                    call2.enqueue(new Callback<List<Kit>>() {
                        @Override
                        public void onResponse(Call<List<Kit>> call2, Response<List<Kit>> response) {
                            List<Kit> kits = response.body();
                            AtomicInteger cntCoincidUsr = new AtomicInteger(0);
                            if (kits != null) {
                                for (Kit kit : kits) {
                                    if (kit.getCodPresa().equals(finalCodPresa)) {
                                        View usuarioView = LayoutInflater.from(HomeTutor.this).inflate(R.layout.item_usuario, userContainer, false);

                                        ImageView imgUsuario = usuarioView.findViewById(R.id.imgCadaUsuario);
                                        TextView txtNombre = usuarioView.findViewById(R.id.usrNameCdaUsr);
                                        imgUsuario.setImageResource(R.drawable.icn_user_gris);
                                        txtNombre.setText(kit.getNombreUsuario());

                                        LinearLayout usuarioLayout = usuarioView.findViewById(R.id.layoutCadUsr);

                                        final Kit finalKit = kit;
                                        int idKit = kit.getIdKit();
                                        usuarioLayout.setOnClickListener(v -> {
                                            limpiarSesionModalActis();

                                            SharedPreferences sharedPreferences = getSharedPreferences("usrKitCuentaTutor", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("idKit", idKit);
                                            editor.apply();

                                            //Inicio del código para mostrar el toast personalizado
                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.toast_personalizado, null);

                                            //Inicio de código para cambiar elementos del toast personalizado

                                            //Se cambia la imágen
                                            ImageView icon = layout.findViewById(R.id.toast_icon);
                                            icon.setImageResource(R.drawable.icn_user_gris);

                                            //Se cambia el texto
                                            TextView text = layout.findViewById(R.id.toast_text);
                                            text.setText("Usuario seleccionado: " + kit.getNombreUsuario());

                                            //Se cambia el color de fondo
                                            Drawable background = layout.getBackground();
                                            background.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.azul_toast_bienvenido), PorterDuff.Mode.SRC_IN);

                                            // Cambia color del texto
                                            text.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                                            //Fin del código que se encarga de cambiar los elementos del toast personalizado

                                            //Lo crea y lo pone en la parte de arriba del cel
                                            Toast toast = new Toast(getApplicationContext());
                                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
                                            toast.setDuration(Toast.LENGTH_LONG);
                                            toast.setView(layout);
                                            toast.show();

                                            //Ya que se selecciona un usr Kit se actualiza el fragment para que jale la info de ese hijo
                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                                            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                                            String fragmentName = currentFragment.getClass().getSimpleName();
                                            Log.e("DEBUG", "Nombre del fragment: " + fragmentName);

                                            //con el valor que se saca del nombre del fragment se decide que fragment cargar:
                                            if(fragmentName.equals("HomeFragmentTutor")){
                                                HomeFragmentTutor nuevoFragment = new HomeFragmentTutor();
                                                transaction.replace(R.id.frame_container, nuevoFragment);
                                                transaction.commit();
                                            }
                                            else if(fragmentName.equals("ActividadesFragmentTutor")){
                                                ActividadesFragmentTutor nuevoFragment = new ActividadesFragmentTutor();
                                                transaction.replace(R.id.frame_container, nuevoFragment);
                                                transaction.commit();
                                            }
                                            else if(fragmentName.equals("CalendarioFragmentTutor")){
                                                CalendarioFragmentTutor nuevoFragment = new CalendarioFragmentTutor();
                                                transaction.replace(R.id.frame_container, nuevoFragment);
                                                transaction.commit();
                                            }
                                            else if(fragmentName.equals("RecompensasFragmentTutor")){
                                                RecompensasFragmentTutor nuevoFragment = new RecompensasFragmentTutor();
                                                transaction.replace(R.id.frame_container, nuevoFragment);
                                                transaction.commit();
                                            }
                                            else if(fragmentName.equals("DiarioFragmentTutor")){
                                                DiarioFragmentTutor nuevoFragment = new DiarioFragmentTutor();
                                                transaction.replace(R.id.frame_container, nuevoFragment);
                                                transaction.commit();
                                            }
                                            else if(fragmentName.equals("ChatFragmentTutor")){
                                                ChatFragmentTutor nuevoFragment = new ChatFragmentTutor();
                                                transaction.replace(R.id.frame_container, nuevoFragment);
                                                transaction.commit();
                                            }
                                            actuInfoTopNav();

                                        });
                                        userContainer.addView(usuarioView);
                                    }
                                }
                            }
                            View agregarUsuarioView = LayoutInflater.from(HomeTutor.this).inflate(R.layout.item_nuevo_usuario, userContainer, false);
                            userContainer.addView(agregarUsuarioView);

                            LinearLayout botonAgregar = agregarUsuarioView.findViewById(R.id.layoutCadUsr);
                            botonAgregar.setOnClickListener(v -> {
                                Intent intent = new Intent(HomeTutor.this, RegistrarKit.class);
                                intent.putExtra("codPresa", finalCodPresa);
                                startActivity(intent);
                            });
                        }

                        @Override
                        public void onFailure(Call<List<Kit>> call2, Throwable t) {
                            Toast.makeText(HomeTutor.this, "Error en la conexión hijo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(HomeTutor.this, "Ocurrió un error con su cuenta", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeTutor.this, IniciarSesionTutor.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Castor>> call, Throwable t) {
                Toast.makeText(HomeTutor.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setMinWidthForIcons() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidthPx = displayMetrics.widthPixels;

        int minWidthPx = (int) (screenWidthPx * 0.16);
        iconHome.setMinimumWidth(minWidthPx);
        iconActividades.setMinimumWidth(minWidthPx);
        iconCalendar.setMinimumWidth(minWidthPx);
        iconRecompensas.setMinimumWidth(minWidthPx);
        iconDiario.setMinimumWidth(minWidthPx);
        iconChat.setMinimumWidth(minWidthPx);
    }


    private final View.OnClickListener iconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectIcon((ImageView) view);
            switchFragment(view.getId());
        }
    };

    private void selectIcon(ImageView selectedIcon) {
        if (lastSelectedIcon != null) {
            lastSelectedIcon.setColorFilter(null);
            lastSelectedIcon.setSelected(false);
        }
        selectedIcon.setSelected(true);
        animateIcon(selectedIcon);

        lastSelectedIcon = selectedIcon;
    }

    private void animateIcon(ImageView icon) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.2f, 1f);
        scaleX.setDuration(300);
        scaleY.setDuration(300);
        scaleX.start();
        scaleY.start();
    }

    private void switchFragment(int iconId) {
        Fragment fragment = null;
        if (iconId == R.id.icon_home) {
            fragment = new HomeFragmentTutor();
        } else if (iconId == R.id.icon_actividades) {
            fragment = new ActividadesFragmentTutor();
        } else if (iconId == R.id.icon_calendario) {
            fragment = new CalendarioFragmentTutor();
        } else if (iconId == R.id.icon_recompensa) {
            fragment = new RecompensasFragmentTutor();
        } else if (iconId == R.id.icon_diario) {
            fragment = new DiarioFragmentTutor();
        } else if (iconId == R.id.icon_chat) {
            fragment = new ChatFragmentTutor();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }
}
