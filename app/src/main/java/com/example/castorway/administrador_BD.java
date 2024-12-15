package com.example.CastorWay;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class administrador_BD extends SQLiteOpenHelper {
    public administrador_BD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase Base) {
        Base.execSQL("create table Castor (idCastor INTEGER  primary key AUTOINCREMENT, codPresa text, nombre text, apellidos text, edad int, email text, contrasena text);");
        Base.execSQL("create table Kit (idKit INTEGER  primary key AUTOINCREMENT, codPresa text, nombreUsuario text, nombre text, apellidos text, edad int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
