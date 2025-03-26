package com.example.castorway.api;

import com.example.castorway.modelsDB.Actividad;
import com.example.castorway.modelsDB.Castor;
import com.example.castorway.modelsDB.Kit;
import com.example.castorway.modelsDB.Premios;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface  ApiService{
    @GET("tablaCastor")
    Call<List<Castor>> getAllCastores();

    @POST("tablaCastor")
    Call<Castor> createCastor(@Body Castor castor);

    @GET("tablaKit")
    Call<List<Kit>> getAllKits();

    @POST("tablaKit")
    Call<Kit> createKit(@Body Kit kit);

    //Actividad:
    @GET("tablaActividad")
    Call<List<Actividad>> getAllActividades();

    @POST("tablaActividad")
    Call<Actividad> createActividad(@Body Actividad actividad);

    @PUT("tablaActividad")
    Call<Actividad> updateActividad(@Body Actividad actividad);

    @DELETE("tablaActividad")
    Call<Void> deleteActividad(@Query("idActividad") int idActividad);

    //Premios:
    @GET("tablaPremios")
    Call<List<Premios>> getAllPremios();

    @POST("tablaPremios")
    Call<Premios> createPremio(@Body Premios premios);

    @PUT("tablaPremios")
    Call<Premios> updatePremios(@Body Premios premios);

    @DELETE("tablaPremios")
    Call<Void> deletePremio(@Query("idPremio") int idPremio);
}

