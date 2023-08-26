package com.example.kioskosnacks.WebService.Adapters;

import com.example.kioskosnacks.WebService.Services.ConceptService;
import com.example.kioskosnacks.WebService.Services.PayService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PayAdapter {
    private static PayService API_SERVICE;

    private static final String BASE_URL = com.example.kioskosnacks.BuildConfig.API_URL;

    public static PayService getApiService() {
        // Creamos un interceptor y le indicamos el log level a usar
        final HttpLoggingInterceptor payConcept = new HttpLoggingInterceptor();
        payConcept.setLevel(HttpLoggingInterceptor.Level.BODY);

        int timeoutSeconds = 6;
        // Asociamos el interceptor a las peticiones
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                // Timeout para conectarse al servidor
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                // Timeout pra recibir respuesta del servidor
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                // Timeout para enviar datos al servidor
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .addInterceptor(payConcept);

        if (API_SERVICE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()) // <-- set log level
                    .build();

            API_SERVICE = retrofit.create(PayService.class);
        }

        return API_SERVICE;
    }
}
