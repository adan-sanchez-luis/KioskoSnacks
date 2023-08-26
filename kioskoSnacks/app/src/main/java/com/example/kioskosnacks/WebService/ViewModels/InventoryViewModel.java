package com.example.kioskosnacks.WebService.ViewModels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.WebService.Adapters.InventoryAdapter;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.InventoryObserver;
import com.example.kioskosnacks.WebService.Responses.InventoryResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryViewModel {
    private String tag = "InventoryViewModel";
    private InventoryObserver observer;
    private Context context;

    public InventoryViewModel(Context context) {
        this.context = context;
    }

    // Método para registrar nuevos observadores
    public void addObserver(InventoryObserver observer) {
        this.observer = observer;
    }

    public void getInventory() {
        String token = getToken();
        Call<JsonObject> call = InventoryAdapter.getApiService().Inventory(token);

        //String log = String.format("%s [%s]: body %s", tag, "login", body.toString());
        //Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject inventory = response.body();
                //String log = String.format("%s [%s]: %s", tag, "login", loggedIn.toString());
                //Timber.i(log.toString());

                // Notificar a los observadores
                if (inventory == null) {
                    String info = "Please review the data entered and try again";
                    InventoryResponse nullJsonLoginResponse = new InventoryResponse("Respuesta JSON nula.", InventoryResponse.MessageType.NULL_JSON, null);
                    notifyError(nullJsonLoginResponse);
                    return;
                }

                String message = inventory.get("message").getAsString();
                if (!inventory.get("success").getAsBoolean()) {
                    InventoryResponse errorLoginResponse = new InventoryResponse(message, InventoryResponse.MessageType.ERROR, null);
                    notifyError(errorLoginResponse);
                    return;
                }

                if (inventory.get("value").toString().equals("null")) {
                    InventoryResponse errorLoginResponse = new InventoryResponse(message, InventoryResponse.MessageType.ERROR, null);
                    notifyError(errorLoginResponse);
                } else {
                    JsonArray value = (JsonArray) inventory.get("value");

                    Gson gson = new Gson();
                    Concept[] inventoryItems = gson.fromJson(value, Concept[].class);

                    InventoryResponse successResponse = new InventoryResponse(message, InventoryResponse.MessageType.SUCCESS, inventoryItems);
                    notifySuccess(successResponse);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                InventoryResponse requestErrorMessage = new InventoryResponse("Error en la petición.", InventoryResponse.MessageType.REQUEST_ERROR, null);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    // Método para notificar a los observadores sobre el resultado del inicio de sesión exitoso
    private void notifySuccess(InventoryResponse response) {
        if (observer != null)
            observer.onSuccessInventory(response);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(InventoryResponse errorResponse) {
        if (observer != null)
            observer.onErrorInventory(errorResponse);
    }

    public String getToken() {
        String token = null;
        TokenDB dataHelper = new TokenDB(context);
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        Cursor fila = db.rawQuery("select token from TokenTable", null);
        while (fila.moveToNext()) {
            token = "Bearer " + fila.getString(0);
        }
        db.close();
        return token;
    }
}
