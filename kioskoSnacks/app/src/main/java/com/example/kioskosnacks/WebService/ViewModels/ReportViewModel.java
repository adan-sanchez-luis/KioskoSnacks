package com.example.kioskosnacks.WebService.ViewModels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.WebService.Adapters.ReportAdapter;
import com.example.kioskosnacks.WebService.Constant.ConstantServis;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Models.ConceptReport;
import com.example.kioskosnacks.WebService.Observers.ReportObserver;
import com.example.kioskosnacks.WebService.Responses.ReportResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportViewModel {
    private String tag = "ReportViewModel";
    private ReportObserver reportObserver;
    private Context context;

    public ReportViewModel(Context context) {
        this.context = context;
    }

    // Método para registrar nuevos observadores
    public void setReportObserver(ReportObserver reportObserver) {
        this.reportObserver = reportObserver;
    }

    public void getTopSales(String date1, String date2, int order) {
        String token = getToken();
        HashMap<String, Object> body = new HashMap<>();
        body.put(ConstantServis.STARTTIME, date1);
        body.put(ConstantServis.ENDTIME, date2);
        body.put(ConstantServis.ORDERSORT, order);

        Call<JsonObject> call = ReportAdapter.getApiService().getTopSales(token, body);
        ;
        //String log = String.format("%s [%s]: body %s", tag, "login", body.toString());
        //Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject conceptJson = response.body();
                //String log = String.format("%s [%s]: %s", tag, "login", loggedIn.toString());
                //Timber.i(log.toString());
                if (conceptJson == null) {
                    String info = "Please review the data entered and try again";
                    ReportResponse nullJsonResponse = new ReportResponse("Respuesta JSON nula.", ReportResponse.MessageType.NULL_JSON, null);
                    notifyError(nullJsonResponse);
                    return;
                }

                String message = conceptJson.get("message").getAsString();
                if (!conceptJson.get("success").toString().toLowerCase().equals(String.valueOf(true))) {
                    ReportResponse errorResponse = new ReportResponse(message, ReportResponse.MessageType.ERROR, null);
                    notifyError(errorResponse);
                    return;
                }

                if (conceptJson.get("value").toString().equals("null")) {

                    //concept.setId(code);
                    ReportResponse errorResponse = new ReportResponse(message, ReportResponse.MessageType.NOTFOUND, null);
                    notifyError(errorResponse);
                } else {
                    JsonArray value = (JsonArray) conceptJson.get("value");
                    Gson gson = new Gson();
                    ConceptReport[] concept = gson.fromJson(value, ConceptReport[].class);

                    // Notificar a los observadores sobre el inicio de sesión exitoso y enviar el token
                    ReportResponse successResponse = new ReportResponse(message, ReportResponse.MessageType.SUCCESS, concept);
                    notifySuccess(successResponse);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                ReportResponse requestErrorMessage = new ReportResponse("Error en la petición.", ReportResponse.MessageType.REQUEST_ERROR, null);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    // Método para notificar a los observadores sobre el resultado del inicio de sesión exitoso
    private void notifySuccess(ReportResponse response) {
        if (reportObserver != null)
            reportObserver.onSuccessReport(response);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(ReportResponse errorResponse) {
        if (reportObserver != null)
            reportObserver.onErrorReport(errorResponse);
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
