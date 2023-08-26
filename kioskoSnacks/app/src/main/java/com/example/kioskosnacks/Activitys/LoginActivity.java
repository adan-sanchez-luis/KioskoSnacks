package com.example.kioskosnacks.Activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kioskosnacks.Alertas.AlertMessage;
import com.example.kioskosnacks.DataBase.TokenDB;
import com.example.kioskosnacks.R;
import com.example.kioskosnacks.Validator.InputValidator;
import com.example.kioskosnacks.WebService.Observers.LoginObserver;
import com.example.kioskosnacks.WebService.Responses.LoginResponse;
import com.example.kioskosnacks.WebService.ViewModels.LoginViewModel;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginObserver {
    private CircularProgressButton btnLogin;
    private LoginViewModel loginViewModel;
    private AlertMessage alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(this);

        Button btnCancel = findViewById(R.id.btnCancelLogin);
        btnCancel.setOnClickListener(this);

        loginViewModel = new LoginViewModel();
        loginViewModel.addObserver(this); // Registrar esta actividad como observador

        alert = new AlertMessage(this);
        alert.setListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                login();
                break;
            case R.id.btnCancelLogin:
                this.finish();
                break;
            case R.id.alertConfirm:
                alert.alertDismiss();
                break;
        }
    }

    private void login() {
        EditText tvUserName = findViewById(R.id.userName);
        EditText tvPassword = findViewById(R.id.password);
        EditText tvcode = findViewById(R.id.codeApp);

        /*
        tvUserName.setText("admin");
        tvPassword.setText("admin");
        tvcode.setText("snacks");*/

        if (InputValidator.isEmpty(tvUserName) || InputValidator.isEmpty(tvPassword) || InputValidator.isEmpty(tvcode)) {
            alert.setTitle("Please fill in the empty fields");
            alert.setAnimation(R.raw.warning_orange);
            alert.setLoop(true);
            alert.disableCancel(true);
            alert.showAlert();
            return;
        }

        delateLastToken();
        starBtn();
        String userName = tvUserName.getText().toString();
        String password = tvPassword.getText().toString();
        String appName = tvcode.getText().toString();

        loginViewModel.login(userName, password, appName);
    }

    private void starBtn() {
        btnLogin.startAnimation();
        btnLogin.setEnabled(false);
    }

    private void endBtn() {
        btnLogin.revertAnimation();
        btnLogin.setEnabled(true);
    }

    public void delateLastToken() {
        String token = "";
        TokenDB dataHelper = new TokenDB(this);
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        Cursor fila = db.rawQuery("select token from TokenTable", null);
        while (fila.moveToNext()) {
            token = fila.getString(0);
            int dele = db.delete("TokenTable", "token='" + token + "'", null);
        }
        db.close();
    }

    @Override
    public void onSuccessLogin(LoginResponse succesResponse) {
        endBtn();
        String token = succesResponse.getToken();
        if (token == null)
            return;

        TokenDB DB = new TokenDB(this);
        SQLiteDatabase tokenDB = DB.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("token", token);
        tokenDB.insert("TokenTable", null, registro);
        tokenDB.close();

        Intent intent = new Intent(this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onErrorLogin(LoginResponse errorResponse) {
        endBtn();
        alert.disableCancel(true);
        switch (errorResponse.getType()) {
            case ERROR:
                alert.setTitle("User or password incorrect");
                alert.setAnimation(R.raw.warning);
                alert.showAlert();
                break;
            case NULL_JSON:
                alert.setTitle("Please try again");
                alert.setAnimation(R.raw.warning_orange);
                alert.setLoop(true);
                alert.showAlert();
                break;
            case REQUEST_ERROR:
                alert.setTitle(getString(R.string.noConnexion));
                alert.setAnimation(R.raw.warning_red);
                alert.showAlert();
                break;
        }
        String errorMessage = errorResponse.getMessage();
        if (errorMessage != null) {
            // Manejar el error del inicio de sesión y mostrar algún mensaje de error en la actividad
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}