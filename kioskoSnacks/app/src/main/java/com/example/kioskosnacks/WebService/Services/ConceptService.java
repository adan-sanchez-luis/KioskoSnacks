package com.example.kioskosnacks.WebService.Services;

import com.example.kioskosnacks.WebService.Constant.ConstantServis;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConceptService {

    @GET("/api/Concept?")
    Call<JsonObject> getConcept(@Query("id") String id, @Header(ConstantServis.AUTHORIZATION) String token);

    @POST("/api/Concept")
    Call<JsonObject> saveConcept(@Header(ConstantServis.AUTHORIZATION) String token, @Body HashMap<String, Object> body);

    @POST("/api/Concept/Update")
    Call<JsonObject> updateConcept(@Header(ConstantServis.AUTHORIZATION) String token, @Body HashMap<String, Object> body);
}
