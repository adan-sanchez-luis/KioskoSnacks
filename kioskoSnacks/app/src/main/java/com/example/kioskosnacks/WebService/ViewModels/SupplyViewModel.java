package com.example.kioskosnacks.WebService.ViewModels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.WebService.Adapters.SupplyAdapter;
import com.example.kioskosnacks.WebService.Constant.ConstantServis;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Models.SupplyDetail;
import com.example.kioskosnacks.WebService.Observers.SupplyObserver;
import com.example.kioskosnacks.WebService.Responses.SupplyResponse;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupplyViewModel {

    private SupplyObserver supplyObserver;
    private Context context;

    public SupplyViewModel(Context context) {
        this.context = context;
    }

    public void addObserver(SupplyObserver supplyObserver) {
        this.supplyObserver = supplyObserver;
    }


    public void addSuply(ArrayList<Concept> supplyList) {
        String token = getToken();

        HashMap<String, Object> body = new HashMap<>();
        body.put(ConstantServis.STATUS,1);
        body.put(ConstantServis.IDSYSUSER,1);
        body.put(ConstantServis.SUPPLYDATE,dateNow());
        List<SupplyDetail> supplyDetails = new ArrayList<>();

        for (Concept item : supplyList) {
            SupplyDetail supplyDetail = new SupplyDetail(1,item.getId(),item.getInCart());
            supplyDetails.add(supplyDetail);
        }
        body.put(ConstantServis.SUPPLYDETAILS,supplyDetails);

        Call<JsonObject> call = SupplyAdapter.getApiService().Supply(token, body);

        //String log = String.format("%s [%s]: body %s", tag, "login", body.toString());
        //Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject supplyJson = response.body();
                //String log = String.format("%s [%s]: %s", tag, "login", loggedIn.toString());
                //Timber.i(log.toString());
                if (supplyJson == null) {
                    String info = "Please review the data entered and try again";
                    SupplyResponse nullJsonLoginResponse = new SupplyResponse("Respuesta JSON nula.", SupplyResponse.MessageType.NULL_JSON, false);
                    notifyError(nullJsonLoginResponse);
                    return;
                }

                String info = supplyJson.get("message").getAsString();
                if (!supplyJson.get("success").toString().toLowerCase().equals(String.valueOf(true))) {
                    SupplyResponse errorLoginResponse = new SupplyResponse(info, SupplyResponse.MessageType.ERROR, false);
                    notifyError(errorLoginResponse);
                    return;
                }

                if (supplyJson.get("value").toString().equals("null")) {
                    SupplyResponse errorLoginResponse = new SupplyResponse(info, SupplyResponse.MessageType.ERROR, false);
                    notifyError(errorLoginResponse);
                } else {
                    JsonObject value = (JsonObject) supplyJson.get("value");
                    //String token = value.get("token").getAsString();

                    // Notificar a los observadores sobre el inicio de sesión exitoso y enviar el token
                    SupplyResponse successLoginResponse = new SupplyResponse(info, SupplyResponse.MessageType.SUCCESS, true);
                    notifySuccess(successLoginResponse);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                SupplyResponse requestErrorMessage = new SupplyResponse("Error en la petición.", SupplyResponse.MessageType.REQUEST_ERROR, false);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    private String dateNow(){
        // Obtener la hora actual como un objeto Date
        Date currentDate = new Date();

        // Definir el formato deseado
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // Formatear la fecha actual con el formato especificado
        String formattedDate = sdf.format(currentDate);

        return formattedDate;
    }

    // Método para notificar a los observadores sobre el resultado del inicio de sesión exitoso
    private void notifySuccess(SupplyResponse supplyResponse) {
        if (supplyObserver != null)
            supplyObserver.onSuccessSuply(supplyResponse);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(SupplyResponse supplyResponse) {
        if (supplyObserver != null)
            supplyObserver.onErrorSupply(supplyResponse);
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
