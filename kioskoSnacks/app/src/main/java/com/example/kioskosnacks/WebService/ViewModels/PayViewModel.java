package com.example.kioskosnacks.WebService.ViewModels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.WebService.Adapters.PayAdapter;
import com.example.kioskosnacks.WebService.Constant.ConstantServis;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Models.SalesDetails;
import com.example.kioskosnacks.WebService.Observers.PayObserver;
import com.example.kioskosnacks.WebService.Responses.PayConceptResponse;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PayViewModel {
    private String tag = "PayViewModel";
    private PayObserver payObserver;
    private Context context;

    public PayViewModel(Context context) {
        this.context = context;
    }

    // Método para registrar nuevos observadores
    public void addObserver(PayObserver observer) {
        this.payObserver = observer;
    }

    public void Pay(ArrayList<Concept> concepts, double total) {
        String token = getToken();

        String date = dateNow();
        HashMap<String, Object> body = new HashMap<>();
        body.put(ConstantServis.IDCUSTOMER, 8);
        body.put(ConstantServis.DATE, date);
        body.put(ConstantServis.TOTAL, total);
        body.put(ConstantServis.STATUS, 1);
        body.put(ConstantServis.PAYDATE, date);

        List<SalesDetails> salesDetails = new ArrayList<>();
        for (Concept item : concepts) {
            double price = item.getPrice() * item.getInCart();
            SalesDetails details = new SalesDetails(item.getId(), item.getInCart(), price, 1);
            salesDetails.add(details);
        }
        body.put(ConstantServis.SALESDETAILS, salesDetails);

        Call<JsonObject> call = PayAdapter.getApiService().PayConcepts(token, body);

        //String log = String.format("%s [%s]: body %s", tag, "login", body.toString());
        //Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject payConceps = response.body();
                if (payConceps == null) {
                    String info = "Please review the data entered and try again";
                    PayConceptResponse nullJsonLoginResponse = new PayConceptResponse("Respuesta JSON nula.", PayConceptResponse.MessageType.NULL_JSON, false);
                    notifyError(nullJsonLoginResponse);
                    return;
                }

                if (!payConceps.get("success").toString().toLowerCase().equals(String.valueOf(true))) {
                    String info = payConceps.get("message").getAsString();
                    PayConceptResponse errorLoginResponse = new PayConceptResponse(info, PayConceptResponse.MessageType.ERROR, false);
                    notifyError(errorLoginResponse);
                    return;
                }

                if (payConceps.get("value").toString().equals("null")) {
                    String info = payConceps.get("message").getAsString();
                    PayConceptResponse errorLoginResponse = new PayConceptResponse(info, PayConceptResponse.MessageType.ERROR, false);
                    notifyError(errorLoginResponse);
                } else {
                    JsonObject value = (JsonObject) payConceps.get("value");
                    //boolean success = value.get("success").getAsBoolean();

                    // Notificar a los observadores sobre el inicio de sesión exitoso y enviar el token
                    PayConceptResponse successLoginResponse = new PayConceptResponse("Success Payment.", PayConceptResponse.MessageType.SUCCESS, true);
                    notifySuccess(successLoginResponse);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                PayConceptResponse requestErrorMessage = new PayConceptResponse("Error en la petición.", PayConceptResponse.MessageType.REQUEST_ERROR, false);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });

        //String log = String.format("%s [%s]: %s", tag, "pay", e.getMessage());
        //Timber.i(log.toString());

    }

    // Método para notificar a los observadores sobre el resultado del inicio de sesión exitoso
    private void notifySuccess(PayConceptResponse response) {
        if (payObserver != null)
            payObserver.onSuccessPayConcept(response);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(PayConceptResponse errorResponse) {
        if (payObserver != null)
            payObserver.onErrorPayConcept(errorResponse);
    }

    private String dateNow() {
        // Obtener la hora actual como un objeto Date
        Date currentDate = new Date();

        // Definir el formato deseado
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        // Formatear la fecha actual con el formato especificado
        String formattedDate = sdf.format(currentDate);

        return formattedDate;
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
