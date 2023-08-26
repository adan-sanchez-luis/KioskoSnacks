package com.example.kioskosnacks.WebService.ViewModels;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.WebService.Adapters.ConceptAdapter;
import com.example.kioskosnacks.WebService.Constant.ConstantServis;
import com.example.kioskosnacks.WebService.Models.Concept;
import com.example.kioskosnacks.WebService.Observers.ConceptObserver;
import com.example.kioskosnacks.WebService.Responses.ConceptResponse;
import com.example.kioskosnacks.WebService.Observers.SaveConceptObserver;
import com.example.kioskosnacks.WebService.Responses.SaveConceptResponse;
import com.example.kioskosnacks.WebService.Observers.UpdateConceptObserver;
import com.example.kioskosnacks.WebService.Responses.UpdateConceptResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ConceptViewModel {
    private String tag = "InventoryViewModel";
    private ConceptObserver conceptObserver;
    private SaveConceptObserver saveConceptObserver;
    private UpdateConceptObserver updateConceptObserver;
    private Context context;

    public ConceptViewModel(Context context) {
        this.context = context;
    }

    // Método para registrar nuevos observadores
    public void addObserverGet(ConceptObserver observer) {
        this.conceptObserver = observer;
    }

    public void addObserverSave(SaveConceptObserver observer) {
        this.saveConceptObserver = observer;
    }

    public void addObserverUpdate(UpdateConceptObserver observer) {
        this.updateConceptObserver = observer;
    }

    public void getConcept(String code) {
        String token = getToken();
        Call<JsonObject> call = ConceptAdapter.getApiService().getConcept(code, token);
        System.out.println("codigo a buscar: " + code);
        String log = String.format("%s [%s]: send: code %s", tag, "getConcept", code);
        Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject conceptJson = response.body();
                //String log = String.format("%s [%s]: %s", tag, "login", conceptJson.toString());
                //Timber.i(log.toString());
                if (conceptJson == null) {
                    String info = "Please review the data entered and try again";
                    ConceptResponse nullJsonResponse = new ConceptResponse("Respuesta JSON nula.", ConceptResponse.MessageType.NULL_JSON, null);
                    notifyError(nullJsonResponse);
                    return;
                }

                String message = conceptJson.get("message").getAsString();
                if (!conceptJson.get("success").getAsBoolean()) {
                    ConceptResponse errorResponse = new ConceptResponse(message, ConceptResponse.MessageType.ERROR, null);
                    notifyError(errorResponse);
                    return;
                }

                if (conceptJson.get("value").toString().equals("null")) {
                    Concept concept = new Concept();
                    concept.setId(code);
                    ConceptResponse errorResponse = new ConceptResponse(message, ConceptResponse.MessageType.NOTFOUND, concept);
                    notifyError(errorResponse);
                } else {
                    JsonObject value = (JsonObject) conceptJson.get("value");
                    Gson gson = new Gson();
                    Concept concept = gson.fromJson(value, Concept.class);

                    // Notificar a los observadores sobre el inicio de sesión exitoso y enviar el token
                    ConceptResponse successResponse = new ConceptResponse(message, ConceptResponse.MessageType.SUCCESS, concept);
                    notifySuccess(successResponse);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                ConceptResponse requestErrorMessage = new ConceptResponse("Error en la petición.", ConceptResponse.MessageType.REQUEST_ERROR, null);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    public void saveConcept(Concept concept) {
        String token = getToken();
        HashMap<String, Object> body = new HashMap<>();
        body.put(ConstantServis.ID, concept.getId());
        body.put(ConstantServis.NAME, concept.getName());
        body.put(ConstantServis.DESCRIPTION, concept.getDescription());
        body.put(ConstantServis.UNIT, concept.getUnit());
        body.put(ConstantServis.PRICE, concept.getPrice());
        body.put(ConstantServis.ACOUNTTYPE, concept.getAcountType());
        body.put(ConstantServis.TAG, concept.getTag());
        body.put(ConstantServis.GROUP, concept.getGroup());
        body.put(ConstantServis.NOTES, concept.getNotes());
        body.put(ConstantServis.STATUS, concept.getStatus());
        body.put(ConstantServis.QUANTITY, concept.getQuantity());

        Call<JsonObject> call = ConceptAdapter.getApiService().saveConcept(token, body);

        //String log = String.format("%s [%s]: body %s", tag, "login", body.toString());
        //Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject conceptJson = response.body();
                //String log = String.format("%s [%s]: %s", tag, "login", loggedIn.toString());
                //Timber.i(log.toString());
                if (conceptJson != null) {
                    String message = conceptJson.get("message").getAsString();
                    if (conceptJson.get("success").toString().toLowerCase().equals(String.valueOf(true))) {
                        JsonObject value = (JsonObject) conceptJson.get("value");

                        Gson gson = new Gson();
                        Concept concept = gson.fromJson(value, Concept.class);

                        // Notificar a los observadores sobre el inicio de sesión exitoso y enviar el token
                        SaveConceptResponse successResponse = new SaveConceptResponse(message, SaveConceptResponse.MessageType.SUCCESS, null);
                        notifySuccess(successResponse);
                    } else {
                        SaveConceptResponse errorResponse = new SaveConceptResponse(message, SaveConceptResponse.MessageType.ERROR, null);
                        notifyError(errorResponse);
                    }
                } else {
                    String info = "Please review the data entered and try again";
                    SaveConceptResponse nullJsonResponse = new SaveConceptResponse("Respuesta JSON nula.", SaveConceptResponse.MessageType.NULL_JSON, null);
                    notifyError(nullJsonResponse);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                SaveConceptResponse requestErrorMessage = new SaveConceptResponse("Error en la petición.", SaveConceptResponse.MessageType.REQUEST_ERROR, null);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    public void updateConcept(Concept concept) {
        String token = getToken();
        HashMap<String, Object> body = new HashMap<>();
        body.put(ConstantServis.ID, concept.getId());
        body.put(ConstantServis.NAME, concept.getName());
        body.put(ConstantServis.DESCRIPTION, concept.getDescription());
        body.put(ConstantServis.QUANTITY, concept.getQuantity());
        body.put(ConstantServis.UNIT, concept.getUnit());
        body.put(ConstantServis.PRICE, concept.getPrice());
        body.put(ConstantServis.ACOUNTTYPE, concept.getAcountType());
        body.put(ConstantServis.TAG, concept.getTag());
        body.put(ConstantServis.GROUP, concept.getGroup());
        body.put(ConstantServis.NOTES, concept.getNotes());
        body.put(ConstantServis.STATUS, concept.getStatus());

        Call<JsonObject> call = ConceptAdapter.getApiService().updateConcept(token, body);

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
                    UpdateConceptResponse nullJsonResponse = new UpdateConceptResponse("Respuesta JSON nula.", UpdateConceptResponse.MessageType.NULL_JSON, false);
                    notifyError(nullJsonResponse);
                    return;
                }

                String message = conceptJson.get("message").getAsString();

                if (!conceptJson.get("success").toString().toLowerCase().equals(String.valueOf(true))) {
                    UpdateConceptResponse errorResponse = new UpdateConceptResponse(message, UpdateConceptResponse.MessageType.ERROR, false);
                    notifyError(errorResponse);
                    return;
                }

                if (conceptJson.get("value").toString().equals("null")) {
                    UpdateConceptResponse errorResponse = new UpdateConceptResponse(message, UpdateConceptResponse.MessageType.ERROR, false);
                    notifyError(errorResponse);
                } else {
                    JsonObject value = (JsonObject) conceptJson.get("value");
                    UpdateConceptResponse successResponse = new UpdateConceptResponse(message, UpdateConceptResponse.MessageType.SUCCESS, true);
                    notifySuccess(successResponse);
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                UpdateConceptResponse requestErrorMessage = new UpdateConceptResponse("Error en la petición.", UpdateConceptResponse.MessageType.REQUEST_ERROR, false);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    // Método para notificar a los observadores sobre el resultado del inicio de sesión exitoso
    private void notifySuccess(ConceptResponse response) {
        if (conceptObserver != null)
            conceptObserver.onSuccessConcept(response);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(ConceptResponse errorResponse) {
        if (conceptObserver != null)
            conceptObserver.onErrorConcept(errorResponse);
    }

    private void notifySuccess(SaveConceptResponse response) {
        if (saveConceptObserver != null)
            saveConceptObserver.onSuccessSaveConcept(response);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(SaveConceptResponse errorResponse) {
        if (saveConceptObserver != null)
            saveConceptObserver.onErrorSaveConcept(errorResponse);
    }

    private void notifySuccess(UpdateConceptResponse response) {
        if (updateConceptObserver != null)
            updateConceptObserver.onSuccessUpdate(response);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(UpdateConceptResponse errorResponse) {
        if (updateConceptObserver != null)
            updateConceptObserver.onErrorUpdate(errorResponse);
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
