package com.example.CastorWay.api;

import com.example.CastorWay.modelsDB.Castor;
import com.example.CastorWay.modelsDB.Kit;

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
}
