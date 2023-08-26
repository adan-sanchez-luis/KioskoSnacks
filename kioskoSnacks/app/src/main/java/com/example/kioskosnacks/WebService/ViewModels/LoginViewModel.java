package com.example.kioskosnacks.WebService.ViewModels;

import com.example.kioskosnacks.WebService.Adapters.LoginAdapter;
import com.example.kioskosnacks.WebService.Constant.ConstantServis;
import com.example.kioskosnacks.WebService.Observers.LoginObserver;
import com.example.kioskosnacks.WebService.Responses.LoginResponse;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel {
    private String tag = "LoginViewModel";
    private LoginObserver loginObserver;

    // Método para registrar nuevos observadores
    public void addObserver(LoginObserver observer) {
        this.loginObserver = observer;
    }

    public void login(String user, String password, String appName) {
        HashMap<String, Object> body = new HashMap<>();
        body.put(ConstantServis.USERNAME, user);
        body.put(ConstantServis.PASSWORD, password);
        body.put(ConstantServis.APPNAME, appName);
        Call<JsonObject> call = LoginAdapter.getApiService().login(body);

        //String log = String.format("%s [%s]: body %s", tag, "login", body.toString());
        //Timber.i(log.toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Tratamos el JsonObject response para hacer la validación del usuario, obtenemos su token en iniciamos session
                JsonObject loggedIn = response.body();
                //String log = String.format("%s [%s]: %s", tag, "login", loggedIn.toString());
                //Timber.i(log.toString());
                if (loggedIn != null) {
                    if (loggedIn.get("success").toString().toLowerCase().equals(String.valueOf(true))) {
                        JsonObject value = (JsonObject) loggedIn.get("value");
                        String token = value.get("token").getAsString();

                        // Notificar a los observadores sobre el inicio de sesión exitoso y enviar el token
                        LoginResponse successLoginResponse = new LoginResponse("Inicio de sesión exitoso.", LoginResponse.MessageType.SUCCESS, token);
                        notifySuccess(successLoginResponse);
                    } else {
                        String info = loggedIn.get("message").getAsString();
                        LoginResponse errorLoginResponse = new LoginResponse(info, LoginResponse.MessageType.ERROR, null);
                        notifyError(errorLoginResponse);
                    }
                } else {
                    String info = "Please review the data entered and try again";
                    LoginResponse nullJsonLoginResponse = new LoginResponse("Respuesta JSON nula.", LoginResponse.MessageType.NULL_JSON, null);
                    notifyError(nullJsonLoginResponse);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //alerta.messageAcept(getString(R.string.sinConexion), getString(R.string.verificarConexion), R.drawable.no_wifi);
                //writerLog.registerLog(TypeLog.SIN_CONEXCION, getString(R.string.sin_conexcion_log), className);
                LoginResponse requestErrorMessage = new LoginResponse("Error en la petición.", LoginResponse.MessageType.REQUEST_ERROR, null);

                // Notificar a los observadores sobre el error en el inicio de sesión y enviar el mensaje
                notifyError(requestErrorMessage);
            }
        });
    }

    // Método para notificar a los observadores sobre el resultado del inicio de sesión exitoso
    private void notifySuccess(LoginResponse token) {
        if (loginObserver != null)
            loginObserver.onSuccessLogin(token);
    }

    // Método para notificar a los observadores sobre el error en el inicio de sesión
    private void notifyError(LoginResponse errorLoginResponse) {
        if (loginObserver != null)
            loginObserver.onErrorLogin(errorLoginResponse);
    }

}
