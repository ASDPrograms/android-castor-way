package com.example.castorway.api;

import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
public interface  ApiService{
    @GET("tablaCastor")
    Call<List<Castor>> getAllCastores();

    @POST("tablaCastor")
    Call<Castor> createCastor(@Body Castor castor);

    @GET("tablaKit")
    Call<List<Kit>> getAllKits();

    @POST("tablaKit")
    Call<Kit> createKit(@Body Kit kit);

    @GET("tablaActividad")
    Call<List<Actividad>> getAllActividades();

    @POST("tablaActividad")
    Call<Actividad> createActividad(@Body Actividad actividad);
}
