package com.example.castorway;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeUsuarioSesion extends AppCompatActivity {
    TextView txtTitHome, txtComienaHome;
    Button btnIniciarSesion, btnRegistrarse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_usuario_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            txtTitHome = findViewById(R.id.txtTitHome);
            String fullText = "Hábitos saludables, \nfuturo brillante.";

            String greenWord = "Hábitos";
            String blueWord = "futuro";

            SpannableString spannableString = new SpannableString(fullText);

            applyColorToWord(spannableString, fullText, greenWord, 0xFF52B788);
            applyColorToWord(spannableString, fullText, blueWord, 0xFF879FD4);
            txtTitHome.setText(spannableString);


            txtComienaHome = findViewById(R.id.txtComienaHome);
            String txtFullLink = "Comienza tu CastorWay ->";
            String clickableText = "Way ->";

            SpannableString spannableString2 = new SpannableString(txtFullLink);

            applyClickableColor(spannableString2, txtFullLink, clickableText, 0xFFBA6958);

            txtComienaHome.setText(spannableString2);
            txtComienaHome.setMovementMethod(LinkMovementMethod.getInstance());

            btnIniciarSesion = findViewById(R.id.btnIniciarSesionUsr);
            btnRegistrarse = findViewById(R.id.btnRegistrarUsr);

            btnIniciarSesion.setOnClickListener(this::iniciarSesion);
            btnRegistrarse.setOnClickListener(this::registrarse);

            return insets;
        });
    }private void iniciarSesion(View view){
        Intent iniciarSesion = new Intent(this, ElegirUsrIniciarSesion.class);
        startActivity(iniciarSesion);
    }
    private void registrarse(View view){
        Intent registrarse = new Intent(this, ElegirUsrRegistrarse.class);
        startActivity(registrarse);
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
    }private void applyClickableColor(SpannableString spannableString, String fullText, String fragment, int color) {
        int startIndex = fullText.indexOf(fragment);
        if (startIndex != -1) {
            int endIndex = startIndex + fragment.length();
            spannableString.setSpan(
                    new ForegroundColorSpan(color),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeUsuarioSesion.this, ElegirUsrRegistrarse.class);
                    startActivity(intent);
                }
                public void updateDrawState(android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(0xFFBA6958);
                    ds.bgColor = 0x00000000;
                }
            }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}