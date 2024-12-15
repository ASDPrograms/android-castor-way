package com.example.castorway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerAppWebKit extends AppCompatActivity {
    Button btnSalirWeb, btnVerInfo;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_app_web_kit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            btnSalirWeb = findViewById(R.id.btnSalirWeb);
            btnVerInfo = findViewById(R.id.btnVerInfo);

            btnSalirWeb.setOnClickListener(this::cerrarSesion);
            btnVerInfo.setOnClickListener(this::irVerInfo);

            webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("http://192.168.0.24:8080/CastorWay/");

            return insets;
        });
    }public void irVerInfo(View vista){
        Intent irVerInfo = new Intent(this, InformacionKit.class);
        startActivity(irVerInfo);
    }
    public void cerrarSesion(View vista){
        SharedPreferences preferences = getSharedPreferences("Castor", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.apply();

        Intent cerrarSesion = new Intent(this, MainActivity.class);
        startActivity(cerrarSesion);
        finish();
    }
}